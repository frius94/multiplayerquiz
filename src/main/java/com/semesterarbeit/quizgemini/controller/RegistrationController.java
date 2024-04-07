package com.semesterarbeit.quizgemini.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    MessageController messageController;

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);


    @PostMapping("/register")
    public void registerUser(HttpServletRequest request, @RequestBody String jsonUserAndPassword) {
        logger.error(request.getSession().getId());
        logger.error(jsonUserAndPassword);
        JSONObject userJson = new JSONObject(jsonUserAndPassword);
        String username = userJson.getString("username");
//        String password = userJson.getString("password");
        logger.error("username in register" + username);
        if (!MessageController.allUsers.contains(username)) {
            MessageController.allUsers.add(username);
        }

//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("USER"));
//        User user = new User(username, password, true, true, true, true, authorities);
//        inMemoryUserDetailsManager.createUser(user);
//        sessionRegistry.registerNewSession(request.getSession().getId(), user);
        messageController.activePlayers();
    }
}
