package com.example.demo.repository;

import com.example.demo.entity.OrderDetailEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderDetailRepository extends CrudRepository<OrderDetailEntity, Integer> {
    List<OrderDetailEntity> findByOrdersEntityId(int orderId);
}
