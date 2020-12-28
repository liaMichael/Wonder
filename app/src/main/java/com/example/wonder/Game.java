package com.example.wonder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.wonder.gameobject.Player;
import com.example.wonder.gameobject.Room;
import com.example.wonder.gamepanel.GameOver;
import com.example.wonder.gamepanel.Joystick;
import com.example.wonder.gamepanel.Performance;

/**
 *  Game manages all objects in the game and is responsible for updating all states and render all objects to the screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {

    private final Joystick joystick;
    private Room room;
    private GameLoop gameLoop;
    private Level[] levels;
    private int levelNum;
    private GameOver gameOver;
    private Performance performance;
    private GameDisplay gameDisplay;

    public Game(Context context) {
        super(context);

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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gameDisplay = new GameDisplay(displayMetrics.widthPixels, displayMetrics.heightPixels, room.getPlayer());

        levels = new Level[2];
        levelNum = 0;
        for (int i = 0; i< levels.length; i++) {
            levels[i] = new Level(context, gameDisplay, joystick, room);
        }

        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Handle touch event actions
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
        levels[levelNum].draw(canvas);

        performance.draw(canvas);
    }

    public void update() {
        levels[levelNum].update();
        if (room.isFinish() && levelNum + 1 <= levels.length - 1) {
            levelNum++;
            newLevel();
        }
    }

    public void pause() {
        gameLoop.stopLoop();
    }

    private void newLevel() {
        room = new Room(getContext(), joystick);

        room.getPlayer().setPositionX(room.getPositionX() + room.getWidth() / 2.0);
        room.getPlayer().setPositionY(room.getPositionY() + room.getHeight() - room.getPlayer().getHeight());

        // Changeable
        room.setNumberOfEnemies(10);
        room.setPlayerSpellDamagePoints(1);
        room.setEnemySpellDamagePoints(1);
        room.setPlayerMaxHealthPoints(10);
        room.setEnemyMaxHealthPoints(2);
        room.setEnemySpeedPixelsPerSecond(Player.SPEED_PIXELS_PER_SECOND * 0.1 * (levelNum + 1));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gameDisplay = new GameDisplay(displayMetrics.widthPixels, displayMetrics.heightPixels, room.getPlayer());

        levels[levelNum] = new Level(getContext(), gameDisplay, joystick, room);
    }
}
