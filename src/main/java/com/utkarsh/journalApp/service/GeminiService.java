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
}
