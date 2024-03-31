package com.semesterarbeit.quizgemini.controller;

import com.semesterarbeit.quizgemini.models.Player;
import com.semesterarbeit.quizgemini.models.Question;
import com.semesterarbeit.quizgemini.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;


@Controller
public class QuizGameController {
    private List<Question> questions;
    private List<Player> players;
    private int currentQuestionIndex;

    @Autowired
    private QuestionRepository questionRepository;


    private final SimpMessagingTemplate messagingTemplate;

    public QuizGameController(SimpMessagingTemplate messagingTemplate) {
        questions = new ArrayList<>();
        players = new ArrayList<>();
        currentQuestionIndex = 0;
        this.messagingTemplate = messagingTemplate;
    }


    public void loadQuestions(String filePath) {
        // ... (Implementation from previous examples) ...
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(WebSocketSession session) {
        players.removeIf(player -> player.getSession().equals(session));
        broadcastPlayerListUpdate(); // Update all clients about the new player list
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void startGame() {
        Collections.shuffle(questions);
        currentQuestionIndex = 0;
//        sendNextQuestion();
    }

//    public void handleAnswer(Player player, String answer) {
//        Question currentQuestion = questions.get(currentQuestionIndex);
//        if (currentQuestion.getCorrectAnswer().equals(answer)) {
//            player.setScore(player.getScore() + 1);
//            broadcastMessage(player.getName() + " answered correctly!");
//        } else {
//            broadcastMessage(player.getName() + " answered incorrectly");
//        }
//        sendNextQuestion();
//    }

//    @GetMapping("nextQuestion")
//    private void sendNextQuestion() {
//        Question question = questionRepository.findRandomQuestion();
//
//        if (currentQuestionIndex < questions.size()) {
//            broadcastQuestion(questions.get(currentQuestionIndex));
//            currentQuestionIndex++;
//        } else {
//            endGame();
//        }
//    }

    private void endGame() {
        // ... Determine winners, etc.
        broadcastMessage("Game Over!");
    }

    private void broadcastQuestion(Question question) {
        messagingTemplate.convertAndSend("/topic/questions", question);
    }

    private void broadcastMessage(String message) {
        messagingTemplate.convertAndSend("/topic/messages", message);
    }

    private void broadcastPlayerListUpdate() {
        // ... Send the updated list of players to all clients
    }
}



