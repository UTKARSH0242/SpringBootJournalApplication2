package com.utkarsh.journalApp;

import com.utkarsh.journalApp.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendMail() {
        assertNotNull(emailService, "EmailService is not injected");
        assertNotNull(emailService.javaMailSender, "JavaMailSender is not injected");
        emailService.sendMail("adarsh.sri545@gmail.com",
                "Testing Java mail sender",
                "Hello Utkarsh, this is a test mail from Spring Boot");
    }
}
