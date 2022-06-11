package com.finalproject.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.finalproject.game.Base;
import com.finalproject.game.screens.Overworld;

public class Title extends ScreenAdapter {

    // start with a reference foundation of the base class
    private final Base base;
    public Title(Base b) {
        this.base = b;
    }

    @Override
    public void show() {

        base.titleBGMPlayer.setVolume(1);
        base.titleBGMPlayer.play();

        // update input readings on this screen in particular
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keyCode) {

                if (keyCode == Input.Keys.ENTER) {
                    // go to the next room
                    base.titleBGMPlayer.stop();
                    base.setScreen(new Overworld(base));
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.valueOf("000000"));

        // draw game win information on screen
        base.batch.begin();
        base.myTitleFont.draw(base.batch, "LUCID DREAMER", 350, 600);
        base.myFont.draw(base.batch, "PRESS ENTER TO BEGIN YOUR DREAM...", 275, 200);
        base.batch.end();
    }

    // prevents the keyboard behaviors from persisting into the next room
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

}
