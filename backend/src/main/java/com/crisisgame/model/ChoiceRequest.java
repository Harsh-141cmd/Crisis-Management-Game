package com.crisisgame.model;

public class ChoiceRequest {
    private String sessionId;
    private String choice;

    public String getSessionId() { return sessionId; }
    public String getChoice() { return choice; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setChoice(String choice) { this.choice = choice; }
}