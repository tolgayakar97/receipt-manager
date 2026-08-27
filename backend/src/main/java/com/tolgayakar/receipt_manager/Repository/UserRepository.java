package com.tolgayakar.receipt_manager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tolgayakar.receipt_manager.Model.User;

public interface UserRepository extends JpaRepository<User, Long> { }
