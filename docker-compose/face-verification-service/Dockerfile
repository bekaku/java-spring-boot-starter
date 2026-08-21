FROM python:3.12-slim

WORKDIR /app

# ติดตั้ง Dependencies สำหรับ OpenCV / onnxruntime
# หมายเหตุ: InsightFace 1.0 ไม่ต้อง build C++/Cython extension แล้วโดย default
# แต่เผื่อ wheel บาง arch ไม่มี prebuilt ก็ยังเก็บ build-essential ไว้ ถ้า build image
# ของคุณผ่านแบบไม่ต้องคอมไพล์อะไรเลย ค่อยเอาสองบรรทัดนี้ออกเพื่อลด image size
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    g++ \
    libglib2.0-0 \
    libgomp1 \
    curl \
    # opencv-python-headless ยัง dynamically link กับ lib พวกนี้แม้เป็น "headless" build
    # (ผ่าน ffmpeg/gstreamer backend ที่ bundle มาด้วย) ขาดตัวไหนไม่ได้ ไม่งั้น import cv2 พังทันที
    libgl1 \
    libxcb1 \
    libxcb-shm0 \
    libx11-6 \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# โมเดล anti-spoofing (MiniFASNet) — ต้อง download ไฟล์ onnx สองไฟล์มาวางไว้ที่
# ./models/ ก่อน build (ดู README.md) ห้ามดึงจาก internet ตอน build เพราะไม่ reproducible
COPY models/ ./models/

COPY . .

EXPOSE 8000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]