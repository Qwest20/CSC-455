package com.finalproject.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.finalproject.game.Base;
import com.finalproject.game.menus.Pause;

import java.util.Random;

public class Overworld extends ScreenAdapter {

    // Horizontal boundaries along the edges of the map
    private Polygon leftBound = new Polygon();
    private Polygon rightBound = new Polygon();

    // start with a reference foundation of the base class
    private final Base base;

    // handy variables to keep track of throughout game lifetime
    private float elapsedTime;
    private float currentX, currentY;

    // character elements
    private Animation<TextureRegion> downAni;
    private Animation<TextureRegion> upAni;
    private Animation<TextureRegion> leftAni;
    private Animation<TextureRegion> rightAni;
    private int walkState = 4;
    private int lastDir = 4;

    // overworld Blocks
    private final Texture block1 = new Texture(Gdx.files.internal("Blocks/Block1.png")); // 96 x 156 (top left)
    private final Texture block2 = new Texture(Gdx.files.internal("Blocks/Block2.png")); // 82 x 110 (top right)
    private final Texture block3 = new Texture(Gdx.files.internal("Blocks/Block3.png")); // 180 x 160 (bottom left)
    private final Texture block4 = new Texture(Gdx.files.internal("Blocks/Block4.png")); // 96 x 223 (bottom right)
    private final Rectangle[] blocks = new Rectangle[4];

    public Overworld(Base b) {
        this.base = b;
    }

