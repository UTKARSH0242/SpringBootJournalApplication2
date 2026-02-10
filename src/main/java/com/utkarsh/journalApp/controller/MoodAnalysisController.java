package com.utkarsh.journalApp.controller;

import com.utkarsh.journalApp.entity.JournalEntry;
import com.utkarsh.journalApp.entity.User;
import com.utkarsh.journalApp.enums.Sentiment;
import com.utkarsh.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mood-analytics")
public class MoodAnalysisController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getMoodAnalytics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<JournalEntry> entries = user.getJournalEntries();
        Map<Sentiment, Integer> sentimentCounts = new HashMap<>();

        for (JournalEntry entry : entries) {
            if (entry.getSentiment() != null) {
                sentimentCounts.put(entry.getSentiment(), sentimentCounts.getOrDefault(entry.getSentiment(), 0) + 1);
            }
        }

        return new ResponseEntity<>(sentimentCounts, HttpStatus.OK);
    }
}
