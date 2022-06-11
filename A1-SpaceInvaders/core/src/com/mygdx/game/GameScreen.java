package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;
import java.util.Random;

public class GameScreen extends ScreenAdapter {

	// start with a reference foundation of the base class
	private Base base;
	public GameScreen(Base b) {
		this.base = b;
	}

	// private variables to define our assets
	private Texture livesImage;
	private Texture enemyShipImage;
	private Texture shotImage;
	private Texture enemyShotImage;
	private Texture moundFull;
	private Texture moundHalf;
	private Texture moundQuarter;
	private Sound shootSound;
	private Sound enemyShootSound;
	private Sound enemyDamageSound;
	private Sound shipDamageSound;
	private Sound moundDamageSound;
	private Texture bgImage;
	private Sound gameOverSound;

	// Additional Player Information
	private int numLives = 3;

	// Player Bullet Information
	private Array<Rectangle> shots;
	private long lastShotTime; // buffers shots

	// Enemy and Enemy Bullet Information
	private Array<Rectangle> enemies;
	private Array<Rectangle> enemyShots;
	private boolean down = false; // down and left help decide movement pattern
	private boolean left = false;
	private long lastEnemyShotTime; // buffers shots
	private long lastMoveTime; // buffers movement

	// Mounds (Defenses) Information
	private Array<Rectangle> mounds;
	private Array<Integer> moundLife = new Array<>(); // an array of ints to measure life vals of each mound
	
	@Override
	public void show () {

		// load assets via their file addresses
		bgImage = new Texture(Gdx.files.internal("MyAssets/Background.png")); // screen dimensions
		livesImage = new Texture(Gdx.files.internal("MyAssets/LivesIcon.png")); // 56x38
		shotImage = new Texture(Gdx.files.internal("MyAssets/MyBullet.png")); //15x42
		enemyShipImage = new Texture(Gdx.files.internal("MyAssets/MyEnemy.png")); // 69x60
		enemyShotImage = new Texture(Gdx.files.internal("MyAssets/MyEnemyBullet.png")); // 15x42
		shootSound = Gdx.audio.newSound(Gdx.files.internal("BorrowedAssets/PlayerShot.wav"));
		enemyShootSound = Gdx.audio.newSound(Gdx.files.internal("BorrowedAssets/EnemyShot.wav"));
		enemyDamageSound = Gdx.audio.newSound(Gdx.files.internal("BorrowedAssets/EnemyDead.mp3"));
		shipDamageSound = Gdx.audio.newSound(Gdx.files.internal("BorrowedAssets/PlayerDamage.wav"));
		moundDamageSound = Gdx.audio.newSound(Gdx.files.internal("BorrowedAssets/MoundDamage.wav"));
		moundFull = new Texture(Gdx.files.internal("MyAssets/MoundFull.png")); // 90x60
		moundHalf = new Texture(Gdx.files.internal("MyAssets/MoundHalf.png")); // 90x60
		moundQuarter = new Texture(Gdx.files.internal("MyAssets/MoundQuarter.png")); // 90x60
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("BorrowedAssets/GameOver.wav"));

		// start the bgm and loop it
		base.bgm.setLooping(true);
		base.bgm.play();

		// instantiate a new collection of shots, enemy shots, mounds and enemies for this run of the game
		shots = new Array<Rectangle>();
		enemyShots = new Array<Rectangle>();
		enemies = new Array<Rectangle>();
		mounds = new Array<Rectangle>();

