package com.example.docgen.processor;

import com.example.docgen.util.PdfUtil;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Component
public final class PdfTemplateProcessor implements TemplateProcessor {
    @Override
    public byte[] process(InputStream templateStream, Map<String, Object> data) {
        return PdfUtil.replacePlaceholders(templateStream, data);
    }

    @Override
    public boolean supports(String templateName) {
        return templateName != null && templateName.toLowerCase().endsWith(".pdf");
    }
}
