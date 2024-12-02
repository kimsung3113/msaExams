package com.sparta.msa_exam.product.products.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductsRequestDto {
    private String name;
    private String description;
    private Integer supply_price;
    private Integer quantity;
}
