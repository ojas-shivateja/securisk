package com.insure.rfq.controller;

import com.insure.rfq.dto.*;
import com.insure.rfq.repository.ProductRepository;
import com.insure.rfq.service.ProductClientService;
import com.insure.rfq.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
@Slf4j
public class ProductController {
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductClientService clientService;
	@Autowired
	private ProductRepository productRepository;
	@GetMapping("/get/{id}")
	public ProductChildDto getById(@PathVariable("id") Long id) {
		return productService.findProducts(id);
	}
	@GetMapping("/getProductcategory/{categoryId}")
	public ResponseEntity<List<ProductChildDto>> getProductcategoryById(@PathVariable("categoryId") Long id) {
		return new ResponseEntity<>(productService.getProductsByCategory(id), HttpStatus.OK);
	}
	@PostMapping("/addProduct")
	@ResponseStatus(value = HttpStatus.CREATED)
	public ProductCreateDto addProduct(@Valid @RequestBody ProductCreateDto product) {
		return productService.addProduct(product);
	}
	@PostMapping("/addProduct/{clientListId}")
	public ResponseEntity<AddProductClientDto> createClientListUser(
			@Valid @RequestBody AddProductClientDto clientListproduct, @PathVariable Long clientListId) {
		log.info("{}", clientListproduct.toString());
		AddProductClientDto createProduct = productService.createProduct(clientListproduct, clientListId);
		return new ResponseEntity<>(createProduct, HttpStatus.CREATED);
	}
	@GetMapping("/getAllproduct/{clientListId}")
	public ResponseEntity<List<ProductClientDto>> getAllUsers(@PathVariable Long clientListId) {
		List<ProductClientDto> allProducts = clientService.getProductsBasedonClientId(clientListId);
		return new ResponseEntity<>(allProducts, HttpStatus.OK);
	}
	@PatchMapping("/updateproductById/{productId}")
	public ResponseEntity<ProductIntermediaryDto> updateproductById(@PathVariable Long productId,
																	@RequestBody ProductIntermediaryDto productDto) {
		ProductIntermediaryDto updatePoductById = productService.updatePoductById(productId, productDto);
		return new ResponseEntity<>(updatePoductById, HttpStatus.OK);
	}
	@DeleteMapping("/deleteproductById/{productId}")
	public ResponseEntity<String> deleteproductById(@PathVariable Long productId) {
		String deletePoductById = productService.deletePoductById(productId);
		return new ResponseEntity<>(deletePoductById, HttpStatus.OK);
	}
	@GetMapping("/getAllProduct")
	public ResponseEntity<List<GetProductDropdownDto>> getAllProductsWithProductId() {
		List<GetProductDropdownDto> allProductsWithId = productService.getAllProductsWithId();
		return new ResponseEntity<>(allProductsWithId, HttpStatus.OK);
	}
	@GetMapping("/getAllProductsWithClientListId/{clientListId}")
	public ResponseEntity<List<GetAllProductsByClientListIdDto>> getAllProductsWithClientListId(
			@PathVariable Long clientListId) {
		List<GetAllProductsByClientListIdDto> allProductsWithId = productService
				.getAllProductsByClientListId(clientListId);
		return new ResponseEntity<>(allProductsWithId, HttpStatus.OK);
	}
	@GetMapping("/count")
	public Long getRfqCountByCount() {
		return productRepository.countApplicationsByStatus();
	}
	@DeleteMapping("/deleteClientListProduct")
	@ResponseStatus(value = HttpStatus.OK)
	public String deleteClientListProduct(@RequestParam Long productId, @RequestParam Long clientListId) {
		return productService.deleteProductRelationForClientList(productId,clientListId);
	}
	@PatchMapping("/updateClientListProduct")
	@ResponseStatus(value = HttpStatus.OK)
	public AddProductClientDto updateClientListProduct(@RequestParam Long clientListId,
													   @RequestParam Long productListId, @RequestBody AddProductClientDto addProductClientDto) {
		return productService.updateClientListProduct(clientListId, productListId, addProductClientDto);
	}
}