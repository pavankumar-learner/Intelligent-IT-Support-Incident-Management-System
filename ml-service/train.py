import os
import pandas as pd
import numpy as np
import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import Pipeline
from sklearn.metrics import classification_report, accuracy_score

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DATA_PATH = os.path.join(BASE_DIR, "data", "tickets_dataset.csv")
MODELS_DIR = os.path.join(BASE_DIR, "models")

os.makedirs(MODELS_DIR, exist_ok=True)

def train():
    print(f"Loading dataset from {DATA_PATH}...")
    df = pd.read_csv(DATA_PATH)
    print(f"Total dataset size: {len(df)} records")

    # Clean text data
    df["text_clean"] = df["text"].astype(str).str.lower().str.strip()

    # 1. Train Category Prediction Model
    print("\n--- Training Category Prediction Model ---")
    tfidf_category = TfidfVectorizer(ngram_range=(1, 2), stop_words='english', min_df=1)
    X_cat_tfidf = tfidf_category.fit_transform(df["text_clean"])
    
    category_model = LogisticRegression(C=1.0, max_iter=1000, random_state=42)
    category_model.fit(X_cat_tfidf, df["category"])
    
    y_cat_pred = category_model.predict(X_cat_tfidf)
    cat_acc = accuracy_score(df["category"], y_cat_pred)
    print(f"Category Classifier Training Accuracy: {cat_acc * 100:.2f}%")

    # 2. Train Priority Prediction Model
    print("\n--- Training Priority Prediction Model ---")
    tfidf_priority = TfidfVectorizer(ngram_range=(1, 2), stop_words='english', min_df=1)
    X_pri_tfidf = tfidf_priority.fit_transform(df["text_clean"])
    
    priority_model = LogisticRegression(C=1.0, max_iter=1000, random_state=42)
    priority_model.fit(X_pri_tfidf, df["priority"])
    
    y_pri_pred = priority_model.predict(X_pri_tfidf)
    pri_acc = accuracy_score(df["priority"], y_pri_pred)
    print(f"Priority Classifier Training Accuracy: {pri_acc * 100:.2f}%")

    # 3. Fit Duplicate Detection Index
    print("\n--- Building Duplicate Detection Index ---")
    tfidf_duplicate = TfidfVectorizer(ngram_range=(1, 2), stop_words='english', min_df=1)
    dup_vectors = tfidf_duplicate.fit_transform(df["text_clean"])

    # Save artifacts
    print("\nSaving ML artifacts to models/ directory...")
    joblib.dump(tfidf_category, os.path.join(MODELS_DIR, "tfidf_category.pkl"))
    joblib.dump(category_model, os.path.join(MODELS_DIR, "category_model.pkl"))
    
    joblib.dump(tfidf_priority, os.path.join(MODELS_DIR, "tfidf_priority.pkl"))
    joblib.dump(priority_model, os.path.join(MODELS_DIR, "priority_model.pkl"))
    
    joblib.dump(tfidf_duplicate, os.path.join(MODELS_DIR, "tfidf_duplicate.pkl"))
    joblib.dump(dup_vectors, os.path.join(MODELS_DIR, "duplicate_vectors.pkl"))
    joblib.dump(df[["text", "category", "priority"]], os.path.join(MODELS_DIR, "dataset_reference.pkl"))

    print("Model training complete successfully!")

if __name__ == "__main__":
    train()
