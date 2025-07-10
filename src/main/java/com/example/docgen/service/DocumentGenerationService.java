package com.example.docgen.service;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.docgen.exception.TemplateNotFoundException;
import com.example.docgen.model.DocumentRequest;
import com.example.docgen.model.DocumentResponse;
import com.example.docgen.processor.TemplateProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
public class DocumentGenerationService {
    private final List<TemplateProcessor> processors;
    private final ResourceLoader resourceLoader;

    @Autowired
    public DocumentGenerationService(List<TemplateProcessor> processors, ResourceLoader resourceLoader) {
        this.processors = processors;
        this.resourceLoader = resourceLoader;
    }

    public DocumentResponse fillData(DocumentRequest request,MultipartFile file) {
        TemplateProcessor processor = processors.stream()
                .filter(p -> p.supports(request.templateName()))
                .findFirst()
                .orElseThrow(() -> new TemplateNotFoundException("No processor for template: " + request.templateName()));
        try (InputStream templateStream = file.getInputStream()) {
        	byte[] responseDoc = processor.process(templateStream, request.data());
        	HttpHeaders headers = processor.getHeaders(request.templateName());
        	DocumentResponse documentResponse = new DocumentResponse(headers, responseDoc);
			return documentResponse;
        } catch (Exception e) {
            throw new TemplateNotFoundException("Template not found: " + request.templateName(), e);
        }
    }

	public String getAvailableFields(MultipartFile file) {
		String result=null;
		try (InputStream templateStream = file.getInputStream();PDDocument document = PDDocument.load(templateStream)) {
	        	  PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
	        	  if(acroForm != null) {
	        		  Map<String, String> fieldMap = new LinkedHashMap<>();

	                  for (PDField field : acroForm.getFieldTree()) {
	                      fieldMap.put(field.getFullyQualifiedName(), field.getValueAsString());
	                  }
	                  ObjectMapper mapper = new ObjectMapper();
	                  mapper.enable(SerializationFeature.INDENT_OUTPUT);
	                  result = mapper.writeValueAsString(fieldMap);

	                  System.out.println("📋 AcroForm fields as JSON:\n" + result);
	        	  }
            return result;
        } catch (Exception e) {
            return e.getMessage();
        }
	}
}
