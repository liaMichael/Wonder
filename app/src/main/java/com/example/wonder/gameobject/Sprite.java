package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.wonder.Direction;
import com.example.wonder.GameDisplay;
import com.example.wonder.gamepanel.StatusBar;

/**
 * Sprite is an abstract class which implements a draw method from GameObject for drawing the object
 * as an image sprite.
 */
public abstract class Sprite extends GameObject {

    protected Bitmap[] rightWalkingAnimation;
    protected Bitmap[] leftWalkingAnimation;
    protected Bitmap[] downWalkingAnimation;
    protected Bitmap[] upWalkingAnimation;
    protected Direction direction = Direction.DOWN;
    protected Room room;
    protected StatusBar healthBar;
    protected int maxHealthPoints;
    protected int healthPoints;
    protected int damagePoints;

    public Sprite(Context context, Bitmap bitmap, Bitmap[] rightWalkingAnimation, Bitmap[] leftWalkingAnimation, Bitmap[] downWalkingAnimation, Bitmap[] upWalkingAnimation, Room room, double positionX, double positionY, int maxHealthPoints, int healthColor) {
        super(bitmap, positionX, positionY);
        this.room = room;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.maxHealthPoints = maxHealthPoints;
        this.healthBar = new StatusBar(context,this, healthColor);
        healthPoints = maxHealthPoints;
        this.rightWalkingAnimation = rightWalkingAnimation;
        this.leftWalkingAnimation = leftWalkingAnimation;
        this.downWalkingAnimation = downWalkingAnimation;
        this.upWalkingAnimation = upWalkingAnimation;
        damagePoints = 1;
    }

    @Override
    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        Paint paint = new Paint();
        canvas.drawBitmap(
                bitmap,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY),
                paint
        );
        healthBar.setPositionX((float) positionX);
        healthBar.setPositionY((float) positionY);
        healthBar.draw(canvas, gameDisplay, healthPoints, maxHealthPoints);
    }

    public void keepInBounds() {
        if (this.positionY < room.positionY) {
            this.positionY = room.positionY;
        }
        if (this.positionX < room.positionX) {
            this.positionX = room.positionX;
        }
        if (this.positionY + this.height > room.positionY + room.height) {
            this.positionY = room.positionY + room.height - this.height;
        }
        if (this.positionX + this.width > room.positionX + room.width) {
            this.positionX = room.positionX + room.width - this.width;
        }
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

    public Direction getDirection() {
        return direction;
    }

    public int getDamagePoints() {
        return damagePoints;
    }
    public void setDamagePoints(int damagePoints) {
        this.damagePoints = damagePoints;
    }
}
