//package com.project.messanger.controllers;
//
//import com.project.messanger.utils.Birt;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.Date;
//
//@Controller
//public class ReportController {
//    @Autowired
//    private Birt birt;
//    @GetMapping("/report-users")
//    public String report() {
//        return "report-users";
//    }
//    @PostMapping("/report")
//    public void generateUserReport(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
//            HttpServletResponse response,
//            HttpServletRequest request) {
//
//        birt.generateUsersByDateReport(startDate, endDate, response, request);
//    }
//}