package com.example.wonder.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.wonder.R;
import com.example.wonder.Utils;

public class Joystick {

    private int outerCircleCenterPositionX;
    private int outerCircleCenterPositionY;
    private int innerCircleCenterPositionX;
    private int innerCircleCenterPositionY;

    private int outerCircleRadius;
    private int innerCircleRadius;

    private Paint outerCirclePaint;
    private Paint innerCirclePaint;
    private double joystickCenterToTouchDistance;
    private boolean isPressed;
    private double actuatorX;
    private double actuatorY;

    public Joystick(Context context, int centerPositionX, int centerPositionY, int outerCircleRadius, int innerCircleRadius) {

        // Outer and inner circle make up the joystick
        outerCircleCenterPositionX = centerPositionX;
        outerCircleCenterPositionY = centerPositionY;
        innerCircleCenterPositionX = centerPositionX;
        innerCircleCenterPositionY = centerPositionY;

        // Radii of circles
        this.outerCircleRadius = outerCircleRadius;
        this.innerCircleRadius = innerCircleRadius;

        // Paint of circles
        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(ContextCompat.getColor(context, R.color.joystickOuterCircle));
        outerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(ContextCompat.getColor(context, R.color.joystickInnerCircle));
        innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void update() {
        updateInnerCirclePosition();
    }

    private void updateInnerCirclePosition() {
        innerCircleCenterPositionX = (int) (outerCircleCenterPositionX + actuatorX * outerCircleRadius);
        innerCircleCenterPositionY = (int) (outerCircleCenterPositionY + actuatorY * outerCircleRadius);
    }

    public void draw(Canvas canvas) {
        // Draw outer circle
        canvas.drawCircle(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                outerCircleRadius,
                outerCirclePaint
        );

        // Draw inner circle
        canvas.drawCircle(
                innerCircleCenterPositionX,
                innerCircleCenterPositionY,
                innerCircleRadius,
                innerCirclePaint
        );
    }

    public boolean isPressed(double touchPositionX, double touchPositionY) {
        joystickCenterToTouchDistance = Utils.getDistanceBetweenPoints(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                touchPositionX,
                touchPositionY
        );
        return joystickCenterToTouchDistance < outerCircleRadius;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    public boolean getIsPressed() {
        return isPressed;
    }

    public void setActuator(double touchPositionX, double touchPositionY) {
        double deltaX = touchPositionX - outerCircleCenterPositionX;
        double deltaY = touchPositionY - outerCircleCenterPositionY;
        double deltaDistance = Utils.getDistanceBetweenPoints(0, 0, deltaX, deltaY);

        if (deltaDistance < outerCircleRadius) {
            actuatorX = deltaX / outerCircleRadius;
            actuatorY = deltaY / outerCircleRadius;
        } else {
            actuatorX = deltaX / deltaDistance;
            actuatorY = deltaY / deltaDistance;
        }
    }

    public void resetActuator() {
        actuatorX = 0.0;
        actuatorY = 0.0;
    }

    public double getActuatorX() {
        return actuatorX;
    }

    public double getActuatorY() {
        return actuatorY;
    }

    public int getOuterCircleCenterPositionX() {
        return outerCircleCenterPositionX;
    }

    public void setOuterCircleCenterPositionX(int outerCircleCenterPositionX) {
        this.outerCircleCenterPositionX = outerCircleCenterPositionX;
    }

    public int getOuterCircleCenterPositionY() {
        return outerCircleCenterPositionY;
    }

    public void setOuterCircleCenterPositionY(int outerCircleCenterPositionY) {
        this.outerCircleCenterPositionY = outerCircleCenterPositionY;
    }

    public int getInnerCircleCenterPositionX() {
        return innerCircleCenterPositionX;
    }

    public void setInnerCircleCenterPositionX(int innerCircleCenterPositionX) {
        this.innerCircleCenterPositionX = innerCircleCenterPositionX;
    }

    public int getInnerCircleCenterPositionY() {
        return innerCircleCenterPositionY;
    }

    public void setInnerCircleCenterPositionY(int innerCircleCenterPositionY) {
        this.innerCircleCenterPositionY = innerCircleCenterPositionY;
    }
}
