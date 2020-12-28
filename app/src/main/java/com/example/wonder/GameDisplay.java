package com.example.wonder;

import com.example.wonder.gameobject.GameObject;

public class GameDisplay {

    private double gameToDisplayCoordinateOffsetX;
    private double gameToDisplayCoordinateOffsetY;
    private double displayCenterX;
    private double displayCenterY;
    private double gameCenterX;
    private double gameCenterY;
    private GameObject centerObject;

    public GameDisplay(int widthPixels, int heightPixels, GameObject centerObject) {
        this.centerObject = centerObject;

        displayCenterX = widthPixels / 2.0;
        displayCenterY = heightPixels / 2.0;
    }

    public void update() {
        gameCenterX = centerObject.getPositionX() + centerObject.getWidth() / 2.0;
        gameCenterY = centerObject.getPositionY() + centerObject.getHeight() / 2.0;

        gameToDisplayCoordinateOffsetX = displayCenterX - gameCenterX;
        gameToDisplayCoordinateOffsetY = displayCenterY - gameCenterY;
    }

    public double gameToDisplayCoordinatesX(double x) {
        return x + gameToDisplayCoordinateOffsetX;
    }

    public double gameToDisplayCoordinatesY(double y) {
        return y + gameToDisplayCoordinateOffsetY;
    }

    public double getDisplayCenterX() {
        return displayCenterX;
    }
}
