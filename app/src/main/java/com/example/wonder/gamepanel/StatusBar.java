package com.example.wonder.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.wonder.GameDisplay;
import com.example.wonder.R;
import com.example.wonder.gameobject.Sprite;

/**
 * StatusBar displays the status of a sprite
 */
public class StatusBar {

    protected int width, height, margin;
    protected Paint borderPaint, barPaint;
    private Sprite sprite;
    private float positionX, positionY;
    private float distanceToSprite;

    public StatusBar(Context context, Sprite sprite, int distanceToSprite, int color) {
        this.sprite = sprite;
        this.distanceToSprite = distanceToSprite;
        this.width = 100;
        this.height = 20;
        this.margin = 2;

        positionX = (float) sprite.getPositionX();
        positionY = (float) sprite.getPositionY();

        this.borderPaint = new Paint();
        int borderColor = ContextCompat.getColor(context, R.color.statusBarBorder);
        borderPaint.setColor(borderColor);

        this.barPaint = new Paint();
        barPaint.setColor(color);
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay, int barPoints, int maxBarPoints) {
        if (barPoints < 0) {
            barPoints = 0;
        }
        float barPointPercentage = (float) barPoints / maxBarPoints;

        // Draw border
        float borderLeft = (float) (sprite.getPositionX() + sprite.getWidth() / 2 - width / 2);
        float borderRight = borderLeft + width;
        float borderTop = (float) (sprite.getPositionY() - distanceToSprite - height);
        float borderBottom = borderTop + height;

        canvas.drawRect(
                (float) gameDisplay.gameToDisplayCoordinatesX(borderLeft),
                (float) gameDisplay.gameToDisplayCoordinatesY(borderTop),
                (float) gameDisplay.gameToDisplayCoordinatesX(borderRight),
                (float) gameDisplay.gameToDisplayCoordinatesY(borderBottom),
                borderPaint
        );

        // Draw health
        float barWidth = width - margin * 2;
        float barHeight = height - margin * 2;
        float barLeft = borderLeft + margin;
        float barRight = barLeft + barWidth * barPointPercentage;
        float barBottom = borderBottom - margin;
        float barTop = borderBottom - barHeight;
        canvas.drawRect(
                (float) gameDisplay.gameToDisplayCoordinatesX(barLeft),
                (float) gameDisplay.gameToDisplayCoordinatesY(barTop),
                (float) gameDisplay.gameToDisplayCoordinatesX(barRight),
                (float) gameDisplay.gameToDisplayCoordinatesY(barBottom),
                barPaint
        );
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
