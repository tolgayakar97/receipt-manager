from fastapi import FastAPI, UploadFile, File
from paddleocr import PaddleOCR
import tempfile
import os

ocr_engine = PaddleOCR(
    lang="tr",
    use_doc_orientation_classify=False,
    use_doc_unwarping=False,
    use_textline_orientation=False
)

app = FastAPI()

@app.get("/health")
def health_check():
    return {"status": "ok"}

@app.post("/ocr")
async def ocr(file: UploadFile = File(...)):

    file_contents =  await file.read() # Uploaded file binary content

    file_type = os.path.splitext(file.filename)[1] # Obtain file suffix such as .jpg, .png etc.

    # Creation a temp file from uploaded file with tempfile
    with tempfile.NamedTemporaryFile(delete=False, suffix=file_type) as temp_file:
        temp_file.write(file_contents) # creates temp_file
        temp_file_path = temp_file.name

        try :
            result = ocr_engine.predict(temp_file_path)
            rec_texts = result[0]['rec_texts']
            print(f"OCR RESULT: {rec_texts}")
            #TODO: Send result to the parser

            return {
                "filename": file.filename,
                "texts": rec_texts
            }
        finally:
            os.unlink(temp_file_path)
