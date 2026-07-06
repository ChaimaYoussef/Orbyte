package com.ecommerce.backend_orbyte.dto.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ChatMessageResponse {
    private UUID id;
    private String role;
    private String content;
    private LocalDateTime timestamp;
    private String thinking;
    private String thinkingUrl;
    private List<SourceDTO> sources;

    /**
     * Parses JSON string to list of SourceDTO. Falls back to empty list on error.
     */
    public void setSourcesFromJson(String sourcesJson) {
        if (sourcesJson == null || sourcesJson.isBlank()) {
            this.sources = new ArrayList<>();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.sources = mapper.readValue(sourcesJson, new TypeReference<List<SourceDTO>>() {});
        } catch (Exception e) {
            this.sources = new ArrayList<>();
        }
    }

    @Data
    @Builder
    public static class SourceDTO {
        private String label;
        private String url;
        private String snippet;
    }
}
