package com.finalproject.game.entities;

import com.badlogic.gdx.graphics.Texture;

public class MelancholyStats extends EntityBase{

    private final int hope = 10; // a modifier that can help her survive a critical blow

    public MelancholyStats(String n, int h, int a, int sp, Texture s) {
        super(n, h, a, sp, s);
    }

    public int getHope() {
        return hope;
    }
}
