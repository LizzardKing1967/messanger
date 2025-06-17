package com.project.messanger;

import com.project.messanger.config.JwtAuthenticationFilter;
import com.project.messanger.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(JwtAuthenticationFilterTest.Config.class)
public class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    // Регистрируем фильтр в тестовом контексте
    @TestConfiguration
    static class Config {
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter();
        }

        @Bean
        public SecurityConfig securityConfig() {
            return new SecurityConfig();
        }
    }

    @Test
    void jwtFilter_shouldRedirectToLoginPageOnInvalidToken() throws Exception {
        mockMvc.perform(get("/chats/all")
                        .header("Authorization", "Bearer INVALID.TOKEN.HERE"))
                .andExpect(status().isFound())  // 302 Found
                .andExpect(redirectedUrl("/auth")); // убедиться, что редирект на /auth
    }
}
