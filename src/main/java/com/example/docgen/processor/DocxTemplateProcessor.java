package com.example.docgen.processor;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

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
            throw new RuntimeException("Failed to process DOCX template", e);
        }
    }

    @Override
    public boolean supports(String templateName) {
        return templateName != null && templateName.toLowerCase().endsWith(".docx");
    }
}
