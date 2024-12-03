package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.core.OrderDetailEntity;
import com.sparta.msa_exam.order.core.OrderEntity;
import com.sparta.msa_exam.order.core.client.ProductClient;
import com.sparta.msa_exam.order.core.client.ProductResponseDto;
import com.sparta.msa_exam.order.core.repository.OrderDetailRepository;
import com.sparta.msa_exam.order.core.repository.OrderRepository;
import com.sparta.msa_exam.order.orders.dto.OrderRequestDto;
import com.sparta.msa_exam.order.orders.dto.OrderResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductClient productClient;

    /**
     *  주문 추가 API
     * @param orderRequestDto
     * @param email
     * @param userId
     * @return
     */
    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackcreateOrder")
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto, String email, String userId) {

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

        OrderEntity order = OrderEntity.createOrder(userId, email);
        OrderEntity savedOrder = orderRepository.save(order);

        List<OrderDetailEntity> orderDetailEntityList = new ArrayList<>();

        OrderResponseDto response = OrderResponseDto.builder()
                .orderId(savedOrder.getId())
                .status(savedOrder.getStatus())
                .description("order success")
                .build();

        List<OrderResponseDto.OrderProducts> orderProducts = new ArrayList<>();

        // 여기에 OrderDetailEntity를 쭉 넣어야 된다.
        for(OrderRequestDto.ProductsInOrder products : orderRequestDto.getProductsInOrderList()) {

            // OrderDetails 객체 하나씩 만든다.
            OrderDetailEntity orderDetails = OrderDetailEntity.createOrderDetailEntity
                    (savedOrder, products.getProductId(), products.getOrderCount());

            orderDetailEntityList.add(orderDetails);

            // response list에도 담는다.
            orderProducts.add(new OrderResponseDto.OrderProducts(products.getProductId(), products.getOrderCount()));

        }

        response.setOrderProducts(orderProducts);

        orderDetailRepository.saveAll(orderDetailEntityList);

        return response;

    }

    public OrderResponseDto fallbackcreateOrder(OrderRequestDto orderRequestDto, String userId, Throwable t) {

        log.error("####Fallback triggered due to: {}", t.getMessage());
        return OrderResponseDto.builder()
                .description("잠시 후에 주문 추가를 요청 해주세요.")
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "orderCache", key = "args[0]")
    public OrderResponseDto getOrderById(Long orderId) {

        OrderEntity order = orderRepository.findById(orderId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found."));

        List<OrderDetailEntity> orderDetails = orderDetailRepository.findAllByOrders(order);

        OrderResponseDto response = OrderResponseDto.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .description(order.getId() + " 번 상품 조회")
                .build();

        List<OrderResponseDto.OrderProducts> orderProducts = new ArrayList<>();

        for(OrderDetailEntity orderDetail : orderDetails){
            orderProducts.add(new OrderResponseDto.OrderProducts(orderDetail.getProduct_id(), orderDetail.getProduct_count()));
        }

        response.setOrderProducts(orderProducts);

        return response;
    }



     /*TODO 2. [상품 목록 조회 API]를 호출해서 파라미터로 받은 `product_id` 가 상품 목록에 존재하는지 검증
      TODO 3. 존재할경우 주문에 상품을 추가하고, 존재하지 않는다면 주문에 저장하지 않음.*/
}
