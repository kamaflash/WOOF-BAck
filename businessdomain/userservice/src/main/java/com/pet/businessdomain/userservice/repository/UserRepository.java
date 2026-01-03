/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.userservice.repository;

import com.pet.businessdomain.userservice.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Pc
 */
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String username);
    Optional<User> findUserByUsernameOrEmail(String username,String email);
    List<User> findByAccountType(String accountType);
    Page<User> findByIdNotAndAccountType(Long id, String accountType, Pageable pageable);
    Page<User> findByAccountType(String accountType, Pageable pageable);
    boolean existsByUsername(String username);
    boolean existsByEmail(String username);
}
