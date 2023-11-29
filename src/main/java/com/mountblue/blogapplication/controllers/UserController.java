package com.mountblue.blogapplication.controllers;

import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class UserController {
    @Autowired
    private  UserService userService;

    @Autowired
    private  PasswordEncoder passwordEncoder;


    public  User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        System.out.println(username);
        User user= userService.findUserByUsername(username);
        return user;
    }

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user",new User());
        return "register";
    }

    @PostMapping("/registration")
    public String saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.SaveNewUser(user);
        return "login";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/my-posts")
    public String userBlogs(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "desc" )
                            String sort, Model model){

           List<Post> posts=getUser().getPosts();
            model.addAttribute("user", getUser());
            model.addAttribute("sort", sort);
            model.addAttribute("posts", posts);
            model.addAttribute("currentPage", page);
            model.addAttribute("url", "/my-posts");
            return "home";
        }


    public List<String> findAllAuthorsName() {
        return userService.findAllNames();
    }
}
