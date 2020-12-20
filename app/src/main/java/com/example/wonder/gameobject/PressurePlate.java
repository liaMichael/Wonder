package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.example.wonder.R;

public class PressurePlate extends GameObject {

    public PressurePlate(Context context, double positionX, double positionY) {
        super(BitmapFactory.decodeResource(context.getResources(), R.drawable.pressure_plate_pressed), positionX, positionY);
    }

    @Override
    public void update() {

    }
}
