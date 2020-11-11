package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.wonder.GameDisplay;
import com.example.wonder.GameLoop;
import com.example.wonder.R;

public class Spell extends GameObject {

    private final Sprite spellCaster;
    public static final double SPEED_PIXELS_PER_SECOND = 400.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private int damagePoints;

    public Spell(Context context, Sprite spellCaster) {
        super(BitmapFactory.decodeResource(context.getResources(), R.drawable.wonderlike_right), spellCaster.positionX, spellCaster.positionY);

        velocityX = spellCaster.getDirectionX() * MAX_SPEED;
        velocityY = spellCaster.getDirectionY() * MAX_SPEED;

        this.spellCaster = spellCaster;
        damagePoints = 1;
    }

    public void update() {
        positionX += velocityX;
        positionY += velocityY;
    }

    public int getDamagePoints() {
        return damagePoints;
    }
}