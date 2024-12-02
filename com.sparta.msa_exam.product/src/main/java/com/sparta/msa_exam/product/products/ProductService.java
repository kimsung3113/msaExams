package com.sparta.msa_exam.product.products;

import com.sparta.msa_exam.product.core.ProductEntity;
import com.sparta.msa_exam.product.products.dto.ProductsRequestDto;
import com.sparta.msa_exam.product.products.dto.ProductsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductsRepository productsRepository;

    /**
     *
     * @param productRequestDto 이름, 가격, 수량, 설명
     * @param email
     * @return
     */
    @Transactional
    public ProductsResponseDto createProduct(ProductsRequestDto productRequestDto, String email) {

        ProductEntity product = ProductEntity.createProduct(productRequestDto, email);
        ProductEntity savedProduct = productsRepository.save(product);
        return toResponseDto(savedProduct);
    }

    /**
     *
     * @param productId 상품 id
     * @return
     */
    @Transactional(readOnly = true)
    public ProductsResponseDto getProductById(Long productId) {
        ProductEntity product = productsRepository.findById(productId)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found or has been deleted"));
        return toResponseDto(product);
    }

    @Transactional
    public void reduceProductQuantity(Long productId, int quantity) {
        ProductEntity product = productsRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough quantity for product ID: " + productId);
        }

        product.reduceQuantity(quantity);
    }



    private ProductsResponseDto toResponseDto(ProductEntity product) {
        return ProductsResponseDto.builder()
                .product_id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .supply_price(product.getSupply_price())
                .quantity(product.getQuantity())
                .build();
    }


}
