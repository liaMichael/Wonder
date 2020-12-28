package com.example.wonder.gameobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.example.wonder.GameDisplay;
import com.example.wonder.R;
import com.example.wonder.gamepanel.Joystick;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Room extends GameObject {

    private Context context;

    // Game objects
    private Player player;
    private MoveableObject mudCube;
    private List<Enemy> enemyList = new ArrayList<Enemy>();
    private List<Spell> spellList = new ArrayList<Spell>();
    private List<Spell> enemySpellList = new ArrayList<Spell>();
    private PressurePlate pressurePlate;
    private Door door;

    // Iterators
    private Iterator<Enemy> enemyIterator = enemyList.iterator();
    private Iterator<Spell> spellIterator = spellList.iterator();
    private Iterator<Spell> enemySpellIterator = enemySpellList.iterator();

    // Changeable
    private int numberOfEnemies = 10;
    private int playerSpellDamagePoints = 1;
    private int enemySpellDamagePoints = 1;
    private int playerMaxHealthPoints = 10;
    private int enemyMaxHealthPoints = 2;
    private double enemySpeedPixelsPerSecond = Player.SPEED_PIXELS_PER_SECOND  * 0.2;

    private int numberOfSpellsToCast = 0;
    private int enemiesAlive = numberOfEnemies;

    private Bitmap border;

    private boolean finish;

    private int sizeMultiplier = 8;
    public final int TILE_SIZE = 16 * sizeMultiplier;
    private int borderWidth = 5 * sizeMultiplier;

    public Room(Context context, Joystick joystick) {
        super(BitmapFactory.decodeResource(context.getResources(), R.drawable.room), 0, 0);
        this.context = context;

        border = BitmapFactory.decodeResource(context.getResources(), R.drawable.room_border);

        // Initialize game objects
        player = new Player(context, joystick, this, positionX + width / 2.0, positionY + height / 2.0);
        player.damagePoints = playerSpellDamagePoints;
        player.maxHealthPoints = playerMaxHealthPoints;
        mudCube = new MoveableObject(context, positionX + TILE_SIZE * 3, positionY + TILE_SIZE * 3, this);
        pressurePlate = new PressurePlate(context, positionX + TILE_SIZE * 5, positionY + TILE_SIZE * 4);
        door = new Door(context, positionX + TILE_SIZE * 4.5, positionY);
        door.positionY = positionY + TILE_SIZE - door.height;
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
                (float) gameDisplay.gameToDisplayCoordinatesX(positionX - borderWidth),
                (float) gameDisplay.gameToDisplayCoordinatesY(positionY - TILE_SIZE),
                paint
        );

        // Draw door
        if (door.isOpen()) {
            door.draw(canvas, gameDisplay);
        }

        // Draw game objects
        pressurePlate.draw(canvas, gameDisplay);
        if (enemiesAlive == 0) {
            mudCube.draw(canvas, gameDisplay);
        }
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

        if (door.isOpen() && isColliding(player, door) && player.positionY <= this.positionY) {
            finish = true;
        }

        if (isColliding(player, mudCube) && enemiesAlive == 0) {
            if (player.getMudCube() == null) {
                mudCube.setDirection(player.direction);
                player.setMudCube(mudCube);
            }
        } else {
            player.setMudCube(null);
        }

        // Spawn enemy if it is time to spawn new enemies
        if (Enemy.readyToSpawn() && numberOfEnemies > 0) {
            Enemy enemy = new Enemy(context, player, this);
            enemy.damagePoints = enemySpellDamagePoints;
            enemy.maxHealthPoints = enemyMaxHealthPoints;
            enemy.setSpeedPixelsPerSecond(enemySpeedPixelsPerSecond);
            enemyList.add(enemy);
            numberOfEnemies--;
        }

        // Player casts a spell if requested
        if (player.wonderPoints <= 0) {
            numberOfSpellsToCast = 0;
        }

        while (numberOfSpellsToCast > 0) {
            spellList.add(new Spell(context, player));
            numberOfSpellsToCast--;
            player.wonderPoints--;
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

        pressurePlate.setPressed(isColliding(player, pressurePlate) || (isColliding(mudCube, pressurePlate) && enemiesAlive == 0));

        // -------------------------------------------------------------
        // Iterate through enemyList and check for collision
        // between each enemy, the player and spells
        // -------------------------------------------------------------
        enemyIterator = enemyList.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            // Enemy and cube collision
            if (isColliding(enemy, mudCube) && enemiesAlive == 0) {
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

                if (isColliding(enemy, spell)) {
                    enemy.setHealthPoints(enemy.getHealthPoints() - spell.getDamagePoints());
                    if (enemy.getHealthPoints() == 0) {
                        if (enemiesAlive == 1) {
                            mudCube.positionX = enemy.positionX;
                            mudCube.positionY = enemy.positionY;
                            if (mudCube.positionX - positionX <= player.width) {
                                mudCube.positionX = positionX + player.width + 5;
                            } else if (positionX + width - mudCube.positionX + mudCube.width <= player.width) {
                                mudCube.positionX = positionX + width - player.width - 5;
                            }
                            if (mudCube.positionY - positionY <= player.height) {
                                mudCube.positionY = positionY + player.height + 5;
                            } else if (positionY + height - mudCube.positionY + mudCube.height <= player.height) {
                                mudCube.positionY = positionY + height - player.height - 5;
                            }
                        }
                        enemiesAlive--;
                        enemyIterator.remove();
                        spellIterator.remove();
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
                if (isColliding(enemySpell, mudCube) && enemiesAlive == 0) {
                    enemySpellIterator.remove();
                    continue;
                }
            }

            // If player and enemy are colliding
            if (isColliding(enemy, player)) {
                // Remove enemy if it collides with the player
                Log.d("Room.java", "Enemy collision. X: " + enemy.getPositionX() + ", Y: " + enemy.getPositionY());
                if (enemiesAlive == 1) {
                    mudCube.positionX = enemy.positionX;
                    mudCube.positionY = enemy.positionY;
                    if (mudCube.positionX - positionX <= player.width) {
                        mudCube.positionX = positionX + player.width + 5;
                    } else if (positionX + width - mudCube.positionX + mudCube.width <= player.width) {
                        mudCube.positionX = positionX + width - player.width - 5;
                    }
                    if (mudCube.positionY - positionY <= player.height) {
                        mudCube.positionY = positionY + player.height + 5;
                    } else if (positionY + height - mudCube.positionY + mudCube.height <= player.height) {
                        mudCube.positionY = positionY + height - player.height - 5;
                    }
                }
                enemiesAlive--;
                enemyIterator.remove();
                player.setHealthPoints(player.getHealthPoints() - 1);
                continue;
            }

            if (!pressurePlate.isPressed()) {
                pressurePlate.setPressed(isColliding(enemy, pressurePlate));
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

                if (isColliding(enemySpell, spell)) {
                    spellIterator.remove();
                    enemySpellIterator.remove();
                    break;
                }
            }

            // If player and enemySpell are colliding
            if (isColliding(enemySpell, player)) {
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
            if (isColliding(enemySpell, mudCube) && enemiesAlive == 0) {
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
            if (isColliding(spell, mudCube) && enemiesAlive == 0) {
                spellIterator.remove();
                continue;
            }
        }

        // Update pressure plate
        pressurePlate.update();

        door.setOpen(pressurePlate.isPressed());
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

    public boolean isFinish() {
        return finish;
    }

    // ---------------
    // Changeable
    // ---------------
    public void setNumberOfEnemies(int numberOfEnemies) {
        this.numberOfEnemies = numberOfEnemies;
    }

    public void setPlayerSpellDamagePoints(int playerSpellDamagePoints) {
        this.playerSpellDamagePoints = playerSpellDamagePoints;
    }

    public void setEnemySpellDamagePoints(int enemySpellDamagePoints) {
        this.enemySpellDamagePoints = enemySpellDamagePoints;
    }

    public void setPlayerMaxHealthPoints(int playerMaxHealthPoints) {
        this.playerMaxHealthPoints = playerMaxHealthPoints;
    }

    public void setEnemyMaxHealthPoints(int enemyMaxHealthPoints) {
        this.enemyMaxHealthPoints = enemyMaxHealthPoints;
    }

    public void setEnemySpeedPixelsPerSecond(double enemySpeedPixelsPerSecond) {
        this.enemySpeedPixelsPerSecond = enemySpeedPixelsPerSecond;
    }
}
