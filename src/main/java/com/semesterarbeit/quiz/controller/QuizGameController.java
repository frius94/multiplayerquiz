package com.semesterarbeit.quiz.controller;

import com.semesterarbeit.quiz.configuration.MessageConverterConfig;
import com.semesterarbeit.quiz.models.Question;
import com.semesterarbeit.quiz.repositories.QuestionRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class QuizGameController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    MessageConverterConfig messageConverterConfig;


    @Autowired
    InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    SimpUserRegistry simpUserRegistry;

    static List<String> pendingUsers = new ArrayList<>();
    static List<String> allUsers = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(QuizGameController.class);

    @MessageMapping("/question")
    @SendTo("/all/question")
    public Question sendQuestion() throws Exception {
        pendingUsers.addAll(allUsers);
        return questionRepository.findRandomQuestion();
    }

    @MessageMapping("/readyForNextQuestion")
    @SendTo("/all/readyForNextQuestion")
    public void readyForNextQuestion() throws Exception {
        simpMessagingTemplate.convertAndSend("/all/readyForNextQuestion", true);
    }

    @MessageMapping("/activePlayers")
    @SendTo("/all/activePlayers")
    public void activePlayers() {
        JSONArray allUsersJson = new JSONArray();
        allUsersJson.putAll(allUsers);
        simpMessagingTemplate.convertAndSend("/all/activePlayers", allUsersJson.toString());
    }

    @MessageMapping("/submitOption")
    @SendTo("/all/submitOption")
    public String submitOption(final String question, final String option, final String username) throws Exception {
        JSONObject questionObj = new JSONObject(question);
        JSONObject optionObj = new JSONObject(option);
        JSONObject usernameObj = new JSONObject(username);

        logger.error("pendingUsers before {}", pendingUsers.toString());

        pendingUsers.remove(usernameObj.getString("username"));
        if (pendingUsers.isEmpty()) {
            readyForNextQuestion();
        }

        Optional<Question> result = questionRepository.getResult(questionObj.getString("question"), optionObj.getString("option"));
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("username", usernameObj.getString("username"));
        jsonResult.put("response", String.valueOf(result.isPresent()));

        return jsonResult.toString();
    }
}
