package com.finalproject.game.menus;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.finalproject.game.Base;

public class Credits extends ScreenAdapter {

    private final Base base;

    public Credits(Base b) {
        this.base = b;
    }

    @Override
    public void show() {

        base.overworldBGMPlayer.setVolume(1);
        base.overworldBGMPlayer.play();

        // update input readings on this screen in particular
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keyCode) {

                if (keyCode == Input.Keys.ENTER) {
                    base.overworldBGMPlayer.stop();
                    System.exit(0);
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.valueOf("000000"));

        base.batch.begin();
        base.myFont.draw(base.batch, "THANKS FOR PLAYING OUR GAME!!!", base.melancholy.x - 250, base.melancholy.y + 350);
        base.myFont.draw(base.batch, "PROGRAMMING:      TWIST", base.melancholy.x - 180, base.melancholy.y + 200);
        base.myFont.draw(base.batch, "PIXEL ART:           TWIST", base.melancholy.x - 180, base.melancholy.y + 150);
        base.myFont.draw(base.batch, "MUSIC:                HAPZARD", base.melancholy.x - 180, base.melancholy.y + 100);
        base.myFont.draw(base.batch, "MADE IN SPRING '22 FOR CSC 455", base.melancholy.x - 270, base.melancholy.y - 50);
        base.myFont.draw(base.batch, "PRESS ENTER TO QUIT", base.melancholy.x - 150, base.melancholy.y - 200);
        base.batch.end();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
