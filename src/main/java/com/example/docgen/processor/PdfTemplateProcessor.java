package com.example.docgen.processor;

import java.io.InputStream;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.example.docgen.util.PdfUtil;

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

	@Override
	public HttpHeaders getHeaders(String templateName) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(templateName).build());
        headers.setContentType(MediaType.APPLICATION_PDF);
		return headers;
	}
}
