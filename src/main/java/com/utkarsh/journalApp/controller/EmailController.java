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

            // Calculate overall sentiment
            java.util.Map<com.utkarsh.journalApp.enums.Sentiment, Integer> sentimentCounts = new java.util.HashMap<>();
            if (user.getJournalEntries() != null) {
                for (com.utkarsh.journalApp.entity.JournalEntry entry : user.getJournalEntries()) {
                    if (entry.getSentiment() != null) {
                        sentimentCounts.put(entry.getSentiment(),
                                sentimentCounts.getOrDefault(entry.getSentiment(), 0) + 1);
                    }
                }
            }

            com.utkarsh.journalApp.enums.Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (java.util.Map.Entry<com.utkarsh.journalApp.enums.Sentiment, Integer> entry : sentimentCounts
                    .entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }

            String sentimentMessage = "Neutral 😐";
            if (mostFrequentSentiment != null) {
                switch (mostFrequentSentiment) {
                    case HAPPY:
                        sentimentMessage = "Happy 😃";
                        break;
                    case SAD:
                        sentimentMessage = "Sad 😢";
                        break;
                    case ANGRY:
                        sentimentMessage = "Angry 😠";
                        break;
                    case ANXIOUS:
                        sentimentMessage = "Anxious 😰";
                        break;
                    case NEUTRAL:
                        sentimentMessage = "Neutral 😐";
                        break;
                }
            }

            String subject = "Your Weekly Emotional Summary";
            String body = "Hello " + user.getUsername() + ",\n\n" +
                    "Based on your recent journal entries, your overall emotion is: " + sentimentMessage + "\n\n" +
                    "Keep journaling to track your mood!\n\n" +
                    "Best,\n" +
                    "Journal App Team";

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendMail(user.getEmail(), subject, body);

                // Log the email
                if (user.getEmailLogs() == null) {
                    user.setEmailLogs(new java.util.ArrayList<>());
                }
                user.getEmailLogs().add(LocalDateTime.now());
                userService.saveUser(user); // Fixed: Use saveUser instead of saveNewUser

                return new ResponseEntity<>("Email sent successfully with sentiment: " + sentimentMessage,
                        HttpStatus.OK);
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
