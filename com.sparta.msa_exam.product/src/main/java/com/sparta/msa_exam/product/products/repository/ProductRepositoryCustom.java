package com.sparta.msa_exam.product.products.repository;

import com.sparta.msa_exam.product.products.dto.ProductSearchDto;
import com.sparta.msa_exam.product.products.dto.ProductsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductsResponseDto> searchProducts(ProductSearchDto searchDto, Pageable pageable);
}
