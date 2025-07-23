package com.example.docgen.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public final class XSLTemplateProcessor implements TemplateProcessor {

	@Override
	public byte[] process(InputStream templateStream, Map<String, Object> data) {
		try (ByteArrayOutputStream outStream = new ByteArrayOutputStream();) {
			FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outStream);
			Result res = new SAXResult(fop.getDefaultHandler());
			Source xslTemplate = new StreamSource(templateStream);
			String xmlData = (String) data.get("xmlData");
			Source xmlSource = new StreamSource(new ByteArrayInputStream(xmlData.getBytes()));

			// Setup the transformer
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer(xslTemplate);

			// Transform the XML to PDF
			transformer.transform(xmlSource, res);

			// Return the generated PDF as byte array
			return outStream.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	@Override
	public boolean supports(String templateName) {
		return templateName != null && templateName.toLowerCase().endsWith(".xsl");
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
		return "This processor does not support extracting fields from XSL templates.";
	}

}
