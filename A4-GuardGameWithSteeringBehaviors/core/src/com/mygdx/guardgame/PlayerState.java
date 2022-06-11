package com.mygdx.guardgame;

import com.badlogic.gdx.math.Vector2;

public class PlayerState extends EntityState{

    // specify an acceleration at which the character ought to increase their speed
    private float acceleration;

    // constructor
    public PlayerState(Vector2 location, Vector2 view) {
        super(location, view);
        this.maxSpeed = 3.5F;
        this.acceleration = 0.05F;
    }

    public float getAcceleration() {
        return acceleration;
    }
}
