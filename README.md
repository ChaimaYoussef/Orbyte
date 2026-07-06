# 🚀 Orbyte — Monorepo

Orbyte est une plateforme RAG multi-agents souveraine avec un backend Spring Boot, un frontend React/Vite et un service d'IA Python (FastAPI + ChromaDB).

```
projet/
├── backend_orbyte/     # API REST Spring Boot (Java 17) — Port 8081
├── frontend/           # Application React/Vite                — Port 5173
└── Obyte-ai-main/      # Service RAG FastAPI (Python)          — Port 8000
```

---

## ✅ Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Java JDK | 17+ |
| Maven (ou mvnw) | 3.9+ |
| Node.js | 18+ |
| npm | 9+ |
| Python | 3.10+ |

---

## 1️⃣ Service IA — FastAPI + RAG (`Obyte-ai-main`)

> ⚠️ À lancer **en premier**, le backend Spring Boot en dépend.

### Étape 1 — Créer l'environnement Python

```bash
cd Obyte-ai-main

# Créer l'environnement virtuel
python -m venv .venv

# Activer l'environnement
# Windows :
.venv\Scripts\activate
# Linux / Mac :
source .venv/bin/activate
```

### Étape 2 — Installer les dépendances (dont ChromaDB)

```bash
pip install -r requirements.txt
```

> Cela installe automatiquement **ChromaDB**, sentence-transformers, FastAPI, Uvicorn, et toutes les autres dépendances.
> ChromaDB fonctionne **en local** (base de données persistante dans le dossier `database/`) — aucun serveur externe n'est requis.

### Étape 3 — Configurer les variables d'environnement

```bash
# Copier le fichier d'exemple
cp .env.example .env
```

**Éditer `.env` :**

```env
# Choisir le mode LLM : openrouter | local_api | hf_inference | mock
LLM_API_TYPE=openrouter

# Clé OpenRouter (https://openrouter.ai) — requise si LLM_API_TYPE=openrouter
OPENROUTER_API_KEY=sk-or-v1-xxxxxxxxxxxxxxxxxxxxxxxx

# OU utiliser un LLM local via Ollama :
# LLM_API_TYPE=local_api
# LOCAL_LLM_API_URL=http://localhost:11434/v1
# LOCAL_LLM_MODEL=llama3.2:1b
```

### Étape 4 — Initialiser la base de données ChromaDB (ingestion des documents)

> **À faire une seule fois** avant de lancer le service pour la première fois.
> Cette étape télécharge le dataset de documents légaux depuis HuggingFace et les encode dans ChromaDB.

```bash
# Depuis le dossier Obyte-ai-main (avec l'environnement activé)
python -m rag.embed_documents
```

**Options disponibles :**

```bash
# Limiter le nombre de documents (rapide, pour tester)
python -m rag.embed_documents --limit 50

# Contrôler la taille des batchs d'insertion
python -m rag.embed_documents --limit 200 --batch-size 100
```

> ⏳ La première exécution peut prendre plusieurs minutes car elle télécharge le modèle d'embedding `all-MiniLM-L6-v2` (~90 MB) et le dataset HuggingFace.

**Vérifier que la base est bien remplie :**

```bash
python check_db.py
```

Vous devez voir :
```
Total chunks: [nombre > 0]
--- Clearance distribution ---
  Public: X chunks
  Internal: X chunks
  Confidential: X chunks
```

> 💡 Si le dataset HuggingFace est privé (gated), vous devez vous connecter :
> ```bash
> huggingface-cli login
> # Ou définir la variable : HF_TOKEN=votre_token
> ```

### Étape 5 — Lancer le serveur FastAPI

```bash
uvicorn api.main:app --host 0.0.0.0 --port 8000 --reload
```

Le service IA sera disponible sur **http://localhost:8000**

| Endpoint | URL |
|----------|-----|
| Swagger UI | http://localhost:8000/docs |
| Health check | http://localhost:8000/api/v1/health |
| ReDoc | http://localhost:8000/redoc |

---

## 2️⃣ Backend — Spring Boot (`backend_orbyte`)

> ⚠️ À lancer **après** le service IA.

### Configuration

Le backend utilise une base **H2 en mémoire** par défaut (aucune installation requise).

**Variables d'environnement (optionnelles en dev) :**

| Variable | Valeur par défaut |
|----------|-------------------|
| `JWT_SECRET` | `OrbyteJwtSecretKeyForLocalDevSigningKeyAndTesting` |
| `ENCRYPTION_SECRET` | `OrbyteEncryptionKey32BytesPadded!` |
| `ADMIN_EMAIL` | `admin@orbyte.ai` |
| `ADMIN_PASSWORD` | `admin123` |
| `FASTAPI_URL` | `http://localhost:8000` |
| `CORS_ORIGINS` | `http://localhost:3000,http://localhost:5173` |

### Lancement

```bash
cd backend_orbyte

# Windows
mvnw.cmd spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

L'API sera disponible sur **http://localhost:8081**

| Endpoint | URL |
|----------|-----|
| Swagger UI | http://localhost:8081/swagger-ui.html |
| API Docs | http://localhost:8081/v3/api-docs |

---

## 3️⃣ Frontend — React/Vite (`frontend`)

> ⚠️ À lancer **en dernier**.

```bash
cd frontend

# Installer les dépendances
npm install

# Lancer en mode développement
npm run dev
```

L'application sera disponible sur **http://localhost:5173**

---

## 🔄 Ordre de démarrage recommandé

```
1. Obyte-ai-main  (Port 8000)  ← uvicorn api.main:app --port 8000 --reload
2. backend_orbyte (Port 8081)  ← mvnw.cmd spring-boot:run
3. frontend       (Port 5173)  ← npm run dev
```

---

## 🔑 Compte admin par défaut

| Champ | Valeur |
|-------|--------|
| Email | `admin@orbyte.ai` |
| Mot de passe | `admin123` |

> ⚠️ Changez ces valeurs via les variables d'environnement en production.

---

## 📦 Tableau récapitulatif des ports

| Service | Port | URL |
|---------|------|-----|
| FastAPI (IA/RAG) | 8000 | http://localhost:8000 |
| Spring Boot (API) | 8081 | http://localhost:8081 |
| React/Vite (UI) | 5173 | http://localhost:5173 |

---

## 🗂️ Structure de ChromaDB

ChromaDB stocke les données **localement** dans `Obyte-ai-main/database/`.
Aucun serveur externe n'est nécessaire. La base est persistante entre les redémarrages.

```
Obyte-ai-main/
├── database/           ← Base de données ChromaDB (générée par embed_documents.py)
├── data/               ← Fichiers Parquet du dataset (optionnel, si téléchargé localement)
└── rag/
    └── embed_documents.py  ← Script d'ingestion des documents
```
