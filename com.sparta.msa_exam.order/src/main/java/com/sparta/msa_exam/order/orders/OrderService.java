package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.core.client.ProductClient;
import com.sparta.msa_exam.order.core.client.ProductResponseDto;
import com.sparta.msa_exam.order.core.repository.OrderDetailRepository;
import com.sparta.msa_exam.order.core.repository.OrderRepository;
import com.sparta.msa_exam.order.orders.dto.OrderRequestDto;
import com.sparta.msa_exam.order.orders.dto.OrderResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductClient productClient;

    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackcreateOrder")
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto, String email) {

        for (OrderRequestDto.ProductsInOrder products : orderRequestDto.getProductsInOrderList()) {
            ProductResponseDto product = productClient.getProduct(products.getProductId());
            log.info("############################ Product 수량 확인 : " + product.getQuantity());
            if (product.getQuantity() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with ID " + products.getProductId() + " is out of stock.");
            }
        }

        // ordercount수로 수량 Reduce
        for (OrderRequestDto.ProductsInOrder products : orderRequestDto.getProductsInOrderList()) {
            productClient.reduceProductQuantity(products.getProductId(), products.getOrderCount());
        }


        return null;

    }

    public OrderResponseDto fallbackcreateOrder(OrderRequestDto orderRequestDto, String userId, Throwable t) {

        log.error("####Fallback triggered due to: {}", t.getMessage());
        return OrderResponseDto.builder()
                .description("잠시 후에 주문 추가를 요청 해주세요.")
                .build();
    }



     /*TODO 2. [상품 목록 조회 API]를 호출해서 파라미터로 받은 `product_id` 가 상품 목록에 존재하는지 검증
      TODO 3. 존재할경우 주문에 상품을 추가하고, 존재하지 않는다면 주문에 저장하지 않음.*/
}
