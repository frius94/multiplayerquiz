package com.semesterarbeit.quiz.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    QuizGameController quizGameController;

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);


    @PostMapping("/register")
    public void registerUser(HttpServletRequest request, @RequestBody String jsonUserAndPassword) {
        logger.error(request.getSession().getId());
        logger.error(jsonUserAndPassword);
        JSONObject userJson = new JSONObject(jsonUserAndPassword);
        String username = userJson.getString("username");
//        String password = userJson.getString("password");
        logger.error("username in register" + username);
        if (!QuizGameController.allUsers.contains(username)) {
            QuizGameController.allUsers.add(username);
        }
        quizGameController.activePlayers();
    }
}
