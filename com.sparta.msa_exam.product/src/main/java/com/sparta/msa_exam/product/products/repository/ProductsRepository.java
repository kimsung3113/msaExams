package com.sparta.msa_exam.product.products.repository;

import com.sparta.msa_exam.product.core.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<ProductEntity, Long>, ProductRepositoryCustom {
}
