package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.example.wonder.R;

public class Door extends GameObject {

    private boolean isOpen;

    public Door(Context context, double positionX, double positionY) {
        super(BitmapFactory.decodeResource(context.getResources(), R.drawable.door), positionX, positionY);
        isOpen = false;
    }

    @Override
    public void update() {

    }

    public boolean isOpen() {
        return isOpen;
    }
    public void setOpen(boolean open) {
        isOpen = open;
    }
}
