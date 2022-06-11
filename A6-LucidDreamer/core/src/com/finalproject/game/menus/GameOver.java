package com.finalproject.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.finalproject.game.Base;

public class GameOver extends ScreenAdapter {

    // start with a reference foundation of the base class
    private final Base base;
    public GameOver(Base b) {
        this.base = b;
    }

    @Override
    public void show() {

        // start the music
        base.gameLoseBGMPlayer.setVolume(1);
        base.gameLoseBGMPlayer.play();

        // update input readings on this screen in particular
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keyCode) {

                if (keyCode == Input.Keys.ENTER) {
                    base.gameLoseBGMPlayer.stop();
                    System.exit(0);
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.valueOf("000000"));

        // draw game lose information on screen
        base.batch.begin();
        base.myTitleFont.draw(base.batch, "YOU WOKE UP...", base.melancholy.x - 200, base.melancholy.y + 300);
        base.myFont.draw(base.batch, "PRESS ENTER", base.melancholy.x - 75, base.melancholy.y - 100);
        base.batch.end();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

}
