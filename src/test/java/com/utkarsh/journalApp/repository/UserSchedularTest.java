package com.utkarsh.journalApp.repository;

import com.utkarsh.journalApp.scheduler.UserSchedular;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserSchedularTest {

    @Autowired
    private UserSchedular userSchedular;

    @Test
    public void testFetchUsersAndSendSaMail() {
        userSchedular.fetchUsersAndSendSaMail();
    }
}
