package com.project.messanger.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ParticipantsManagerController {
    @GetMapping("/participants")
    public String home() {
        return "chatParticipantsManager";
    }
}