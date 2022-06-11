package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;

public class GameOverScreen extends ScreenAdapter {

    // start with a reference foundation of the base class
    private Base base;
    public GameOverScreen(Base b) {
        this.base = b;
    }

    @Override
    public void show() {

        // update input readings on this screen in particular
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keyCode) {

                if (keyCode == Input.Keys.ENTER) {
                    System.exit(0);
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {

        // stop the music
        base.bgm.stop();

        // draw game lose information on screen
        base.batch.begin();
        base.myFont.draw(base.batch, "Game Over! You LOSE!", 900 * .25f, 600 * .2f);
        base.myFont.draw(base.batch, "Press ENTER to close the game.", 900 * .075f, 600 * .1f);
        base.batch.end();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
