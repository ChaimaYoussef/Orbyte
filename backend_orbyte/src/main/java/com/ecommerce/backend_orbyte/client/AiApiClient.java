package com.ecommerce.backend_orbyte.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiApiClient {

    private final RestTemplate restTemplate;

    @Value("${fastapi.url:http://localhost:8000}")
    private String fastapiUrl;

    public List<List<Double>> embedText(List<String> texts) {
        String url = fastapiUrl + "/api/v1/embeddings";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of("texts", texts), headers);
        try {
            EmbedResponse response = restTemplate.postForObject(url, entity, EmbedResponse.class);
            if (response != null && response.getEmbeddings() != null) {
                return response.getEmbeddings();
            }
        } catch (Exception e) {
            log.error("Failed to fetch embeddings from FastAPI, using mock embeddings: {}", e.getMessage());
        }

        // Fallback: return mock embeddings (vector size 384 for sentence-transformers)
        List<List<Double>> mockEmbeddings = new ArrayList<>();
        for (String ignored : texts) {
            List<Double> vector = new ArrayList<>();
            for (int i = 0; i < 384; i++) {
                vector.add(0.0);
            }
            mockEmbeddings.add(vector);
        }
        return mockEmbeddings;
    }

    public QueryResponse query(QueryRequest request) {
        String url = fastapiUrl + "/api/v1/query";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QueryRequest> entity = new HttpEntity<>(request, headers);
        try {
            QueryResponse response = restTemplate.postForObject(url, entity, QueryResponse.class);
            if (response != null) {
                return response;
            }
            throw new RuntimeException("Service IA: Réponse vide");
        } catch (Exception e) {
            log.error("Failed to query FastAPI IA service: {}", e.getMessage());
            throw new RuntimeException("Service IA indisponible ou erreur: " + e.getMessage(), e);
        }
    }

    public void ingestChunks(IngestRequest request) {
        String url = fastapiUrl + "/api/v1/documents";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<IngestRequest> entity = new HttpEntity<>(request, headers);
        try {
            restTemplate.postForObject(url, entity, Void.class);
            log.info("Successfully ingested document chunks via FastAPI");
        } catch (Exception e) {
            log.error("Failed to ingest document chunks to FastAPI: {}", e.getMessage());
            throw new RuntimeException("Error ingesting documents to Vector DB", e);
        }
    }

    public boolean isHealthy() {
        String url = fastapiUrl + "/api/v1/health";
        try {
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            return response != null && "healthy".equalsIgnoreCase(String.valueOf(response.get("status")));
        } catch (Exception e) {
            return false;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmbedResponse {
        private List<List<Double>> embeddings;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QueryRequest {
        private String username;
        private String query;
        private Integer top_k;
        private String clearance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QueryResponse {
        private String query;
        private String username;
        private String clearance;
        private String response;
        private Boolean approved;
        private List<ChunkResult> chunks_used;
        private Integer chunks_filtered;
        private AuditResult evaluation;
        private Double processing_time;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChunkResult {
        private String chunk_id;
        private Double similarity;
        private String clearance;
        private String snippet;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuditResult {
        private Integer faithfulness_score;
        private Integer completeness_score;
        private Boolean approved;
        private String feedback;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IngestRequest {
        private String doc_id;
        private String source_label;
        private String clearance;
        private List<String> chunks;
    }
}
