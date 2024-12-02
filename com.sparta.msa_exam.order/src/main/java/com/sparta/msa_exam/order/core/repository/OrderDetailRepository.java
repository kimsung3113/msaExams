package com.sparta.msa_exam.order.core.repository;

import com.sparta.msa_exam.order.core.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
}
