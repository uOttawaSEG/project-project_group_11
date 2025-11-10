package com.project.backend;

import com.project.data.model.User;

public class LoginResult {
    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_FAILURE = 1;

    private final int type;
    private final User user;
    private final String message;

    public LoginResult(int type, User user, String message) {
        this.type = type;
        this.user = user;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}