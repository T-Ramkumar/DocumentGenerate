package com.example.docgen.processor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class PdfTemplateProcessorTest {
	@Test
	void testProcess() {
		PdfTemplateProcessor processor = new PdfTemplateProcessor();
		try (InputStream is = getClass().getResourceAsStream("/ManagerAffidavitTemplate.pdf")) {
			assertNotNull(is);
			byte[] result = processor.process(is, getData());
			assertNotNull(result);
			assertTrue(result.length > 0);
		} catch (Exception e) {
			fail(e);
		}
	}

	private Map<String, Object> getData() {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("pool", "tempPool");
		dataMap.put("location", "Thirunelveli");
		dataMap.put("supervisorName", "Ramkumar");
		dataMap.put("supervisorPhone", "1234567890");
		dataMap.put("collectorName", "Rajan");
		dataMap.put("collectionID", "1234");
		dataMap.put("employeesList", "abc");

		dataMap.put("employeesList.1", "Sam");
		dataMap.put("employeesList.1.formattedName", "Ram");
		dataMap.put("employeesList.1.employeeID", "5543");
		dataMap.put("employeesList.1.positionTitle", "Software Engineer");
		return dataMap;
	}

	@Test
	void testSupports() {
		PdfTemplateProcessor processor = new PdfTemplateProcessor();
		assertTrue(processor.supports("template.pdf"));
		assertTrue(processor.supports("TEMPLATE.PDF"));
		assertFalse(processor.supports("template.docx"));
		assertFalse(processor.supports(null));
	}

	@Test
	void testGetHeaders() {
		PdfTemplateProcessor processor = new PdfTemplateProcessor();
		String filename = "output.pdf";
		var headers = processor.getHeaders(filename);
		assertNotNull(headers);
		assertTrue(headers.getContentDisposition().toString().contains(filename));
		assertTrue(headers.getContentType().toString().contains("application/pdf"));
	}

	@Test
	void testGetAvailableFields() throws Exception {
		PdfTemplateProcessor processor = new PdfTemplateProcessor();
		try (InputStream is = getClass().getResourceAsStream("/ManagerAffidavitTemplate.pdf")) {
			assertNotNull(is);

			byte[] pdfBytes = is.readAllBytes();
			MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", pdfBytes);
			String result = processor.getAvailableFields(file);
			assertNotNull(result);
			assertTrue(result.contains("employeesList.4.positionTitle"));
			assertTrue(result.contains("collectorName"));
		} catch (Exception e) {
			fail(e);
		}
	}
}