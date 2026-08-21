import os
import logging
from typing import Optional

import cv2
import numpy as np
import onnxruntime as ort
from fastapi import FastAPI, File, UploadFile, HTTPException
from insightface.app import FaceAnalysis


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("face-detection")

app = FastAPI(title="Face Vector Extraction API")


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

MODEL_NAME = os.getenv("MODEL_NAME", "buffalo_sc")
DET_SIZE = int(os.getenv("DET_SIZE", "640"))
ANTISPOOF_MODEL_DIR = os.getenv("ANTISPOOF_MODEL_DIR", "models")

# MiniFASNet class mapping:
#
#   class 0 = Fake / Print
#   class 1 = Real / Live
#   class 2 = Fake / Replay
#
LIVE_CLASS_INDEX = int(
    os.getenv("LIVE_CLASS_INDEX", "1")
)

ANTISPOOF_THRESHOLD = float(
    os.getenv("ANTISPOOF_THRESHOLD", "0.85")
)


# ---------------------------------------------------------------------------
# 1. InsightFace
# ---------------------------------------------------------------------------

face_app = FaceAnalysis(
    name=MODEL_NAME,
    providers=["CPUExecutionProvider"],
)

face_app.prepare(
    ctx_id=0,
    det_size=(DET_SIZE, DET_SIZE),
)


# ---------------------------------------------------------------------------
# 2. MiniFASNet ONNX models
# ---------------------------------------------------------------------------

_ANTISPOOF_MODEL_SPECS = [
    {
        "file": "2.7_80x80_MiniFASNetV2.onnx",
        "scale": 2.7,
    },
    {
        "file": "4_0_0_80x80_MiniFASNetV1SE.onnx",
        "scale": 4.0,
    },
]


antispoof_sessions = []

for spec in _ANTISPOOF_MODEL_SPECS:

    path = os.path.join(
        ANTISPOOF_MODEL_DIR,
        spec["file"],
    )

    if not os.path.isfile(path):
        logger.warning(
            "Anti-spoof model not found: %s",
            path,
        )
        continue

    try:
        session = ort.InferenceSession(
            path,
            providers=["CPUExecutionProvider"],
        )

        antispoof_sessions.append(
            {
                "session": session,
                "scale": spec["scale"],
                "file": spec["file"],
            }
        )

        logger.info(
            "Loaded Anti-Spoofing model: %s (scale=%s)",
            path,
            spec["scale"],
        )

    except Exception as e:
        logger.exception(
            "Failed to load anti-spoof model %s: %s",
            path,
            e,
        )


if antispoof_sessions:
    logger.info(
        "Anti-spoofing engine: MiniFASNet Ensemble (%d models)",
        len(antispoof_sessions),
    )
else:
    logger.warning(
        "No MiniFASNet models loaded. "
        "Liveness detection will be disabled."
    )


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _softmax(x: np.ndarray) -> np.ndarray:
    x = x - np.max(x)
    exp_x = np.exp(x)
    return exp_x / exp_x.sum()


def _get_new_box(
    src_w: int,
    src_h: int,
    bbox: list,
    scale: float,
):
    """
    Crop algorithm matching Silent-Face-Anti-Spoofing-onnx.

    bbox format:
        [x, y, width, height]
    """

    x, y, box_w, box_h = bbox

    if box_w <= 0 or box_h <= 0:
        raise ValueError("Invalid face bounding box")

    scale = min(
        (src_h - 1) / box_h,
        min(
            (src_w - 1) / box_w,
            scale,
        ),
    )

    new_width = box_w * scale
    new_height = box_h * scale

    center_x = box_w / 2 + x
    center_y = box_h / 2 + y

    left_top_x = center_x - new_width / 2
    left_top_y = center_y - new_height / 2

    right_bottom_x = center_x + new_width / 2
    right_bottom_y = center_y + new_height / 2

    if left_top_x < 0:
        right_bottom_x -= left_top_x
        left_top_x = 0

    if left_top_y < 0:
        right_bottom_y -= left_top_y
        left_top_y = 0

    if right_bottom_x > src_w - 1:
        left_top_x -= right_bottom_x - src_w + 1
        right_bottom_x = src_w - 1

    if right_bottom_y > src_h - 1:
        left_top_y -= right_bottom_y - src_h + 1
        right_bottom_y = src_h - 1

    return (
        int(left_top_x),
        int(left_top_y),
        int(right_bottom_x),
        int(right_bottom_y),
    )


