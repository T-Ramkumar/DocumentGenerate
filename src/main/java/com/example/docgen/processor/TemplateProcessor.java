package com.example.docgen.processor;

import java.io.InputStream;
import java.util.Map;

import org.springframework.http.HttpHeaders;

public sealed interface TemplateProcessor permits PdfTemplateProcessor, DocxTemplateProcessor {
    byte[] process(InputStream templateStream, Map<String, Object> data);
    boolean supports(String templateName);
    HttpHeaders getHeaders(String templateName);
}
