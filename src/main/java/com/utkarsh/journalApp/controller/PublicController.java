package com.utkarsh.journalApp.controller;

import com.utkarsh.journalApp.entity.User;
import com.utkarsh.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String healthCheck(){
        return "You are fit and fine";
    }
    @PostMapping("create-user")
    public void createUser(@RequestBody User user) {
        userService.saveNewUser(user);
    }
}
