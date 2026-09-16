import os
import re
import numpy as np
import joblib
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import TfidfVectorizer

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODELS_DIR = os.path.join(BASE_DIR, "models")

app = FastAPI(
    title="Intelligent IT Support ML Service",
    description="NLP microservice for IT ticket category prediction, priority estimation, and duplicate detection.",
    version="1.0.0"
)

# Enable CORS for Spring Boot / Frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Global variables for models
tfidf_category = None
category_model = None
tfidf_priority = None
priority_model = None
tfidf_duplicate = None
dataset_ref = None

def load_models():
    global tfidf_category, category_model, tfidf_priority, priority_model, tfidf_duplicate, dataset_ref
    try:
        tfidf_category = joblib.load(os.path.join(MODELS_DIR, "tfidf_category.pkl"))
        category_model = joblib.load(os.path.join(MODELS_DIR, "category_model.pkl"))
        tfidf_priority = joblib.load(os.path.join(MODELS_DIR, "tfidf_priority.pkl"))
        priority_model = joblib.load(os.path.join(MODELS_DIR, "priority_model.pkl"))
        tfidf_duplicate = joblib.load(os.path.join(MODELS_DIR, "tfidf_duplicate.pkl"))
        dataset_ref = joblib.load(os.path.join(MODELS_DIR, "dataset_reference.pkl"))
        print("ML Models loaded successfully!")
    except Exception as e:
        print(f"Warning: Could not load models ({e}). Make sure to run train.py first.")

@app.on_event("startup")
def startup_event():
    load_models()

# Pydantic Schemas
class TicketPredictRequest(BaseModel):
    title: str
    description: Optional[str] = ""
    category: Optional[str] = None

class TicketPredictResponse(BaseModel):
    prediction: str
    confidence: float
    top_keywords: List[str]

class CandidateTicket(BaseModel):
    ticketNumber: str
    title: str
    description: Optional[str] = ""

class DuplicateCheckRequest(BaseModel):
    title: str
    description: Optional[str] = ""
    existingTickets: Optional[List[CandidateTicket]] = []
    threshold: Optional[float] = 0.65

class DuplicateCheckResponse(BaseModel):
    isDuplicate: bool
    similarityScore: float
    matchedTicketNumber: Optional[str] = None
    matchedTitle: Optional[str] = None
    message: str

def extract_keywords(text: str, vectorizer: TfidfVectorizer, top_n=3) -> List[str]:
    if not vectorizer:
        return []
    clean_text = text.lower()
    feature_names = np.array(vectorizer.get_feature_names_out())
    try:
        response = vectorizer.transform([clean_text])
        sorted_nz = np.argsort(response.data)[::-1]
        top_indices = response.indices[sorted_nz[:top_n]]
        return list(feature_names[top_indices])
    except Exception:
        # Fallback simple split
        words = [w for w in re.findall(r'\b[a-zA-Z]{3,}\b', clean_text) if w not in ['the','and','for','not','with','this']]
        return list(set(words[:top_n]))

# Fallback heuristic rules if model is not loaded yet
def fallback_category(text: str) -> str:
    t = text.lower()
    if any(w in t for w in ["wifi", "network", "internet", "vpn", "router", "ethernet", "ip", "dns", "ping"]):
        return "Network"
    if any(w in t for w in ["printer", "toner", "paper", "spooler", "print", "scanner"]):
        return "Printer"
    if any(w in t for w in ["login", "password", "2fa", "access", "account", "permission", "locked", "sso"]):
        return "Access/Login"
    if any(w in t for w in ["email", "outlook", "mailbox", "inbox", "spam", "attachment"]):
        return "Email"
    if any(w in t for w in ["virus", "malware", "phishing", "firewall", "security", "threat", "certificate", "ssl"]):
        return "Security"
    if any(w in t for w in ["database", "sql", "mysql", "postgres", "query", "mongodb", "table", "deadlock"]):
        return "Database"
    if any(w in t for w in ["laptop", "monitor", "keyboard", "mouse", "screen", "power", "hardware", "battery", "disk"]):
        return "Hardware"
    if any(w in t for w in ["app", "software", "install", "crash", "bug", "bsod", "blue screen", "licence", "code"]):
        return "Software"
    return "Other"

def fallback_priority(text: str) -> str:
    t = text.lower()
    if any(w in t for w in ["entire", "down", "outage", "crashing", "critical", "security breach", "ransomware", "virus", "deadlock", "production"]):
        return "CRITICAL"
    if any(w in t for w in ["unable", "broken", "failing", "failed", "cannot", "high", "error 500", "bsod", "locked out"]):
        return "HIGH"
    if any(w in t for w in ["slow", "lag", "issue", "problem", "warning", "license"]):
        return "MEDIUM"
    return "LOW"

@app.get("/health")
def health_check():
    return {
        "status": "UP",
        "service": "Intelligent IT Support ML Microservice",
        "models_loaded": category_model is not None and priority_model is not None
    }

