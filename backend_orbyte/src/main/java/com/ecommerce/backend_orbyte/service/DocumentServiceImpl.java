package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.client.AiApiClient;

import com.ecommerce.backend_orbyte.common.exception.ResourceNotFoundException;
import com.ecommerce.backend_orbyte.dto.request.DocumentRequest;
import com.ecommerce.backend_orbyte.dto.response.DocumentResponse;
import com.ecommerce.backend_orbyte.entity.Connector;
import com.ecommerce.backend_orbyte.entity.Document;
import com.ecommerce.backend_orbyte.repository.ConnectorRepository;
import com.ecommerce.backend_orbyte.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ConnectorRepository connectorRepository;
    private final AiApiClient aiApiClient;


    private final ConcurrentHashMap<UUID, String> jobStatuses = new ConcurrentHashMap<>();

    @Override
    public List<DocumentResponse> findAll() {
        return documentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentResponse findById(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
        return toResponse(document);
    }

    @Override
    public DocumentResponse create(DocumentRequest request) {
        Document document = Document.builder()
                .title(request.getTitle())
                .source(request.getSource())
                .status(request.getStatus() != null ? request.getStatus() : "INDEXED")
                .build();
        if (request.getConnectorId() != null) {
            Connector connector = connectorRepository.findById(request.getConnectorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Connector", "id", request.getConnectorId()));
            document.setConnector(connector);
        }
        return toResponse(documentRepository.save(document));
    }

    @Override
    public DocumentResponse update(UUID id, DocumentRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
        document.setTitle(request.getTitle());
        document.setSource(request.getSource());
        if (request.getStatus() != null) {
            document.setStatus(request.getStatus());
        }
        return toResponse(documentRepository.save(document));
    }

    @Override
    public void delete(UUID id) {
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Document", "id", id);
        }
        documentRepository.deleteById(id);
    }

    private DocumentResponse toResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .source(document.getSource())
                .status(document.getStatus())
                .fileType(document.getFileType())
                .connectorId(document.getConnector() != null ? document.getConnector().getId() : null)
                .createdAt(document.getCreatedAt())
                .build();
    }

    @Override
    public UUID uploadDocument(MultipartFile file, List<String> userRoles, UUID connectorId) {
        UUID jobId = UUID.randomUUID();
        jobStatuses.put(jobId, "PROCESSING");

        byte[] fileBytes;
        String originalFilename = file.getOriginalFilename();
        try {
            fileBytes = file.getBytes();
        } catch (Exception e) {
            log.error("Failed to read uploaded file bytes", e);
            jobStatuses.put(jobId, "FAILED");
            return jobId;
        }

        CompletableFuture.runAsync(() -> {
            Document doc = null;
            try {
                Connector connector = null;
                if (connectorId != null) {
                    connector = connectorRepository.findById(connectorId).orElse(null);
                }

                doc = Document.builder()
                        .title(originalFilename)
                        .source(originalFilename)
                        .status("PROCESSING")
                        .fileType(resolveFileType(originalFilename))
                        .connector(connector)
                        .build();
                doc = documentRepository.save(doc);

                String text = extractText(originalFilename, fileBytes);
                if (text == null || text.trim().isEmpty()) {
                    throw new RuntimeException("Text extraction resulted in empty string.");
                }

                List<String> chunks = chunkText(text, 512, 64);
                log.info("Split document '{}' into {} chunks", originalFilename, chunks.size());

                String clearance = "Public";
                if (userRoles != null && userRoles.contains("ROLE_ADMIN")) {
                    clearance = "Confidential";
                } else if (userRoles != null && userRoles.contains("ROLE_USER")) {
                    clearance = "Internal";
                }

                AiApiClient.IngestRequest ingestRequest = AiApiClient.IngestRequest.builder()
                        .doc_id(doc.getId().toString())
                        .source_label(originalFilename)
                        .clearance(clearance)
                        .chunks(chunks)
                        .build();

                aiApiClient.ingestChunks(ingestRequest);

                doc.setStatus("INDEXED");
                documentRepository.save(doc);
                jobStatuses.put(jobId, "COMPLETED");
                log.info("Document '{}' successfully indexed under jobId {}", originalFilename, jobId);

            } catch (Exception e) {
                log.error("Failed ingestion pipeline for document '{}'", originalFilename, e);
                jobStatuses.put(jobId, "FAILED");
                if (doc != null) {
                    doc.setStatus("FAILED");
                    documentRepository.save(doc);
                }
            }
        });

        return jobId;
    }

    @Override
    public String getJobStatus(UUID jobId) {
        return jobStatuses.getOrDefault(jobId, "NOT_FOUND");
    }

    private String resolveFileType(String filename) {
        if (filename == null) return "TXT";
        int dot = filename.lastIndexOf('.');
        if (dot >= 0 && dot < filename.length() - 1) {
            return filename.substring(dot + 1).toUpperCase();
        }
        return "FILE";
    }

    private String extractText(String filename, byte[] bytes) throws Exception {
        if (filename == null) {
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        }
        if (filename.toLowerCase().endsWith(".pdf")) {
            try (org.apache.pdfbox.pdmodel.PDDocument pdDoc = org.apache.pdfbox.Loader.loadPDF(bytes)) {
                org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
                return stripper.getText(pdDoc);
            }
        } else if (filename.toLowerCase().endsWith(".docx")) {
            try (java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(bytes);
                    org.apache.poi.xwpf.usermodel.XWPFDocument doc = new org.apache.poi.xwpf.usermodel.XWPFDocument(
                            bais);
                    org.apache.poi.xwpf.extractor.XWPFWordExtractor extractor = new org.apache.poi.xwpf.extractor.XWPFWordExtractor(
                            doc)) {
                return extractor.getText();
            }
        } else {
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    private List<String> chunkText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");
        if (words.length <= chunkSize) {
            chunks.add(text);
            return chunks;
        }

        int i = 0;
        while (i < words.length) {
            int end = Math.min(i + chunkSize, words.length);
            StringBuilder chunk = new StringBuilder();
            for (int k = i; k < end; k++) {
                chunk.append(words[k]).append(" ");
            }
            chunks.add(chunk.toString().trim());
            if (end == words.length) {
                break;
            }
            i += (chunkSize - overlap);
        }
        return chunks;
    }
}
