package com.example.wonder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.example.wonder.gameobject.Enemy;
import com.example.wonder.gameobject.GameObject;
import com.example.wonder.gameobject.MoveableObject;
import com.example.wonder.gameobject.Player;
import com.example.wonder.gameobject.Spell;
import com.example.wonder.gamepanel.Joystick;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Room extends GameObject {

    private Context context;
    private Player player;
    private MoveableObject mudCube;
    private List<Enemy> enemyList = new ArrayList<Enemy>();
    private Iterator<Enemy> enemyIterator = enemyList.iterator();
    private List<Spell> spellList = new ArrayList<Spell>();
    private Iterator<Spell> spellIterator = spellList.iterator();
    private List<Spell> enemySpellList = new ArrayList<Spell>();
    private Iterator<Spell> enemySpellIterator = enemySpellList.iterator();
    private int numberOfSpellsToCast = 0;
    private int numberOfEnemies = 10;
    private Bitmap border;

    public Room(Context context, Joystick joystick) {
        super(BitmapFactory.decodeResource(context.getResources(), R.drawable.room), 0, 0);
        this.context = context;

        border = BitmapFactory.decodeResource(context.getResources(), R.drawable.room_border);

        // Initialize game objects
        player = new Player(context, joystick, this, positionX + width / 2.0, positionY + height / 2.0);
        mudCube = new MoveableObject(context, 400, 400, this);
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        // Draw room
        Paint paint = new Paint();
        canvas.drawBitmap(
                bitmap,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY),
                paint
        );
        canvas.drawBitmap(
                border,
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX - 5 * 8),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY),
                paint
        );

        // Draw game objects
        player.draw(canvas, gameDisplay);
        mudCube.draw(canvas, gameDisplay);

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

        if (isColliding(player, mudCube)) {
            if (player.getMudCube() == null) {
                mudCube.setDirection(player.getDirection());
                player.setMudCube(mudCube);
            }
        } else {
            player.setMudCube(null);
        }

        // Spawn enemy if it is time to spawn new enemies
        if (Enemy.readyToSpawn() && numberOfEnemies > 0) {
            enemyList.add(new Enemy(context, player, this));
            numberOfEnemies--;
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

        // -------------------------------------------------------
        // Iterate through enemyList and check for collision
        // between each enemy, the player and spells
        // -------------------------------------------------------
        enemyIterator = enemyList.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            // Enemy and cube collision
            if (isColliding(enemy, mudCube)) {
                if (enemy.getMudCube() == null) {
                    enemy.setMudCube(mudCube);
                }
            } else {
                enemy.setMudCube(null);
            }

            // Enemies cast spell
            if (enemy.readyToCastSpell()) {
                enemySpellList.add(new Spell(context, enemy));
            }

            // If spell and enemy are colliding
            spellIterator = spellList.iterator();
            while (spellIterator.hasNext()) {
                Spell spell = spellIterator.next();

                if (GameObject.isColliding(enemy, spell)) {
                    spellIterator.remove();
                    enemy.setHealthPoints(enemy.getHealthPoints() - spell.getDamagePoints());
                    if (enemy.getHealthPoints() == 0) {
                        enemyIterator.remove();
                        break;
                    }
                }
            }

            // Remove enemySpell if it reaches a wall
            enemySpellIterator = enemySpellList.iterator();
            while (enemySpellIterator.hasNext()) {
                Spell enemySpell = enemySpellIterator.next();

                if (enemySpell.getPositionY() < this.positionY ||
                        enemySpell.getPositionX() < this.positionX ||
                        enemySpell.getPositionY() + enemySpell.getHeight() > this.positionY + this.height ||
                        enemySpell.getPositionX() + enemySpell.getWidth() > this.positionX + this.width) {
                    enemySpellIterator.remove();
                    continue;
                }

                // Remove enemySpell if it reaches a cube
                if (isColliding(enemySpell, mudCube)) {
                    enemySpellIterator.remove();
                    continue;
                }
            }

            // If player and enemy are colliding
            if (enemy != null) {
                if (GameObject.isColliding(enemy, player)) {
                    // Remove enemy if it collides with the player
                    Log.d("Game.java", "Enemy collision. X: " + enemy.getPositionX() + ", Y: " + enemy.getPositionY());
                    enemyIterator.remove();
                    player.setHealthPoints(player.getHealthPoints() - 1);
                    continue;
                }
            }
        }

        // -------------------------------------------------------------
        // Iterate through enemySpellList and check for collision
        // between each enemySpell, the player and spells
        // -------------------------------------------------------------
        enemySpellIterator = enemySpellList.iterator();
        while (enemySpellIterator.hasNext()) {
            Spell enemySpell = enemySpellIterator.next();

            // If spell and enemySpell are colliding
            spellIterator = spellList.iterator();
            while (spellIterator.hasNext()) {
                Spell spell = spellIterator.next();

                if (GameObject.isColliding(enemySpell, spell)) {
                    spellIterator.remove();
                    enemySpellIterator.remove();
                    break;
                }
            }

            // If player and enemySpell are colliding
            if (GameObject.isColliding(enemySpell, player)) {
                enemySpellIterator.remove();
                player.setHealthPoints(player.getHealthPoints() - 1);
                continue;
            }
        }

        // Remove enemySpell if it reaches a wall
        enemySpellIterator = enemySpellList.iterator();
        while (enemySpellIterator.hasNext()) {
            Spell enemySpell = enemySpellIterator.next();

            if (enemySpell.getPositionY() < this.positionY ||
                    enemySpell.getPositionX() < this.positionX ||
                    enemySpell.getPositionY() + enemySpell.getHeight() > this.positionY + this.height ||
                    enemySpell.getPositionX() + enemySpell.getWidth() > this.positionX + this.width) {
                enemySpellIterator.remove();
                continue;
            }

            // Remove enemySpell if it reaches a cube
            if (isColliding(enemySpell, mudCube)) {
                enemySpellIterator.remove();
                continue;
            }
        }

        // -----------------------------------------------------
        // Iterate through spellList and check for collision
        // between each spell, the walls and the cube
        // -----------------------------------------------------
        // Remove spell if it reaches a wall
        spellIterator = spellList.iterator();
        while (spellIterator.hasNext()) {
            Spell spell = spellIterator.next();

            if (spell.getPositionY() < this.positionY ||
                    spell.getPositionX() < this.positionX ||
                    spell.getPositionY() + spell.getHeight() > this.positionY + this.height ||
                    spell.getPositionX() + spell.getWidth() > this.positionX + this.width) {
                spellIterator.remove();
                continue;
            }

            // Remove spell if it reaches a cube
            if (isColliding(spell, mudCube)) {
                spellIterator.remove();
                continue;
            }
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
