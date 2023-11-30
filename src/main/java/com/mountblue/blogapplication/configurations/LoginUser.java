package com.mountblue.blogapplication.configurations;

import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LoginUser {

    private final UserService userService;

    @Autowired
    public LoginUser(UserService userService) {
        this.userService = userService;
    }

    public  User getDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        User user= userService.findUserByEmail(email);
        return user;
    }
}
