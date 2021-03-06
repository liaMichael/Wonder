package com.example.wonder.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.wonder.R;

/**
 * GameOver is a panel which draws the text Game Over to the screen.
 */
public class GameOver {

    private Context context;

    public GameOver(Context context) {
        this.context = context;
    }

    public void draw(Canvas canvas, DisplayMetrics displayMetrics) {
        String text = "Game Over";

        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.gameOver);
        paint.setColor(color);
        paint.setTextSize(150);
        Typeface plain = ResourcesCompat.getFont(context, R.font.fipps_regular);
        paint.setTypeface(plain);

        float x = (float) (displayMetrics.widthPixels / 2.0 - paint.measureText(text) / 2.0);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        float y = (float) (displayMetrics.heightPixels / 2.0 - bounds.height() / 2.0);
        canvas.drawText(text, x, y, paint);
    }
}
