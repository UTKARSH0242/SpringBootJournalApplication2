package com.utkarsh.journalApp.controller;

import com.utkarsh.journalApp.entity.JournalEntry;
import com.utkarsh.journalApp.entity.User;
import com.utkarsh.journalApp.service.JournalEntryService;
import com.utkarsh.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @Autowired
    private org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUsername(userName);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        String key = "journal_entries:" + userName;
        List<JournalEntry> all = null;

        // 1. Try fetching from Redis
        try {
            String cachedData = redisTemplate.opsForValue().get(key);
            if (cachedData != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                all = mapper.readValue(cachedData,
                        new com.fasterxml.jackson.core.type.TypeReference<List<JournalEntry>>() {
                        });
                return new ResponseEntity<>(all, HttpStatus.OK);
            }
        } catch (Exception e) {
            // Redis error, fallback to DB
        }

        // 2. Fetch from DB
        all = user.getJournalEntries();

        // 3. Save to Redis
        if (all != null && !all.isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                String jsonString = mapper.writeValueAsString(all);
                redisTemplate.opsForValue().set(key, jsonString, 10, java.util.concurrent.TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.saveEntry(myEntry, userName);

            // Invalidate Cache
            try {
                redisTemplate.delete("journal_entries:" + userName);
            } catch (Exception e) {
            }

            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{myId}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable String myId) {
        try {
            ObjectId objectId = new ObjectId(myId);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByUsername(userName);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            List<JournalEntry> collect = user.getJournalEntries().stream()
                    .filter(x -> x.getId().equals(objectId))
                    .collect(Collectors.toList());
            if (!collect.isEmpty()) {
                Optional<JournalEntry> journalEntry = journalEntryService.findById(objectId);
                if (journalEntry.isPresent()) {
                    return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>("Journal entry not found", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid ObjectId", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable String myId) {
        try {
            ObjectId objectId = new ObjectId(myId);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            boolean removed = journalEntryService.deleteById(objectId, userName);
            if (removed) {
                // Invalidate Cache
                try {
                    redisTemplate.delete("journal_entries:" + userName);
                } catch (Exception e) {
                }

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>("Journal entry not found", HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid ObjectId", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<?> updateJournalById(@PathVariable String myId, @RequestBody JournalEntry newEntry) {
        try {
            ObjectId objectId = new ObjectId(myId);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByUsername(userName);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            List<JournalEntry> collect = user.getJournalEntries().stream()
                    .filter(x -> x.getId().equals(objectId))
                    .collect(Collectors.toList());
            if (!collect.isEmpty()) {
                Optional<JournalEntry> journalEntry = journalEntryService.findById(objectId);
                if (journalEntry.isPresent()) {
                    JournalEntry old = journalEntry.get();
                    old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty() ? newEntry.getTitle()
                            : old.getTitle());
                    old.setContent(
                            newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent()
                                    : old.getContent());
                    journalEntryService.saveEntry(old);

                    // Invalidate Cache
                    try {
                        redisTemplate.delete("journal_entries:" + userName);
                    } catch (Exception e) {
                    }

                    return new ResponseEntity<>(old, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>("Journal entry not found", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid ObjectId", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    private com.utkarsh.journalApp.service.GeminiService geminiService;

    @GetMapping("/weekly-summary")
    public ResponseEntity<?> getWeeklySummary() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUsername(userName);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Filter entries for the last 7 days
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<JournalEntry> recentEntries = user.getJournalEntries().stream()
                .filter(entry -> entry.getDate() != null && entry.getDate().isAfter(sevenDaysAgo))
                .collect(Collectors.toList());

        if (recentEntries.isEmpty()) {
            return new ResponseEntity<>(
                    java.util.Collections.singletonMap("summary", "No entries found for the last 7 days to summarize."),
                    HttpStatus.OK);
        }

        try {
            String summary = geminiService.getWeeklySummary(recentEntries);
            return new ResponseEntity<>(java.util.Collections.singletonMap("summary", summary), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating summary: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}