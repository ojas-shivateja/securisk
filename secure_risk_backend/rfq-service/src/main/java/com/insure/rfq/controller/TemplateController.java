package com.insure.rfq.controller;

import com.insure.rfq.dto.TemplateDto;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.TemplateService;
import com.insure.rfq.service.impl.ProductServiceImpl;
import com.insure.rfq.service.impl.TemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "*")
public class TemplateController {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private ProductServiceImpl impl;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private TemplateServiceImpl Service;

    @PostMapping("/create")
    public ResponseEntity<String> createTemplate(@RequestParam("templateFile") MultipartFile templateFile,
                                                 @RequestParam("templateName") String templateName, @RequestParam("templateType") String templateType,
                                                 @RequestParam("type") String type, @RequestParam(name = "permissions", required = false) String permissions,
                                                 @RequestParam(name = "productId", required = false) Long productId) throws IOException {
        try {
            System.out.println("Received file: " + templateFile.getOriginalFilename());
            System.out.println("templateName: " + templateName);
            TemplateDto templatesDto = new TemplateDto();
            templatesDto.setTemplateName(templateName);
            templatesDto.setTemplateType(templateType);
            templatesDto.setType(type);
            templatesDto.setPermissions(permissions);
            templatesDto.setTemplateFile(templateFile);
            templatesDto.setTemplateFileName(templateFile.getOriginalFilename());

            if (productId == null) {
                productId = null;
            }

            templatesDto.setProductId(productId);
            System.out.println(templateFile.getOriginalFilename());
            TemplateDto createdTemplate = templateService.createTemplate(templatesDto);
            String responseString = "Template created with ID: " + createdTemplate.getId();
            return ResponseEntity.status(HttpStatus.CREATED).body(responseString);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create template.");
        }
    }

    @GetMapping("/data/{id}")
    public ResponseEntity<TemplateDto> getTemplateById(@PathVariable Long id) {
        TemplateDto templateDto = templateService.getTemplateById(id);

        if (templateDto != null) {
            return ResponseEntity.ok(templateDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/byType/{type}")
    public ResponseEntity<?> getFilesByType(@PathVariable String type) {
        try {
            List<TemplateDto> templatesByType = templateService.getTemplatesByType(type);

            if (!templatesByType.isEmpty()) {
                return ResponseEntity.ok(templatesByType);
            } else {
                return ResponseEntity.ok("No data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/files/byType/{type}")
    public ResponseEntity<List<byte[]>> getTemplateFilesByType(@PathVariable String type) {
        try {
            List<byte[]> templateFiles = templateService.getTemplateFilesByType(type);

            if (!templateFiles.isEmpty()) {
                return ResponseEntity.ok(templateFiles);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> getFileDataById(@PathVariable Long id) {
        try {
            byte[] fileData = templateService.getFileDataById(id);
            ByteArrayResource resource = new ByteArrayResource(fileData);
            String FileName = Service.getTemplateFileNameById(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(resource.contentLength());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // Set the filename for the download
            headers.setContentDispositionFormData("attachment", FileName);

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<TemplateDto>> getAllTemplates() {
        try {
            List<TemplateDto> templates = templateService.getAllTemplates();
            if (!templates.isEmpty()) {
                return ResponseEntity.ok(templates);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/update/{id}")
    public String updateTemplate(@PathVariable Long id, @RequestParam("templateName") String templateName,
                                 @RequestParam("templateType") String templateType, @RequestParam("type") String type,
                                 @RequestParam(name = "permissions", required = false) String permissions,
                                 @RequestParam(value = "templateFile", required = false) MultipartFile templateFile) {
        try {
            TemplateDto updatedTemplateDto = new TemplateDto();
            updatedTemplateDto.setTemplateName(templateName);
            updatedTemplateDto.setTemplateType(templateType);
            updatedTemplateDto.setType(type);
            updatedTemplateDto.setPermissions(permissions);

            if (templateFile != null && !templateFile.isEmpty()) {
                System.out.println("Received file: " + templateFile.getOriginalFilename()); // Debug
                updatedTemplateDto.setTemplateFile(templateFile);
                updatedTemplateDto.setTemplateFileName(templateFile.getOriginalFilename());
            }

            TemplateDto updatedTemplate = templateService.updateTemplate(id, updatedTemplateDto);

            if (updatedTemplate != null) {
                return "Template updated successfully";
            } else {
                return "Template not found or update failed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Internal Server Error";
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTemplate1(@PathVariable Long id) {
        try {
            boolean deleted = templateService.deleteTemplate(id);

            if (deleted) {
                return ResponseEntity.ok("Template with ID " + id + " deleted successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the template.");
        }
    }

}
