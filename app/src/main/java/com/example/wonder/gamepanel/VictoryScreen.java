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
 * VictoryScreen is a panel which draws the text The End to the screen.
 */
public class VictoryScreen {

    private Context context;

    public VictoryScreen(Context context) {
        this.context = context;
    }

    public void draw(Canvas canvas, DisplayMetrics displayMetrics) {
        String text = "The End";

        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.victory);
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
