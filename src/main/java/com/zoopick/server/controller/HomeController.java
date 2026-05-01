package com.zoopick.server.controller;

import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@NullMarked
public class HomeController {
    @GetMapping("/")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
