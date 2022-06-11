package com.mygdx.guardgame;

import com.badlogic.gdx.math.Vector2;

public class SteeringManager {

    private final PlayerState playerState;
    private final GuardState guardState;

    public SteeringManager(PlayerState pS, GuardState gS) {
        this.playerState = pS;
        this.guardState = gS;
    }

    // methods for steering
    public Vector2 seek(Vector2 sP){
        Vector2 steeringForce;
        // derive the vector from the guard to the guard spot
        Vector2 seekPoint = sP.sub(guardState.location.cpy());
        steeringForce = seekPoint.cpy();
        steeringForce.nor();
        return steeringForce;
    }
    public Vector2 avoid(){

        // mess around with this value...
        int magFactor = 50;

        Vector2 steeringForce = new Vector2();
        Vector2 centerOfGuard = new Vector2(guardState.location.x + 30, guardState.location.y + 30);

        // derive vectors pointing from the guard to each of the four walls
        // this method will ultimately have NOTHING to do with whether the guard is looking into the building or not
        // since the guard can still hit the building without seeing it head on
        Vector2 guardToNorth = new Vector2(GuardGame.building.x + (GuardGame.building.width/2), GuardGame.building.y + GuardGame.building.height);
        Vector2 guardToSouth = new Vector2(GuardGame.building.x + (GuardGame.building.width/2), GuardGame.building.y);
        Vector2 guardToEast = new Vector2(GuardGame.building.x + GuardGame.building.width, GuardGame.building.y + (GuardGame.building.height/2));
        Vector2 guardToWest = new Vector2(GuardGame.building.x, GuardGame.building.y + (GuardGame.building.height/2));
        guardToNorth.sub(centerOfGuard.cpy());
        guardToSouth.sub(centerOfGuard.cpy());
        guardToEast.sub(centerOfGuard.cpy());
        guardToWest.sub(centerOfGuard.cpy());

        // derive the "magnetic forces" away from each of the walls
        Vector2 northMag = new Vector2(0,1);
        Vector2 southMag = new Vector2(0,-1);
        Vector2 eastMag = new Vector2(1,0);
        Vector2 westMag = new Vector2(-1,0);

        //compute the dot products (if negative, we want to derive a force based on the distance
        float northDot = guardToNorth.dot(northMag);
        float southDot = guardToSouth.dot(southMag);
        float eastDot = guardToEast.dot(eastMag);
        float westDot = guardToWest.dot(westMag);

        // add a repelling force if we have a negative dot product
        if(northDot < 0 &&
                centerOfGuard.x > GuardGame.building.x - 30 &&
                centerOfGuard.x < GuardGame.building.x+GuardGame.building.width + 30){
            System.out.println("North Force");
            Vector2 northPush = northMag.cpy();
            // north of the building
            float t = centerOfGuard.y - (GuardGame.building.y+GuardGame.building.height);
            northPush.setLength((1/(t+1))*magFactor);
            steeringForce.add(northPush);
            System.out.println("Steering force: "+northPush.len());
        }
        else if(southDot < 0 &&
                centerOfGuard.x > GuardGame.building.x - 30 &&
                centerOfGuard.x < GuardGame.building.x+GuardGame.building.width + 30){
            System.out.println("South Force");
            Vector2 southPush = southMag.cpy();
            // south of the building
            float t = GuardGame.building.y - centerOfGuard.y;
            southPush.setLength((1/(t+1))*magFactor);
            steeringForce.add(southPush);
            System.out.println("Steering force: "+southPush.len());
        }
        else if(eastDot < 0 &&
                centerOfGuard.y > GuardGame.building.y - 30 &&
                centerOfGuard.y < GuardGame.building.y+GuardGame.building.height + 30){
            System.out.println("East Force");
            Vector2 eastPush = eastMag.cpy();
            // east of the building
            float t = centerOfGuard.x - (GuardGame.building.x+GuardGame.building.width);
            eastPush.setLength((1/(t+1))*magFactor);
            steeringForce.add(eastPush);
            System.out.println("Steering force: "+eastPush.len());
        }
        else if(westDot < 0 &&
                centerOfGuard.y > GuardGame.building.y - 30 &&
                centerOfGuard.y < GuardGame.building.y+GuardGame.building.height + 30){
            System.out.println("West Force");
            Vector2 westPush = westMag.cpy();
            // west of the building
            float t = GuardGame.building.x - centerOfGuard.x;
            westPush.setLength((1/(t+1))*magFactor);
            steeringForce.add(westPush);
            System.out.println("Steering force: "+westPush.len());
        }
        return steeringForce;
    }

    public Vector2 pursuit(){
        Vector2 steeringForce;
        Vector2 pursuitPoint;

        // if the player is moving, pursue where they're likely to end up
        if(playerState.velocity.len() != 0) {

            // derive the vector from the guard to the expected target destination
            // with respect to the current distance between the two entities
            Vector2 betweenEnts = playerState.location.cpy();
            betweenEnts.sub(guardState.location.cpy());
            float t = 30;

            // derives the location of where it should point to
            // we want to derive a location with an adjusted velocity!
            Vector2 pred = playerState.location.cpy();
            Vector2 modVel = playerState.velocity.cpy();
            modVel.setLength(modVel.len()*t);
            pred.add(modVel);

            // derive the vector pointing to this prediction from the guard
            pursuitPoint = seek(pred.cpy());
        }

        // if not, just go straight for the position!
        else
            pursuitPoint = seek(playerState.location.cpy());
        steeringForce = pursuitPoint.cpy();
        steeringForce.nor();

        return steeringForce;
    }
}
