import sys, os
sys.path.append(os.path.abspath('.'))
if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

import chromadb
from chromadb.utils import embedding_functions
from rag.config import DATABASE_DIR, COLLECTION_NAME, EMBEDDING_MODEL_NAME

print("=== ChromaDB Diagnostic ===")
print(f"Database dir: {DATABASE_DIR}")
print(f"Collection: {COLLECTION_NAME}")

client = chromadb.PersistentClient(path=str(DATABASE_DIR))
collections = client.list_collections()
print(f"Available collections: {[c.name for c in collections]}")

emb_fn = embedding_functions.SentenceTransformerEmbeddingFunction(model_name=EMBEDDING_MODEL_NAME)
col = client.get_collection(name=COLLECTION_NAME, embedding_function=emb_fn)
count = col.count()
print(f"\nTotal chunks: {count}")

if count == 0:
    print("\n!!! DATABASE IS EMPTY - no documents indexed !!!")
    sys.exit(1)

# Show sample metadata to understand clearance distribution
sample_all = col.get(limit=10, include=["metadatas", "documents"])
print("\n--- Sample of 10 chunks (metadata) ---")
for i, (cid, meta) in enumerate(zip(sample_all['ids'], sample_all['metadatas'])):
    print(f"  [{i}] id={cid} | clearance={meta.get('clearance')} | source={meta.get('source','?')}")

# Count clearance distribution
all_meta = col.get(include=["metadatas"])
clearance_counts = {}
for m in all_meta['metadatas']:
    cl = m.get('clearance', 'unknown')
    clearance_counts[cl] = clearance_counts.get(cl, 0) + 1
print(f"\n--- Clearance distribution ---")
for k, v in clearance_counts.items():
    print(f"  {k}: {v} chunks")

# Test query in French
print("\n--- Query: 'litiges contractuels' ---")
res1 = col.query(query_texts=['litiges contractuels'], n_results=5)
for i in range(len(res1['ids'][0])):
    dist = res1['distances'][0][i]
    sim = max(0, 1 - dist)
    meta = res1['metadatas'][0][i]
    doc = res1['documents'][0][i][:200]
    print(f"  [{i+1}] sim={sim:.4f} | clearance={meta.get('clearance')} | text={doc}...")

# Test query in English
print("\n--- Query: 'contractual disputes litigation' ---")
res2 = col.query(query_texts=['contractual disputes litigation essential elements'], n_results=5)
for i in range(len(res2['ids'][0])):
    dist = res2['distances'][0][i]
    sim = max(0, 1 - dist)
    meta = res2['metadatas'][0][i]
    doc = res2['documents'][0][i][:200]
    print(f"  [{i+1}] sim={sim:.4f} | clearance={meta.get('clearance')} | text={doc}...")

# Test query in English (general)
print("\n--- Query: 'What is compliance policy?' ---")
res3 = col.query(query_texts=['What is compliance policy?'], n_results=3)
for i in range(len(res3['ids'][0])):
    dist = res3['distances'][0][i]
    sim = max(0, 1 - dist)
    meta = res3['metadatas'][0][i]
    doc = res3['documents'][0][i][:200]
    print(f"  [{i+1}] sim={sim:.4f} | clearance={meta.get('clearance')} | text={doc}...")

print("\n=== Done ===")
