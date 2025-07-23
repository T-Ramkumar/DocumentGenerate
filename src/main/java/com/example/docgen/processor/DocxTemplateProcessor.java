package com.example.docgen.processor;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public final class DocxTemplateProcessor implements TemplateProcessor {
    @Override
    public byte[] process(InputStream templateStream, Map<String, Object> data) {
        try (XWPFDocument doc = new XWPFDocument(templateStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (XWPFParagraph p : doc.getParagraphs()) {
                String text = p.getText();
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    text = text.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
                }
                p.getRuns().forEach(run -> run.setText("", 0));
                p.createRun().setText(text);
            }
            doc.write(out);
            return out.toByteArray();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public boolean supports(String templateName) {
        return templateName != null && templateName.toLowerCase().endsWith(".docx");
    }

	@Override
	public HttpHeaders getHeaders(String templateName) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(templateName).build());
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
		return headers;
	}

	@Override
	public String getAvailableFields(MultipartFile file) {
		return "This processor does not support extracting fields from DOCX templates.";
	}
}
