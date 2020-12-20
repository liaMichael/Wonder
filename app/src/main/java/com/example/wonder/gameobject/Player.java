package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.wonder.Direction;
import com.example.wonder.GameLoop;
import com.example.wonder.Room;
import com.example.wonder.gamepanel.Joystick;
import com.example.wonder.R;
import com.example.wonder.Utils;

/**
 * Player is the main character of the game, which the user can control with a touch joystick.
 * The player class is an extension of a Sprite, which is an extension of GameObject
 */
public class Player extends Sprite {

    private static Context context;
    public static final double SPEED_PIXELS_PER_SECOND = 400.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private final Joystick joystick;
    private MoveableObject mudCube;
    private double currentImageIndex = 0;
    private boolean pushingCube = false;
    private Direction directionToCube = Direction.RIGHT;

    public Player(Context context, Joystick joystick, Room room, double positionX, double positionY) {
        super(
                context,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.orielpx_down),
                null,
                null,
                null,
                null,
                room,
                positionX,
                positionY,
                10,
                ContextCompat.getColor(context, R.color.statusBarPlayerHealth)
        );
        this.joystick = joystick;
        this.context = context;
        mudCube = null;

        // -------------------------
        // Initialize animations
        // -------------------------
        // Right walking animation
        rightWalkingAnimation = new Bitmap[8];
        for (int i = 0; i < rightWalkingAnimation.length; i++) {
            rightWalkingAnimation[i] = findBitmapByName("orielpx_right_walkinganimation_00" + i);
        }

        // Left walking animation
        leftWalkingAnimation = new Bitmap[8];
        for (int i = 0; i < leftWalkingAnimation.length; i++) {
            leftWalkingAnimation[i] = findBitmapByName("orielpx_left_walkinganimation_00" + i);
        }

        // Down walking animation
        downWalkingAnimation = new Bitmap[8];
        for (int i = 0; i < downWalkingAnimation.length; i++) {
            downWalkingAnimation[i] = findBitmapByName("orielpx_down_walkinganimation_00" + i);
        }

