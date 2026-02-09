package com.utkarsh.journalApp.service;

import org.springframework.stereotype.Service;

@Service
public class SentimentAnalysisService {

    public String getSentiment(String text) {
        if (text == null)
            return "";
        String lower = text.toLowerCase();

        // 1. Check for specific negations first
        if (lower.contains("not good") || lower.contains("not happy") || lower.contains("not great")
                || lower.contains("not okay")) {
            return "SAD";
        }

        // 2. Check for ANGRY keywords
        if (lower.contains("angry") || lower.contains("hate") || lower.contains("mad")
                || lower.contains("furious") || lower.contains("annoyed") || lower.contains("irritated")
                || lower.contains("fight") || lower.contains("argument") || lower.contains("conflict")
                || lower.contains("frustrated") || lower.contains("stupid") || lower.contains("idiot")
                || lower.contains("rage") || lower.contains("resent")) {
            return "ANGRY";
        }

        // 3. Check for SAD keywords
        if (lower.contains("sad") || lower.contains("bad") || lower.contains("cry")
                || lower.contains("terrible") || lower.contains("unhappy") || lower.contains("depressed")
                || lower.contains("grief") || lower.contains("lonely") || lower.contains("worst")
                || lower.contains("awful") || lower.contains("horrible") || lower.contains("mistake")
                || lower.contains("fault") || lower.contains("regret") || lower.contains("fail")
                || lower.contains("failure") || lower.contains("miss") || lower.contains("hurt")
                || lower.contains("pain") || lower.contains("sorry") || lower.contains("guilt")
                || lower.contains("disaster") || lower.contains("ruined") || lower.contains("fucked")) {
            return "SAD";
        }

        // 4. Check for ANXIOUS keywords
        if (lower.contains("anxious") || lower.contains("worried") || lower.contains("nervous")
                || lower.contains("scared") || lower.contains("fear") || lower.contains("stressed")
                || lower.contains("panic") || lower.contains("tension") || lower.contains("pressure")
                || lower.contains("uneasy") || lower.contains("dread")) {
            return "ANXIOUS";
        }

        // 5. Check for HAPPY keywords (Last, so negative context overrides it)
        if (lower.contains("happy") || lower.contains("good") || lower.contains("great")
                || lower.contains("love") || lower.contains("excited") || lower.contains("joy")
                || lower.contains("amazing") || lower.contains("fantastic") || lower.contains("wonderful")
                || lower.contains("excellent") || lower.contains("best") || lower.contains("blessed")
                || lower.contains("awesome") || lower.contains("cool") || lower.contains("nice")) {
            return "HAPPY";
        }

        return "NEUTRAL"; // Default when no strong emotion is detected
    }
}