@app.post("/predict-category", response_model=TicketPredictResponse)
def predict_category(req: TicketPredictRequest):
    combined_text = f"{req.title} {req.description or ''}".strip()
    if not combined_text:
        raise HTTPException(status_code=400, detail="Title and description cannot be empty")
    
    clean_text = combined_text.lower()

    if tfidf_category and category_model:
        vec = tfidf_category.transform([clean_text])
        probs = category_model.predict_proba(vec)[0]
        max_idx = np.argmax(probs)
        pred_cat = category_model.classes_[max_idx]
        conf = round(float(probs[max_idx]), 2)
        keywords = extract_keywords(clean_text, tfidf_category)
    else:
        pred_cat = fallback_category(clean_text)
        conf = 0.85
        keywords = extract_keywords(clean_text, None)

    return TicketPredictResponse(
        prediction=pred_cat,
        confidence=conf,
        top_keywords=keywords
    )

@app.post("/predict-priority", response_model=TicketPredictResponse)
def predict_priority(req: TicketPredictRequest):
    combined_text = f"{req.title} {req.description or ''} {req.category or ''}".strip()
    if not combined_text:
        raise HTTPException(status_code=400, detail="Text cannot be empty")
    
    clean_text = combined_text.lower()

    if tfidf_priority and priority_model:
        vec = tfidf_priority.transform([clean_text])
        probs = priority_model.predict_proba(vec)[0]
        max_idx = np.argmax(probs)
        pred_pri = priority_model.classes_[max_idx]
        conf = round(float(probs[max_idx]), 2)
        keywords = extract_keywords(clean_text, tfidf_priority)
    else:
        pred_pri = fallback_priority(clean_text)
        conf = 0.80
        keywords = extract_keywords(clean_text, None)

    return TicketPredictResponse(
        prediction=pred_pri,
        confidence=conf,
        top_keywords=keywords
    )

@app.post("/check-duplicate", response_model=DuplicateCheckResponse)
def check_duplicate(req: DuplicateCheckRequest):
    query_text = f"{req.title} {req.description or ''}".strip().lower()
    if not query_text:
        return DuplicateCheckResponse(
            isDuplicate=False,
            similarityScore=0.0,
            message="Empty query text."
        )

    # 1. Check against provided existing tickets from DB
    if req.existingTickets and len(req.existingTickets) > 0:
        titles = [f"{t.title} {t.description or ''}".strip().lower() for t in req.existingTickets]
        vectorizer = TfidfVectorizer(ngram_range=(1, 2), stop_words='english')
        try:
            tfidf_matrix = vectorizer.fit_transform(titles + [query_text])
            query_vec = tfidf_matrix[-1]
            candidate_vecs = tfidf_matrix[:-1]

            sims = cosine_similarity(query_vec, candidate_vecs)[0]
            max_idx = int(np.argmax(sims))
            max_score = float(sims[max_idx])

            threshold = req.threshold or 0.65
            is_dup = max_score >= threshold
            matched_ticket = req.existingTickets[max_idx]

            return DuplicateCheckResponse(
                isDuplicate=is_dup,
                similarityScore=round(max_score * 100, 1),
                matchedTicketNumber=matched_ticket.ticketNumber,
                matchedTitle=matched_ticket.title,
                message=f"Potential duplicate incident detected ({round(max_score * 100, 1)}% similarity with {matched_ticket.ticketNumber})." if is_dup else "No duplicate incidents detected."
            )
        except Exception as e:
            print(f"Error computing live cosine similarity: {e}")

    # 2. Fallback check against trained reference dataset
    if dataset_ref is not None and not dataset_ref.empty:
        ref_texts = dataset_ref["text"].str.lower().tolist()
        vectorizer = TfidfVectorizer(ngram_range=(1, 2), stop_words='english')
        try:
            tfidf_matrix = vectorizer.fit_transform(ref_texts + [query_text])
            sims = cosine_similarity(tfidf_matrix[-1], tfidf_matrix[:-1])[0]
            max_idx = int(np.argmax(sims))
            max_score = float(sims[max_idx])
            
            is_dup = max_score >= (req.threshold or 0.65)
            matched_row = dataset_ref.iloc[max_idx]

            return DuplicateCheckResponse(
                isDuplicate=is_dup,
                similarityScore=round(max_score * 100, 1),
                matchedTicketNumber=f"INC-REF-{1000 + max_idx}",
                matchedTitle=matched_row["text"],
                message=f"Potential duplicate incident detected ({round(max_score * 100, 1)}% similarity with historical ticket)." if is_dup else "No duplicate incidents detected."
            )
        except Exception as e:
            print(f"Error checking dataset reference similarity: {e}")

    return DuplicateCheckResponse(
        isDuplicate=False,
        similarityScore=0.0,
        message="No duplicate incidents detected."
    )
