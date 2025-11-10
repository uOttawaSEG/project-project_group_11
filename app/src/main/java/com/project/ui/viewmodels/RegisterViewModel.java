package com.project.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.project.backend.RegisterResult;
import com.project.data.repositories.RegistrationRequestRepository;
import com.project.data.repositories.UserRepository;

public class RegisterViewModel extends ViewModel {

    // database stuff
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final UserRepository userRepository = new UserRepository();
    private final RegistrationRequestRepository requestRepository = new RegistrationRequestRepository();

    // observable data
    private final MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();


}
