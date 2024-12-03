package com.sparta.msa_exam.product.products.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class ProductsResponseDto {

    private Long product_id;

    private String name;

    private Integer supply_price;

    private String description;

    private Integer quantity;

}
