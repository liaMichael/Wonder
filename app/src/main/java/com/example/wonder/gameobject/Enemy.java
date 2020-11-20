package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.core.content.ContextCompat;

import com.example.wonder.GameLoop;
import com.example.wonder.R;
import com.example.wonder.Room;

import java.util.Random;

/**
 * Enemy is a character which always moves in the direction of the player and casts spells
 * The Enemy class is an extension of a Sprite, which is an extension of GameObject
 */
public class Enemy extends Sprite {

    private Context context;

    private static final double SPEED_PIXELS_PER_SECOND = Player.SPEED_PIXELS_PER_SECOND  * 0.4;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;

    private static final double SPAWNS_PER_MINUTE = 20;
    private static final double UPDATES_PER_SPAWN = GameLoop.MAX_UPS / (SPAWNS_PER_MINUTE / 60.0);
    private static double updatesUntilNextSpawn = UPDATES_PER_SPAWN;

    private static final double SPELL_CASTS_PER_MINUTE = 10;
    private static final double UPDATES_PER_SPELL_CAST = GameLoop.MAX_UPS / (SPELL_CASTS_PER_MINUTE / 60.0);
    private static double updatesUntilNextSpellCast = UPDATES_PER_SPELL_CAST;

    private final Player player;
    private static Random rg = new Random();

    public Enemy(Context context, Player player, Room room) {
        super(
                context,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.golempx_front),
                room,
                0,
                0,
                2,
                ContextCompat.getColor(context, R.color.statusBarEnemyHealth)
        );

        int radiusDistanceToPlayer = 5;

        int minPositionX  = (int) room.positionX;
        int maxPositionX = (int) room.positionX;
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

        positionX = rg.nextInt(maxPositionX - minPositionX) + minPositionX;
        positionY = rg.nextInt(maxPositionY - minPositionY) + minPositionY;

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

        // Set velocity in the direction of the player
        velocityX = directionX * MAX_SPEED;
        velocityY = directionY * MAX_SPEED;

        // Update the position of the enemy
        positionX += velocityX;
        positionY += velocityY;

        keepInBounds();

        // TODO: Update bitmap according to direction
        /**if (velocityX != 0 || velocityY != 0) {
            if (velocityX > 0 && velocityX >= Math.abs(velocityY)) {
                // Moving right
                //Log.d("Enemy.java", "Moving right");
            } else if (velocityX < 0 && Math.abs(velocityX) >= Math.abs(velocityY)) {
                // Moving left
                //Log.d("Enemy.java", "Moving left");
            } else if (velocityY < 0 && Math.abs(velocityY) >= Math.abs(velocityX)) {
                // Moving up
                //Log.d("Enemy.java", "Moving up");
            } else {
                // Moving down
                //Log.d("Enemy.java", "Moving down");
            }
        }**/
    }
}
