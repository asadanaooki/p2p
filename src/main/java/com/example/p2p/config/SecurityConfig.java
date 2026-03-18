package com.example.p2p.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // http
        // .formLogin(form -> form
        // .loginPage("/login")
        // .loginProcessingUrl("/login")
        // .defaultSuccessUrl("/test", true)
        // .failureHandler((req, res, exp) -> {
        // String u = req.getParameter("username");
        // req.getSession().setAttribute("LAST_LOGIN_USERNAME", u);
        // res.sendRedirect("/login?error");
        // })
        // )
        // .authorizeHttpRequests(auth -> auth
        // .requestMatchers("/login","/css/**").permitAll()
        // .anyRequest().authenticated()
        // );
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
