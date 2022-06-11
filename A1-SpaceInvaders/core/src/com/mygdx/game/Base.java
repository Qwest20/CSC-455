package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

// A Base class used for any components that will persist throughout the game's lifecycle
public class Base extends Game {

    // Components and Assets made public for reference in multiple game states (Play, Win, Lose)
    // Including components for the player
    public OrthographicCamera camera;
    public SpriteBatch batch;
    public BitmapFont myFont;
    public Music bgm;
    public Texture myShipImage;
    public Rectangle myShip;

    @Override
    public void create() {

        // The sprite batch!
        batch = new SpriteBatch();

        // Specify player components, including image and starting position
        myShipImage = new Texture(Gdx.files.internal("MyAssets/MyShip.png")); // 78x57
        myShip = new Rectangle();
        myShip.x = 600 / 2 - 78 / 2;
        myShip.y = 10;
        myShip.width = 78;
        myShip.height = 57;

        // Specify the music (DMCA Free music from Pierre Oliver)
        bgm = Gdx.audio.newMusic(Gdx.files.internal("BorrowedAssets/Pierre Oliver - Dystopia _ Ninety9Lives Release.mp3"));

        // define the camera, a window that allows us to partially view our game world
        // In our case here however, game dimensions are equivalent to camera dimensions
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 900, 600);

        // initialize font asset
        myFont = new BitmapFont(Gdx.files.internal("BorrowedAssets/SquidGame.fnt"),
                Gdx.files.internal("BorrowedAssets/SquidGame.png"), false);

        // specify the play screen as the first screen to be used
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose () {
        // some cleanup
        batch.dispose();
        bgm.dispose();
        myFont.dispose();
        myShipImage.dispose();
    }
}
