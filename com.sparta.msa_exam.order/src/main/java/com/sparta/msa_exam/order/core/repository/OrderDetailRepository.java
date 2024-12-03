package com.sparta.msa_exam.order.core.repository;

import com.sparta.msa_exam.order.core.OrderDetailEntity;
import com.sparta.msa_exam.order.core.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

    List<OrderDetailEntity> findAllByOrders(OrderEntity orders);

}
