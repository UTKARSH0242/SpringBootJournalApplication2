package com.utkarsh.journalApp;

import com.utkarsh.journalApp.entity.User;
import com.utkarsh.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAdd(){
        assertEquals(4,2+2);
        User user = userRepository.findByUsername("Akanksha");
        assertTrue(!user.getJournalEntries().isEmpty());
    }
}
