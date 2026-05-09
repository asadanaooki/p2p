package com.example.p2p.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
         http
         .formLogin(form -> form
         .loginPage("/login")
         .loginProcessingUrl("/login")
         .defaultSuccessUrl("/test", false)
         .failureHandler((req, res, exp) -> {
         String u = req.getParameter("username");
         req.getSession().setAttribute("LAST_LOGIN_USERNAME", u);
         res.sendRedirect("/login?error");
         })
         )
         .authorizeHttpRequests(auth -> auth
         .requestMatchers("/login",
                 "/account/initial-password-setup",
                 "/account/email-change/confirm",
                 "/css/**",
                 "/js/**"
                 ).permitAll()
         .requestMatchers("/setting/**").hasAuthority("管理者")
         .anyRequest().authenticated()
         );
//        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
