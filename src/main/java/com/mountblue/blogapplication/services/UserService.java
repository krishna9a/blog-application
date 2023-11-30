package com.mountblue.blogapplication.services;

import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void SaveNewUser(User user) {
        user.setRole("USER");
        System.out.println(user.toString());
        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<String> findAllNames() {
        return userRepository.findAllUserNames();
    }
}
