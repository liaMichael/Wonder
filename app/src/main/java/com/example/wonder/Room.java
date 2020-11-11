package com.example.wonder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.example.wonder.gameobject.Enemy;
import com.example.wonder.gameobject.GameObject;
import com.example.wonder.gameobject.Player;
import com.example.wonder.gameobject.Spell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Room {

    private Context context;
    private Player player;
    private List<Enemy> enemyList = new ArrayList<Enemy>();
    private List<Spell> spellList = new ArrayList<Spell>();
    private List<Spell> enemySpellList = new ArrayList<Spell>();
    private int numberOfSpellsToCast = 0;
    private Bitmap floorBitmap;
    private Bitmap  wallBitmap;
    private double positionX;
    private double positionY;

    public Room(Context context, Player player) {
        this.context = context;
        this.player = player;
        floorBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tmp_room_floor);
        wallBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tmp_room_wall);
        positionX = 0;
        positionY = 0;
    }

    public boolean isObjectInBounds(GameObject obj) {
        // Check top & bottom
        for (int col = (int) obj.getPositionX(); col < (int) (obj.getPositionX() + obj.getWidth()); col++) {
            if (floorBitmap.getPixel(col, (int) obj.getPositionY()) == Color.TRANSPARENT  ||
                    floorBitmap.getPixel(col, (int) (obj.getPositionY() + obj.getHeight())) == Color.TRANSPARENT ) {
                return false;
            }
        }

        // Check right & left
        for (int row = (int) obj.getPositionY(); row < (int) (obj.getPositionY() + obj.getHeight()); row++) {
            if (floorBitmap.getPixel((int) obj.getPositionX(), row) == Color.TRANSPARENT  || floorBitmap.getPixel((int) (obj.getPositionX() + obj.getWidth()), row) == Color.TRANSPARENT ) {
                return false;
            }
        }

        return true;
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        // Draw floor
        Paint paint = new Paint();
        canvas.drawBitmap(
                floorBitmap,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY),
                paint
        );

        // Draw walls
        canvas.drawBitmap(
                wallBitmap,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY),
                paint
        );

        // Draw game objects
        player.draw(canvas, gameDisplay);


        for (Enemy enemy : enemyList) {
            enemy.draw(canvas, gameDisplay);
        }

        for (Spell spell : spellList) {
            spell.draw(canvas, gameDisplay);
        }

        for (Spell enemySpell : enemySpellList) {
            enemySpell.draw(canvas, gameDisplay);
        }

        player.getHealthBar().draw(canvas, gameDisplay, player.getHealthPoints(), player.getMaxHealthPoints());

        for (Enemy enemy : enemyList) {
            enemy.getHealthBar().draw(canvas, gameDisplay, enemy.getHealthPoints(), enemy.getMaxHealthPoints());
        }
    }

    public void update() {
        player.update();

        // Spawn enemy if it is time to spawn new enemies
        if (Enemy.readyToSpawn()) {
            enemyList.add(new Enemy(context, player));
        }

        // Player casts a spell if requested
        while (numberOfSpellsToCast > 0) {
            spellList.add(new Spell(context, player));
            numberOfSpellsToCast--;
        }

        // Update state of each enemy
        for (Enemy enemy : enemyList) {
            enemy.update();
        }

        // Update state of each spell
        for (Spell spell : spellList) {
            spell.update();
        }
        for (Spell enemySpell : enemySpellList) {
            enemySpell.update();
        }

        // Iterate through enemyList and check for collision between each enemy, the player and
        // all spells
        Iterator<Enemy> enemyIterator = enemyList.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            // Enemies cast spell
            if (enemy.readyToCastSpell()) {
                enemySpellList.add(new Spell(context, enemy));
            }

            if (GameObject.isColliding(enemy, player)) {
                // Remove enemy if it collides with the player
                enemyIterator.remove();
                player.setHealthPoints(player.getHealthPoints() - 1);
                Log.d("Game.java", "Enemy collision. X: " + enemy.getPositionX() + ", Y: " + enemy.getPositionY());
                continue;
            }

            Iterator<Spell> spellIterator = spellList.iterator();
            while (spellIterator.hasNext()) {
                Spell spell = spellIterator.next();
                // Remove spell if it collides with an enemy
                if (GameObject.isColliding(spell, enemy)) {
                    spellIterator.remove();
                    enemy.setHealthPoints(enemy.getHealthPoints() - spell.getDamagePoints());
                    if (enemy.getHealthPoints() == 0) {
                        enemyIterator.remove();
                    }
                    break;
                }
            }

            Iterator<Spell> enemySpellIterator = enemySpellList.iterator();
            while (enemySpellIterator.hasNext()) {
                Spell enemySpell = enemySpellIterator.next();
                // Remove enemySpell if it collides with the player
                if (GameObject.isColliding(enemySpell, player)) {
                    enemySpellIterator.remove();
                    player.setHealthPoints(player.getHealthPoints() - enemySpell.getDamagePoints());
                    Log.d("Game.java", "enemySpell collision. X: " + enemySpell.getPositionX() + ", Y: " + enemySpell.getPositionY());
                    break;
                }
            }

            // TODO: If enemySpell and spell collide, remove both
        }
    }

    public Player getPlayer() {
        return player;
    }

    public int getNumberOfSpellsToCast() {
        return numberOfSpellsToCast;
    }

    public void setNumberOfSpellsToCast(int numberOfSpellsToCast) {
        this.numberOfSpellsToCast = numberOfSpellsToCast;
    }
}