def _preprocess_face(
    img_bgr: np.ndarray,
    bbox,
    scale: float,
    out_size: int = 80,
) -> np.ndarray:
    """
    Preprocessing matching Silent-Face-Anti-Spoofing-onnx.

    IMPORTANT:
    - Keep BGR
    - Do NOT convert to RGB
    - Do NOT divide by 255
    - Resize to 80x80
    - HWC -> CHW
    - Add batch dimension
    """

    src_h, src_w = img_bgr.shape[:2]

    # InsightFace bbox:
    # [x1, y1, x2, y2]

    x1, y1, x2, y2 = [
        float(v)
        for v in bbox
    ]

    box_w = x2 - x1
    box_h = y2 - y1

    if box_w <= 0 or box_h <= 0:
        raise ValueError("Invalid InsightFace bbox")

    # Convert:
    # [x1, y1, x2, y2]
    #
    # to:
    # [x, y, width, height]

    ref_bbox = [
        x1,
        y1,
        box_w,
        box_h,
    ]

    (
        left_top_x,
        left_top_y,
        right_bottom_x,
        right_bottom_y,
    ) = _get_new_box(
        src_w,
        src_h,
        ref_bbox,
        scale,
    )

    face_crop = img_bgr[
        left_top_y:right_bottom_y + 1,
        left_top_x:right_bottom_x + 1,
    ]

    if face_crop.size == 0:
        raise ValueError(
            "Empty anti-spoof crop: "
            f"{left_top_x},{left_top_y},"
            f"{right_bottom_x},{right_bottom_y}"
        )

    face_resized = cv2.resize(
        face_crop,
        (out_size, out_size),
    )

    # IMPORTANT:
    # Keep BGR.
    # Keep pixel range 0-255.

    face_float = face_resized.astype(
        np.float32
    )

    # HWC -> CHW

    face_chw = np.transpose(
        face_float,
        (2, 0, 1),
    )

    # NCHW

    face_batch = np.expand_dims(
        face_chw,
        axis=0,
    )

    return face_batch


# ---------------------------------------------------------------------------
# Anti-Spoofing
# ---------------------------------------------------------------------------

