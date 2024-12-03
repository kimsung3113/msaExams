package com.sparta.msa_exam.product.products.controller;

import com.sparta.msa_exam.product.products.ProductService;
import com.sparta.msa_exam.product.products.dto.ProductSearchDto;
import com.sparta.msa_exam.product.products.dto.ProductsRequestDto;
import com.sparta.msa_exam.product.products.dto.ProductsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Value("${server.port}")
    private String serverPort;

    @PostMapping("/products")
    public ResponseEntity<ProductsResponseDto> createProduct(@RequestBody ProductsRequestDto productRequestDto,
                                                            @RequestHeader(value = "X-User-Email", required = true) String email,
                                                            @RequestHeader(value = "X-Role", required = true) String role) {
        if (!"MANAGER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. User role is not MANAGER.");
        }

        ProductsResponseDto response = productService.createProduct(productRequestDto, email);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Server-Port", serverPort).body(response);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductsResponseDto> getProductById(@PathVariable Long productId) {

        ProductsResponseDto response = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Server-Port", serverPort).body(response);
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductsResponseDto>> getProducts(ProductSearchDto searchDto,
         @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ProductsResponseDto> response = productService.getProducts(searchDto, pageable);

        return ResponseEntity.status(HttpStatus.OK).header("Server-Port", serverPort).body(response);
    }

    @GetMapping("/products/{id}/reduceQuantity")
    public void reduceProductQuantity(@PathVariable Long id, @RequestParam int quantity) {
        productService.reduceProductQuantity(id, quantity);
    }

}
