package com.mygdx.guardgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GuardState extends EntityState {

    // constants for the guard's properties
    private final int guardSpeed = 100;
    private final int guardReturnSpeed = 75;
    private final int guardRot = 2;

    private float currentX, currentY;

    // some Vector2's needed in global reference
    private Vector2 guardSpot = new Vector2(); // save the initial survey spot
    private Vector2 guardToPlayer; // save the vector pointing from guard to player

    // added radius value on top of EntityState class
    private final int radius;

    // global variables for various behaviors
    private boolean left = true;
    private float angleBetween;
    private behavior currentBehavior;

    // constructor
    public GuardState(Vector2 location, Vector2 view, int r) {
        super(location, view);
        currentBehavior = behavior.SURVEY;
        // initialize the starting guardSpot
        guardSpot.x = location.x;
        guardSpot.y = location.y;
        radius = r;
    }

    // behavior G/S
    public void updateBehavior(behavior b) {
        currentBehavior = b;
    }

    public behavior getCurrentBehavior() {
        return currentBehavior;
    }

    // STATE BEHAVIOR METHODS
    public void guardSurvey(PlayerState pS) {

        // compare player state to the guard's FOV (mimics a cone)
        // FIRST: check distance
        if (pS.getLocation().dst(location) <= radius) {

            // SECOND: check angle
            // (if the angle difference between the guardToPlayer vector
            // and the guard's view is no greater than 60 either way)
            angleBetween = deriveAngleBetween(pS);
            if (angleBetween <= 60)
                updateBehavior(GuardState.behavior.CHASE);
                // player not spotted, keep rotating
            else {
                guardRotate();
            }
        }
        // player not spotted, keep rotating
        else {
            guardRotate();
        }
    }

    public void guardChase(PlayerState pS, Sprite gS, Rectangle b, Rectangle g) {

        // compare player state to the guard's FOV (mimics a cone)
        // FIRST: check distance
        if (pS.getLocation().dst(location) <= radius) {

            // SECOND: check angle
            // (if the angle difference between the guardToPlayer vector
            // and the guard's view is no greater than 60 either way)
            angleBetween = deriveAngleBetween(pS);
            // NOTE: if the angle is NAN, it is because the player is not moving. This is a hardcoded check...
            if (angleBetween <= 60 || Double.isNaN(angleBetween)) {

                // create a new vector that latches on to the player
                Vector2 chase = guardToPlayer.cpy();
                chase.nor();

                // follow the angle given by the relevant vector (for both chase and return)
                followVector(gS, chase, guardSpeed, b, g);
            }

            // if we can't see the guard any more under either circumstance, go into the return state
            // angle failed
            else
                updateBehavior(behavior.RETURN);
        }
        // radius failed
        else
            updateBehavior(behavior.RETURN);
    }

    public void guardReturn(PlayerState pS, Sprite gS, Rectangle b, Rectangle g) {

        // if the guard is close enough to the survey spot, just snap him in place
        if (Math.abs(location.x - guardSpot.x) < 5 &&
                Math.abs(location.y - guardSpot.y) < 5) {
            // snap the guard in place and change its behavior
            location.x = guardSpot.x;
            location.y = guardSpot.y;
            updateBehavior(behavior.SURVEY);
        }

        // otherwise, keep heading back over, but remain open to the possibility of detecting the chase state
        else {

            // compare player state to the guard's FOV (mimics a cone)
            // FIRST: check distance
            if (pS.getLocation().dst(location) <= radius) {

                // SECOND: check angle
                // (if the angle difference between the guardToPlayer vector
                // and the guard's view is no greater than 60 either way)
                angleBetween = deriveAngleBetween(pS);
                // NOTE: if the angle is NAN, it is because the player is not moving. This is a hardcoded check...
                if (angleBetween <= 60 || Double.isNaN(angleBetween))
                    updateBehavior(GuardState.behavior.CHASE);
            }

            // unique return code, starting with a vector to return to the original spot
            Vector2 goBack = new Vector2(guardSpot);
            goBack.sub(this.location);
            goBack.nor();

            // follow the angle given by the relevant vector (for both chase and return)
            followVector(gS, goBack, guardReturnSpeed, b, g);
        }
    }

    // HELPER METHODS
    // guard rotation method
    private void guardRotate() {
        // if we're turning to the left
        if (left) {
            // rotate until 360 (or close enough) then turn left to false
            this.view.rotateDeg(guardRot);
            if (this.view.angleDeg() >= 360 - guardRot)
                left = false;
        } else {
            // rotate until 90 (or close enough) then turn left to true
            this.view.rotateDeg(-guardRot);
            if (this.view.angleDeg() <= 90 + guardRot)
                left = true;
        }
    }

    // derive the angle between the guard's view and the player's location with respect to the guard
    private float deriveAngleBetween(PlayerState pS) {
        // create vectors for view vs. location comparison
        guardToPlayer = new Vector2(pS.getLocation());
        guardToPlayer.sub(location);
        Vector2 normalizedGuardView = new Vector2(view);

        // normalize these vectors for simpler mathematics
        guardToPlayer.nor();
        normalizedGuardView.nor();

        // dot product operation to derive angle difference between player location
        // and the guard's orientation
        angleBetween = (float) ((Math.acos((guardToPlayer.dot(view)) /
                (guardToPlayer.len() * normalizedGuardView.len()))) * (180 / Math.PI));

        return angleBetween;
    }

    // have the guard follow a vector at a given speed
    private void followVector(Sprite gS, Vector2 v, int speed, Rectangle b, Rectangle guard) {

        if (!b.overlaps(guard)) {
            // save player position from before the collision with the building, since we know this is okay
            currentX = location.x;
            currentY = location.y;
            // bump the location in the proper direction
            location.x += (v.x) * speed * Gdx.graphics.getDeltaTime();
            location.y += (v.y) * speed * Gdx.graphics.getDeltaTime();

            // set the view angle to be this vector for accurate depiction
            this.view = v;

            gS.setRotation(view.angleDeg());
        } else {
            // reload the safe position from the last frame if we're having trouble
            location.x = currentX;
            location.y = currentY;
        }
    }

    // behavior algorithm ENUM
    public enum behavior {
        SURVEY, CHASE, RETURN
    }

}
