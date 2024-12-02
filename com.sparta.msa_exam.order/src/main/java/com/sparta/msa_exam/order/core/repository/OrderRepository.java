package com.sparta.msa_exam.order.core.repository;

import com.sparta.msa_exam.order.core.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