		// Enemy Spawn 8x4 grid (this is where we can make modifications for a difficulty feature perhaps!)
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 3; j++) {
				// spawn a new enemy
				Rectangle enemy = new Rectangle();
				enemy.x = 75 + (69+10)*i;
				enemy.y = 550 - (60+10)*j;
				enemy.width = 69;
				enemy.height = 60;
				enemies.add(enemy);
			}
		}

		// Mound Spawn
		for (int i = 0; i < 5; i++) {
			// spawn a new mound
			Rectangle mound = new Rectangle();
			mound.x = 75 + (69+100)*i;
			mound.y = base.myShip.y + 150;
			mound.width = 69;
			mound.height = 60;
			mounds.add(mound);
		}

		// Mound life initialize
		for (int i = 0; i < 5; i++) {
			moundLife.add(6);
		}
	}

	@Override
	public void render (float delta) {

		// IMPORTANT:
		// update the camera once per frame (generally good practice)
		base.camera.update();

		// close the game if we die
		if(numLives == 0) {
			// play the game over sound
			gameOverSound.play();
			// go to the game over screen
			base.setScreen(new GameOverScreen(base));
		}

		// close the game if we win
		if(enemies.size == 0) {
			// go to the game win screen
			base.setScreen(new GameWinScreen(base));
		}

		// Player Movement and shooting
		playerMoveAndShoot();
		
		// Bullet movement or clear
		bulletHandling();

		// Enemy movement, shot creation, and clear
		enemyHandling();

		// Enemy bullet movement and removal
		enemyBulletHandling();

		// check to see if we need to remove any mounds from the scene or update their liveliness
		moundHandling();

		// Spritebatch draw your objects (bottom up ordering mind you)
		base.batch.begin();
		batchWork();
		base.batch.end();
	}

	// RENDER METHODS
	private void playerMoveAndShoot() {
		// Player movement code (keyboard)
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) base.myShip.x -= 300 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) base.myShip.x += 300 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.A)) base.myShip.x -= 300 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.D)) base.myShip.x += 300 * Gdx.graphics.getDeltaTime();

		// Player shooting code (keyboard)
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			// spawn a new shot if enough time has passed since the last one.
			// this appears to be every half second I think...
			if (TimeUtils.nanoTime() - lastShotTime > 500000000) {
				// make a new bullet with respect to the player's current location
				Rectangle shot = new Rectangle();
				shot.x = base.myShip.x + 78 / 2 - 15 / 2;
				shot.y = base.myShip.y + 60;
				shot.width = 15;
				shot.height = 42;
				shots.add(shot);
				lastShotTime = TimeUtils.nanoTime();
				shootSound.play();
			}
		}

		// boundaries (just bump the position off the boundaries)
		if(base.myShip.x < 0) base.myShip.x = 0;
		if(base.myShip.x > 900 - 78) base.myShip.x = 900 - 78;
	}
	private void bulletHandling() {
		for (Iterator<Rectangle> iter = shots.iterator(); iter.hasNext(); ) {
			Rectangle shot = iter.next();
			// shift it up the screen!
			shot.y += 600 * Gdx.graphics.getDeltaTime();
			// remove if it's off the screen
			if(shot.y > 1000) iter.remove();
			// collision is handled among the ENEMIES, since they are the ones getting hit
		}
	}
	private void enemyHandling() {

		// ENEMY MOVEMENT
		if(TimeUtils.nanoTime()/1000000000 - lastMoveTime/1000000000 > 0.5) {
			// shift it down or to a side on screen
			if (!down && !left) {
				// go right
				// iterate through the enemies
				for (Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext(); ) {
					// which enemy?
					Rectangle enemy = iter.next();
					enemy.x += 2000 * Gdx.graphics.getDeltaTime();
				}
				// update booleans
				down = true;
				left = true;
			} else if (down && left) {
				// go down then left after
				// iterate through the enemies
				for (Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext(); ) {
					// which enemy?
					Rectangle enemy = iter.next();
					enemy.y -= 1000 * Gdx.graphics.getDeltaTime();
				}
				// update booleans
				down = false;
				left = true;
			} else if (!down && left) {
				// go left
				// iterate through the enemies
				for (Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext(); ) {
					// which enemy?
					Rectangle enemy = iter.next();
					enemy.x -= 2000 * Gdx.graphics.getDeltaTime();
				}
				// update booleans
				down = true;
				left = false;
			} else if (down && !left) {
				// go down then right after
				// iterate through the enemies
				for (Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext(); ) {
					// which enemy?
					Rectangle enemy = iter.next();
					enemy.y -= 1000 * Gdx.graphics.getDeltaTime();
				}
				// update booleans
				down = false;
				left = false;
			}
			lastMoveTime = TimeUtils.nanoTime();
		}

		// ENEMY SHOOTING

		// a random enemy in the collection will be the designated shooter in this time interval
		Random rand = new Random(); //instance of random class
		int designatedShooter = 0;
		if(enemies.size > 0)
			designatedShooter = rand.nextInt(enemies.size);
		int i=0;

		// iterate through the enemies
		for (Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext(); ) {

			// which enemy?
			Rectangle enemy = iter.next();

			// shooting!
			if(i==designatedShooter && enemies.size > 0){
				// Enemy shot spawn (one every half seconds)
				if(TimeUtils.nanoTime()/1000000000 - lastEnemyShotTime/1000000000 > 0.25) {
					// spawn a new bullet
					Rectangle enemyShot = new Rectangle();
					enemyShot.x = enemy.x + 69 / 2 - 15 / 2;
					enemyShot.y = enemy.y - 30;
					enemyShot.width = 15;
					enemyShot.height = 42;
					enemyShots.add(enemyShot);
					lastEnemyShotTime = TimeUtils.nanoTime();
					enemyShootSound.play();
				}
			}

			// ENEMY COLLISION

			if(enemy.overlaps(base.myShip)) {
				numLives--;
				shipDamageSound.play();
				iter.remove();
				enemyDamageSound.play();
			}
			int j=0;
			for (Rectangle shot:shots) {
				if(enemy.overlaps(shot)) {
					shots.removeIndex(j);
					iter.remove();
					enemyDamageSound.play();
				}
				j++;
			}
			// increment to next enemy
			i++;
		}
	}
	private void enemyBulletHandling() {
		for (Iterator<Rectangle> iter = enemyShots.iterator(); iter.hasNext(); ) {
			Rectangle enemyShot = iter.next();
			// shift it down the screen!
			enemyShot.y -= 600 * Gdx.graphics.getDeltaTime();
			// remove if it's off the screen
			if(enemyShot.y < -100) iter.remove();
			// collision detection for the enemy shots on player
			if(enemyShot.overlaps(base.myShip)){
				iter.remove();
				shipDamageSound.play();
				numLives--;
			}
		}
	}
	private void moundHandling() {

		int i=0;
		outer:
		for (Rectangle mound: mounds) {

			// enemy ran into it
			int j=0;
			for (Rectangle enemy: enemies) {
				if(mound.overlaps(enemy)){
					moundDamageSound.play();
					mounds.removeIndex(i);
					enemies.removeIndex(j);
					// this mound and bullet are destroyed, so we will iterate to the next mound in the collection
					i++;
					continue outer;
				}
				j++;
			}

			// hit by enemy bullet
			int k=0;
			for (Rectangle enemyBullet: enemyShots) {
				if(mound.overlaps(enemyBullet)){
					moundDamageSound.play();
					moundLife.set(i,moundLife.get(i)-1);
					enemyShots.removeIndex(k);
				}
				k++;
			}

			// hit by player bullet
			int l=0;
			for (Rectangle playerBullet: shots) {
				if(mound.overlaps(playerBullet)){
					moundDamageSound.play();
					moundLife.set(i,moundLife.get(i)-1);
					shots.removeIndex(l);
				}
				l++;
			}

			// liveness check
			if(moundLife.get(i) == 0){
				mounds.removeIndex(i);
				moundLife.removeIndex(i);
			}
			i++;
		}
	}
	private void batchWork() {

		// draw the background
		base.batch.draw(bgImage,0,0);
		// draw the player
		if(numLives > 0)
			base.batch.draw(base.myShipImage, base.myShip.x, base.myShip.y);
		// draw the shots
		for(Rectangle shot: shots) {
			base.batch.draw(shotImage, shot.x, shot.y);
		}
		// draw the enemies
		for(Rectangle enemy: enemies) {
			base.batch.draw(enemyShipImage, enemy.x, enemy.y);
		}
		// draw the enemy shots
		for(Rectangle enemyShot: enemyShots) {
			base.batch.draw(enemyShotImage, enemyShot.x, enemyShot.y);
		}
		// draw the mounds
		int i=0;
		for(Rectangle mound: mounds){
			if(moundLife.get(i) > 4){
				// full
				base.batch.draw(moundFull, mound.x, mound.y);
			}
			else if(moundLife.get(i) > 2){
				// half
				base.batch.draw(moundHalf, mound.x, mound.y);
			}
			else if(moundLife.get(i) > 0){
				// quarter
				base.batch.draw(moundQuarter, mound.x, mound.y);
			}
			i++;
		}
		// draw the lives on screen
		base.myFont.draw(base.batch, "Lives:", 10, 600-10);
		for(int k=0; k<numLives; k++){
			base.batch.draw(livesImage, (40*k)+150, 600-40);
		}
	}

	@Override
	public void dispose () {

		// a lot of clean up
		livesImage.dispose();
		enemyShipImage.dispose();
		shotImage.dispose();
		enemyShotImage.dispose();
		moundFull.dispose();
		moundHalf.dispose();
		moundQuarter.dispose();
		shootSound.dispose();
		enemyShootSound.dispose();
		enemyDamageSound.dispose();
		shipDamageSound.dispose();
		moundDamageSound.dispose();
		bgImage.dispose();
		base.myFont.dispose();
	}
}
