package com.example.docgen.controller;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.docgen.model.DocumentRequest;
import com.example.docgen.model.DocumentResponse;
import com.example.docgen.service.DocumentGenerationService;
import com.pst.app.crewpro.common.web.support.vo.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/")
@Tag(name = "Document Generate", description = "Fills the data into the pdf,xml,docx templates")
public class DocumentController {
	Logger logger = Logger.getLogger(DocumentController.class.getName());
	private final DocumentGenerationService documentGenerationService;

	public DocumentController(DocumentGenerationService documentGenerationService) {
		this.documentGenerationService = documentGenerationService;
	}

	@Operation(summary = "Get Avalilable Fields", description = "Gets Avalilable Fields in Template")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully returned the available fields from the template."),
			@ApiResponse(responseCode = "500", description = "Unable to fetch available fields from template.") })
	@PostMapping(value = "/getAvailableFields", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Response<String>> getAvailableFields(@RequestPart("data") DocumentRequest request,
			@RequestPart("template") MultipartFile template) {
		logger.info("Request received to get available fields from template: " + template.getOriginalFilename());
		String availableFields = documentGenerationService.getAvailableFields(request, template);
		if (availableFields == null || availableFields.isEmpty()) {
			logger.warning("Unable to fetch available fields from template.");
			return new ResponseEntity<>(new Response<>("Unable to fetch available fields from template."),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(new Response<>(availableFields), HttpStatus.OK);
	}

	@Operation(summary = "Fill Data", description = "Fills the data into the template")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Filled the data into the template successfully.") })
	@PostMapping(value = "/fillData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<byte[]> fillData(@RequestPart("data") DocumentRequest request,
			@RequestPart("template") MultipartFile template) {
		logger.info("Request received to fill data in template: " + template.getOriginalFilename());
		DocumentResponse documentResponse = documentGenerationService.fillData(request, template);
		return ResponseEntity.ok().headers(documentResponse.headers()).body(documentResponse.body());
	}
}
