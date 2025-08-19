package com.example.docgen.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.example.docgen.model.DocumentRequest;
import com.example.docgen.model.DocumentResponse;
import com.example.docgen.service.DocumentGenerationService;
import com.pst.app.crewpro.common.web.support.vo.Response;

class DocumentControllerTest {
    @Mock
    private DocumentGenerationService documentGenerationService;

    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAvailableFields() {
        DocumentRequest request = new DocumentRequest("test.docx", new HashMap<>());
        MockMultipartFile template = new MockMultipartFile("template", "test.docx", MediaType.APPLICATION_OCTET_STREAM_VALUE, "dummy content".getBytes());
        when(documentGenerationService.getAvailableFields(any(), any())).thenReturn("field1,field2");
        ResponseEntity<Response<String>> response = documentController.getAvailableFields(request, template);
        assertEquals("field1,field2", response.getBody().getData());
    }

    @Test
    void testFillData() {
        DocumentRequest request = new DocumentRequest("ManagerAffidavitTemplate.pdf", new HashMap<>());
        MockMultipartFile template = new MockMultipartFile("template", "ManagerAffidavitTemplate.pdf", MediaType.APPLICATION_OCTET_STREAM_VALUE, "dummy content".getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=output.docx");
        byte[] body = "output content".getBytes();
        DocumentResponse docResponse = new DocumentResponse(headers, body);
        when(documentGenerationService.fillData(any(), any())).thenReturn(docResponse);
        ResponseEntity<byte[]> response = documentController.fillData(request, template);
        assertArrayEquals(body, response.getBody());
        assertEquals("attachment; filename=output.docx", response.getHeaders().getFirst("Content-Disposition"));
    }
}