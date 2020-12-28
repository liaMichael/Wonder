package com.example.wonder;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.example.wonder.gameobject.Room;
import com.example.wonder.gamepanel.GameOver;
import com.example.wonder.gamepanel.Joystick;

public class Level {

    private GameDisplay gameDisplay;

    private Joystick joystick;
    private Room room;
    private GameOver gameOver;

    private int joystickPointerId = 0;
    private boolean movingTouch = false;

    public Level(Context context, GameDisplay gameDisplay, Joystick joystick, Room room) {
        this.gameDisplay = gameDisplay;
        this.joystick = joystick;
        this.room = room;

        gameOver = new GameOver(context);
    }

    public boolean onTouchEvent(MotionEvent event) {
        // Handle touch event actions
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (!joystick.getIsPressed() && (double) event.getX() < gameDisplay.getDisplayCenterX()) {
                    joystick.setInnerCircleCenterPositionX((int) event.getX());
                    joystick.setInnerCircleCenterPositionY((int) event.getY());
                    joystick.setOuterCircleCenterPositionX((int) event.getX());
                    joystick.setOuterCircleCenterPositionY((int) event.getY());
                    joystick.setIsPressed(true);
                } else {
                    room.setNumberOfSpellsToCast(room.getNumberOfSpellsToCast() + 1);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                // Joystick was pressed previously and is now moved
                if ((double) event.getX() < gameDisplay.getDisplayCenterX()) {
                    if (joystick.getIsPressed() || joystick.isPressed((double) event.getX(), (double) event.getY())) {
                        joystick.setActuator((double) event.getX(), (double) event.getY());
                    } else {
                        joystick.setInnerCircleCenterPositionX((int) event.getX());
                        joystick.setInnerCircleCenterPositionY((int) event.getY());
                        joystick.setOuterCircleCenterPositionX((int) event.getX());
                        joystick.setOuterCircleCenterPositionY((int) event.getY());
                    }
                    joystick.setIsPressed(true);
                } else {
                    joystick.setIsPressed(false);
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
        if (joystick.getIsPressed()) {
            joystick.draw(canvas);
        }

        // Draw Game over if the player is dead
        if (room.getPlayer().getHealthPoints() <= 0) {
            gameOver.draw(canvas);
        }
    }

    public void update() {
        // Stop updating the game if the player is dead
        if (room.getPlayer().getHealthPoints() <= 0 ||
                room.isFinish()) {
            return;
        }

        // Update game state
        joystick.update();
        room.update();

        gameDisplay.update();
    }

    // ---------------
    // Changeable
    // ---------------
    /**public void setNumberOfEnemies(int numberOfEnemies) {
        room.setNumberOfEnemies(numberOfEnemies);
    }

    public void setPlayerSpellDamagePoints(int playerSpellDamagePoints) {
        room.setPlayerSpellDamagePoints(playerSpellDamagePoints);
    }

    public void setEnemySpellDamagePoints(int enemySpellDamagePoints) {
        room.setEnemySpellDamagePoints(enemySpellDamagePoints);
    }

    public void setPlayerMaxHealthPoints(int playerMaxHealthPoints) {
        room.setPlayerMaxHealthPoints(playerMaxHealthPoints);
    }

    public void setEnemyMaxHealthPoints(int enemyMaxHealthPoints) {
        room.setEnemyMaxHealthPoints(enemyMaxHealthPoints);
    }

    public void setEnemySpeedPixelsPerSecond(double enemySpeedPixelsPerSecond) {
        room.setEnemySpeedPixelsPerSecond(enemySpeedPixelsPerSecond);
    }***/
}
