package com.example.docgen.processor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;
@Component
public final class XMLTemplateProcessor implements TemplateProcessor {
	private TemplateEngine templateEngine;

	@Autowired
	public XMLTemplateProcessor(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	@Override
	public byte[] process(InputStream templateStream, Map<String, Object> data) {
		try {
			Context context = new Context();
			context.setVariables(data);
			String templateContent = new String(templateStream.readAllBytes(), StandardCharsets.UTF_8);

			StringTemplateResolver resolver = new StringTemplateResolver();
			resolver.setTemplateMode("XML"); // or "HTML", "TEXT"
			resolver.setCacheable(false); // optional
			
			templateEngine.setTemplateResolver(resolver);
			String filledXML=templateEngine.process(templateContent, context);
			return filledXML.getBytes(StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0]; // Return an empty byte array in case of error
		}
	}

	@Override
	public boolean supports(String templateName) {
		return templateName != null && templateName.toLowerCase().endsWith(".xml");
	}

	@Override
	public HttpHeaders getHeaders(String templateName) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(templateName).build());
        headers.setContentType(MediaType.APPLICATION_XML);
		return headers;
	}

	@Override
	public String getAvailableFields(MultipartFile file) {
		return "This processor does not support extracting fields from XML templates.";
	}

}