    // works like create
    @Override
    public void show() {

        // initialize boundary polygons
        leftBound.setVertices(new float[] {-1280,1460,-1280,550,-550,1460});
        rightBound.setVertices(new float[] {2560,1460,2560,570,1860,1460});

        // start the music
        base.overworldBGMPlayer.setLooping(true);
        base.overworldBGMPlayer.setVolume(1);
        base.overworldBGMPlayer.play();

        // block initialization
        Rectangle B1 = new Rectangle(40, 960, 96*3, base.melancholy.height);
        Rectangle B2 = new Rectangle(1230, 1100, 96*3, base.melancholy.height);
        Rectangle B3 = new Rectangle(-700, 1280 - 1045, 96*3, base.melancholy.height);
        Rectangle B4 = new Rectangle(1525, 1280 - 1190, 96*3, base.melancholy.height);
        blocks[0] = B1;
        blocks[1] = B2;
        blocks[2] = B3;
        blocks[3] = B4;

        // animation stuffs
        TextureRegion[][] tmpFrames = TextureRegion.split
                (new Texture("Melancholy.Ref/MelancholySpriteSheet.png"), 128, 192);
        TextureRegion[] downWalk = new TextureRegion[4];
        TextureRegion[] upWalk = new TextureRegion[4];
        TextureRegion[] leftWalk = new TextureRegion[4];
        TextureRegion[] rightWalk = new TextureRegion[4];
        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0)
                    rightWalk[index++] = tmpFrames[i][j];
                else if (i == 1)
                    leftWalk[index++] = tmpFrames[i][j];
                else if (i == 2)
                    downWalk[index++] = tmpFrames[i][j];
                else
                    upWalk[index++] = tmpFrames[i][j];
            }
            // reset index variable, since we are on a new row
            index = 0;
        }

        downAni = new Animation(1f / 4f, downWalk);
        upAni = new Animation(1f / 4f, upWalk);
        leftAni = new Animation(1f / 4f, leftWalk);
        rightAni = new Animation(1f / 4f, rightWalk);
    }

    @Override
    public void render(float delta) {

        boolean collided = false;

        // rest walkState, so we don't assume that we're walking on the next frame
        // this should only be changed from 4 if a key is being pressed
        walkState = 4;

        ScreenUtils.clear(Color.valueOf("000000"));

        // Enemy collision will start a turn based fight
        for (Rectangle e : base.enemies) {
            if (base.melancholy.overlaps(e)) {
                base.overworldBGMPlayer.pause();
                base.setScreen(new Battle(base, base.eSList.get(base.enemies.indexOf(e)), this)); // go to the battle screen with this enemy
                base.eSList.remove(base.enemies.indexOf(e));
                base.enemies.remove(e); // assume a win since we otherwise won't come back here
                break;
            } else {
                Random rand = new Random();
                int direc = rand.nextInt(4); // 0 - 3
                switch (direc) {
                    case 0:
                        if(!(e.x + base.enemySpeed > 2530 - e.width))
                            e.x += base.enemySpeed;
                        else
                            e.x = 2530 - e.width;
                        break;
                    case 1:
                        if(!(e.x - base.enemySpeed < 0))
                            e.x -= base.enemySpeed;
                        else
                            e.x = 0;
                        break;
                    case 2:
                        if(!(e.y + base.enemySpeed > 1425))
                            e.y += base.enemySpeed;
                        else
                            e.y = 1425;
                        break;
                    default:
                        if(!(e.y - base.enemySpeed < 0))
                            e.y -= base.enemySpeed;
                        else
                            e.y = 0;
                        break;
                }
            }
        }

        // check for pause input
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            base.overworldBGMPlayer.pause();
            base.setScreen(new Pause(base, this));
        }

        // input directions
        // check for any collision with the boundaries or the blocks
        for (Rectangle b : blocks) {
            if (b.overlaps(base.melancholy))
                collided = true;
        }

        // horizontal walls
        Polygon melPoly = new Polygon();
        melPoly.setVertices(melToVertices(base.melancholy));
        if(Intersector.overlapConvexPolygons(melPoly,leftBound)){
            collided = true;
        }
        else if (Intersector.overlapConvexPolygons(melPoly,rightBound)){
            collided = true;
        }
        if (!collided) {
            // save player position from before the collision with the building, since we know this is okay
            currentX = base.melancholy.x;
            currentY = base.melancholy.y;

            // move the player
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                if(!(base.melancholy.y + base.melancholySpeed > 1425))
                    base.melancholy.y += base.melancholySpeed;
                else
                    base.melancholy.y = 1425;
                walkState = 0;
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                if(!(base.melancholy.x - base.melancholySpeed < -1280))
                    base.melancholy.x -= base.melancholySpeed;
                else
                    base.melancholy.x = -1280;
                walkState = 1;
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if(!(base.melancholy.y - base.melancholySpeed < 50))
                    base.melancholy.y -= base.melancholySpeed;
                else
                    base.melancholy.y = 50;
                walkState = 2;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                if(!(base.melancholy.x + base.melancholySpeed > 2560 - base.melancholy.width))
                    base.melancholy.x += base.melancholySpeed;
                else
                    base.melancholy.x = 2560 - base.melancholy.width;
                walkState = 3;
            }
        } else {
            base.melancholy.x = currentX;
            base.melancholy.y = currentY;
        }

        // increment elapsedTime
        elapsedTime += Gdx.graphics.getDeltaTime();

        // update the camera once per frame (generally good practice)
        base.camera.position.x = base.melancholy.x + base.melancholy.width / 2f;
        base.camera.position.y = base.melancholy.y + base.melancholy.height / 2f;
        base.camera.update();


        // batch drawing phase -----------------------------------------------------------------------------------------
        base.batch.begin();

        // background
        base.batch.draw(base.bgImage, -1280, 0);

        // enemies
        for (Rectangle e : base.enemies) {
            base.batch.draw(base.enemy, e.x, e.y - 49);
        }

        // player animation
        switch (walkState) {
            case 0:
                base.batch.draw(upAni.getKeyFrame(elapsedTime, true), base.melancholy.x, base.melancholy.y - 64);
                break;
            case 1:
                base.batch.draw(leftAni.getKeyFrame(elapsedTime, true), base.melancholy.x, base.melancholy.y - 64);
                break;
            case 2:
                base.batch.draw(downAni.getKeyFrame(elapsedTime, true), base.melancholy.x, base.melancholy.y - 64);
                break;
            case 3:
                base.batch.draw(rightAni.getKeyFrame(elapsedTime, true), base.melancholy.x, base.melancholy.y - 64);
                break;
            default:
                switch (lastDir) {
                    case 0:
                        base.batch.draw(base.upIdle, base.melancholy.x, base.melancholy.y - 64);
                        break;
                    case 1:
                        base.batch.draw(base.leftIdle, base.melancholy.x, base.melancholy.y - 64);
                        break;
                    case 2:
                        base.batch.draw(base.downIdle, base.melancholy.x, base.melancholy.y - 64);
                        break;
                    case 3:
                        base.batch.draw(base.rightIdle, base.melancholy.x, base.melancholy.y - 64);
                        break;
                    default:
                        base.batch.draw(base.downIdle, base.melancholy.x, base.melancholy.y - 64);
                        break;
                }
                walkState = lastDir;
                break;
        }
        // record the walking direction from last time if the next frame has no walking direction specified
        lastDir = walkState;

        // draw the blocks now
        base.batch.draw(block1, blocks[0].x, blocks[0].y);
        base.batch.draw(block2, blocks[1].x, blocks[1].y);
        base.batch.draw(block3, blocks[2].x, blocks[2].y);
        base.batch.draw(block4, blocks[3].x, blocks[3].y);

        base.batch.end();

        // You NEED the following line to actually draw with respect to your camera! XD
        base.batch.setProjectionMatrix(base.camera.combined);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    // helper method for polygon collision
    public static float[] melToVertices(Rectangle mel) {
        float[] result = new float[8];
        result[0] = mel.x;
        result[1] = mel.y;
        result[2] = mel.x + mel.width;
        result[3] = mel.y;
        result[4] = mel.x + mel.width;
        result[5] = mel.y + mel.height;
        result[6] = mel.x;
        result[7] = mel.y + mel.height;
        return result;
    }
}
