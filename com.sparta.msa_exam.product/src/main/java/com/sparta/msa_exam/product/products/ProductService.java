package com.sparta.msa_exam.product.products;

import com.sparta.msa_exam.product.core.ProductEntity;
import com.sparta.msa_exam.product.products.dto.ProductSearchDto;
import com.sparta.msa_exam.product.products.dto.ProductsRequestDto;
import com.sparta.msa_exam.product.products.dto.ProductsResponseDto;
import com.sparta.msa_exam.product.products.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductsRepository productsRepository;
    private final CacheManager cacheManager;

    /**
     *
     * @param productRequestDto 이름, 가격, 수량, 설명
     * @param email
     * @return
     */
    @CachePut(cacheNames = "productCache", key = "#result.product_id")
    @Transactional
    public ProductsResponseDto createProduct(ProductsRequestDto productRequestDto, String email) {

        ProductEntity product = ProductEntity.createProduct(productRequestDto, email);
        ProductEntity savedProduct = productsRepository.save(product);

        refreshPageCache(new ProductSearchDto(), PageRequest.of(0, 10)); // 첫 페이지 갱신 예시
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

    @Cacheable(
            cacheNames = "ProductSearchCache",
            key = "{ args[1].pageNumber, args[1].pageSize }"
    )
    @Transactional(readOnly = true)
    public Page<ProductsResponseDto> getProducts(ProductSearchDto searchDto, Pageable pageable) {
        return productsRepository.searchProducts(searchDto, pageable);
    }

    private void refreshPageCache(ProductSearchDto searchDto, Pageable pageable) {
        Page<ProductsResponseDto> page = productsRepository.searchProducts(searchDto, pageable);

        // 첫번째 page만 변경
        cacheManager.getCache("ProductSearchCache")
                .put(pageable.getPageNumber() + "::" + pageable.getPageSize(), page);
    }

}
