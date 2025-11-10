package com.project.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.backend.LoginResult;
import com.project.data.model.RegistrationRequest;
import com.project.data.model.User;
import com.project.data.repositories.RegistrationRequestRepository;
import com.project.data.repositories.UserRepository;

public class LoginViewModel extends ViewModel {

    // database stuff
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final UserRepository userRepository = new UserRepository();
    private final RegistrationRequestRepository requestRepository = new RegistrationRequestRepository();

    // observable data
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    private String userID;

    public void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(this::onAuthSuccess)
                .addOnFailureListener(this::onAuthFailure);
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    private void onAuthSuccess(AuthResult authResult) {
        userID = authResult.getUser().getUid();

        requestRepository.getRequest(userID)
                .addOnSuccessListener(this::onFetchRegistrationRequestSuccess)
                .addOnFailureListener(this::onFetchRegistrationRequestFailure);
    }

    private void onFetchRegistrationRequestSuccess(DocumentSnapshot document) {
        if (!document.exists()) {
            userRepository.getUserProfile(userID)
                    .addOnSuccessListener(this::onFetchUserSuccess)
                    .addOnFailureListener(this::onFetchUserFailure);

            return;
        }

        RegistrationRequest request = document.toObject(RegistrationRequest.class);

        if (request.getStatus().equals("approved")) {
            userRepository.getUserProfile(userID)
                    .addOnSuccessListener(this::onFetchUserSuccess)
                    .addOnFailureListener(this::onFetchUserFailure);
        }
        else if (request.getStatus().equals("rejected")) {
            postLoginFailure("Your registration was rejected. Please contact admin at 613-111-1111");
        }
        else if (request.getStatus().equals("pending")) {
            postLoginFailure("Your registration is awaiting administrator approval");
        }
    }

    private void onFetchUserSuccess(DocumentSnapshot document) {
        if (!document.exists()) {
            postLoginFailure("Profile not found in database");
            return;
        }

        User user = document.toObject(User.class);
        postLoginSuccess(user);
    }

    private void onAuthFailure(Exception error) {
        Log.w("Auth", "Login failed", error);
        postLoginFailure("Error authenticating account");
    }

    private void onFetchRegistrationRequestFailure(Exception error) {
        Log.w("Firestore", "Failed to fetch registration request", error);
        postLoginFailure("Error checking registration status");
    }

    private void onFetchUserFailure(Exception error) {
        Log.w("Firestore", "Failed to fetch user profile", error);
        postLoginFailure("Error loading profile");
    }

    private void postLoginSuccess(User user) {
        loginResult.postValue(new LoginResult(LoginResult.LOGIN_SUCCESS, user, null));
    }

    private void postLoginFailure(String message) {
        loginResult.postValue(new LoginResult(LoginResult.LOGIN_FAILURE, null, message));
    }

}
