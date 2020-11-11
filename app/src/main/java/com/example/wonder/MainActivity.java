package com.example.wonder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("ClickableViewAccessibility")
/**
 * MainActivity is the entry point to the application
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Rect rect;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog loginDialog;
    private AlertDialog signupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set window to fullscreen (will hide status bar)
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        updateUI(mAuth.getCurrentUser());

        // Play button
        final Button playBtn = findViewById(R.id.button_play);
        playBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Construct a rect of the view's bounds
                        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                        playBtn.setBackgroundResource(R.drawable.play_btn_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        playBtn.setBackgroundResource(R.drawable.play_btn);
                        // Go to play screen
                        startActivity(new Intent(MainActivity.this, PlayActivity.class));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                            // User moved outside bounds
                            playBtn.setBackgroundResource(R.drawable.play_btn);
                        }
                        break;
                }
                return false;
            }
        });

        // Login button
        final Button loginMainBtn = findViewById(R.id.button_loginmain);
        if (loginMainBtn != null) {
            loginMainBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Construct a rect of the view's bounds
                            rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                            loginMainBtn.setBackgroundResource(R.drawable.login_btn_pressed);
                            break;
                        case MotionEvent.ACTION_UP:
                            loginMainBtn.setBackgroundResource(R.drawable.login_btn);
                            createNewLoginDialog();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                                // User moved outside bounds
                                loginMainBtn.setBackgroundResource(R.drawable.login_btn);
                            }
                            break;
                    }
                    return false;
                }
            });
        }

        // Logout button
        final Button logoutMainBtn = findViewById(R.id.button_logoutmain);
        if (logoutMainBtn != null) {
            logoutMainBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Construct a rect of the view's bounds
                            rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                            logoutMainBtn.setBackgroundResource(R.drawable.logout_btn_pressed);
                            break;
                        case MotionEvent.ACTION_UP:
                            logoutMainBtn.setBackgroundResource(R.drawable.logout_btn);

                            // Sign out
                            FirebaseAuth.getInstance().signOut();
                            updateUI(mAuth.getCurrentUser());

                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                                // User moved outside bounds
                                logoutMainBtn.setBackgroundResource(R.drawable.logout_btn);
                            }
                            break;
                    }
                    return false;
                }
            });
        }

        // Settings button
        final Button settingsBtn = findViewById(R.id.button_settings);
        settingsBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Construct a rect of the view's bounds
                        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                        settingsBtn.setBackgroundResource(R.drawable.settings_btn_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        settingsBtn.setBackgroundResource(R.drawable.settings_btn);
                        // Go to settings screen
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                            // User moved outside bounds
                            settingsBtn.setBackgroundResource(R.drawable.settings_btn);
                        }
                        break;
                }
                return false;
            }
        });

        // Help button
        final Button helpBtn = findViewById(R.id.button_help);
        helpBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Construct a rect of the view's bounds
                        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                        helpBtn.setBackgroundResource(R.drawable.help_btn_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        helpBtn.setBackgroundResource(R.drawable.help_btn);
                        // Go to help screen
                        startActivity(new Intent(MainActivity.this, HelpActivity.class));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                            // User moved outside bounds
                            helpBtn.setBackgroundResource(R.drawable.help_btn);
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

        final Button loginMainBtn = findViewById(R.id.button_loginmain);
        final Button logoutMainBtn = findViewById(R.id.button_logoutmain);

        if (currentUser != null) {

            if (loginMainBtn != null) loginMainBtn.setVisibility(loginMainBtn.GONE);
            if (logoutMainBtn != null) logoutMainBtn.setVisibility(logoutMainBtn.VISIBLE);
        }
        else {

            if (loginMainBtn != null) loginMainBtn.setVisibility(loginMainBtn.VISIBLE);
            if (logoutMainBtn != null) logoutMainBtn.setVisibility(logoutMainBtn.GONE);
        }
    }

    // Builds & displays login popup
    public void createNewLoginDialog() {

        final Button loginMainBtn = findViewById(R.id.button_loginmain);
        final Button logoutMainBtn = findViewById(R.id.button_logoutmain);

        dialogBuilder = new AlertDialog.Builder(this );
        final View loginPopupView = getLayoutInflater().inflate(R.layout.popup_login, null);
        dialogBuilder.setView(loginPopupView);
        loginDialog = dialogBuilder.create();
        loginDialog.show();

        //Login button
        final Button loginBtn = loginPopupView.findViewById(R.id.button_login);
        loginBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Construct a rect of the view's bounds
                        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                        loginBtn.setBackgroundResource(R.drawable.login_btn_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        loginBtn.setBackgroundResource(R.drawable.login_btn);

                        // login user
                        login(loginPopupView);

                        // TODO: if logged in successfully, do function that includes:
                        //loginMainBtn.setVisibility(view.GONE);
                        //logoutMainBtn.setVisibility(view.VISIBLE);

                        // TODO: if failed to log in... (error msg)

                        loginDialog.dismiss();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                            // User moved outside bounds
                            loginBtn.setBackgroundResource(R.drawable.login_btn);
                        }
                        break;
                }
                return false;
            }
        });

        //Close button
        final Button closeBtn = loginPopupView.findViewById(R.id.button_closelogin);
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    loginMainBtn.setVisibility(view.VISIBLE);
                    logoutMainBtn.setVisibility(view.GONE);
                    loginDialog.dismiss();
                }
                return false;
            }
        });

        //Signup text press
        final TextView singupText = loginPopupView.findViewById(R.id.textView_signup);
        singupText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    createNewSignupDialog();
                    loginDialog.dismiss();
                }
                return false;
            }
        });
    }

    public void login(View view) {

        //Get email & password
        EditText emailEditText = view.findViewById(R.id.editText_email_login);
        EditText passwordEditText = view.findViewById(R.id.editText_password_login);

        if (emailEditText.getText().toString() == null || passwordEditText.getText().toString() == null) {

            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            updateUI(null);
        }

        //Sign in user with the email & password
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LogIn", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else {

                            // If sign in fails, display a message to the user.
                            Log.w("LogIn", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    // Builds & displays signup popup
    public void createNewSignupDialog() {

        final Button loginMainBtn = findViewById(R.id.button_loginmain);
        final Button logoutMainBtn = findViewById(R.id.button_logoutmain);

        dialogBuilder = new AlertDialog.Builder(this );
        final View signupPopupView = getLayoutInflater().inflate(R.layout.popup_signup, null);
        dialogBuilder.setView(signupPopupView);
        signupDialog = dialogBuilder.create();
        signupDialog.show();

        //Login button
        final Button signupBtn = signupPopupView.findViewById(R.id.button_signup);
        signupBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Construct a rect of the view's bounds
                        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                        signupBtn.setBackgroundResource(R.drawable.login_btn_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        signupBtn.setBackgroundResource(R.drawable.login_btn);

                        // signup user
                        signup(signupPopupView);

                        // TODO: if signed up successfully, do function that includes:
                        //loginMainBtn.setVisibility(view.GONE);
                        //logoutMainBtn.setVisibility(view.VISIBLE);

                        // TODO: if failed to sign up... (error msg)

                        signupDialog.dismiss();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                            // User moved outside bounds
                            signupBtn.setBackgroundResource(R.drawable.login_btn);
                        }
                        break;
                }
                return false;
            }
        });

        //Close button
        final Button closeBtn = signupPopupView.findViewById(R.id.button_closesignup);
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    loginMainBtn.setVisibility(view.VISIBLE);
                    logoutMainBtn.setVisibility(view.GONE);
                    signupDialog.dismiss();
                }
                return false;
            }
        });
    }

    public void signup(final View view) {

        //Get email & password
        EditText emailEditText = view.findViewById(R.id.editText_email_signup);
        EditText passwordEditText = view.findViewById(R.id.editText_password_signup);

        if (emailEditText.getText().toString() == null || passwordEditText.getText().toString() == null) {

            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            updateUI(null);
        }

        //Create new user with the email & password
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's inf
                            Log.d("SignIn", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else {

                            // If sign in fails, display a message to the user.
                            Log.w("SignIn", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}