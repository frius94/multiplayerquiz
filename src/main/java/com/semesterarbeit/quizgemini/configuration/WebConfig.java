package com.semesterarbeit.quizgemini.configuration;

import com.semesterarbeit.quizgemini.controller.QuizGameController;
import com.semesterarbeit.quizgemini.handler.QuizSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");

        registry.enableSimpleBroker("/all","/specific");

        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/quiz-websocket").setAllowedOriginPatterns("*").withSockJS();
    }

    @Bean
    public QuizGameController quizGame(SimpMessagingTemplate messagingTemplate) {
        return new QuizGameController(messagingTemplate);
    }

    @Bean
    public QuizSocketHandler quizSocketHandler(QuizGameController quizGameController) {
        return new QuizSocketHandler(quizGameController);
    }

    // ... Your other beans, if any: quizGame, quizSocketHandler ...
}


