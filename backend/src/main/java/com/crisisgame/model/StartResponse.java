package com.crisisgame.model;

import java.util.List;

public class StartResponse {
    public String sessionId;
    public int turn;
    public String narrative;
    public List<String> options;

    public StartResponse(String sessionId, int turn, String narrative, List<String> options) {
        this.sessionId = sessionId;
        this.turn = turn;
        this.narrative = narrative;
        this.options = options;
    }
}