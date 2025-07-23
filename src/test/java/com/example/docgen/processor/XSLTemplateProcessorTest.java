package com.example.docgen.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XSLTemplateProcessorTest {
    private XSLTemplateProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new XSLTemplateProcessor();
    }

    @Test
    void testSupports() {
        assertTrue(processor.supports("template.xsl"));
        assertTrue(processor.supports("TEMPLATE.XSL"));
        assertFalse(processor.supports("template.docx"));
        assertFalse(processor.supports(null));
    }

    @Test
    void testGetHeaders() {
        HttpHeaders headers = processor.getHeaders("output.xsl");
        assertEquals("attachment; filename=\"output.xsl\"", headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(MediaType.APPLICATION_PDF, headers.getContentType());
    }

    @Test
    void testProcessWithInvalidInput() {
        // Invalid XSL and XML, should return empty byte array
        InputStream templateStream = new ByteArrayInputStream("invalid xsl".getBytes());
        Map<String, Object> data = new HashMap<>();
        data.put("xmlData", "<root></root>");
        byte[] result = processor.process(templateStream, data);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void testProcessWithMissingXmlData() {
        InputStream templateStream = new ByteArrayInputStream("<xsl></xsl>".getBytes());
        Map<String, Object> data = new HashMap<>();
        // No xmlData key
        byte[] result = processor.process(templateStream, data);
        assertNotNull(result);
        assertEquals(0, result.length);
    }
}
