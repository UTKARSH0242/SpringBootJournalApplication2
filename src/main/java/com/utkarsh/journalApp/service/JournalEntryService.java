package com.utkarsh.journalApp.service;

import com.utkarsh.journalApp.entity.JournalEntry;
import com.utkarsh.journalApp.entity.User;
import com.utkarsh.journalApp.repository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;
    @Autowired
    private GeminiService geminiService;

    private static final Logger logger = LoggerFactory.getLogger(JournalEntryService.class);

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            User user = userService.findByUsername(username);
            journalEntry.setDate(LocalDateTime.now());

            // AI Sentiment Analysis
            String sentimentStr = sentimentAnalysisService
                    .getSentiment(journalEntry.getTitle() + " " + journalEntry.getContent());
            try {
                if (sentimentStr != null && !sentimentStr.isEmpty()) {
                    journalEntry.setSentiment(com.utkarsh.journalApp.enums.Sentiment.valueOf(sentimentStr));
                }
                // AI Coach Feedback (Gemini)
                String feedback = geminiService
                        .getCoachFeedback(journalEntry.getTitle() + " " + journalEntry.getContent());
                journalEntry.setAiFeedback(feedback);
            } catch (Exception e) {
                log.warn("Error processing AI features for entry: " + e.getMessage());
            }

            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        } catch (Exception e) {
            log.error("Error while saving entry", e);
            throw new RuntimeException("Error while saving entry", e);
        }
    }

    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String username) {
        boolean removed = false;
        try {
            User user = userService.findByUsername(username);

            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveUser(user); // saveNewUser and saveUser
                journalEntryRepository.deleteById(id);
            } else {
                log.warn("Entry with ID {} not found in user {}'s journal entries.", id, username);
            }

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting the entry " + e);
        }

        return removed;
    }

}