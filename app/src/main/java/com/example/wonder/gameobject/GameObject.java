package com.example.wonder.gameobject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.example.wonder.GameDisplay;
import com.example.wonder.Utils;

/**
 * GameObject is an abstract class which is the foundation of all world objects in the game.
 */
public abstract class GameObject {

    protected Bitmap bitmap;
    protected double positionX;
    protected double positionY;
    protected double velocityX = 0;
    protected double velocityY = 0;
    protected double directionX = 0;
    protected double directionY = 1;
    protected int width;
    protected  int height;

    public GameObject(Bitmap bitmap, double positionX, double positionY) {
        this.bitmap = bitmap;
        this.positionX = positionX;
        this.positionY = positionY;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
    }

    /**
     * isColliding checks if two sprite objects are colliding, based on their bitmap pixels
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean isColliding(GameObject obj1, GameObject obj2) {
        int left = (int) Math.max(obj1.positionX, obj2.positionX);
        int right = (int) Math.min(obj1.positionX + obj1.width, obj2.positionX + obj2.width);
        int top = (int) Math.max(obj1.positionY, obj2.positionY);
        int bottom = (int) Math.min(obj1.positionY + obj1.height, obj2.positionY + obj2.height);
        for (int col = left; col < right; col++) {
            for (int row = top; row < bottom; row++) {

                if ((int) (col - obj1.positionX) >= obj1.width) {
                    Log.d("GameObject.java", "obj1: x >=  bitmap.width(): " + (int) (col - obj1.positionX) + " >= " + obj1.width + ". Position: " +  obj1.positionX + ", " + obj1.positionY);
                }

                if ((int) (row - obj1.positionY) >= obj1.height) {
                    Log.d("GameObject.java", "obj1: y >=  bitmap.height(): " + (int) (row - obj1.positionY) + " >= " + obj1.height + ". Position: " +  obj1.positionX + ", " + obj1.positionY);
                }

                if ((int) (col - obj2.positionX) >= obj2.width) {
                    Log.d("GameObject.java", "obj2: x >=  bitmap.width(): " + (int) (col - obj2.positionX) + " >= " + obj2.width + ". Position: " +  obj2.positionX + ", " + obj2.positionY);
                }

                if ((int) (row - obj2.positionY) >= obj2.height) {
                    Log.d("GameObject.java", "obj2: y >=  bitmap.height(): " + (int) (row - obj2.positionY) + " >= " + obj2.height + ". Position: " +  obj2.positionX + ", " + obj2.positionY);
                }

                if (obj1.bitmap.getPixel((int) (col - obj1.positionX), (int) (row - obj1.positionY)) != Color.TRANSPARENT &&
                        obj2.bitmap.getPixel((int) (col - obj2.positionX), (int) (row - obj2.positionY)) != Color.TRANSPARENT) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isContaining(GameObject big, GameObject small) {
        if (!(big.positionX <= small.positionX && big.positionY<= small.positionY && big.width >= small.width && big.height >= small.height)) {
            return false;
        }

        for (int col = (int) small.positionX; col < (int) (small.positionX + small.width); col++) {
            for (int row = (int) small.positionY; row < (int) (small.positionY + small.height); row++) {
                if (small.bitmap.getPixel((int) (col - small.positionX), (int) (row - small.positionY)) != Color.TRANSPARENT &&
                big.bitmap.getPixel(col, row) == Color.TRANSPARENT) {
                    return false;
                }
            }
        }
        return true;
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        Paint paint = new Paint();
        canvas.drawBitmap(
                bitmap,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY),
                paint
        );
    }

    public abstract void update();

    protected static double getDistanceBetweenObjects(GameObject obj1, GameObject obj2) {
        return Utils.getDistanceBetweenPoints(obj1.getPositionX(), obj1.getPositionY(), obj2.getPositionX(), obj2.getPositionY());
    }
    public double getPositionX() {
        return positionX;
    }
    public double getPositionY() {
        return positionY;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }
    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    protected double getDirectionX() {
        return directionX;
    }
    protected double getDirectionY() {
        return directionY;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }
}
