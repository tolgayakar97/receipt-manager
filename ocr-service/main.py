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
            # For now, we return the result as if a parser were present here and the output came from the `ocr.predict` + `parse` operation.

            # result = ocr_engine.predict(temp_file_path)
            # rec_texts = result[0]['rec_texts']
            #print(f"OCR RESULT: {rec_texts}")
            #TODO: Send result to the parser

            parsed_receipt = {
                "merchantName": "YUNUS MARKET ISLT.TIC.A.Ş.",
                "receiptNumber": "0039",
                "purchaseDate": "2026-08-29",
                "totalAmount": 405.73,
                "items": [
                    {
                        "name": "CUMHURIYET SUCUK KG",
                        "quantity": 0.244,
                        "unit": "KG",
                        "unitPrice": 1519.80
                    },
                    {
                        "name": "ULKER PETIBOR COKOK",
                        "quantity": 1,
                        "unit": "AD",
                        "unitPrice": 34.90
                    }
                ]
            }

            return {
                "filename": file.filename,
                "texts": ["rec_texts"],
                "parsedReceipt": parsed_receipt
            }
        finally:
            os.unlink(temp_file_path)
