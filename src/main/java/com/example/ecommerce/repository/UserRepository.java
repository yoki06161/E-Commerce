package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ecommerce.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
