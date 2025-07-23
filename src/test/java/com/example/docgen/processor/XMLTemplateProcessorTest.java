package com.example.docgen.processor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;

@SpringBootTest
class XMLTemplateProcessorTest {

	@Autowired
	TemplateEngine templateEngine;

	@Test
	void testProcess() {
		//TemplateEngine templateEngine = new TemplateEngine();
		XMLTemplateProcessor xmlTemplateProcessor = new XMLTemplateProcessor(templateEngine);
		try (InputStream is = getClass().getResourceAsStream("/User.xml")) {
			assertNotNull(is);
			byte[] result = xmlTemplateProcessor.process(is, getData());
			assertNotNull(result);
			assertTrue(result.length > 0);
		} catch (Exception e) {
			fail(e);
		}
	}

	private Map<String, Object> getData() {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("username", "JohnDoe");
		dataMap.put("email", "john.doe@example.com");
		dataMap.put("role", "admin");
		return dataMap;
	}
}
