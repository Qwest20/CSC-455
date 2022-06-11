package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;

public class GameWinScreen extends ScreenAdapter {

    // start with a reference foundation of the base class
    private Base base;
    public GameWinScreen(Base b) {
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

        // draw game win information on screen
        base.batch.begin();
        base.batch.draw(base.myShipImage, base.myShip.x, base.myShip.y);
        base.myFont.draw(base.batch, "Congratulations! You WIN!", 900 * .2f, 600 * .8f);
        base.myFont.draw(base.batch, "Press ENTER to close the game.", 900 * .075f, 600 * .7f);
        base.batch.end();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
