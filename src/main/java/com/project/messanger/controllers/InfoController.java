package com.project.messanger.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Controller
public class InfoController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/db-info")
    public String getDatabaseInfo(Model model) {
        try (Connection connection = dataSource.getConnection()) {
            model.addAttribute("databaseName", connection.getMetaData().getDatabaseProductName());
            model.addAttribute("driverName", connection.getMetaData().getDriverName());
            model.addAttribute("url", connection.getMetaData().getURL());
            model.addAttribute("username", connection.getMetaData().getUserName());
        } catch (SQLException e) {
            model.addAttribute("error", "Failed to connect to the database: " + e.getMessage());
        }
        return "db-info"; // Имя шаблона Thymeleaf
    }


}

