package com.mountblue.blogapplication.configurations;


import com.mountblue.blogapplication.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;


public class CustomUser implements UserDetails {

    private final User user;

    public CustomUser(User user) {
        this.user = user;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return   Arrays.stream(user.getRole().split(","))
                .map(SimpleGrantedAuthority::new).toList();

    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}