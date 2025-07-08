package com.example.docgen.controller;

import com.example.docgen.model.DocumentRequest;
import com.example.docgen.service.DocumentGenerationService;
import jakarta.validation.Valid;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final DocumentGenerationService documentGenerationService;

    public DocumentController(DocumentGenerationService documentGenerationService) {
        this.documentGenerationService = documentGenerationService;
    }

    //@PostMapping(value="/generate", consumes = "multipart/form-data")
    @PostMapping(value="/generate")
    public ResponseEntity<byte[]> generateDocument(@RequestBody DocumentRequest request) {
    	byte[] responseDoc = documentGenerationService.generateDocument(request);
        //return ResponseEntity.ok(base64);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("filled.pdf").build());
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseDoc);
    }
}
