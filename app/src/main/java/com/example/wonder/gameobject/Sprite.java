package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.wonder.GameDisplay;
import com.example.wonder.Room;
import com.example.wonder.gamepanel.StatusBar;

/**
 * Sprite is an abstract class which implements a draw method from GameObject for drawing the object
 * as an image sprite.
 */
public abstract class Sprite extends GameObject {

    protected StatusBar healthBar;
    protected int maxHealthPoints;
    protected int healthPoints;

    public Sprite(Context context, Bitmap bitmap, double positionX, double positionY, int maxHealthPoints, int healthColor) {
        super(bitmap, positionX, positionY);
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.maxHealthPoints = maxHealthPoints;
        this.healthBar = new StatusBar(context,this, healthColor);
        healthPoints = maxHealthPoints;
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        super.draw(canvas, gameDisplay);
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        // Only allow positive values
        if (healthPoints >= 0)
            this.healthPoints = healthPoints;
    }

    public int getMaxHealthPoints() {
        return maxHealthPoints;
    }

    public StatusBar getHealthBar() {
        return healthBar;
    }
}
