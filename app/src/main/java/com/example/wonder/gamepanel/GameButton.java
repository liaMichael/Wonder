package com.example.wonder.gamepanel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class GameButton {

    private Bitmap bitmap;
    private int positionX, positionY;
    public boolean isPressed = false;

    public GameButton(Bitmap bitmap, int positionX, int positionY) {
        this.bitmap = bitmap;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        canvas.drawBitmap(
                bitmap,
                (float) positionX,
                (float) positionY,
                paint
        );
    }

    public boolean isPressed(double touchPositionX, double touchPositionY) {
        return touchPositionX >= positionX && touchPositionX <= positionX + bitmap.getWidth() &&
                touchPositionY >= positionY && touchPositionY <= positionY + bitmap.getHeight();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
