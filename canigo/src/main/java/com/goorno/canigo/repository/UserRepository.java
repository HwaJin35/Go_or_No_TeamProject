package com.goorno.canigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
