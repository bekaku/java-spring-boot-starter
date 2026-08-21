# Face Detection API — Production Liveness Notes

### 1. Passive liveness with MiniFASNet (Silent-Face-Anti-Spoofing)

A CNN model specifically trained to separate live/print-attack/replay-attack from a single frame.
Uses an ensemble of 2 models (based on the original minivision-ai/Silent-Face-Anti-Spoofing):

- `2.7_80x80_MiniFASNetV2.onnx`
- `4_0_0_80x80_MiniFASNetV1SE.onnx`

```bash
git clone https://github.com/QingHeYang/Silent-Face-Anti-Spoofing-onnx.git
mkdir -p models
cp Silent-Face-Anti-Spoofing-onnx/onnx/2.7_80x80_MiniFASNetV2.onnx models/
cp Silent-Face-Anti-Spoofing-onnx/onnx/4_0_0_80x80_MiniFASNetV1SE.onnx models/
```
**You need to download it yourself** and place it in `models/` before building the image (it's not included here because it's a binary weight from a third-party repo).

### Test curl see result
Fake Image
```curl
curl -X POST \                                          
  -F "file=@/Users/bekaku/Downloads/image_F1.jpg" \                        
  http://localhost:8000/extract-face | python3 -m json.tool
  {
    "success": true,
    "face_count": 1,
    "faces": [
        {
            "bbox": [
                192,
                105,
                385,
                354
            ],
            "det_score": 0.8671905398368835,
            "embedding": [...],
            "pose": null,
            "is_real_face": false,
            "liveness_score": 0.0081,
            "raw_probs": {
                "index_0_fake": 0.0581,
                "index_1_live": 0.2925,
                "index_2_fake": 0.6494,
                "decision_method": "min_model_live_score",
                "model_live_scores": [
                    0.0081,
                    0.5768
                ],
                "live_class_index_used": 1,
                "threshold": 0.85
            },
            "models": [
                {
                    "model": "2.7_80x80_MiniFASNetV2.onnx",
                    "scale": 2.7,
                    "raw": [
                        -0.224055,
                        -2.256454,
                        2.481452
                    ],
                    "probs": {
                        "class_0_fake": 0.0621,
                        "class_1_live": 0.0081,
                        "class_2_fake": 0.9297
                    }
                },
                {
                    "model": "4_0_0_80x80_MiniFASNetV1SE.onnx",
                    "scale": 4.0,
                    "raw": [
                        -1.428944,
                        0.937463,
                        0.491044
                    ],
                    "probs": {
                        "class_0_fake": 0.0541,
                        "class_1_live": 0.5768,
                        "class_2_fake": 0.3691
                    }
                }
            ]
        }
    ]
}
```

Real Image
```curl
curl -X POST \                                          
  -F "file=@/Users/bekaku/Downloads/image_T1.jpg" \                        
  http://localhost:8000/extract-face | python3 -m json.tool
  {
    "success": true,
    "face_count": 1,
    "faces": [
        {
            "bbox": [
                117,
                109,
                296,
                351
            ],
            "det_score": 0.8219590187072754,
            "embedding": [...],
            "pose": null,
            "is_real_face": true,
            "liveness_score": 0.9997,
            "raw_probs": {
                "index_0_fake": 0.0,
                "index_1_live": 0.9998,
                "index_2_fake": 0.0002,
                "decision_method": "min_model_live_score",
                "model_live_scores": [
                    0.9997,
                    0.9999
                ],
                "live_class_index_used": 1,
                "threshold": 0.85
            },
            "models": [
                {
                    "model": "2.7_80x80_MiniFASNetV2.onnx",
                    "scale": 2.7,
                    "raw": [
                        -4.819232,
                        6.497588,
                        -1.68257
                    ],
                    "probs": {
                        "class_0_fake": 0.0,
                        "class_1_live": 0.9997,
                        "class_2_fake": 0.0003
                    }
                },
                {
                    "model": "4_0_0_80x80_MiniFASNetV1SE.onnx",
                    "scale": 4.0,
                    "raw": [
                        -4.438199,
                        6.966324,
                        -2.530952
                    ],
                    "probs": {
                        "class_0_fake": 0.0,
                        "class_1_live": 0.9999,
                        "class_2_fake": 0.0001
                    }
                }
            ]
        }
    ]
}
```

```curl
curl http://localhost:8000/health | python3 -m json.tool
{
    "status": "ok",
    "model": "buffalo_sc",
    "liveness_engine": "MiniFASNet-ensemble",
    "antispoof_models_loaded": 2,
    "live_class_index": 1,
    "antispoof_threshold": 0.85,
    "decision_method": "min_model_live_score"
}
```