        // Up walking animation
        upWalkingAnimation = new Bitmap[8];
        for (int i = 0; i < upWalkingAnimation.length; i++) {
            upWalkingAnimation[i] = findBitmapByName("orielpx_up_walkinganimation_00" + i);
        }
    }

    public void update() {
        // Update bitmap according to direction
        bitmap = findBitmapByName("orielpx_" + direction.toString().toLowerCase());
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        // Update velocity based on actuator of joystick
        velocityX = joystick.getActuatorX() * MAX_SPEED;
        velocityY = joystick.getActuatorY() * MAX_SPEED;

        //Log.d("Player.java", "velocityX: " + velocityX + ". velocityY:" + velocityY + ".");

        keepInBounds();

        // Determine whether player can push cube
        pushingCube = false;
        if (mudCube != null) {
            int countTrue = 0;

            boolean inRangeX = positionX <= mudCube.positionX + mudCube.width / 2.0 &&
                    positionX + width >= mudCube.positionX + mudCube.width / 2.0;
            boolean inRangeY = positionY <= mudCube.positionY + mudCube.height / 2.0 &&
                    positionY + height >= mudCube.positionY + mudCube.height / 2.0;

            if ((positionX + width / 2.0) - (mudCube.positionX + mudCube.width / 2.0) >= Math.abs((positionY + height / 2.0) - (mudCube.positionY + mudCube.height / 2.0))) {
                // Player is to the right of the cube
                if (inRangeY) {
                    directionToCube = Direction.RIGHT;
                    pushingCube = true;
                    countTrue++;
                }
            }

            if ((mudCube.positionX + mudCube.width / 2.0) - (positionX + width / 2.0) >= Math.abs((positionY + height / 2.0) - (mudCube.positionY + mudCube.height / 2.0))) {
                // Player is to the left of the cube
                if (inRangeY) {
                    directionToCube = Direction.LEFT;
                    pushingCube = true;
                    countTrue++;
                }
            }

            if ((positionY + height / 2.0) - (mudCube.positionY + mudCube.height / 2.0) >= Math.abs((positionX + width / 2.0) - (mudCube.positionX + mudCube.width / 2.0) )) {
                // Player is under the cube
                if (inRangeX) {
                    directionToCube = Direction.DOWN;
                    pushingCube = true;
                    countTrue++;
                }
            }

            if ((mudCube.positionY + mudCube.height / 2.0) - (positionY + height / 2.0) >= Math.abs((positionX + width / 2.0) - (mudCube.positionX + mudCube.width / 2.0) )) {
                // Player is above the cube
                if (inRangeX) {
                    directionToCube = Direction.UP;
                    pushingCube = true;
                    countTrue++;
                }
            }

            if (countTrue > 1) {
                pushingCube = false;
            }
        }

        // Update direction if moving
        if (velocityX != 0 || velocityY != 0) {
            // Normalize velocity to get direction (unit vector of velocity)
            double distance = Utils.getDistanceBetweenPoints(0, 0, velocityX, velocityY);
            if (distance != 0) {
                directionX = velocityX / distance;
                directionY = velocityY / distance;
            }

            // Update bitmap animation according to direction
            if (velocityX > 0 && velocityX >= Math.abs(velocityY)) {
                // Moving right
                updateDirection(Direction.RIGHT);
                bitmap = rightWalkingAnimation[(int) currentImageIndex];
            } else if (velocityX < 0 && Math.abs(velocityX) >= Math.abs(velocityY)) {
                // Moving left
                updateDirection(Direction.LEFT);
                bitmap = leftWalkingAnimation[(int) currentImageIndex];
            } else if (velocityY < 0 && Math.abs(velocityY) >= Math.abs(velocityX)) {
                // Moving up
                updateDirection(Direction.UP);
                bitmap = upWalkingAnimation[(int) currentImageIndex];
            } else {
                // Moving down
                updateDirection(Direction.DOWN);
                bitmap = downWalkingAnimation[(int) currentImageIndex];
            }
            // Set frame from animation array
            currentImageIndex += 0.3;
            currentImageIndex %= 8;

            if (mudCube == null || direction != mudCube.getDirection()) {
                pushingCube = false;
            }

            // If player is in the wrong position to push
            if (pushingCube) {
                if ((directionToCube == Direction.RIGHT && direction != Direction.LEFT)  ||
                        (directionToCube == Direction.LEFT && direction != Direction.RIGHT ) ||
                        (directionToCube == Direction.UP && direction != Direction.DOWN ) ||
                        (directionToCube == Direction.DOWN && direction != Direction.UP )) {
                    pushingCube = false;
                }
            }

            // Keep cube in bounds
            if (mudCube != null) {
                if (mudCube.positionY < room.positionY) {
                    if (pushingCube && mudCube.getDirection() == Direction.UP) {
                        pushingCube = false;
                    }
                }
                if (mudCube.positionX < room.positionX) {
                    if (pushingCube && mudCube.getDirection() == Direction.LEFT) {
                        pushingCube = false;
                    }
                }
                if (mudCube.positionY + mudCube.height > room.positionY + room.height) {
                    if (pushingCube && mudCube.getDirection() == Direction.DOWN) {
                        pushingCube = false;
                    }
                }
                if (mudCube.positionX + mudCube.width > room.positionX + room.width) {
                    if (pushingCube && mudCube.getDirection() == Direction.RIGHT) {
                        pushingCube = false;
                    }
                }
            }

            if (pushingCube) {
                switch (mudCube.getDirection()) {
                    case RIGHT:
                    case LEFT:
                        mudCube.velocityX = velocityX;
                        mudCube.velocityY = 0;
                        velocityY = 0;
                        break;
                    case DOWN:
                    case UP:
                        mudCube.velocityX = 0;
                        mudCube.velocityY = velocityY;
                        velocityX = 0;
                        break;
                }
                mudCube.update();
            } else if (mudCube != null) {
                // If not pushing cube but colliding
                mudCube.velocityX = 0;
                mudCube.velocityY = 0;
                mudCube.update();

                switch (directionToCube) {
                    // Player moving towards the cube in the X axis
                    case RIGHT:
                        velocityY = 0;
                        if (velocityX < 0) {
                            velocityX = 0;
                        }
                        break;
                    case LEFT:
                        velocityY = 0;
                        if (velocityX > 0) {
                            velocityX = 0;
                        }
                        break;
                    // Player moving towards the cube in the Y axis
                    case UP:
                        velocityX = 0;
                        if (velocityY > 0) {
                            velocityY = 0;
                        }
                        break;
                    case DOWN:
                        velocityX = 0;
                        if (velocityY < 0) {
                            velocityY = 0;
                        }
                        break;
                }
            }

            // Update position
            positionX += velocityX;
            positionY += velocityY;
        }
    }

    private void updateDirection(Direction newDirection) {
        if (direction != newDirection) {
            currentImageIndex = 0;
            direction = newDirection;
        }
    }

    private Bitmap findBitmapByName(String name) {
        int resId = context.getResources().getIdentifier(
                name,
                "drawable",
                context.getPackageName()
        );
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public MoveableObject getMudCube() {
        return mudCube;
    }
    public void setMudCube(MoveableObject mudCube) {
        this.mudCube = mudCube;
    }

    public boolean isPushingCube() {
        return pushingCube;
    }
}
