package com.example.wonder;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.example.wonder.gameobject.Enemy;
import com.example.wonder.gameobject.GameObject;
import com.example.wonder.gameobject.Player;
import com.example.wonder.gameobject.Spell;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Room extends GameObject {

    private Context context;
    private Player player;
    private List<Enemy> enemyList = new ArrayList<Enemy>();
    private List<Spell> spellList = new ArrayList<Spell>();
    private List<Spell> enemySpellList = new ArrayList<Spell>();
    private int numberOfSpellsToCast = 0;

    public Room(Context context, Player player) {
        super(BitmapFactory.decodeResource(context.getResources(), R.drawable.tmp_room), 0, 0);
        this.context = context;
        this.player = player;
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
            enemyList.add(new Enemy(context, player, this));
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

            Iterator<Spell> enemySpellIterator = enemySpellList.iterator();
            Iterator<Spell> spellIterator = spellList.iterator();
            while (spellIterator.hasNext()) {
                Spell spell = spellIterator.next();
                if (GameObject.isColliding(spell, enemy)) {
                    // Remove spell if it collides with an enemy
                    spellIterator.remove();
                    enemy.setHealthPoints(enemy.getHealthPoints() - spell.getDamagePoints());
                    if (enemy.getHealthPoints() == 0) {
                        enemyIterator.remove();
                    }
                    break;
                }

                if (enemySpellIterator.hasNext()) {
                    Spell enemySpell = enemySpellIterator.next();
                    if (GameObject.isColliding(spell, enemySpell)) {
                        // If enemySpell and spell collide, remove both
                        spellIterator.remove();
                        enemySpellIterator.remove();
                        break;
                    }
                }
            }

            while (enemySpellIterator.hasNext()) {
                Spell enemySpell = enemySpellIterator.next();

                // Remove enemySpell if it reaches a wall
                if (enemySpell.getPositionY() < this.positionY ||
                        enemySpell.getPositionX() < this.positionX ||
                        enemySpell.getPositionY() + enemySpell.getHeight() > this.positionY + this.height ||
                        enemySpell.getPositionX() + enemySpell.getWidth() > this.positionX + this.width) {
                    enemySpellIterator.remove();
                } else if (GameObject.isColliding(enemySpell, player)) {
                    // Remove enemySpell if it collides with the player
                    enemySpellIterator.remove();
                    player.setHealthPoints(player.getHealthPoints() - enemySpell.getDamagePoints());
                    Log.d("Game.java", "enemySpell collision. X: " + enemySpell.getPositionX() + ", Y: " + enemySpell.getPositionY());
                    break;
                }
                if (spellIterator.hasNext()) {
                    Spell spell = spellIterator.next();
                    if (GameObject.isColliding(spell, enemySpell)) {
                        // If enemySpell and spell collide, remove both
                        spellIterator.remove();
                        enemySpellIterator.remove();
                        break;
                    }
                }
            }
        }

        Iterator<Spell> enemySpellIterator = enemySpellList.iterator();
        Iterator<Spell> spellIterator = spellList.iterator();
        while (spellIterator.hasNext()) {
            Spell spell = spellIterator.next();

            // Remove spell if it reaches a wall
            if (spell.getPositionY() < this.positionY ||
                    spell.getPositionX() < this.positionX ||
                    spell.getPositionY() + spell.getHeight() > this.positionY + this.height ||
                    spell.getPositionX() + spell.getWidth() > this.positionX + this.width) {
                spellIterator.remove();
                break;
            }

            if (enemySpellIterator.hasNext()) {
                Spell enemySpell = enemySpellIterator.next();
                if (GameObject.isColliding(spell, enemySpell)) {
                    // If enemySpell and spell collide, remove both
                    spellIterator.remove();
                    enemySpellIterator.remove();
                    break;
                }
            }
        }
    }

    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getNumberOfSpellsToCast() {
        return numberOfSpellsToCast;
    }

    public void setNumberOfSpellsToCast(int numberOfSpellsToCast) {
        this.numberOfSpellsToCast = numberOfSpellsToCast;
    }
}
