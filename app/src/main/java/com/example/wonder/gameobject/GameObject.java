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
        int left = (int) Math.max(obj1.getPositionX(), obj2.getPositionX());
        int right = (int) Math.min(obj1.getPositionX() + obj1.getWidth(), obj2.getPositionX() + obj2.getWidth());
        int top = (int) Math.max(obj1.getPositionY(), obj2.getPositionY());
        int bottom = (int) Math.min(obj1.getPositionY() + obj1.getHeight(), obj2.getPositionY() + obj2.getHeight());
        for (int col = left; col < right; col++) {
            for (int row = top; row < bottom; row++) {

                if ((int) (col - obj1.getPositionX()) >= obj1.bitmap.getWidth()) {
                    Log.d("GameObject.java", "obj1: x >=  bitmap.width(): " + (int) (col - obj1.getPositionX()) + " >= " + obj1.bitmap.getWidth() + ". Position: " +  obj1.getPositionX() + ", " + obj1.getPositionY());
                }

                if ((int) (row - obj1.getPositionY()) >= obj1.bitmap.getHeight()) {
                    Log.d("GameObject.java", "obj1: y >=  bitmap.height(): " + (int) (row - obj1.getPositionY()) + " >= " + obj1.bitmap.getHeight() + ". Position: " +  obj1.getPositionX() + ", " + obj1.getPositionY());
                }

                if ((int) (col - obj2.getPositionX()) >= obj2.bitmap.getWidth()) {
                    Log.d("GameObject.java", "obj2: x >=  bitmap.width(): " + (int) (col - obj2.getPositionX()) + " >= " + obj2.bitmap.getWidth() + ". Position: " +  obj2.getPositionX() + ", " + obj2.getPositionY());
                }

                if ((int) (row - obj2.getPositionY()) >= obj2.bitmap.getHeight()) {
                    Log.d("GameObject.java", "obj2: y >=  bitmap.height(): " + (int) (row - obj2.getPositionY()) + " >= " + obj2.bitmap.getHeight() + ". Position: " +  obj2.getPositionX() + ", " + obj2.getPositionY());
                }

                if (obj1.bitmap.getPixel((int) (col - obj1.getPositionX()), (int) (row - obj1.getPositionY())) != Color.TRANSPARENT &&
                        obj2.bitmap.getPixel((int) (col - obj2.getPositionX()), (int) (row - obj2.getPositionY())) != Color.TRANSPARENT) {
                    return true;
                }
            }
        }
        return false;
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

    public double getPositionX() {
        return positionX;
    }
    public double getPositionY() {
        return positionY;
    }

    protected static double getDistanceBetweenObjects(GameObject obj1, GameObject obj2) {
        return Utils.getDistanceBetweenPoints(obj1.getPositionX(), obj1.getPositionY(), obj2.getPositionX(), obj2.getPositionY());
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

    public void setBitmap(Bitmap bitmap) {
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.bitmap = bitmap;
    }
}
