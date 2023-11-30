package com.mountblue.blogapplication.restapicontrollers;

import com.mountblue.blogapplication.configurations.LoginUser;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class RestUserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;
    private final LoginUser loginUser;
    @Autowired
    public RestUserController(UserService userService, PasswordEncoder passwordEncoder, LoginUser loginUser) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.loginUser = loginUser;
    }

    @GetMapping("/current")
    public User getCurrentUser() {
        return loginUser.getDetails();
    }
    @GetMapping("/registration")
    public User getRegistration(){
        return new User();
    }
    @PostMapping("/registration")
    public String saveUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.SaveNewUser(user);
        return "User registered successfully";
    }

    @GetMapping("/my-posts")
    public List<Post> getUserPosts(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "desc") String sort) {
        return getCurrentUser().getPosts();
    }

    @GetMapping("/authors")
    public List<String> getAuthorsNames() {
        return userService.findAllNames();
    }
}
