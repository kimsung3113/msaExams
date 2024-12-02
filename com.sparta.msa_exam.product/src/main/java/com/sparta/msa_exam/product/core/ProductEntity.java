package com.sparta.msa_exam.product.core;

import com.sparta.msa_exam.product.products.dto.ProductsRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer supply_price;

    private String description;

    private Integer quantity;

    private String userId;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime deletedAt;
    private String deletedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static ProductEntity createProduct(ProductsRequestDto requestDto, String email) {
        return ProductEntity.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .supply_price(requestDto.getSupply_price())
                .quantity(requestDto.getQuantity())
                .createdBy(email)
                .build();
    }

    public void reduceQuantity(int i) {
        this.quantity = this.quantity - i;
    }

}
