package com.sparta.msa_exam.order.core;

import com.sparta.msa_exam.order.core.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "orderdetails")
public class OrderDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    OrderEntity orders;

    private Long product_id;

    private Integer product_count;

    public static OrderDetailEntity createOrderDetailEntity(OrderEntity orders, Long product_id, Integer product_count) {
        return OrderDetailEntity.builder()
                .orders(orders)
                .product_id(product_id)
                .product_count(product_count)
                .build();
    }

}
