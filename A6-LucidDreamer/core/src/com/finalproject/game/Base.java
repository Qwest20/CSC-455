package com.finalproject.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.finalproject.game.entities.EnemyStats;
import com.finalproject.game.entities.MelancholyStats;
import com.finalproject.game.menus.Title;

import java.util.ArrayList;

public class Base extends Game {

    // public objects for all the classes
    public SpriteBatch batch;
    public BitmapFont myFont;
    public BitmapFont myTitleFont;
    public OrthographicCamera camera;
    public Music overworldBGMPlayer;
    public Music battleBGMPlayer;
    public Music titleBGMPlayer;
    public Music gameWinBGMPlayer;
    public Music gameLoseBGMPlayer;

    // Overworld
    public Rectangle melancholy;
    public Texture rightIdle, leftIdle, upIdle, downIdle;
    public int melancholySpeed = 3;
    public float enemySpeed = 3;
    public ArrayList<Rectangle> enemies;
    public Texture enemy, Ghost, Spider, Broccoli, bgImage;

    // Battle
    public MelancholyStats mS;
    public ArrayList<EnemyStats> eSList;

    @Override
    public void create() {

        // initialize tools
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.update();

        // initialize music
        overworldBGMPlayer = Gdx.audio.newMusic(Gdx.files.internal("Music/overworld_4_indy.mp3"));
        battleBGMPlayer = Gdx.audio.newMusic(Gdx.files.internal("Music/fight_4_indy_alt_1.mp3"));
        titleBGMPlayer = Gdx.audio.newMusic(Gdx.files.internal("Music/title_screen_indy_2.mp3"));
        gameWinBGMPlayer = Gdx.audio.newMusic(Gdx.files.internal("Music/goodending_4_indy.mp3"));
        gameLoseBGMPlayer = Gdx.audio.newMusic(Gdx.files.internal("Music/badending_4_indy_1.mp3"));

        // initialize fonts
        myFont = new BitmapFont(Gdx.files.internal("UI/Font Stuff/SquidGame.fnt"),
                Gdx.files.internal("UI/Font Stuff/SquidGame.png"), false);
        myTitleFont = new BitmapFont(Gdx.files.internal("UI/Font Stuff/SquidGameLarge.fnt"),
                Gdx.files.internal("UI/Font Stuff/SquidGameLarge.png"), false);

        // initialize overworld stuffs
        bgImage = new Texture(Gdx.files.internal("Backgrounds/DreamerCave.png")); // screen dimensions
        melancholy = new Rectangle((1280/2f - 128/2f),(720/2f - 128/2f),128, 128);
        rightIdle = new Texture(Gdx.files.internal("Melancholy.Ref/Melancholy1.png"));
        leftIdle = new Texture(Gdx.files.internal("Melancholy.Ref/Melancholy5.png"));
        downIdle = new Texture(Gdx.files.internal("Melancholy.Ref/Melancholy9.png"));
        upIdle = new Texture(Gdx.files.internal("Melancholy.Ref/Melancholy13.png"));
        enemy = new Texture(Gdx.files.internal("Enemies/Enemy.png"));
        Ghost = new Texture(Gdx.files.internal("Enemies/ghost.png"));
        Spider = new Texture(Gdx.files.internal("Enemies/Spider.png"));
        Broccoli = new Texture(Gdx.files.internal("Enemies/Broccoli.png"));
        // in the future, automate this and generate them randomly within the conceivable game space
        Rectangle enemy1 = new Rectangle(-720,400,96,96);
        Rectangle enemy2 = new Rectangle(1200,600,96,96);
        Rectangle enemy3 = new Rectangle(400,1400,96,96);
        enemies = new ArrayList<>();
        enemies.add(enemy1);
        enemies.add(enemy2);
        enemies.add(enemy3);

        // initialize battle stuffs
        mS = new MelancholyStats("Melancholy", 100, 10, 8, rightIdle);
        EnemyStats e1 = new EnemyStats("Spider", 70, 5, 7, Spider);
        EnemyStats e2 = new EnemyStats("Ghost", 150, 8, 5, Ghost);
        EnemyStats e3 = new EnemyStats("Broccoli", 10, 3, 120, Broccoli);
        eSList = new ArrayList<>();
        eSList.add(e1);
        eSList.add(e2);
        eSList.add(e3);

        // specify the title screen as the first screen to be used
        setScreen(new Title(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        myFont.dispose();
        myTitleFont.dispose();
        rightIdle.dispose();
        leftIdle.dispose();
        upIdle.dispose();
        downIdle.dispose();
        overworldBGMPlayer.dispose();
        gameLoseBGMPlayer.dispose();
        gameWinBGMPlayer.dispose();
        titleBGMPlayer.dispose();
        battleBGMPlayer.dispose();
    }
}
