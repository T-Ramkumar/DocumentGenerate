package com.example.docgen.processor;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.docgen.util.PdfUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public final class PdfTemplateProcessor implements TemplateProcessor {
	private static final Logger logger = Logger.getLogger(PdfTemplateProcessor.class.getName());

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

	@Override
	public String getAvailableFields(MultipartFile file) {
		String result = null;
		try (InputStream templateStream = file.getInputStream();
				PDDocument document = PDDocument.load(templateStream)) {
			PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
			if (acroForm != null) {
				Map<String, String> fieldMap = new LinkedHashMap<>();

				for (PDField field : acroForm.getFieldTree()) {
					fieldMap.put(field.getFullyQualifiedName(), field.getValueAsString());
				}
				ObjectMapper mapper = new ObjectMapper();
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				result = mapper.writeValueAsString(fieldMap);

				logger.log(Level.INFO, "AcroForm fields as JSON:{0} ", result);
			}
			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
