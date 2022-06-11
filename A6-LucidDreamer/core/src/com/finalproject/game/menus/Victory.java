package com.finalproject.game.menus;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.finalproject.game.Base;

public class Victory extends ScreenAdapter {

    // start with a reference foundation of the base class
    private final Base base;
    public Victory(Base b) {
        this.base = b;
    }

    @Override
    public void show() {

        // play the sound effect!
        base.gameWinBGMPlayer.setVolume(1);
        base.gameWinBGMPlayer.play();

        // update input readings on this screen in particular
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keyCode) {

                if (keyCode == Input.Keys.ENTER) {
                    System.exit(0);
                }
                if (keyCode == Input.Keys.C) {
                    base.setScreen(new Credits(base));
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
        base.myFont.draw(base.batch, "YOU'VE CONQUERED YOUR NIGHTMARES!", base.melancholy.x - 300, base.melancholy.y + 300);
        base.myFont.draw(base.batch, "YOU WIN!", base.melancholy.x - 10, base.melancholy.y + 250);
        base.myFont.draw(base.batch, "PRESS ENTER TO QUIT", base.melancholy.x - 140, base.melancholy.y - 100);
        base.myFont.draw(base.batch, "PRESS \"C\" FOR CREDITS", base.melancholy.x - 160, base.melancholy.y - 150);
        base.batch.end();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
