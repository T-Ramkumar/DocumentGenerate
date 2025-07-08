package com.example.docgen.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public record DocumentRequest(
    @NotBlank String templateName,
    @NotNull Map<String, Object> data
   // MultipartFile file
) {}
