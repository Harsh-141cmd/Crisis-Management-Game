package com.crisisgame.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final String sessionId;
    private final PlayerProfile player;
    private int turn;
    private final List<String> messagesHistory;
    private final List<String> choiceHistory;
    private boolean finished;

    public GameState(String sessionId, PlayerProfile player) {
        this.sessionId = sessionId;
        this.player = player;
        this.turn = 1;
        this.messagesHistory = new ArrayList<>();
        this.choiceHistory = new ArrayList<>();
        this.finished = false;
    }

    public String getSessionId() { return sessionId; }
    public PlayerProfile getPlayer() { return player; }
    public int getTurn() { return turn; }
    public void nextTurn() { this.turn++; }
    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }
    public List<String> getMessagesHistory() { return messagesHistory; }
    public List<String> getChoiceHistory() { return choiceHistory; }
    public void addChoice(String choice) { this.choiceHistory.add(choice); }
}