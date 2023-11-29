package com.mountblue.blogapplication.services;

import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void SaveNewUser(User user) {
        user.setRole("USER");
        System.out.println(user.toString());
        userRepository.save(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByEmail(username);
    }


    public List<String> findAllNames() {
        return userRepository.findAllUserNames();
    }
}
