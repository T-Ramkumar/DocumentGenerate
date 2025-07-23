package com.example.docgen.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.docgen.exception.TemplateNotFoundException;
import com.example.docgen.model.DocumentRequest;
import com.example.docgen.model.DocumentResponse;
import com.example.docgen.processor.PdfTemplateProcessor;

class DocumentGenerationServiceTest {
	@Mock
	private PdfTemplateProcessor pdfProcessor;

	@InjectMocks
	private DocumentGenerationService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		service = new DocumentGenerationService(List.of(pdfProcessor));
	}

	@Test
	void testFillData_Success() throws Exception {
		DocumentRequest request = new DocumentRequest("file.pdf", Map.of("key", "value"));
		MultipartFile file = new MockMultipartFile("file", "file.pdf", "application/pdf", "dummy".getBytes());
		byte[] processed = "processed".getBytes();
		HttpHeaders headers = new HttpHeaders();
		when(pdfProcessor.supports(eq("file.pdf"))).thenReturn(true);
		when(pdfProcessor.process(any(InputStream.class), eq(request.data()))).thenReturn(processed);
		when(pdfProcessor.getHeaders(eq("file.pdf"))).thenReturn(headers);
		DocumentResponse result = service.fillData(request, file);
		assertArrayEquals(processed, result.body());
		assertEquals(headers, result.headers());
	}

	@Test
	void testFillData_TemplateNotFound() {
		DocumentRequest request = new DocumentRequest("notfound", Map.of());
		MultipartFile file = new MockMultipartFile("file", "file.pdf", "application/pdf", "dummy".getBytes());
		when(pdfProcessor.supports(eq("notfound"))).thenReturn(false);
		assertThrows(TemplateNotFoundException.class, () -> service.fillData(request, file));
	}

}
