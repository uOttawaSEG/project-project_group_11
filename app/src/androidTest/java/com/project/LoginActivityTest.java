package com.project;

import static org.junit.Assert.assertTrue;

import android.widget.EditText;

import androidx.annotation.UiThread;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.project.ui.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> testRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    @UiThread
    public void loginIsValid() {
        testRule.getScenario().onActivity((activity) -> {
            EditText emailField = activity.findViewById(R.id.login_emailField);
            EditText passwordField = activity.findViewById(R.id.login_passwordField);

            emailField.setText("test@email.com");
            passwordField.setText("password");

            boolean result = activity.validateLoginForm(emailField.getText().toString(), passwordField.getText().toString());

            assertTrue(result);
        });
    }
}