def check_liveness(
    img: np.ndarray,
    bbox,
) -> Optional[dict]:
    """
    MiniFASNet ensemble.

    Security policy:
    The weakest model determines liveness.

    Class mapping:
        0 = Fake / Print
        1 = Real / Live
        2 = Fake / Replay
    """

    if not antispoof_sessions:
        return None

    try:

        model_results = []

        # Keep probability from every model.
        model_probs = []

        for model in antispoof_sessions:

            session = model["session"]
            scale = model["scale"]
            model_file = model["file"]

            blob = _preprocess_face(
                img,
                bbox,
                scale,
                out_size=80,
            )

            input_name = (
                session
                .get_inputs()[0]
                .name
            )

            output_name = (
                session
                .get_outputs()[0]
                .name
            )

            raw = session.run(
                [output_name],
                {
                    input_name: blob,
                },
            )[0][0]

            probs = _softmax(raw)

            model_probs.append(probs)

            model_results.append(
                {
                    "model": model_file,
                    "scale": scale,
                    "raw": [
                        round(float(v), 6)
                        for v in raw
                    ],
                    "probs": {
                        "class_0_fake": round(
                            float(probs[0]),
                            4,
                        ),
                        "class_1_live": round(
                            float(probs[1]),
                            4,
                        ),
                        "class_2_fake": round(
                            float(probs[2]),
                            4,
                        ),
                    },
                }
            )

        # -------------------------------------------------------------------
        # IMPORTANT:
        #
        # Do NOT average live scores.
        #
        # Use the weakest model.
        #
        # Example:
        #
        # Model 1 = 0.9997
        # Model 2 = 0.9999
        #
        # live_score = 0.9997
        #
        # Fake:
        #
        # Model 1 = 0.0081
        # Model 2 = 0.5768
        #
        # live_score = 0.0081
        #
        # This is more conservative for security.
        # -------------------------------------------------------------------

        live_scores = [
            float(probs[LIVE_CLASS_INDEX])
            for probs in model_probs
        ]

        live_score = min(live_scores)

        is_real = (
            live_score >= ANTISPOOF_THRESHOLD
        )

        # -------------------------------------------------------------------
        # For reporting, calculate average probabilities.
        #
        # NOTE:
        # This is ONLY for diagnostics.
        # It is NOT used for the security decision.
        # -------------------------------------------------------------------

        probs_avg = np.mean(
            np.stack(model_probs),
            axis=0,
        )

        return {
            "is_real_face": bool(is_real),

            "liveness_score": round(
                live_score,
                4,
            ),

            "raw_probs": {
                "index_0_fake": round(
                    float(probs_avg[0]),
                    4,
                ),
                "index_1_live": round(
                    float(probs_avg[1]),
                    4,
                ),
                "index_2_fake": round(
                    float(probs_avg[2]),
                    4,
                ),

                # This is the score actually
                # used for the decision.
                "decision_method": "min_model_live_score",

                "model_live_scores": [
                    round(score, 4)
                    for score in live_scores
                ],

                "live_class_index_used": LIVE_CLASS_INDEX,

                "threshold": ANTISPOOF_THRESHOLD,
            },

            "models": model_results,
        }

    except Exception as e:

        logger.exception(
            "MiniFASNet liveness check failed: %s",
            e,
        )

        # Fail closed:
        # If anti-spoofing fails, do NOT trust the face.

        return {
            "is_real_face": False,
            "liveness_score": 0.0,
            "raw_probs": None,
        }


# ---------------------------------------------------------------------------
# Image decoding
# ---------------------------------------------------------------------------

def _decode_image(
    contents: bytes,
) -> np.ndarray:

    nparr = np.frombuffer(
        contents,
        np.uint8,
    )

    img = cv2.imdecode(
        nparr,
        cv2.IMREAD_COLOR,
    )

    if img is None:
        raise HTTPException(
            status_code=400,
            detail="Invalid image payload",
        )

    return img


# ---------------------------------------------------------------------------
# API
# ---------------------------------------------------------------------------

@app.get("/health")
def health():

    return {
        "status": "ok",
        "model": MODEL_NAME,

        "liveness_engine": (
            "MiniFASNet-ensemble"
            if antispoof_sessions
            else "DISABLED"
        ),

        "antispoof_models_loaded": len(
            antispoof_sessions
        ),

        "live_class_index": LIVE_CLASS_INDEX,

        "antispoof_threshold": ANTISPOOF_THRESHOLD,

        "decision_method": (
            "min_model_live_score"
        ),
    }


@app.post("/extract-face")
async def extract_face(
    file: UploadFile = File(...),
):

    contents = await file.read()

    img = _decode_image(contents)

    faces = face_app.get(img)

    if not faces:

        return {
            "success": False,
            "message": "No face detected in the image",
            "faces": [],
        }

    face_data = []

    for face in faces:

        bbox = (
            face.bbox
            .astype(int)
            .tolist()
        )

        liveness = check_liveness(
            img,
            bbox,
        )

        entry = {
            "bbox": bbox,

            "det_score": float(
                face.det_score
            ),

            "embedding": (
                face.embedding.tolist()
            ),

            "pose": (
                face.pose.tolist()
                if getattr(
                    face,
                    "pose",
                    None,
                ) is not None
                else None
            ),
        }

        if liveness is not None:

            entry.update(liveness)

        else:

            entry.update(
                {
                    "is_real_face": None,
                    "liveness_score": None,
                    "raw_probs": None,
                }
            )

        face_data.append(entry)

    return {
        "success": True,
        "face_count": len(face_data),
        "faces": face_data,
    }