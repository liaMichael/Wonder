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

    public StatusBar(Context context, Sprite sprite, int color) {
        this.sprite = sprite;
        this.width = 100;
        this.height = 20;
        this.margin = 2;

        this.borderPaint = new Paint();
        int borderColor = ContextCompat.getColor(context, R.color.statusBarBorder);
        borderPaint.setColor(borderColor);

        this.barPaint = new Paint();
        barPaint.setColor(color);
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay, int barPoints, int maxBarPoints) {
        float x = (float) sprite.getPositionX();
        float y = (float) sprite.getPositionY();
        float distanceToSprite = 30;
        float barPointPercentage = (float) barPoints / maxBarPoints;

        // Draw border
        float borderLeft = x + sprite.getWidth() / 2 - width / 2;
        float borderRight = x + sprite.getWidth() / 2 + width / 2;
        float borderBottom = y - distanceToSprite;
        float borderTop = borderBottom - height;
        canvas.drawRect(
                (float) gameDisplay.gameToDisplayCoordinatesX(borderLeft),
                (float) gameDisplay.gameToDisplayCoordinatesY(borderTop),
                (float) gameDisplay.gameToDisplayCoordinatesX(borderRight),
                (float) gameDisplay.gameToDisplayCoordinatesY(borderBottom),
                borderPaint
        );

        // Draw health
        float barWidth = width - 2 * margin;
        float barHeight = height - 2 * margin;
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
}
