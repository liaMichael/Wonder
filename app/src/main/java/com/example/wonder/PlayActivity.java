package com.example.wonder;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * PlayActivity is the entry point to the game
 */
public class PlayActivity extends AppCompatActivity {

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set window to fullscreen (will hide status bar)
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set content view to game, so that objects in the Game class can be rendered to the screen
        game = new Game(this);
        setContentView(game);
    }

    @Override
    protected void onStart() {
        Log.d("PlayActivity.java", "onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("PlayActivity.java", "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("PlayActivity.java", "onPause()");
        game.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("PlayActivity.java", "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("PlayActivity.java", "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d("PlayActivity.java", "onBackPressed()");
        // Comment out super to prevent any back press action
       // super.onBackPressed();
    }
}