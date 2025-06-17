package com.project.messanger;

import com.project.messanger.service.MessageService;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class MessageServiceTest {

    @Autowired
    MessageService messageService;

    @Test
    void shouldInsertMessage() {
        var saved = messageService.sendTextMessage("AlexRage", "Группа 143", "enc-text");
        assertThat(saved).isNotNull();
    }
}
