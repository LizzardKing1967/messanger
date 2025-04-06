package com.project.messanger.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index"; // Перенаправление на /index
    }

    @GetMapping("/index")
    public String showIndex() {
        return "index"; // Возвращает index.html из templates
    }
}