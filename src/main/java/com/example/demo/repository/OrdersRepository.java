package com.example.demo.repository;

import com.example.demo.entity.OrdersEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrdersRepository extends CrudRepository<OrdersEntity, Integer> {
}