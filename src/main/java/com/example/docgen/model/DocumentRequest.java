package com.example.docgen.model;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentRequest(
    @NotBlank String templateName,
    @NotNull Map<String, Object> data
) {}
