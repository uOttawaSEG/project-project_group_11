package com.project.backend;

import com.google.firebase.auth.FirebaseAuth;
import com.project.data.model.RegistrationRequest;
import com.project.data.repositories.RegistrationRequestRepository;
import com.project.data.repositories.UserRepository;

public class App {

    // singleton instance
    private static final App instance = new App();

    // database stuff
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final UserRepository userRepository = new UserRepository();
    private final RegistrationRequestRepository requestRepository = new RegistrationRequestRepository();

    public void createUser(String email, String password, RegistrationRequest request) {

    }

    public void signInUser(String email, String password) {

    }

    public static App getInstance() {
        return instance;
    }
}
