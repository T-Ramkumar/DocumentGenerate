package com.example.docgen.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class PdfUtil {
	static Logger logger = Logger.getLogger(PdfUtil.class.getName());
    public static byte[] replacePlaceholders(InputStream templateStream, Map<String, Object> data) {
    	try (PDDocument document = PDDocument.load(templateStream);
    			ByteArrayOutputStream out = new ByteArrayOutputStream()){
        	  PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        	  if(acroForm != null) {
        		  Map<String, String> fieldMap = new LinkedHashMap<>();

                  for (PDField field : acroForm.getFieldTree()) {
                      fieldMap.put(field.getFullyQualifiedName(), field.getValueAsString());
                  }
                  ObjectMapper mapper = new ObjectMapper();
                  mapper.enable(SerializationFeature.INDENT_OUTPUT);
                  String jsonOutput = mapper.writeValueAsString(fieldMap);

                  System.out.println("📋 AcroForm fields as JSON:\n" + jsonOutput);
                  
        		  acroForm.setNeedAppearances(true); // Ensure that the form fields are updated
				  for (Map.Entry<String, Object> entry : data.entrySet()) {
					  String fieldName = entry.getKey();
					  Object fieldValue = entry.getValue();
					  if (acroForm.getField(fieldName) != null) {
						  acroForm.getField(fieldName).setValue(fieldValue.toString());
					  }else {
						  logger.log(Level.WARNING, ()->"Field not found: " + fieldName);
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
