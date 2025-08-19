package com.example.docgen.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

public class PdfUtil {

	private PdfUtil() {
	}

	static Logger logger = Logger.getLogger(PdfUtil.class.getName());

	public static byte[] replacePlaceholders(InputStream templateStream, Map<String, Object> data) {
		try (PDDocument document = PDDocument.load(templateStream);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
			if (acroForm != null) {
				Map<String, String> fieldMap = new LinkedHashMap<>();

				for (PDField field : acroForm.getFieldTree()) {
					fieldMap.put(field.getFullyQualifiedName(), field.getValueAsString());
				}
				acroForm.setNeedAppearances(true); // Ensure that the form fields are updated
				for (Map.Entry<String, Object> entry : data.entrySet()) {
					String fieldName = entry.getKey();
					Object fieldValue = entry.getValue();
					if (acroForm.getField(fieldName) != null) {
						acroForm.getField(fieldName).setValue((fieldValue==null)?"":fieldValue.toString());
					} else {
						logger.log(Level.WARNING, () -> "Field not found: " + fieldName);
					}
				}
			}
			document.save(out);
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0]; // Return an empty byte array in case of error
		}

	}
}
