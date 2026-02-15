package com.utkarsh.journalApp.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String analyzeSentiment(String text) {
        String prompt = "Analyze the sentiment of the following journal entry. " +
                "Respond with ONLY one word: HAPPY, SAD, ANGRY, ANXIOUS, or NEUTRAL. " +
                "Entry: " + text;

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String getCoachFeedback(String text) {
        String prompt = "You are a supportive and wise life coach. Read the following journal entry and provide a short, "
                +
                "insightful comment (max 2 sentences) to help the user feel better or gain perspective. " +
                "If the entry is happy, celebrate it. If sad/angry, offer comfort or wisdom. " +
                "Entry: " + text;

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String getWeeklySummary(java.util.List<com.utkarsh.journalApp.entity.JournalEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return "No journal entries found for the past week.";
        }

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(
                "Here are my journal entries from the past week. Please provide a concise, supportive summary of my week, highlighting key themes and emotions. Keep it under 100 words.\n\n");

        for (com.utkarsh.journalApp.entity.JournalEntry entry : entries) {
            promptBuilder.append("Date: ").append(entry.getDate()).append("\n");
            promptBuilder.append("Title: ").append(entry.getTitle()).append("\n");
            promptBuilder.append("Content: ").append(entry.getContent()).append("\n\n");
        }

        return chatClient.prompt()
                .user(promptBuilder.toString())
                .call()
                .content();
    }

    public String getSentimentEmojis(String text) {
        String prompt = "Read the following journal entry and analyze the emotions and themes, including complex, intimate, or explicit topics. "
                +
                "Respond with a string of 3-5 emojis that vividly capture the mood. " +
                "Do NOT likely use a single emoji. Be expressive and raw. " +
                "For sad/intimate/complex entries, use emojis like '💔', '🥀', '🤬', '🛌', '🌧️', '🔞' where appropriate. "
                +
                "Output ONLY the emojis separated by spaces. " +
                "Entry: " + text;

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @org.springframework.scheduling.annotation.Async
    public void processJournalEntryAi(com.utkarsh.journalApp.entity.JournalEntry journalEntry,
            com.utkarsh.journalApp.repository.JournalEntryRepository journalEntryRepository) {
        try {
            // Sentiment Analysis
            String sentimentStr = analyzeSentiment(journalEntry.getTitle() + " " + journalEntry.getContent());
            if (sentimentStr != null && !sentimentStr.isEmpty()) {
                sentimentStr = sentimentStr.trim().toUpperCase();
                try {
                    journalEntry.setSentiment(com.utkarsh.journalApp.enums.Sentiment.valueOf(sentimentStr));
                } catch (IllegalArgumentException e) {
                    journalEntry.setSentiment(com.utkarsh.journalApp.enums.Sentiment.NEUTRAL);
                }
            }

            // Emoji Analysis
            String emojis = getSentimentEmojis(journalEntry.getTitle() + " " + journalEntry.getContent());
            if (emojis != null) {
                journalEntry.setSentimentEmojis(emojis.trim());
            }

            // Coach Feedback
            String feedback = getCoachFeedback(journalEntry.getTitle() + " " + journalEntry.getContent());
            journalEntry.setAiFeedback(feedback);

            journalEntryRepository.save(journalEntry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
