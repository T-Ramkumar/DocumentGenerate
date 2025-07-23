package com.example.docgen.model;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
@Schema(description = "DocumentGeneration Details", name = "DocumentRequest")
public record DocumentRequest(
		
    @NotBlank(message="TemplateName cannot be blank")
    @Schema(description = "Name of the template file")
    String templateName,
    @Schema(description = "Data for filling the template")
    Map<String, Object> data
) {}
