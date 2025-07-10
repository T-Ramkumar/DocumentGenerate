package com.example.docgen.model;

import org.springframework.http.HttpHeaders;

public record DocumentResponse(
		HttpHeaders headers,
		byte[] body
		) {}
