package com.sparta.msa_exam.order.orders.dto;

import com.sparta.msa_exam.order.core.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private Long orderId;
    private OrderStatus status;
    private String description;
    private List<Long> products_ids;
}
