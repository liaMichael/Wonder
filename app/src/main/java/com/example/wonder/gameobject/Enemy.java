package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.content.ContextCompat;

import com.example.wonder.Direction;
import com.example.wonder.GameLoop;
import com.example.wonder.R;

import java.util.Random;

/**
 * Enemy is a character which always moves in the direction of the player and casts spells
 * The Enemy class is an extension of a Sprite, which is an extension of GameObject
 */
public class Enemy extends Sprite {

    private Context context;

    private static double speedPixelsPerSecond = Player.SPEED_PIXELS_PER_SECOND  * 0.2;
    private static double maxSpeed = speedPixelsPerSecond / GameLoop.MAX_UPS;

    private static final double SPAWNS_PER_MINUTE = 20;
    private static final double UPDATES_PER_SPAWN = GameLoop.MAX_UPS / (SPAWNS_PER_MINUTE / 60.0);
    private static double updatesUntilNextSpawn = UPDATES_PER_SPAWN;

    private static final double SPELL_CASTS_PER_MINUTE = 10;
    private static final double UPDATES_PER_SPELL_CAST = GameLoop.MAX_UPS / (SPELL_CASTS_PER_MINUTE / 60.0);
    private static double updatesUntilNextSpellCast = UPDATES_PER_SPELL_CAST;

    private final Player player;
    private MoveableObject mudCube;
    private static Random rg = new Random();

    public Enemy(Context context, Player player, Room room) {
        super(
                context,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.golempx_down),
                null,
                null,
                null,
                null,
                room,
                0,
                0,
                2,
                ContextCompat.getColor(context, R.color.statusBarEnemyHealth)
        );

        this.context = context;

        int radiusDistanceToPlayer = 5;

        int minPositionX  = (int) room.positionX;
        int maxPositionX = (int) room.positionX ;
        if (rg.nextInt(2) == 0 && (int) player.positionX - radiusDistanceToPlayer - (int) room.positionX > this.width) {
            // Enemy spawns to the left of the player
            minPositionX = (int) room.positionX;
            maxPositionX = (int) player.positionX - this.width - radiusDistanceToPlayer;
        } else if ((int) room.positionX + room.width - ((int) player.positionX + player.width + radiusDistanceToPlayer) >  this.width){
            // Enemy spawns to the right of the player
            minPositionX = (int) player.positionX + player.width + radiusDistanceToPlayer;
            maxPositionX = (int) room.positionX + room.width - this.width;
        }

        int minPositionY  = (int) room.positionY;
        int maxPositionY = (int) room.positionY;
        if (rg.nextInt(2) == 0 && (int) player.positionY - radiusDistanceToPlayer - (int) room.positionY > this.height) {
            // Enemy spawns above the player
            minPositionY = (int) room.positionY;
            maxPositionY = (int) player.positionY - this.height - radiusDistanceToPlayer;
        } else if ((int) room.positionY + room.height - ((int) player.positionY + player.height + radiusDistanceToPlayer) >  this.height) {
            // Enemy spawns under the player
            minPositionY = (int) player.positionY + player.height + radiusDistanceToPlayer;
            maxPositionY = (int) room.positionY + room.height - this.height;
        }

        if (maxPositionX == minPositionX) {
            positionX = maxPositionX;
        } else {
            positionX = rg.nextInt(maxPositionX - minPositionX) + minPositionX;
            while (mudCube != null) {
                positionX = rg.nextInt(maxPositionX - minPositionX) + minPositionX;
            }
        }

        if (maxPositionY == minPositionY) {
            positionY = maxPositionY;
        } else {
            positionY = rg.nextInt(maxPositionY - minPositionY) + minPositionY;
            while (mudCube != null) {
                positionY = rg.nextInt(maxPositionY - minPositionY) + minPositionY;
            }
        }

        this.player = player;
    }

    /**
     * readyToSpawn checks if a new enemy should spawn, according to the decoded number of spawns
     * per minute (see SPAWNS_PER_MINUTE at top)
     * @return
     */
    public static boolean readyToSpawn() {
        if (updatesUntilNextSpawn <= 0) {
            updatesUntilNextSpawn += UPDATES_PER_SPAWN;
            return true;
        }
        else {
            updatesUntilNextSpawn--;
            return false;
        }
    }

    public boolean readyToCastSpell() {
        if (updatesUntilNextSpellCast <= 0) {
            updatesUntilNextSpellCast += UPDATES_PER_SPELL_CAST;
            return true;
        }
        else {
            updatesUntilNextSpellCast--;
            return false;
        }
    }

    public void update() {
        // -----------------------------------------------------------------------------------------------------
        // Update velocity of the enemy so that the velocity is in the direction of the player
        // -----------------------------------------------------------------------------------------------------
        // Calculate vector from enemy to player (in x and y)
        double distanceToPlayerX = player.getPositionX() - positionX;
        double distanceToPlayerY = player.getPositionY() - positionY;

        // Calculate (absolute) distance between enemy (this) and player
        double distanceToPlayer = GameObject.getDistanceBetweenObjects(this, player);

        // Calculate direction from enemy to player
        if (distanceToPlayer > 0) { // Avoid division by zero
            directionX = distanceToPlayerX / distanceToPlayer;
            directionY = distanceToPlayerY / distanceToPlayer;
        }

        // TODO: Update bitmap animation according to direction
        // Update direction if moving
        if (velocityX != 0 || velocityY != 0) {
            if (velocityX > 0 && velocityX >= Math.abs(velocityY)) {
                // Moving right
                updateDirection(Direction.RIGHT);
                //bitmap = rightWalkingAnimation[(int) currentImageIndex];
            } else if (velocityX < 0 && Math.abs(velocityX) >= Math.abs(velocityY)) {
                // Moving left
                updateDirection(Direction.LEFT);
                //bitmap = leftWalkingAnimation[(int) currentImageIndex];
            } else if (velocityY < 0 && Math.abs(velocityY) >= Math.abs(velocityX)) {
                // Moving up
                updateDirection(Direction.UP);
                //bitmap = upWalkingAnimation[(int) currentImageIndex];
            } else {
                // Moving down
                updateDirection(Direction.DOWN);
                //bitmap = downWalkingAnimation[(int) currentImageIndex];
            }
        }

        // Update bitmap according to direction
        bitmap = findBitmapByName("golempx_" + direction.toString().toLowerCase());
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        // Set velocity in the direction of the player
        velocityX = directionX * maxSpeed;
        velocityY = directionY * maxSpeed;

        keepInBounds();

        if (mudCube != null) {
            if ((positionX > mudCube.positionX && velocityX < 0) ||
                    (positionX < mudCube.positionX && velocityX > 0)) {
                // Enemy moving towards the cube in the X axis
                velocityX = 0;
            }

            if ((positionY > mudCube.positionY && velocityY < 0) ||
                    (positionY < mudCube.positionY && velocityY > 0)) {
                // Enemy moving towards the cube in the Y axis
                velocityY = 0;
            }
        }

        // Update the position of the enemy
        positionX += velocityX;
        positionY += velocityY;
    }

    private void updateDirection(Direction newDirection) {
        if (direction != newDirection) {
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

    public static void setSpeedPixelsPerSecond(double speedPixelsPerSecond) {
        Enemy.speedPixelsPerSecond = speedPixelsPerSecond;
        maxSpeed = speedPixelsPerSecond / GameLoop.MAX_UPS;
    }
}
