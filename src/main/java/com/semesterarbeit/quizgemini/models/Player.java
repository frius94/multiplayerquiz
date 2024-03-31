package com.semesterarbeit.quizgemini.models;

import org.springframework.web.socket.WebSocketSession;

public class Player {
    private String name;
    private int score;
    private WebSocketSession session; // Assuming you're using WebSockets

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    // Alternate constructor if directly associated with the session
    public Player(WebSocketSession session) {
        this.session = session;
        this.score = 0;
        // You might need to generate a name/identifier based on session data
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }
}

