package com.example.docgen.processor;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PdfTemplateProcessorTest {
    @Test
    void testProcess() {
        PdfTemplateProcessor processor = new PdfTemplateProcessor();
        try (InputStream is = getClass().getResourceAsStream("/templates/example.pdf")) {
            assertNotNull(is);
            byte[] result = processor.process(is, Map.of("name", "Test", "invoiceNumber", "INV-1"));
            assertNotNull(result);
            assertTrue(result.length > 0);
        } catch (Exception e) {
            fail(e);
        }
    }
}
