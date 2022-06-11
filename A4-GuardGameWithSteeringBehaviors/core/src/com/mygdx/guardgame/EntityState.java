package com.mygdx.guardgame;

import com.badlogic.gdx.math.Vector2;

public class EntityState{

    // private variables for all entities
    protected Vector2 location; // a vector from the origin to the position of the entity
    protected Vector2 view; // a vector from the origin denoting an angle direction of the entity
    protected Vector2 velocity; // a vector from the origin denoting the velocity of the entity
    protected float maxSpeed; // a value to determine the maximum velocity that the entity can have
    protected float currentSpeed; // value to keep track of the currentSpeed at which an entity is traveling

    // constructor
    public EntityState(Vector2 startLocation, Vector2 startView) {
        this.location = startLocation;
        this.view = startView;
        this.velocity = new Vector2(0, 0);
        this.currentSpeed = 0;
    }

    // getters and setters
    public Vector2 getLocation() {
        return location;
    }
}
