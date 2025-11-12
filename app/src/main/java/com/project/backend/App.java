package com.project.backend;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.data.model.RegistrationRequest;
import com.project.data.repositories.RegistrationRequestRepository;
import com.project.data.repositories.UserRepository;

//TODO: this does nothing for now - it is not used
public class App {

    // singleton instance
    private static final App instance = new App();

    // database stuff
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final UserRepository userRepository = new UserRepository();
    private final RegistrationRequestRepository requestRepository = new RegistrationRequestRepository();

    private String userID;

    public String createUser(String email, String password, RegistrationRequest request) {
        return "";
    }

    public String signInUser(String email, String password) {
        Task<AuthResult> task = auth.signInWithEmailAndPassword(email, password);

        try {
            AuthResult result = Tasks.await(task);
            FirebaseUser fbUser = result.getUser();

            if (fbUser == null) {
                return null;
            }

            return fbUser.getUid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static App getInstance() {
        return instance;
    }
}
