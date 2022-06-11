package com.finalproject.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.finalproject.game.Base;
import com.finalproject.game.screens.Overworld;

public class Pause extends ScreenAdapter {

    // start with a reference foundation of the base class
    private final Base base;
    private final Overworld overworld;
    public Pause(Base b, Overworld o) {
        this.base = b;
        this.overworld = o;
    }

    @Override
    public void show() {

        // update input readings on this screen in particular
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keyCode) {

                if (keyCode == Input.Keys.ENTER) {
                    base.overworldBGMPlayer.play();
                    base.setScreen(overworld);
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {

        // draw game lose information on screen
        base.batch.begin();
        base.myTitleFont.draw(base.batch, "PAUSE", base.melancholy.x - 50, base.melancholy.y + 300);
        base.myFont.draw(base.batch, "PRESS ENTER TO CONTINUE", base.melancholy.x - 200, base.melancholy.y - 100);
        base.batch.end();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
