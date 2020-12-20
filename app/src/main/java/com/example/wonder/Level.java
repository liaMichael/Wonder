package com.example.wonder;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.example.wonder.gameobject.Player;
import com.example.wonder.gamepanel.GameOver;
import com.example.wonder.gamepanel.Joystick;

public class Level {

    private GameDisplay gameDisplay;

    private Joystick joystick;
    private Room room;
    private GameOver gameOver;

    private int joystickPointerId = 0;

    private int numberOfEnemies;
    private int playerSpellDamagePoints;
    private int enemySpellDamagePoints;
    private int playerMaxHealthPoints;
    private int enemyMaxHealthPoints;
    private int enemySpellCastsPerMinute;

    public Level(Context context, GameDisplay gameDisplay) {
        this.gameDisplay = gameDisplay;
        joystick = new Joystick(275, 700, 70, 40);
        room = new Room(context, joystick);

        gameOver = new GameOver(context);

        // Changeable
        numberOfEnemies = 3;
        playerSpellDamagePoints = 1;
        enemySpellDamagePoints = 1;
        playerMaxHealthPoints = 10;
        enemyMaxHealthPoints = 2;
        enemySpellCastsPerMinute = 10;
    }

    public Level(Context context, GameDisplay gameDisplay, Player player, Joystick joystick, Room room) {
        this.gameDisplay = gameDisplay;
        this.joystick = joystick;
        this.room = room;

        gameOver = new GameOver(context);

        // Changeable
        numberOfEnemies = 3;
        playerSpellDamagePoints = 1;
        enemySpellDamagePoints = 1;
        playerMaxHealthPoints = 10;
        enemyMaxHealthPoints = 2;
        enemySpellCastsPerMinute = 10;
    }

    public boolean onTouchEvent(MotionEvent event) {
        // Handle touch event actions
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (joystick.getIsPressed()) {
                    // Joystick was pressed before this event -> cast spell
                    room.setNumberOfSpellsToCast(room.getNumberOfSpellsToCast() + 1);
                } else if (joystick.isPressed((double) event.getX(), (double) event.getY())) {
                    // Joystick is pressed in this event -> setIsPressed(true) and store ID
                    joystickPointerId = event.getPointerId(event.getActionIndex());
                    joystick.setIsPressed(true);
                } else {
                    // Joystick was not pressed previously, and is not pressed in this event -> cast spell
                    room.setNumberOfSpellsToCast(room.getNumberOfSpellsToCast() + 1);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                // Joystick was pressed previously and is now moved
                if (joystick.getIsPressed()) {
                    joystick.setActuator((double) event.getX(), (double) event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (joystickPointerId == event.getPointerId(event.getActionIndex())) {
                    // Joystick was let go of -> setIsPressed(false) and resetActuator
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                }
                return true;
        }
        return false;
    }

    public void draw(Canvas canvas) {
        // Draw game objects
        room.draw(canvas, gameDisplay);

        // Draw game panels
        joystick.draw(canvas);

        // Draw Game over if the player is dead
        if (room.getPlayer().getHealthPoints() <= 0) {
            gameOver.draw(canvas);
        }
    }

    public void update() {
        // Stop updating the game if the player is dead
        if (room.getPlayer().getHealthPoints() <= 0) {
            return;
        }

        // Update game state
        joystick.update();
        room.update();

        gameDisplay.update();
    }
}
