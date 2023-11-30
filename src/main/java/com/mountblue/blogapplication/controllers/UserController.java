package com.mountblue.blogapplication.controllers;

import com.mountblue.blogapplication.configurations.LoginUser;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final LoginUser loginUser;
    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, LoginUser loginUser) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.loginUser = loginUser;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user",new User());
        return "register";
    }

    @PostMapping("/registration")
    public String saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.SaveNewUser(user);
        return "redirect:/login";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/my-posts")
    public String userPosts(Model model){
            User currentUser= loginUser.getDetails();
            List<Post> posts=currentUser.getPosts();
            model.addAttribute("user", currentUser);
            model.addAttribute("sort", "desc");
            model.addAttribute("posts", posts);
            model.addAttribute("currentPage", 0);
            model.addAttribute("url", "/my-posts");
            return "home";
        }

}
