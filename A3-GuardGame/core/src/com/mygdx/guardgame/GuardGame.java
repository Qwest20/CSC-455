package com.mygdx.guardgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GuardGame extends ApplicationAdapter {

    // global variables for easier tinkering
    private final int playerSpeed = 150;
    private final int guardRadius = 250;
    private final int playerRot = 5; // in degrees

    // tools for use
    private SpriteBatch batch;
    private OrthographicCamera camera;

    // assets
    private Texture bgImage;
    private Texture buildingImage;
    private Texture playerImage;
    private Texture guardImage;
    private Sprite playerSprite;
    private Sprite guardSprite;

    // collision shapes and vars
    private Rectangle building;
    private Rectangle player;
    private Rectangle guard;
    private float currentX;
    private float currentY;

    // state objects for vector and state variable reference
    private GuardState guardState;
    private PlayerState playerState;

    // Font
    BitmapFont myFont;

    // boolean to keep track of player caught
    private boolean playerCaught = false;

    @Override
    public void create() {

        // initialize tools
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        // background setup
        bgImage = new Texture(Gdx.files.internal("Background.png")); // screen dimensions

        // building setup
        buildingImage = new Texture(Gdx.files.internal("Building.png")); // 337 x 200
        building = new Rectangle();
        building.width = 337;
        building.height = 200;
        building.x = 500;
        building.y = 720 - 200 - building.height;

        // player setup
        playerImage = new Texture(Gdx.files.internal("Player.png")); // 60 x 60
        player = new Rectangle();
        player.x = 100;
        player.y = 300;
        player.width = 60;
        player.height = 60;
        playerSprite = new Sprite(playerImage);
        playerSprite.flip(true, false);
        Vector2 location = new Vector2(player.x, player.y);
        Vector2 view = new Vector2(0, 1);
        playerState = new PlayerState(location, view); // no detection radius for this one

        // guard setup (similar process)
        guardImage = new Texture(Gdx.files.internal("Guard.png")); // 60 x 60
        guard = new Rectangle();
        guard.x = building.x - 65;
        guard.y = building.y - 65;
        guard.width = 60;
        guard.height = 60;
        guardSprite = new Sprite(guardImage);
        guardSprite.flip(true, false);
        guardSprite.setPosition(guard.x, guard.y);
        Vector2 locationG = new Vector2(guard.x, guard.y);
        Vector2 viewG = new Vector2(-1, 0); // faces the left first
        guardState = new GuardState(locationG, viewG, guardRadius);

        // initialize font
        myFont = new BitmapFont(Gdx.files.internal("SquidGame.fnt"),
                Gdx.files.internal("SquidGame.png"), false);
    }

    @Override
    public void render() {

        // update the camera once per frame (generally good practice)
        camera.update();

        // allow for player movement and check for game over
        if(player != null) {
            playerMove();
            guardHandling();
        }

        // batch sprite drawing
        batch.begin();
        batch.draw(bgImage, 0, 0);
        batch.draw(buildingImage, building.x, building.y);
        // drawing sprites is a bit different from textures...
        if(!playerCaught)
            playerSprite.draw(batch);
        else
            myFont.draw(batch, "GAME OVER", 100, 100);
        guardSprite.draw(batch);
        // debug menu
//        myFont.draw(batch, "PROPERTIES: ", 100, 700);
//		myFont.draw(batch, "Distance between guard and player:  " +
//				playerState.location.dst(guardState.location), 100, 500);
//        myFont.draw(batch, "Guard's behavior: " + guardState.getCurrentBehavior(), 100, 50);
        batch.end();
    }

    // RENDER METHODS
    private void playerMove() {

        // player is updated in this method and then logged into playerState class

        // collision with guard?
        if(player != null && player.overlaps(guard)){
            player = null;
            playerCaught = true;
            return;
        }

        // Player movement code (Arcade Style), so long as there isn't a collision
        if (!building.overlaps(player)) {
            // save player position from before the collision with the building, since we know this is okay
            currentX = player.x;
            currentY = player.y;

            // forward and back will move the player given their view vector
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)){
                player.x += (playerState.view.x) * playerSpeed * Gdx.graphics.getDeltaTime();
                player.y += (playerState.view.y) * playerSpeed * Gdx.graphics.getDeltaTime();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)){
                player.x -= (playerState.view.x) * playerSpeed * Gdx.graphics.getDeltaTime();
                player.y -= (playerState.view.y) * playerSpeed * Gdx.graphics.getDeltaTime();
            }

            // left and right will turn the view angle
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                playerState.view.rotateDeg(playerRot);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                playerState.view.rotateDeg(-playerRot);
            }
        } else {
            // reload the safe position from the last frame if we collided
            // (will appear as though the player can't move further into the object)
            player.x = currentX;
            player.y = currentY;
        }

        // screen boundaries (just bump the position off the boundaries)
        if (player.x < 0) player.x = 0;
        if (player.x > 1280 - 60) player.x = 1280 - 60;
        if (player.y < 0) player.y = 0;
        if (player.y > 720 - 60) player.y = 720 - 60;

        // update the location vectors for the player object
        // used for finding the distance between the player and the guard
        playerState.location.x = player.x;
        playerState.location.y = player.y;

        // update player sprite
        playerSprite.setPosition(player.x, player.y);
        playerSprite.setRotation(playerState.view.angleDeg());
    }
    private void guardHandling() {

        // opposite of the player, where the state is changed, and then the sprite
        // reflects that change

        // choose behaviors based on the current recorded state in the state machine class
        switch (guardState.getCurrentBehavior()) {
            case SURVEY:
                guardState.guardSurvey(playerState);
                break;
            case CHASE:
                guardState.guardChase(playerState, guardSprite, building, guard);
                break;
            default:
                guardState.guardReturn(playerState, guardSprite, building, guard);
                break;
        }

        // keep the guard sprite where it ought to be, no matter what behavior
        guard.x = guardState.location.x;
        guard.y = guardState.location.y;

        // update guard sprite
        guardSprite.setPosition(guard.x, guard.y);
        guardSprite.setRotation(guardState.view.angleDeg());
    }

    @Override
    public void dispose() {
        bgImage.dispose();
        buildingImage.dispose();
        playerImage.dispose();
        guardImage.dispose();
        myFont.dispose();
        batch.dispose();
    }
}
