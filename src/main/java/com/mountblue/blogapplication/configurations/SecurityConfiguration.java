
package com.mountblue.blogapplication.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService getDetailsService(){
        return new CustomUserDetailsService();
    }

    @Bean
    public DaoAuthenticationProvider getAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(getDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)  throws Exception{
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(getAuthenticationProvider())
                .authorizeHttpRequests(request->request
                        .requestMatchers("/*","/css/**","/post/**","/search","/filter","/add-comment/**")
                        .permitAll()
                        .requestMatchers("/api/users/my-posts","/api/users/current")
                        .authenticated()
                        .requestMatchers("/api/posts/**","/api/comments/add/**","/api/tags/all","/api/users/*")
                        .permitAll()
                        .requestMatchers("/registration","/registration/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form-> form.loginPage("/login")
                    .usernameParameter("email")
                    .loginProcessingUrl("/").permitAll()
                 )
                .logout(LogoutConfigurer::permitAll);

        httpSecurity.httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }

}
