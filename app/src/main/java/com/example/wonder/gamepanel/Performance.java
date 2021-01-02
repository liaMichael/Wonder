package com.example.wonder.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.wonder.GameLoop;
import com.example.wonder.R;

public class Performance {
    private Context context;
    private GameLoop gameLoop;

    public Performance(Context context, GameLoop gameLoop) {
        this.context = context;
        this.gameLoop = gameLoop;
    }

    public void draw(Canvas canvas, DisplayMetrics displayMetrics) {
        drawUPS(canvas, displayMetrics);
        drawFPS(canvas, displayMetrics);
    }

    public void drawUPS(Canvas canvas, DisplayMetrics displayMetrics) {
        String averageUPS = Double.toString(Math.round(gameLoop.getAverageUPS() * 100.0) / 100.0);
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.white);
        paint.setColor(color);
        paint.setTextSize(30);
        Typeface plain = ResourcesCompat.getFont(context, R.font.pixel);
        paint.setTypeface(plain);
        canvas.drawText("UPS: " + averageUPS, 20, displayMetrics.heightPixels - 50, paint);
    }

    public void drawFPS(Canvas canvas, DisplayMetrics displayMetrics) {
        String averageFPS = Double.toString(Math.round(gameLoop.getAverageFPS() * 100.0) / 100.0);
        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.white);
        paint.setColor(color);
        paint.setTextSize(30);
        Typeface plain = ResourcesCompat.getFont(context, R.font.pixel);
        paint.setTypeface(plain);
        canvas.drawText("FPS: " + averageFPS, 20, displayMetrics.heightPixels - 20, paint);
    }
}
