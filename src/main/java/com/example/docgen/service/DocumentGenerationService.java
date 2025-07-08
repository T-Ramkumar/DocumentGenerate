package com.example.docgen.service;

import com.example.docgen.exception.TemplateNotFoundException;
import com.example.docgen.model.DocumentRequest;
import com.example.docgen.processor.TemplateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@Service
public class DocumentGenerationService {
    private final List<TemplateProcessor> processors;
    private final ResourceLoader resourceLoader;

    @Autowired
    public DocumentGenerationService(List<TemplateProcessor> processors, ResourceLoader resourceLoader) {
        this.processors = processors;
        this.resourceLoader = resourceLoader;
    }

    public byte[] generateDocument(DocumentRequest request) {
        TemplateProcessor processor = processors.stream()
                .filter(p -> p.supports(request.templateName()))
                .findFirst()
                .orElseThrow(() -> new TemplateNotFoundException("No processor for template: " + request.templateName()));
        try (InputStream templateStream = resourceLoader.getResource("classpath:templates/" + request.templateName()).getInputStream()) {
            byte[] result = processor.process(templateStream, request.data());
            return result;
        } catch (Exception e) {
            throw new TemplateNotFoundException("Template not found: " + request.templateName(), e);
        }
    }
}
