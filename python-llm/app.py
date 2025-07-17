from sentence_transformers import SentenceTransformer
import numpy as np
from fastapi import FastAPI
from pydantic import BaseModel


model = SentenceTransformer('sentence-transformers/LaBSE')

app = FastAPI()

class QuestionCheckRequestDTO(BaseModel):
    correctAnswer: str
    givenAnswer: str

@app.get("/")
def home():
    return {"message": "All Ok!"}


@app.post("/llm/check-answer")
def checkAnswer(item: QuestionCheckRequestDTO):
    texts = []
    texts.append(item.correctAnswer)
    texts.append(item.givenAnswer)
    embeddings = model.encode(texts, convert_to_tensor=True)
    sim = np.inner(embeddings[0], embeddings[1])
    return {"output": str(sim)}