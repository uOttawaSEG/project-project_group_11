package com.project.backend;

public class RegisterResult {
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;

    private final int type;
    private final String message;

    public RegisterResult(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
