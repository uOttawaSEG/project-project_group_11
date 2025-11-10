package com.project.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.project.backend.RegisterResult;
import com.project.data.model.RegistrationRequest;
import com.project.data.repositories.RegistrationRequestRepository;
import com.project.data.repositories.UserRepository;

public class RegisterViewModel extends ViewModel {

    // database stuff
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final UserRepository userRepository = new UserRepository();
    private final RegistrationRequestRepository requestRepository = new RegistrationRequestRepository();

    // observable data
    private final MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();

    public void createUser(String email, String password, RegistrationRequest request) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userID = authResult.getUser().getUid();

                    request.setUserId(userID);

                    requestRepository.createRequest(request)
                            .addOnSuccessListener(v -> {
                                postRegisterSuccess("Registration submitted. Awaiting administrator approval.");
                            })
                            .addOnFailureListener(error -> {
                                Log.w("Firestore", "Error creating registration request", error);
                                postRegisterFailure("Error submitting registration");
                            });
                })
                .addOnFailureListener(error -> {
                    Log.w("Auth", "User creation failed", error);
                    postRegisterFailure("Registration failed");
                });
    }

    public LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    private void postRegisterSuccess(String message) {
        registerResult.postValue(new RegisterResult(RegisterResult.SUCCESS, message));
    }

    private void postRegisterFailure(String message) {
        registerResult.postValue(new RegisterResult(RegisterResult.FAILURE, message));
    }
}
