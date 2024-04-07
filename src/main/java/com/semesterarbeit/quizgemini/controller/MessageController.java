package com.semesterarbeit.quizgemini.controller;

import com.semesterarbeit.quizgemini.configuration.MessageConverterConfig;
import com.semesterarbeit.quizgemini.models.Message;
import com.semesterarbeit.quizgemini.models.Question;
import com.semesterarbeit.quizgemini.repositories.QuestionRepository;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MessageController {

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

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    // Mapped as /app/application
    @MessageMapping("/application")
    @SendTo("/all/messages")
    public Message send(final Message message) throws Exception {
        return message;
    }

//    @MessageMapping("/submitOption") //send in sockjs
//    @SendTo("/all/result") //subscribe in sockjs
//    public Boolean submitOption(final String question, final String option) throws Exception {
//        JSONObject questionObj = new JSONObject(question);
//        JSONObject optionObj = new JSONObject(option);
//        Optional<Question> result = questionRepository.getResult(questionObj.getString("question"), optionObj.getString("option"));
//        return result.isPresent();
//    }

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

//    @MessageMapping("/private") //send in sockjs
//    public void submitOptionPrivate(final String question, final String option, final String username) throws Exception {
//        JSONObject questionObj = new JSONObject(question);
//        JSONObject optionObj = new JSONObject(option);
//        JSONObject usernameObj = new JSONObject(username);
//
//        pendingUsers.remove(usernameObj.getString("username"));
//        if (pendingUsers.isEmpty()) {
//            readyForNextQuestion();
//        }
//
//        logger.error("Extracted username: {}", usernameObj.getString("username"));
//        logger.error("allUsers simpUserRegistry: {}", simpUserRegistry.getUsers());
//        logger.error("allUsers sessionRegistry: {}", sessionRegistry.getAllPrincipals());
//        logger.error("allUsers inMemoryUserDetailsManager exists: {}", inMemoryUserDetailsManager.userExists(usernameObj.getString("username")));
//
//
//        Optional<Question> result = questionRepository.getResult(questionObj.getString("question"), optionObj.getString("option"));
//        simpMessagingTemplate.convertAndSendToUser(usernameObj.getString("username"), "/specific", result.isPresent());
//        logger.error("Message sent to user '{}': {}", usernameObj.getString("username"), result.isPresent());
//    }

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

        logger.error("pendingUsers after {}", pendingUsers.toString());

        logger.error("Extracted username: {}", usernameObj.getString("username"));
        logger.error("allUsers simpUserRegistry: {}", simpUserRegistry.getUsers());
        logger.error("allUsers sessionRegistry: {}", sessionRegistry.getAllPrincipals());
        logger.error("allUsers inMemoryUserDetailsManager exists: {}", inMemoryUserDetailsManager.userExists(usernameObj.getString("username")));


        Optional<Question> result = questionRepository.getResult(questionObj.getString("question"), optionObj.getString("option"));
//        JSONArray jsonArray = new JSONArray();
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("username", usernameObj.getString("username"));
        jsonResult.put("response", String.valueOf(result.isPresent()));
//        jsonArray.put(jsonResult);

        return jsonResult.toString();
//        logger.error("Message sent to user '{}': {}", usernameObj.getString("username"), result.isPresent());
    }

//    public List<String> getLoggedInUsers() {
//        return sessionRegistry.getAllPrincipals().stream()
//                .filter(p -> p instanceof UserDetails) // Filter for your user type
//                .map(UserDetails.class::cast)
//                .map(UserDetails::getUsername)
//                .toList();
//    }
}
