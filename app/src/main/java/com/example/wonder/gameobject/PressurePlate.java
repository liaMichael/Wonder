package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.example.wonder.R;

public class PressurePlate extends GameObject {

    private Context context;
    private boolean isPressed = false;

    public PressurePlate(Context context, double positionX, double positionY) {
        super(BitmapFactory.decodeResource(context.getResources(), R.drawable.pressure_plate), positionX, positionY);
        this.context = context;
    }

    @Override
    public void update() {
        if (isPressed) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pressure_plate_pressed);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pressure_plate);
        }
    }

    public boolean isPressed() {
        return isPressed;
    }
    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }
}
