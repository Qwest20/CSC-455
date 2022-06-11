package com.finalproject.game.entities;

import com.badlogic.gdx.graphics.Texture;

public class EntityBase {

    // wariables for all entities (player and enemies)
    private String name;
    private int hp;
    private final int maxHp;
    private int attack;
    private int speed;
    private Texture sprite;

    public EntityBase (String n, int mH, int a, int sp, Texture s){
        this.name = n;
        this.maxHp = mH;
        this.hp = maxHp;
        this.attack = a;
        this.speed = sp;
        this.sprite = s;
    }

    // getters and setters
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = hp;
    }
    public int getAttack() {
        return attack;
    }
    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Texture getSprite() {
        return sprite;
    }
    public int getMaxHp() {
        return maxHp;
    }
}
