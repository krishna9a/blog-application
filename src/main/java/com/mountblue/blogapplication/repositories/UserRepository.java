package com.mountblue.blogapplication.repositories;

import com.mountblue.blogapplication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {



    @Query("SELECT u.name FROM User u")
    List<String> findAllUserNames();

    User findByEmail(String email);
}
