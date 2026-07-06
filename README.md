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

## 1️⃣ Backend — Spring Boot (`backend_orbyte`)

### Configuration

Le backend utilise une base **H2 en mémoire** par défaut (aucune installation requise).

> Pour la production, décommentez la section PostgreSQL dans `application.properties` et définissez les variables d'environnement.

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

# Avec le wrapper Maven fourni (recommandé)
./mvnw spring-boot:run

# Ou sous Windows
mvnw.cmd spring-boot:run
```

L'API sera disponible sur **http://localhost:8081**

- Swagger UI : http://localhost:8081/swagger-ui.html
- API Docs : http://localhost:8081/v3/api-docs

---

## 2️⃣ Service IA — FastAPI + RAG (`Obyte-ai-main`)

### Configuration

```bash
cd Obyte-ai-main

# Copier le fichier d'exemple et le remplir
cp .env.example .env
```

**Éditer `.env` :**

```env
# Choisir le mode LLM : openrouter | local_api | hf_inference | mock
LLM_API_TYPE=openrouter

# Clé OpenRouter (https://openrouter.ai)
OPENROUTER_API_KEY=sk-or-v1-xxxxxxxxxxxxxxxxxxxxxxxx

# OU pour un LLM local (Ollama)
# LLM_API_TYPE=local_api
# LOCAL_LLM_API_URL=http://localhost:11434/v1
# LOCAL_LLM_MODEL=llama3.2:1b
```

### Installation & Lancement

```bash
cd Obyte-ai-main

# Créer un environnement virtuel
python -m venv .venv

# Activer l'environnement
# Windows :
.venv\Scripts\activate
# Linux/Mac :
source .venv/bin/activate

# Installer les dépendances
pip install -r requirements.txt

# Lancer le serveur FastAPI
uvicorn api.main:app --host 0.0.0.0 --port 8000 --reload
```

Le service IA sera disponible sur **http://localhost:8000**

- Swagger UI : http://localhost:8000/docs
- Health check : http://localhost:8000/api/v1/health

---

## 3️⃣ Frontend — React/Vite (`frontend`)

### Configuration

Le frontend pointe par défaut vers le backend sur `http://localhost:8081`.
Vérifiez `frontend/src/api.js` si vous modifiez le port du backend.

### Installation & Lancement

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

Pour que tout fonctionne correctement, démarrez les services dans cet ordre :

```
1. Obyte-ai-main  (Port 8000)  — Le backend en dépend
2. backend_orbyte (Port 8081)  — Le frontend en dépend
3. frontend       (Port 5173)  — Dernière étape
```

### Démarrage rapide (3 terminaux)

**Terminal 1 — IA :**
```bash
cd Obyte-ai-main && .venv\Scripts\activate && uvicorn api.main:app --port 8000 --reload
```

**Terminal 2 — Backend :**
```bash
cd backend_orbyte && mvnw.cmd spring-boot:run
```

**Terminal 3 — Frontend :**
```bash
cd frontend && npm run dev
```

---

## 🔑 Compte admin par défaut

Une fois le backend démarré, un compte administrateur est automatiquement créé :

| Champ | Valeur |
|-------|--------|
| Email | `admin@orbyte.ai` |
| Mot de passe | `admin123` |

> ⚠️ Changez ces valeurs via les variables d'environnement en production.

---

## 🛑 Variables d'environnement à ne jamais commiter

- `.env` (Obyte-ai-main) — Contient la clé API OpenRouter
- `application.properties` avec des secrets de production

Le `.gitignore` racine exclut automatiquement ces fichiers.

---

## 📦 Architecture des ports

| Service | Port | URL |
|---------|------|-----|
| FastAPI (IA/RAG) | 8000 | http://localhost:8000 |
| Spring Boot (API) | 8081 | http://localhost:8081 |
| React/Vite (UI) | 5173 | http://localhost:5173 |
