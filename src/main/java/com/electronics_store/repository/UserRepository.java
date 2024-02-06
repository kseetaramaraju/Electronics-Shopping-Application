package com.electronics_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.electronics_store.entity.User;

@Repository
public interface UserRepository extends JpaRepository<Integer,User> {

}
