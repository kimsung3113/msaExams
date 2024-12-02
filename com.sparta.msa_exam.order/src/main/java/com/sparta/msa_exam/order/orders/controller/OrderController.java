package com.sparta.msa_exam.order.orders.controller;

import com.sparta.msa_exam.order.orders.OrderService;
import com.sparta.msa_exam.order.orders.dto.OrderRequestDto;
import com.sparta.msa_exam.order.orders.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/orders")
    public OrderResponseDto createOrder(@RequestBody OrderRequestDto orderRequestDto,
                                        @RequestHeader(value = "X-User-Email", required = true) String email,
                                        @RequestHeader(value = "X-Role", required = true) String role) {

        return orderService.createOrder(orderRequestDto, email);
    }
}
