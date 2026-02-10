package com.utkarsh.journalApp.scheduler;

import com.utkarsh.journalApp.cache.AppCache;
import com.utkarsh.journalApp.entity.JournalEntry;
import com.utkarsh.journalApp.entity.User;
import com.utkarsh.journalApp.enums.Sentiment;
import com.utkarsh.journalApp.repository.UserRepositoryImpl;
import com.utkarsh.journalApp.service.EmailService;
import com.utkarsh.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserSchedular {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AppCache appCache;

    @Scheduled(cron = "0 0 9 * * SUN")
    public void fetchUsersAndSendSaMail() {
        List<User> users = userRepository.getUserforSA();
        for (User user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream()
                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
                    .map(x -> x.getSentiment()).collect(Collectors.toList());
            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                if (sentiment != null)
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }
            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }

            if (mostFrequentSentiment != null) {
                String sentimentMessage = "Neutral 😐";
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

                emailService.sendMail(user.getEmail(), "Your Weekly Emotional Summary",
                        "Sentiment for previous week: " + sentimentMessage);

                // Log the email
                if (user.getEmailLogEntries() == null) {
                    user.setEmailLogEntries(new java.util.ArrayList<>());
                }
                user.getEmailLogEntries().add(new com.utkarsh.journalApp.entity.EmailLog(LocalDateTime.now(),
                        sentimentMessage, user.getEmail()));
                userService.saveUser(user);
            }
        }
    }

    @Scheduled(cron = "0 0/10 * ? * *")
    public void clearAppCache() {
        appCache.init();
    }

}