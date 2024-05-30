package com.insure.rfq.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insure.rfq.dto.*;
import com.insure.rfq.entity.*;
import com.insure.rfq.exception.*;
import com.insure.rfq.repository.*;
import com.insure.rfq.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository prodRepository;
    @Autowired
    private ProductCategoryRepository prodCategoryRepository;
    @Autowired
    private ClientListRepository clientListRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private InsureListRepository insureListRepository;
    @Autowired
    private TpaRepository tpaRepository;
    @Autowired
    private ClientListProductAssociationRepository clientListProductAssociationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProductChildDto findProducts(Long productId) {
        ProductChildDto productChildDto = new ProductChildDto();
        Optional<Product> findProductNamesByCategory = prodRepository.findByProductId(productId);
        if (findProductNamesByCategory.isPresent()) {
            Product product = findProductNamesByCategory.get();
            productChildDto.setProductId(product.getProductId());
            productChildDto.setProductName(product.getProductName());
        }
        return productChildDto;
    }

    @Override
    public List<ProductChildDto> getProductsByCategory(Long productcategoryId) {
        List<Map<String, Object>> findProductNamesByCategory = prodRepository
                .findProductNamesByCategory(productcategoryId);
        List<ProductChildDto> products = new ArrayList<>();
        for (Map<String, Object> result : findProductNamesByCategory) {
            ProductChildDto dto = objectMapper.convertValue(result, ProductChildDto.class);
            products.add(dto);
        }
        return products;
    }

    @Override
    public ProductCreateDto addProduct(ProductCreateDto productDto) {
        log.info("ProductCategory From AddProduct ", productDto.getProductcategoryId());
        Optional<ProductCategory> prodcategory = prodCategoryRepository
                .findById(Long.parseLong(productDto.getProductcategoryId()));
        Product product = null;
        if (productDto != null && prodcategory.isPresent()) {
            product = new Product();
            product.setCreatedDate(new Date());
            product.setProductcategory(prodcategory.get());
            product.setProductName(productDto.getProductName());
            product.setStatus("ACTIVE");
            prodRepository.save(product);
        } else {
            System.out.println("No Category");
        }
        return productDto;
    }

    @Override
    public AddProductClientDto createProduct(AddProductClientDto productDto, Long clientListId) {
        // Fetch the ClientList entity or throw an InvalidClientList exception if not
        // found
        ClientList clientList = clientListRepository.findById(clientListId)
                .orElseThrow(() -> new InvalidClientList("ClientList Id is not Present"));

        // Fetch the Product entity or throw an InvalidProduct exception if not found
        Product product = prodRepository.findById(Long.parseLong(productDto.getProductId()))
                .orElseThrow(() -> new InvalidProduct("Product Not Found"));

        // Fetch the InsureList entity or throw an exception if not found
        InsureList insurer = insureListRepository.findById(productDto.getInsurerCompanyId())
                .orElseThrow(() -> new InvalidInsurer("Insurer Not Found"));

        // TPA is optional, fetch it only if TPA ID is provided and not empty
        Tpa tpa = null;
        if (productDto.getTpaId() != null && !productDto.getTpaId().trim().isEmpty()
                && Long.parseLong(productDto.getTpaId()) != 0) {
            tpa = tpaRepository.findById(Long.valueOf(productDto.getTpaId()))
                    .orElseThrow(() -> new InvalidTpaException("TPA Not Found"));
        }

        // Create the new association entity
        ClientProductAssociation association = new ClientProductAssociation();
        association.setClientList(clientList);
        association.setProduct(product);
        association.setInsurer(insurer);
        association.setTpa(tpa);
        association.setPolicyType(productDto.getPolicyType());
        association.setRecordStatus("ACTIVE");

        // Save the association
        clientListProductAssociationRepository.save(association);

        // Update DTO for response
        productDto.setProductId(String.valueOf(product.getProductId()));
        productDto.setPolicyType(productDto.getPolicyType());
        productDto.setInsurerCompanyId(insurer != null ? insurer.getInsurerName() : null);
        productDto.setTpaId(tpa != null ? tpa.getTpaName() : null); // Handle null TPA

        return productDto;
    }

    @Override
    public List<ProductDto> getAllProducts(Long id) {
        return prodRepository.findAll().stream().filter(user -> {
            List<ClientList> clientList = user.getClientList();
            return clientList != null && clientList.stream().anyMatch(client -> client.getCid() == id)
                    && user.getStatus().equalsIgnoreCase("ACTIVE");
        }).map(users -> modelMapper.map(users, ProductDto.class)).toList();
    }

    @Override
    public ProductIntermediaryDto updatePoductById(Long productId, ProductIntermediaryDto productDto) {
        Product product = prodRepository.findById(productId).get();
        product.setProductName(productDto.getProductName());
        product.setUpdatedDate(new Date());
        log.info("Product From Update Product ",
                prodCategoryRepository.findByCategoryName(productDto.getCategoryName()).toString());
        product.setProductcategory(prodCategoryRepository.findByCategoryName(productDto.getCategoryName()));
        prodRepository.save(product);
        return productDto;
    }

    @Override
    public String deletePoductById(Long productId) {
        Product product = prodRepository.findById(productId).orElseThrow(() -> new InvalidUser("user Not Found"));
        product.setStatus("INACTIVE");
        prodRepository.save(product);
        return "Deleted Successfully";
    }

    @Override
    public List<GetProductDropdownDto> getAllProductsWithId() {
        List<Product> products = prodRepository.findAll();
        List<GetProductDropdownDto> productsWithId = new ArrayList<>();
        for (Product getProductWithIdDto : products) {
            if (getProductWithIdDto.getStatus() != null && getProductWithIdDto.getStatus().equalsIgnoreCase("ACTIVE")) {
                GetProductDropdownDto productWithIdDto = new GetProductDropdownDto();
                productWithIdDto.setProductId(getProductWithIdDto.getProductId());
                productWithIdDto.setProductName(getProductWithIdDto.getProductName());
                productsWithId.add(productWithIdDto);
            }
        }
        return productsWithId;
    }

    @Override
    public List<GetAllProductsByClientListIdDto> getAllProductsByClientListId(Long clientListId) {
        // Fetch the ClientList entity by its ID
        ClientList clientList = clientListRepository.findById(clientListId)
                .orElseThrow(() -> new IllegalArgumentException("ClientList Id is not Present"));

        // Fetch all associations for the given ClientList
        List<ClientProductAssociation> associations = clientListProductAssociationRepository
                .findByClientList(clientList);

        // Map the associations to GetAllProductsByClientListIdDto
        return associations.stream()
                .filter(association -> "ACTIVE".equalsIgnoreCase(association.getClientList().getStatus())
                        && "ACTIVE".equalsIgnoreCase(association.getProduct().getStatus()))
                .map(association -> {
                    GetAllProductsByClientListIdDto dto = new GetAllProductsByClientListIdDto();
                    dto.setProductId(String.valueOf(association.getProduct().getProductId()));
                    dto.setProductName(association.getProduct().getProductName());
                    dto.setPolicyType(association.getPolicyType());
                    dto.setTpaId(association.getTpa() != null ? association.getTpa().getTpaName() : null);
                    dto.setInsurerCompanyId(
                            association.getInsurer() != null ? association.getInsurer().getInsurerName() : null);
                    return dto;
                }).toList();
    }


    // update ClientList
    @Transactional
    public AddProductClientDto updateClientListProduct(Long clientId, Long productId,
                                                       AddProductClientDto addProductClientDto) {
        if (clientId != null && productId != null) {
            // Fetch the ClientList and Product entities
            ClientList clientList = clientListRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("ClientList Id is not Present"));

            Product product = prodRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product Id is not Present"));

            // Fetch the ClientProductAssociation
            ClientProductAssociation association = clientListProductAssociationRepository
                    .findByClientListAndProduct(clientId, productId).orElse(null);

            // Update the association with new values from the DTO

            if (addProductClientDto.getProductId() != null && !addProductClientDto.getProductId().trim().isEmpty()) {
                Product product1 = prodRepository.findById(Long.valueOf(addProductClientDto.getProductId()))
                        .orElseThrow(() -> new IllegalArgumentException("Product Id is not Present"));
                association.setProduct(product1);
            } else {
                association.setProduct(null);
            }

            if (addProductClientDto.getInsurerCompanyId() != null
                    && !addProductClientDto.getInsurerCompanyId().trim().isEmpty()) {
                InsureList insurer = insureListRepository.findById(addProductClientDto.getInsurerCompanyId())
                        .orElseThrow(() -> new IllegalArgumentException("Insurer Id is not Present"));
                association.setInsurer(insurer);
            } else {
                association.setInsurer(null);
            }

            if (addProductClientDto.getTpaId() != null && !addProductClientDto.getTpaId().trim().isEmpty()) {
                Tpa tpa = tpaRepository.findById(Long.parseLong(addProductClientDto.getTpaId()))
                        .orElseThrow(() -> new IllegalArgumentException("TPA Id is not Present"));
                association.setTpa(tpa);
            } else {
                association.setTpa(null);
            }

            association.setPolicyType(
                    addProductClientDto.getPolicyType() != null ? addProductClientDto.getPolicyType() : null);

            // Save the updated association
            clientListProductAssociationRepository.save(association);

            // Update DTO with the saved values
            addProductClientDto.setProductId(String
                    .valueOf(association.getProduct() != null ? association.getProduct().getProductName() : null));
            addProductClientDto.setInsurerCompanyId(
                    association.getInsurer() != null ? association.getInsurer().getInsurerName() : null);
            addProductClientDto.setTpaId(association.getTpa() != null ? association.getTpa().getTpaName() : null);
            addProductClientDto.setPolicyType(association.getPolicyType());

            return addProductClientDto;
        }

        return null;
    }

    @Transactional
    public void updateProductRelationship(Long newProductId, Long clientListId, Long oldProductId) {
        log.info("newProductId: {}, clientListId: {}, oldProductId: {}", newProductId, clientListId, oldProductId);
        clientListRepository.updateProductRelationship(newProductId, clientListId, oldProductId);
    }

    @Override
    @Transactional
    public String deleteProductRelationForClientList(Long productId, Long clientListId) {
        // Find the association between the product and the client list
        ClientProductAssociation clientProductAssociation = clientListProductAssociationRepository
                .findByClientListAndProduct(clientListId, productId).orElse(null);

        // Check if the association is null and return an error message if it is
        if (clientProductAssociation == null) {
            return "No product relation found for the given client list and product.";
        }

        // If the association is found, delete it
        clientListProductAssociationRepository.delete(clientProductAssociation);
        return "Product relation deleted successfully for client list";
    }

}