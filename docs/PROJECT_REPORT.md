# Academic B.Tech Project Synopsis & Methodology Report

## Project Title
**Intelligent IT Support & Incident Management System with Machine Learning Integration**

## Problem Statement
Traditional IT service management (ITSM) systems depend on manual ticket categorization, static priority assignment, and manual searching for duplicate issues. This causes high mean-time-to-resolution (MTTR), SLA violations, and repetitive support ticket overhead.

## Objectives
1. Implement a web-based IT Incident Management platform with role-based security (Employee, Support Agent, Admin).
2. Integrate a Natural Language Processing (NLP) Machine Learning microservice for:
   - **Automated Ticket Categorization**
   - **Priority Estimation**
   - **Cosine Similarity Duplicate Ticket Detection**
3. Track complete incident lifecycle states and maintain immutable audit histories.
4. Render enterprise telemetry dashboards with visual metric distributions.

## Machine Learning Methodology

### 1. Text Representation: Term Frequency-Inverse Document Frequency (TF-IDF)
$$TF(t, d) = \frac{count(t, d)}{total\_words(d)}$$

$$IDF(t) = \log\left(\frac{1 + N}{1 + df(t)}\right) + 1$$

$$TF\text{-}IDF(t, d) = TF(t, d) \times IDF(t)$$

### 2. Multi-Class Classification: Logistic Regression & Naive Bayes
Calculates posterior probabilities for each category class $C_k$:
$$P(Y = C_k \mid X) = \frac{e^{w_k^T X}}{\sum_{j} e^{w_j^T X}}$$

### 3. Duplicate Detection Engine: Cosine Similarity
$$\text{Similarity}(q, d) = \frac{\vec{q} \cdot \vec{d}}{\|\vec{q}\| \|\vec{d}\|} = \frac{\sum_{i=1}^{n} q_i d_i}{\sqrt{\sum_{i=1}^{n} q_i^2} \sqrt{\sum_{i=1}^{n} d_i^2}}$$
When $\text{Similarity}(q, d) \ge 0.65$, the system triggers a potential duplicate warning alert displaying the matching ticket number.
