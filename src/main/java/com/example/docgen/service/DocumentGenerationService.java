package com.example.docgen.service;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.docgen.exception.TemplateNotFoundException;
import com.example.docgen.model.DocumentRequest;
import com.example.docgen.model.DocumentResponse;
import com.example.docgen.processor.TemplateProcessor;

@Service
public class DocumentGenerationService {
	public static final Logger logger = Logger.getLogger(DocumentGenerationService.class.getName());
	private final List<TemplateProcessor> processors;

	@Autowired
	public DocumentGenerationService(List<TemplateProcessor> processors) {
		this.processors = processors;
	}

	public DocumentResponse fillData(DocumentRequest request, MultipartFile file) {
		TemplateProcessor processor = processors.stream().filter(p -> p.supports(request.templateName())).findFirst()
				.orElseThrow(
						() -> new TemplateNotFoundException("No processor for template: " + request.templateName()));
		try (InputStream templateStream = file.getInputStream()) {
			byte[] responseDoc = processor.process(templateStream, request.data());
			HttpHeaders headers = processor.getHeaders(request.templateName());
			return new DocumentResponse(headers, responseDoc);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getAvailableFields(DocumentRequest request, MultipartFile file) {
		String result = null;
		TemplateProcessor processor = processors.stream().filter(p -> p.supports(request.templateName())).findFirst()
				.orElseThrow(
						() -> new TemplateNotFoundException("No processor for template: " + request.templateName()));
		try (InputStream templateStream = file.getInputStream()) {
			result = processor.getAvailableFields(file);
			return result;
		} catch (Exception e) {
			return "Error retrieving available fields: " + e.getMessage();
		}
	}
}
