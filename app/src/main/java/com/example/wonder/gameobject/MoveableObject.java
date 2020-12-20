package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.wonder.Direction;
import com.example.wonder.R;
import com.example.wonder.Room;

public class MoveableObject extends GameObject {

    private Room room;
    private Direction direction;

    public MoveableObject(Context context, double positionX, double positionY, Room room) {
        super(BitmapFactory.decodeResource(context.getResources(), R.drawable.mudcube), positionX, positionY);
        this.room = room;
        direction = Direction.DOWN;
    }

    @Override
    public void update() {
        // TODO: slow down player's speed
        // Update position
        positionX += velocityX;
        positionY += velocityY;
    }

    public void keepInBounds() {
        if (this.positionY < room.positionY) {
            this.positionY = room.positionY;
        }
        if (this.positionX < room.positionX) {
            this.positionX = room.positionX;
        }
        if (this.positionY + this.height > room.positionY + room.height) {
            this.positionY = room.positionY + room.height - this.height;
        }
        if (this.positionX + this.width > room.positionX + room.width) {
        }
    }

    private boolean isInBounds() {
        return !(this.positionY < room.positionY ||
                this.positionX < room.positionX ||
                this.positionY + this.height > room.positionY + room.height ||
                this.positionX + this.width > room.positionX + room.width);
    }

    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
