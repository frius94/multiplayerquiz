package com.semesterarbeit.quizgemini.handler;

import com.semesterarbeit.quizgemini.controller.QuizGameController;
import com.semesterarbeit.quizgemini.models.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class QuizSocketHandler extends TextWebSocketHandler {

    private final QuizGameController quizGameController;
    private static final Logger logger = LoggerFactory.getLogger(QuizSocketHandler.class);

    public QuizSocketHandler(QuizGameController quizGameController) {
        this.quizGameController = quizGameController;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("New player connected: {}", session.getId());
        Player player = new Player(session.getId());
        quizGameController.addPlayer(player);

        // Send initial game state to the new player (if needed)
        sendGameStatusUpdate(player);
    }

//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
//        logger.info("Message received from {}: {}", session.getId(), message.getPayload());
//        Player player = findPlayerBySession(session);
//        if (player != null) {
//            quizGameController.handleAnswer(player, message.getPayload());
//        } else {
//            logger.warn("Player not found for session: {}", session.getId());
//        }
//    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("Player disconnected: {}", session.getId());
        quizGameController.removePlayer(session);
    }

    private Player findPlayerBySession(WebSocketSession session) {
        return quizGameController.getPlayers().stream()
                .filter(p -> p.getSession().equals(session))
                .findFirst()
                .orElse(null);
    }

    private void sendGameStatusUpdate(Player player) {
        // ... Logic to send necessary game updates (questions, scores) to 'player'
    }
}


