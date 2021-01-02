package com.example.wonder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.wonder.gameobject.Player;
import com.example.wonder.gameobject.Room;
import com.example.wonder.gamepanel.GameButton;
import com.example.wonder.gamepanel.GameOver;
import com.example.wonder.gamepanel.Joystick;
import com.example.wonder.gamepanel.Performance;

/**
 *  Game manages all objects in the game and is responsible for updating all states and render all objects to the screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    private DisplayMetrics displayMetrics;
    private final Joystick joystick;
    private Room room;
    private GameLoop gameLoop;
    private Level[] levels;
    private int levelNum;
    private GameOver gameOver;
    private Performance performance;
    private GameDisplay gameDisplay;
    private boolean victory = false;
    private GameButton homeBtn;
    private GameButton resetBtn;

    public Game(Context context) {
        super(context);
        this.context = context;

        // Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameLoop = new GameLoop(this, surfaceHolder);

       // Initialize game panels / UIs
        performance = new Performance(context, gameLoop);
        gameOver = new GameOver(context);
        joystick = new Joystick(context, 275, 850, 150, 70);

        // Initialize game objects
        room = new Room(context, joystick);

        // Initialize game display and center it around the player
        displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gameDisplay = new GameDisplay(displayMetrics.widthPixels, displayMetrics.heightPixels, room.getPlayer());

        levels = new Level[2];
        levelNum = 0;
        for (int i = 0; i< levels.length; i++) {
            levels[i] = new Level(context, gameDisplay, joystick, room);
        }

        // Buttons
        Bitmap homeBtnBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.home_btn);
        homeBtn = new GameButton(homeBtnBitmap, displayMetrics.widthPixels - homeBtnBitmap.getWidth() - 16, 16);
        Bitmap resetBtnBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.reset_btn);
        resetBtn = new GameButton(resetBtnBitmap, displayMetrics.widthPixels - resetBtnBitmap.getWidth() - 16, displayMetrics.heightPixels - resetBtnBitmap.getHeight() - 16);

        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Handle touch event actions
        if (!joystick.getIsPressed()) {
            // Home button
            if (!homeBtn.isPressed && homeBtn.isPressed(event.getX(), event.getY())) {
                homeBtn.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.home_btn_pressed));
                homeBtn.isPressed = true;
            } else if (homeBtn.isPressed) {
                homeBtn.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.home_btn));
                homeBtn.isPressed = false;
                pause();
                context.startActivity(new Intent(context, MainActivity.class));
            }

            // Reset button
            if (!resetBtn.isPressed && resetBtn.isPressed(event.getX(), event.getY())) {
                resetBtn.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.reset_btn_pressed));
                resetBtn.isPressed = true;
            } else if (resetBtn.isPressed) {
                resetBtn.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.reset_btn));
                resetBtn.isPressed = false;
                newLevel();
            }
        }

        return super.onTouchEvent(event) || levels[levelNum].onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("Game.java", "surfaceCreated()");
        if (gameLoop.getState().equals(Thread.State.TERMINATED)) {
            gameLoop = new GameLoop(this, holder);
        }
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Draw level
        levels[levelNum].draw(canvas, displayMetrics);

        // Draw buttons
        homeBtn.draw(canvas);
        resetBtn.draw(canvas);

        // Draw level title
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.wonder);
        paint.setColor(color);
        paint.setTextSize(75);
        Typeface plain = ResourcesCompat.getFont(context, R.font.diary);
        paint.setTypeface(plain);
        String levelTitle = "Level " + (levelNum + 1);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShadowLayer(5.0f, 10.0f, 10.0f,  getResources().getColor(R.color.wonderLike) );
        canvas.drawText(levelTitle, (float) (displayMetrics.widthPixels / 2.0 - paint.measureText(levelTitle) / 2.0), 100, paint);

        // Draw victory screen.
        if (victory) {
            levels[levelNum].getVictoryScreen().draw(canvas, displayMetrics);
        }

        performance.draw(canvas, displayMetrics);
    }

    public void update() {
        levels[levelNum].update();
        if (room.isFinish()) {
            if (levelNum + 1 <= levels.length - 1) {
                levelNum++;
                newLevel();
            } else {
                // Victory screen.
                victory = true;
            }
        } else {
            victory = false;
        }
    }

    public void pause() {
        gameLoop.stopLoop();
    }

    private void newLevel() {
        victory = false;
        room = new Room(getContext(), joystick);

        // Changeable
        room.setNumberOfEnemies(10);
        room.setPlayerSpellDamagePoints(1);
        room.setEnemySpellDamagePoints(1);
        room.setPlayerMaxHealthPoints(10);
        room.setEnemyMaxHealthPoints(2);
        room.setEnemySpeedPixelsPerSecond(Player.SPEED_PIXELS_PER_SECOND * 0.1 * (levelNum + 1));

        displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gameDisplay = new GameDisplay(displayMetrics.widthPixels, displayMetrics.heightPixels, room.getPlayer());

        levels[levelNum] = new Level(context, gameDisplay, joystick, room);
    }
}
