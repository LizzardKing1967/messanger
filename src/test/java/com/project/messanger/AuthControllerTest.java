package com.project.messanger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void registerUser_success() throws Exception {
		String payload = """
            {
                "username": "testuser04",
                "password": "12345678",
                "email": "test4@example.com",
                "name": "TestName",
                "lastName": "TestName",
                "rsaPublicKey": "FAKE_KEY=="

				
                
            }
        """;


		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isOk());
	}

	@Test
	void loginUser_success() throws Exception {
		String payload = """
            {
                "username": "testuser04",
                "password": "12345678"
            }
        """;

		mockMvc.perform(post("/auth/authenticate")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(status().isOk())
				.andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
				.andExpect((ResultMatcher) jsonPath("$.token").exists())
				.andExpect((ResultMatcher) jsonPath("$.username").value("testuser04"));
	}
}