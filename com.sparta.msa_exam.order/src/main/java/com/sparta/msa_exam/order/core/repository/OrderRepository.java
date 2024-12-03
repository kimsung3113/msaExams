package com.sparta.msa_exam.order.core.repository;

import com.sparta.msa_exam.order.core.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByIdAndUserId(Long id, Long userId);

}
