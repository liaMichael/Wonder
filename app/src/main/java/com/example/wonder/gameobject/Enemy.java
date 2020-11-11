package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import androidx.core.content.ContextCompat;

import com.example.wonder.GameDisplay;
import com.example.wonder.GameLoop;
import com.example.wonder.R;
import com.example.wonder.Utils;

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

    public Enemy(Context context, Player player) {
        super(
                context,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.golempx_front),
                rg.nextInt((int) ((player.getPositionX() + 500) - (player.getPositionX() - 500))) + (player.getPositionX() - 500),
                rg.nextInt((int) ((player.getPositionY() + 500) - (player.getPositionY() - 500))) + (player.getPositionX() - 500),
                2,
                ContextCompat.getColor(context, R.color.statusBarEnemyHealth)
        );

        while (GameObject.isColliding(this, player)) {
            positionX = rg.nextInt((int) ((player.getPositionX() + 500) - (player.getPositionX() - 500))) + (player.getPositionX() - 500);
            positionY = rg.nextInt((int) ((player.getPositionY() + 500) - (player.getPositionY() - 500))) + (player.getPositionX() - 500);
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

        // Set velocity in the direction of the player
        velocityX = directionX * MAX_SPEED;
        velocityY = directionY * MAX_SPEED;

        // Update the position of the enemy
        positionX += velocityX;
        positionY += velocityY;

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
