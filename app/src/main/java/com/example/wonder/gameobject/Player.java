package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.wonder.GameDisplay;
import com.example.wonder.GameLoop;
import com.example.wonder.gamepanel.Joystick;
import com.example.wonder.R;
import com.example.wonder.Utils;

/**
 * Player is thr main character of the game, which the user can control with a touch joystick.
 * The player class is an extension of a Sprite, which is an extension of GameObject
 */
public class Player extends Sprite {

    private static Context context;
    public static final double SPEED_PIXELS_PER_SECOND = 400.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private final Joystick joystick;

    public Player(Context context, Joystick joystick, double positionX, double positionY) {
        super(
                context,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.orielpx_front),
                positionX,
                positionY,
                10,
                ContextCompat.getColor(context, R.color.statusBarPlayerHealth)
        );
        this.joystick = joystick;
        this.context = context;
    }

    public void update() {
        // Update velocity based on actuator of joystick
        velocityX = joystick.getActuatorX() * MAX_SPEED;
        velocityY = joystick.getActuatorY() * MAX_SPEED;

        // Update position
        positionX += velocityX;
        positionY += velocityY;

        // Update direction
        if (velocityX != 0 || velocityY != 0) {
            // Normalize velocity to get direction (unit vector of velocity)
            double distance = Utils.getDistanceBetweenPoints(0, 0, velocityX, velocityY);
            directionX = velocityX / distance;
            directionY = velocityY / distance;

            // Update bitmap according to direction
            if (velocityX > 0 && velocityX >= Math.abs(velocityY)) {
                // Moving right
                this.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.orielpx_right));
            } else if (velocityX < 0 && Math.abs(velocityX) >= Math.abs(velocityY)) {
                // Moving left
                this.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.orielpx_left));
            } else if (velocityY < 0 && Math.abs(velocityY) >= Math.abs(velocityX)) {
                // Moving up
                this.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.orielpx_back));
            } else {
                // Moving down
                this.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.orielpx_front));
            }
        }
    }
}
