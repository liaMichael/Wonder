package com.example.wonder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;

import com.example.wonder.gameobject.Room;
import com.example.wonder.gamepanel.GameButton;
import com.example.wonder.gamepanel.GameOver;
import com.example.wonder.gamepanel.Joystick;
import com.example.wonder.gamepanel.VictoryScreen;

public class Level {

    private Context context;
    private GameDisplay gameDisplay;

    private Joystick joystick;
    private Room room;
    private GameOver gameOver;
    private VictoryScreen victoryScreen;

    private int joystickPointerId = 0;
    private int spellPointerId = 0;
    private float downEventX, downEventY;
    private boolean isCastingSpell = false;

    public Level(Context context, GameDisplay gameDisplay, Joystick joystick, Room room) {
        this.context = context;
        this.gameDisplay = gameDisplay;
        this.joystick = joystick;
        this.room = room;

        gameOver = new GameOver(context);
        victoryScreen = new VictoryScreen(context);
    }

    public boolean onTouchEvent(MotionEvent event) {
        // Handle touch event actions
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                joystickPointerId = 0;
                spellPointerId = 0;
                downEventX = event.getX();
                downEventY = event.getY();
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (joystick.getIsPressed()) {
                    spellPointerId = event.getPointerId(event.getActionIndex());
                }
                downEventX = event.getX();
                downEventY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!joystick.getIsPressed() && Math.abs(event.getX() - downEventX) < 50 &&
                        Math.abs(event.getY() - downEventY) < 50) {
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                    spellPointerId = event.getPointerId(event.getActionIndex());
                } else if (joystick.getIsPressed() && event.getPointerId(event.getActionIndex()) == joystickPointerId) {
                    joystick.setActuator(event.getX(), event.getY());
                } else if (!joystick.getIsPressed()) {
                    joystick.setIsPressed(true);
                    joystick.setInnerCircleCenterPositionX((int) event.getX());
                    joystick.setInnerCircleCenterPositionY((int) event.getY());
                    joystick.setOuterCircleCenterPositionX((int) event.getX());
                    joystick.setOuterCircleCenterPositionY((int) event.getY());
                    joystickPointerId = event.getPointerId(event.getActionIndex());
                } else {
                    spellPointerId = event.getPointerId(event.getActionIndex());
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (joystick.getIsPressed() && event.getPointerId(event.getActionIndex()) == joystickPointerId) {
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                } else {
                     if (event.getPointerId(event.getActionIndex()) == spellPointerId && spellPointerId > joystickPointerId && spellPointerId < event.getPointerCount()) {
                         Log.d("Level.java", "spellPointerId: " + spellPointerId + ", event.getPointerId(event.getActionIndex()): " + event.getPointerId(event.getActionIndex()));
                         downEventX = event.getX(spellPointerId);
                         downEventY = event.getY(spellPointerId);
                     }
                    isCastingSpell = true;
                    room.setNumberOfSpellsToCast(room.getNumberOfSpellsToCast() + 1);
                    Log.d("Level.java", "numberOfSpellsToCast: " + room.getNumberOfSpellsToCast());
                }
                return true;
        }
        return false;
    }

    public void draw(Canvas canvas, DisplayMetrics displayMetrics) {
        // Draw game objects
        room.draw(canvas, gameDisplay);

        // Draw game panels
        if (joystick.getIsPressed()) {
            joystick.draw(canvas);
        }

        Paint spellPaint = new Paint();
        spellPaint.setColor(ContextCompat.getColor(context, R.color.wonderLikeTransparent50));
        spellPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        if (isCastingSpell) {
            canvas.drawCircle(
                    downEventX,
                    downEventY,
                    80,
                    spellPaint
            );
        }
        isCastingSpell = false;

        // Draw Game over if the player is dead
        if (room.getPlayer().getHealthPoints() <= 0) {
            gameOver.draw(canvas, displayMetrics);
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

    public VictoryScreen getVictoryScreen() {
        return victoryScreen;
    }
}
