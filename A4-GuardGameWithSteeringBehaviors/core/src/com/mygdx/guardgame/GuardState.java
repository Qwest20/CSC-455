package com.mygdx.guardgame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GuardState extends EntityState {
    
    public final Vector2 guardSpot = new Vector2();
    private final int radius;
    private float currentX, currentY;
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
        this.maxSpeed = 2.5F;
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


        if (pS.getLocation().dst(location) <= radius) {
            angleBetween = deriveAngleBetween(pS);
            // NOTE: if the angle is NAN, it is because the player is not moving. This is a hardcoded check...
            if (angleBetween <= 60 || Double.isNaN(angleBetween)) {
                // follow the angle given by the relevant vector (for both chase and return)
                processSteering(gS, b, g);
            }
            // if we can't see the guard any more under either circumstance, go into the return state
            // angle failed
            else {
                updateBehavior(behavior.RETURN);
                System.out.println("Angle failed");
            }
        }
        // radius failed
        else {
            updateBehavior(behavior.RETURN);
            System.out.println("Radius failed");
        }
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
            if (pS.getLocation().dst(location) <= radius) {
                angleBetween = deriveAngleBetween(pS);
                if (angleBetween <= 60 || Double.isNaN(angleBetween))
                    updateBehavior(GuardState.behavior.CHASE);
            }
            processSteering(gS, b, g);
        }
    }

    // HELPER METHODS
    private void guardRotate() {
        // if we're turning to the left
        if (left) {
            // rotate until 360 (or close enough) then turn left to false
            this.view.rotateDeg(2);
            if (this.view.angleDeg() >= 360 - 2)
                left = false;
        } else {
            // rotate until 90 (or close enough) then turn left to true
            this.view.rotateDeg(-2);
            if (this.view.angleDeg() <= 90 + 2)
                left = true;
        }
    }
    private float deriveAngleBetween(PlayerState pS) {

        // derive the angle between the guard's view and the player's location

        // create vectors for view vs. location comparison
        // save the vector pointing from guard to player
        Vector2 guardToPlayer = pS.location.cpy();
        guardToPlayer.sub(location);
        Vector2 normalizedGuardView = view.cpy();

        // normalize these vectors for simpler mathematics (is this actually needed...?)
        guardToPlayer.nor();
        normalizedGuardView.nor();

        // dot product operation to derive angle difference between player location
        // and the guard's orientation
        angleBetween = (float) ((Math.acos((guardToPlayer.dot(view)) /
                (guardToPlayer.len() * normalizedGuardView.len()))) * (180 / Math.PI));
        return angleBetween;
    }

    // have the guard follow a vector at a given speed
    private void processSteering(Sprite gS, Rectangle building, Rectangle guard) {

        if (!building.overlaps(guard)) {

            // save player position from before the collision with the building, since we know this is okay
            currentX = location.x;
            currentY = location.y;

            // record an overall vector for steering behavior inclusion
            Vector2 steering = new Vector2();
            if (this.currentBehavior == behavior.CHASE)
                steering.add(GuardGame.sM.pursuit());
            else
                steering.add(GuardGame.sM.seek(guardSpot.cpy()));
            steering.add(GuardGame.sM.avoid());

            // add steering to velocity
            velocity.add(steering);

            // specify the velocity vector's length, so we go at the correct speed
            if (velocity.len() > maxSpeed)
                velocity.setLength(maxSpeed);

            // make the movement with respect to bounds
            location.add(velocity);
            if (location.x < 0)
                location.x = 0;
            if (location.x > 1280 - 60)
                location.x = 1280 - 60;
            if (location.y < 0)
                location.y = 0;
            if (location.y > 720 - 60)
                location.y = 720 - 60;

            // update the view vector with respect to where we're moving
            view = velocity.cpy();
            view.nor();

            // update the guard sprite while we're here as well
            gS.setRotation(view.angleDeg());

        } else {
            // reload the safe position from the last frame if we're having trouble
            location.x = currentX;
            location.y = currentY;
            // should never get in here, but hey, it was once useful, so I won't worry about it for now
        }
    }

    // behavior algorithm ENUM
    public enum behavior {
        SURVEY, CHASE, RETURN
    }
}
