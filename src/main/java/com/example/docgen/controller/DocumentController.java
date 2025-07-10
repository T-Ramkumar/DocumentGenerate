package com.example.docgen.controller;

import java.util.logging.Logger;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.docgen.model.DocumentRequest;
import com.example.docgen.model.DocumentResponse;
import com.example.docgen.service.DocumentGenerationService;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
	Logger logger = Logger.getLogger(DocumentController.class.getName());
    private final DocumentGenerationService documentGenerationService;

    public DocumentController(DocumentGenerationService documentGenerationService) {
        this.documentGenerationService = documentGenerationService;
    }
    @GetMapping(value="/getAvailableFields",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> getAvailableFields(@RequestPart("template") MultipartFile template) {
    	logger.info("Request received to get available fields from template: " + template.getOriginalFilename());
		String availableFields = documentGenerationService.getAvailableFields(template);
		return ResponseEntity.ok(availableFields);
	}
    @PostMapping(value="/fillData",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> fillData(@RequestPart("data") DocumentRequest request, @RequestPart("template") MultipartFile template) {
    	logger.info("Request received to fill data in template: " + template.getOriginalFilename());
    	DocumentResponse documentResponse = documentGenerationService.fillData(request,template);
        

        return ResponseEntity.ok()
                .headers(documentResponse.headers())
                .body(documentResponse.body());
    }
}
