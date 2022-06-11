package com.mygdx.guardgame;

import com.badlogic.gdx.math.Vector2;

public class EntityState{

    // private variables for all entities
    protected Vector2 location; // a vector from the origin to the position of the entity
    protected Vector2 view; // a vector from the origin denoting an angle direction of the entity

    // constructor
    public EntityState(Vector2 location, Vector2 view) {
        this.location = location;
        this.view = view;
    }

    // getters and setters
    public Vector2 getLocation() {
        return location;
    }
}
