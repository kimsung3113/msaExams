package com.sparta.msa_exam.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    private List<ProductsInOrder> productsInOrderList;

    @Getter
    public static class ProductsInOrder{

        private Long productId;
        private Integer orderCount;

    }

}
