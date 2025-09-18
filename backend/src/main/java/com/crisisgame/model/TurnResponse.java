package com.crisisgame.model;

import java.util.List;

public class TurnResponse {
    public String sessionId;
    public int turn;
    public String narrative;
    public List<String> options;
    public boolean gameOver;
    public String outcome;
    public String careerResult;
    public String strengths;
    public String improvements;   
    public String leadershipStyle;
    public String crisisTheory;
    public String imageUrl;
    public int performanceScore; // New field for calculated score

    public static TurnResponse ongoing(String sessionId, int turn, String narrative, List<String> options) {
        TurnResponse r = new TurnResponse();
        r.sessionId = sessionId;
        r.turn = turn;
        r.narrative = narrative;
        r.options = options;
        r.gameOver = false;
        return r;
    }

    public static TurnResponse finished(String sessionId, int turn, String narrative,
                                        String outcome, String careerResult, String strengths,
                                        String improvements, String leadershipStyle, String crisisTheory,
                                        String imageUrl, int performanceScore) {
        TurnResponse r = new TurnResponse();
        r.sessionId = sessionId;
        r.turn = turn;
        r.narrative = narrative;
        r.gameOver = true;
        r.outcome = outcome;
        r.careerResult = careerResult;
        r.strengths = strengths;
        r.improvements = improvements;
        r.leadershipStyle = leadershipStyle;
        r.crisisTheory = crisisTheory;
        r.imageUrl = imageUrl;
        r.performanceScore = performanceScore;
        return r;
    }
}