package com.sparta.msa_exam.order.orders.dto;

import com.sparta.msa_exam.order.core.OrderDetailEntity;
import com.sparta.msa_exam.order.core.enums.OrderStatus;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto implements Serializable {

    private Long orderId;
    private OrderStatus status;
    private String description;
    private List<OrderProducts> orderProducts;

//    public void setOrderProducts(OrderRequestDto.ProductsInOrder products){
//        this.orderProducts.add(new OrderProducts(products.getProductId(), products.getOrderCount()));
//    }

    public void setOrderProducts(List<OrderProducts> orderProducts) {
        this.orderProducts = orderProducts;
    }

//    public void setOrderDetails(OrderDetailEntity detail){
//        this.orderProducts.add(new OrderProducts(detail.getProduct_id(), detail.getProduct_count()));
//    }

    @Data
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderProducts{

        private Long productId;
        private Integer orderCount;

    }
}
