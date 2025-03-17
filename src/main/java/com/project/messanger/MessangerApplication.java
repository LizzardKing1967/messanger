package com.project.messanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class MessangerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessangerApplication.class, args);
	}


}

