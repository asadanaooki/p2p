package com.example.p2p.controller.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLogin() {
        return "app/login";
    }

    @GetMapping("/test")
    public String test() {
        return "app/test";
    }

}
