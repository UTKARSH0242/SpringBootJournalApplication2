package com.utkarsh.journalApp.controller;

import com.utkarsh.journalApp.entity.User;
import com.utkarsh.journalApp.service.EmailService;
import com.utkarsh.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @PostMapping("/trigger")
    public ResponseEntity<?> triggerEmail() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByUsername(userName);

            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            String subject = "Your Journal Summary";
            String body = "Here is a summary of your recent journal activity. Keep writing!";

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendMail(user.getEmail(), subject, body);

                // Log the email
                if (user.getEmailLogs() == null) {
                    user.setEmailLogs(new java.util.ArrayList<>());
                }
                user.getEmailLogs().add(LocalDateTime.now());
                userService.saveNewUser(user);

                return new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User email not found", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (e.getCause() != null) {
                errorMsg += " | Cause: " + e.getCause().getMessage();
            }
            return new ResponseEntity<>("Failed to send email: " + errorMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
