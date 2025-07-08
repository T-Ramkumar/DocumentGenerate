# Document Generation Service

A Spring Boot 3.5 / Java 21 REST API for generating documents (PDF/DOCX) from templates and data.

## Features
- Generate PDF or DOCX from templates with placeholder substitution
- Extensible processor architecture (add HTML, XLSX, etc.)
- Base64-encoded output for easy download
- Error handling and validation

## Build & Run

```sh
mvn clean package
java -jar target/document-generation-service-1.0.0.jar
```

## API Usage

### Generate Document

```sh
curl -X POST http://localhost:8080/api/v1/documents/generate \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "example.pdf",
    "data": {
      "name": "John Doe",
      "invoiceNumber": "INV123"
    }
  }'
```

The response is a Base64-encoded string. To decode and save as PDF:

**Linux/macOS:**
```sh
echo "<base64-string>" | base64 -d > output.pdf
```

**Windows (PowerShell):**
```powershell
[IO.File]::WriteAllBytes("output.pdf", [Convert]::FromBase64String("<base64-string>"))
```

## Extensibility
- Add new processors by implementing `TemplateProcessor` (e.g., HTML, XLSX).
- Place new templates in `src/main/resources/templates/`.
- For localization, add resource bundles and enhance placeholder logic.

## License
Open source, Apache 2.0.
