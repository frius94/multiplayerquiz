package com.semesterarbeit.quizgemini.models;

import java.security.Principal;

public final class User implements Principal {

    private final String name;
    private final String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}