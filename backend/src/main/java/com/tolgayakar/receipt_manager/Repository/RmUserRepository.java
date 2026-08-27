package com.tolgayakar.receipt_manager.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tolgayakar.receipt_manager.Model.RmUser;

public interface RmUserRepository extends JpaRepository<RmUser, Long> { 
    Optional<RmUser> findByEmail(String email);
}
