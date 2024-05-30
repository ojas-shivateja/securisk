package com.insure.rfq.service.impl;

import com.insure.rfq.dto.TemplateDto;
import com.insure.rfq.entity.Product;
import com.insure.rfq.entity.TemplateDetails;
import com.insure.rfq.exception.EntityNotFoundException;
import com.insure.rfq.exception.TemplateFileNameNotFoundException;
import com.insure.rfq.exception.TemplateNotFoundException;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.repository.TemplateRepositry;
import com.insure.rfq.service.TemplateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TemplateRepositry repo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public TemplateDto createTemplate(TemplateDto templateDto) {
        try {
            Product product = null; // Initialize to null
            if (templateDto.getProductId() != null) {
                product = productRepository.findById(templateDto.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Product with ID " + templateDto.getProductId() + " not found"));
            }

            TemplateDetails template = new TemplateDetails();
            template.setTemplateName(templateDto.getTemplateName());
            template.setTemplateType(templateDto.getTemplateType());
            template.setType(templateDto.getType());
            template.setPermissions(templateDto.getPermissions());
            template.setCreateDate(templateDto.getCreateDate());
            template.setProduct(product); // Set product, which can be null

            MultipartFile templateFile = templateDto.getTemplateFile();
            if (templateFile != null && !templateFile.isEmpty()) {
                byte[] fileBytes = templateFile.getBytes();
                template.setTemplateFile(fileBytes);
                template.setTemplateFileName(templateFile.getOriginalFilename());
                template.setCreateDate(new Date());
            }
            TemplateDetails createdTemplate = repo.save(template);
            return mapTemplateDetailsToDto(createdTemplate);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw new TemplateNotFoundException("Failed to create template");
        }
    }

    private TemplateDto mapTemplateDetailsToDto(TemplateDetails template) {
        TemplateDto templateDto = new TemplateDto();
        templateDto.setId(template.getId());
        templateDto.setTemplateName(template.getTemplateName());
        templateDto.setTemplateType(template.getTemplateType());
        templateDto.setType(template.getType());
        templateDto.setPermissions(template.getPermissions());
        templateDto.setTemplateFileName(template.getTemplateFileName());
        templateDto.setCreateDate(template.getCreateDate());
        return templateDto;
    }

    @Override
    public TemplateDto getTemplateById(Long id) {
        Optional<TemplateDetails> templateDetailsOptional = repo.findById(id);

        if (templateDetailsOptional.isPresent()) {
            TemplateDetails templateDetails = templateDetailsOptional.get();
            TemplateDto templateDto = new TemplateDto();
            templateDto.setId(templateDetails.getId());
            templateDto.setTemplateName(templateDetails.getTemplateName());
            templateDto.setType(templateDetails.getType());
            templateDto.setTemplateType(templateDetails.getTemplateType());
            templateDto.setTemplateFileName(templateDetails.getTemplateFileName());
            if (templateDetails.getProduct() != null) {
                templateDto.setProductId(templateDetails.getProduct().getProductId());
            } else {
                templateDto.setProductId(null);
            }
            templateDto.setPermissions(templateDetails.getPermissions());
            templateDto.setCreateDate(templateDetails.getCreateDate());
            templateDto.setUpdateDate(templateDetails.getUpdateDate());

            return templateDto;
        } else {

            return null;
        }
    }

    @Override
    public List<TemplateDto> getTemplatesByType(String type) {
        List<TemplateDetails> templates = repo.findByType(type);
        List<TemplateDto> templatesDtoList = new ArrayList<>();

        for (TemplateDetails templateDetails : templates) {
            TemplateDto templatesDto = new TemplateDto();
            templatesDto.setId(templateDetails.getId());
            templatesDto.setTemplateName(templateDetails.getTemplateName());
            templatesDto.setType(templateDetails.getType());
            templatesDto.setTemplateType(templateDetails.getTemplateType());
            templatesDto.setTemplateFileName(templateDetails.getTemplateFileName());

            if (templateDetails.getProduct() != null) {
                templatesDto.setProductId(templateDetails.getProduct().getProductId());
            } else {
                templatesDto.setProductId(null);
            }

            templatesDto.setPermissions(templateDetails.getPermissions());
            templatesDto.setCreateDate(templateDetails.getCreateDate());
            templatesDto.setUpdateDate(templateDetails.getUpdateDate());
            templatesDtoList.add(templatesDto);
        }

        return templatesDtoList;
    }

    @Override
    public List<byte[]> getTemplateFilesByType(String type) {
        List<TemplateDetails> templates = repo.findByTemplateType(type);
        return templates.stream().map(TemplateDetails::getTemplateFile)
                .toList();
    }

    @Override
    public byte[] getFileDataById(Long id) {
        Optional<TemplateDetails> templateDetailsOptional = repo.findById(id);
        if (templateDetailsOptional.isPresent()) {
            TemplateDetails templateDetails = templateDetailsOptional.get();
            return templateDetails.getTemplateFile();
        } else {
            throw new TemplateNotFoundException("Template with ID " + id + " not found");
        }
    }

    public List<TemplateDetails> getTemplatesByProductId(Long productId) {
        return repo.findByProductId(productId);
    }

    @Override
    public List<TemplateDto> getAllTemplates() {
        try {
            List<TemplateDetails> templates = repo.findAll();
            return templates.stream().map(template -> {
                TemplateDto dto = new TemplateDto();
                dto.setId(template.getId());
                dto.setTemplateName(template.getTemplateName());
                dto.setTemplateType(template.getTemplateType());
                dto.setType(template.getType());
                dto.setPermissions(template.getPermissions());
                dto.setTemplateFileName(template.getTemplateFileName());
                if (template.getProduct() != null) {
                    dto.setProductId(template.getProduct().getProductId());
                } else {
                    dto.setProductId(null);
                }
                return dto;
            }).toList();
        } catch (Exception e) {
            // Log the exception
            System.out.println("Error in getAllTemplates: " + e.getMessage());
            // You can handle the exception or rethrow it as needed
            throw new RuntimeException("Failed to retrieve templates.", e);
        }
    }


    @Override
    public TemplateDto updateTemplate(Long id, TemplateDto updatedTemplateDto) {
        try {
            Optional<TemplateDetails> templateOptional = repo.findById(id);
            if (templateOptional.isPresent()) {
                TemplateDetails existingTemplate = templateOptional.get();
                existingTemplate.setTemplateName(updatedTemplateDto.getTemplateName());
                existingTemplate.setTemplateType(updatedTemplateDto.getTemplateType());
                existingTemplate.setType(updatedTemplateDto.getType());

                // Handle permissions, if not null
                if (updatedTemplateDto.getPermissions() != null) {
                    existingTemplate.setPermissions(updatedTemplateDto.getPermissions());
                }

                // Handle template file
                MultipartFile templateFile = updatedTemplateDto.getTemplateFile();
                if (templateFile != null && !templateFile.isEmpty()) {
                    existingTemplate.setTemplateFile(templateFile.getBytes());
                    System.out.println("temp name :: " + templateFile.getOriginalFilename());
                    existingTemplate.setTemplateFileName(templateFile.getOriginalFilename());
                }

                // Update other fields

                Long productId = updatedTemplateDto.getProductId();

                if (productId != null) {
                    Product product = existingTemplate.getProduct();
                    if (product == null) {
                        product = new Product();
                    }
                    product.setProductId(productId);
                    existingTemplate.setProduct(product);
                }

                existingTemplate.setUpdateDate(new Date());

                TemplateDetails updatedTemplate = repo.save(existingTemplate);
                TemplateDto updatedTemplateDto1 = new TemplateDto();
                updatedTemplateDto1.setId(updatedTemplate.getId());

                // Populate other fields in updatedTemplateDto1

                return updatedTemplateDto1;
            } else {
                throw new EntityNotFoundException("Template with ID " + id + " not found");
            }
        } catch (IOException e) {
            // Log the exception for debugging
            e.printStackTrace();
            throw new TemplateNotFoundException("Failed to update template");
        }
    }

    @Override
    public boolean deleteTemplate(Long id) {
        Optional<TemplateDetails> templateOptional = repo.findById(id);
        if (templateOptional.isPresent()) {
            repo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public String getTemplateFileNameById(Long id) {
        Optional<TemplateDetails> templateDetailsOptional = repo.findById(id);
        if (templateDetailsOptional.isPresent()) {
            TemplateDetails templateDetails = templateDetailsOptional.get();
            String templateFileName = templateDetails.getTemplateFileName();
            if (templateFileName != null) {
                return templateFileName;
            } else {
                throw new TemplateFileNameNotFoundException("Template file name is null for ID ");
            }
        } else {
            throw new TemplateNotFoundException("Template with ID " + id + " not found");
        }
    }

}
