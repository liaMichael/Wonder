package com.example.wonder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("ClickableViewAccessibility")
/**
 * MainActivity is the entry point to the application
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Rect rect;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog logInDialog;
    private AlertDialog signUpDialog;
    private boolean loggedIn;

    // Buttons
    private Button playBtn;
    private Button logInBtn;
    private Button logOutBtn;
    private Button settingsBtn;
    private Button helpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Buttons
        playBtn = findViewById(R.id.button_play);
        logInBtn = findViewById(R.id.button_loginmain);
        logOutBtn = findViewById(R.id.button_logoutmain);
        settingsBtn = findViewById(R.id.button_settings);
        helpBtn = findViewById(R.id.button_help);

        // Set window to fullscreen (will hide status bar)
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        updateUI(mAuth.getCurrentUser());

        // Play button
        playBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (loggedIn) {
                    onButtonClick(playBtn, "play_btn", event, PlayActivity.class);
                } else {
                    onButtonClick(playBtn, "play_btn", event, null);
                    Toast.makeText(MainActivity.this, "You are logged out. Please log in.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        // Settings button
        settingsBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                onButtonClick(settingsBtn, "settings_btn", event, SettingsActivity.class);
                return false;
            }
        });

        // Help button
        helpBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                onButtonClick(helpBtn, "help_btn", event, HelpActivity.class);
                return false;
            }
        });

        // Log in button
        if (logInBtn != null) {
            logInBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Construct a rect of the view's bounds
                            rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                            logInBtn.setBackgroundResource(R.drawable.login_btn_pressed);
                            break;
                        case MotionEvent.ACTION_UP:
                            logInBtn.setBackgroundResource(R.drawable.login_btn);
                            createNewLogInDialog();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                                // User moved outside bounds
                                logInBtn.setBackgroundResource(R.drawable.login_btn);
                            }
                            break;
                    }
                    return false;
                }
            });
        }

        // Log out button
        if (logOutBtn != null) {
            logOutBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Construct a rect of the view's bounds
                            rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                            logOutBtn.setBackgroundResource(R.drawable.logout_btn_pressed);
                            break;
                        case MotionEvent.ACTION_UP:
                            logOutBtn.setBackgroundResource(R.drawable.logout_btn);

                            // Sign out
                            FirebaseAuth.getInstance().signOut();
                            updateUI(mAuth.getCurrentUser());
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                                // User moved outside bounds
                                logOutBtn.setBackgroundResource(R.drawable.logout_btn);
                            }
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            loggedIn = true;
            if (logInBtn != null) logInBtn.setVisibility(logInBtn.GONE);
            if (logOutBtn != null) logOutBtn.setVisibility(logOutBtn.VISIBLE);
        } else {
            loggedIn = false;
            if (logInBtn != null) logInBtn.setVisibility(logInBtn.VISIBLE);
            if (logOutBtn != null) logOutBtn.setVisibility(logOutBtn.GONE);
        }
    }

    // Builds & displays log in popup
    public void createNewLogInDialog() {
        dialogBuilder = new AlertDialog.Builder(this );
        final View logInPopupView = getLayoutInflater().inflate(R.layout.popup_login, null);
        dialogBuilder.setView(logInPopupView);
        logInDialog = dialogBuilder.create();
        logInDialog.show();

        // Login button
        final Button logInPopupBtn = logInPopupView.findViewById(R.id.button_login);
        logInPopupBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Construct a rect of the view's bounds
                        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                        logInPopupBtn.setBackgroundResource(R.drawable.login_btn_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        logInPopupBtn.setBackgroundResource(R.drawable.login_btn);

                        // login user
                        logIn(logInPopupView);

                        // TODO: if logged in successfully, do function that includes:
                        //logInBtn.setVisibility(view.GONE);
                        //logOutBtn.setVisibility(view.VISIBLE);

                        // TODO: if failed to log in... (error msg)

                        logInDialog.dismiss();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                            // User moved outside bounds
                            logInPopupBtn.setBackgroundResource(R.drawable.login_btn);
                        }
                        break;
                }
                return false;
            }
        });

        // Close button
        final Button closeBtn = logInPopupView.findViewById(R.id.button_closelogin);
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    logInBtn.setVisibility(view.VISIBLE);
                    logOutBtn.setVisibility(view.GONE);
                    logInDialog.dismiss();
                }
                return false;
            }
        });

        // Sign up text press
        final TextView singUpText = logInPopupView.findViewById(R.id.textView_signup);
        singUpText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    createNewSignUpDialog();
                    logInDialog.dismiss();
                }
                return false;
            }
        });
    }

    public void logIn(View view) {
        // Get email & password
        final EditText emailEditText = view.findViewById(R.id.editText_email_login);
        final EditText passwordEditText = view.findViewById(R.id.editText_password_login);

        // Sign in user with the email & password
        if (emailEditText.getText().toString().length() != 0 && passwordEditText.toString().length() != 0) {
            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LogIn", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LogIn", "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Failed to log in.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        } else {
            Log.w("LogIn", "signInWithEmail:failure");
            Toast.makeText(MainActivity.this, "Failed to log in.", Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }


    // Builds & displays sign up popup
    public void createNewSignUpDialog() {
        dialogBuilder = new AlertDialog.Builder(this );
        final View signUpPopupView = getLayoutInflater().inflate(R.layout.popup_signup, null);
        dialogBuilder.setView(signUpPopupView);
        signUpDialog = dialogBuilder.create();
        signUpDialog.show();

        // Login button
        final Button signUpBtn = signUpPopupView.findViewById(R.id.button_signup);
        signUpBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Construct a rect of the view's bounds
                        rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                        signUpBtn.setBackgroundResource(R.drawable.login_btn_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        signUpBtn.setBackgroundResource(R.drawable.login_btn);

                        // Sign up user
                        signUp(signUpPopupView);
                        signUpDialog.dismiss();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!rect.contains(view.getLeft() + (int) event.getX(), view.getTop() + (int) event.getY())) {
                            // User moved outside bounds
                            signUpBtn.setBackgroundResource(R.drawable.login_btn);
                        }
                        break;
                }
                return false;
            }
        });

        // Close button
        final Button closeBtn = signUpPopupView.findViewById(R.id.button_closesignup);
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    logInBtn.setVisibility(view.VISIBLE);
                    logOutBtn.setVisibility(view.GONE);
                    signUpDialog.dismiss();
                }
                return false;
            }
        });
    }

    public void signUp(final View view) {
        // Get email & password
        final EditText emailEditText = view.findViewById(R.id.editText_email_signup);
        final EditText passwordEditText = view.findViewById(R.id.editText_password_signup);

        // Create new user with the email & password
        if (emailEditText.getText().toString().length() != 0 && passwordEditText.toString().length() != 0) {
            mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up success, update UI with the signed-up user's inf
                                Log.d("SignUp", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Save user's details in database
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("users");
                                myRef.setValue(emailEditText.getText().toString().toLowerCase());

                                updateUI(user);
                            } else {
                                // If sign up fails, display a message to the user.
                                Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Failed to sign up.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        } else {
            // If sign up fails, display a message to the user.
            Log.w("SignUp", "createUserWithEmail:failure");
            Toast.makeText(MainActivity.this, "Failed to sign up.", Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }


    private void onButtonClick(Button button, String drawableName, MotionEvent event, Class activityClass) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Construct a rect of the view's bounds
                rect = new Rect(button.getLeft(), button.getTop(), button.getRight(), button.getBottom());
                button.setBackgroundResource(findResourceByName(drawableName + "_pressed"));
                break;
            case MotionEvent.ACTION_UP:
                button.setBackgroundResource(findResourceByName(drawableName));
                // Go to screen
                if (activityClass != null) {
                    startActivity(new Intent(MainActivity.this, activityClass));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!rect.contains(button.getLeft() + (int) event.getX(), button.getTop() + (int) event.getY())) {
                    // User moved outside bounds
                    button.setBackgroundResource(findResourceByName(drawableName));
                }
                break;
        }
    }

    private int findResourceByName(String name) {
        return getResources().getIdentifier(
                name,
                "drawable",
                getPackageName()
        );
    }
}