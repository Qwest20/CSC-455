package ships.y2022.liam;

// TODO
// -Rename variables and methods
// -Look for any code that can be removed

import asteroidsfw.Vector2d;
import asteroidsfw.ai.AsteroidPerception;
import asteroidsfw.ai.Perceptions;
import asteroidsfw.ai.ShipControl;
import asteroidsfw.ai.ShipMind;

public class MainShipMind implements ShipMind {

    // A class instance that represents the ship in the game space. Naturally, we can control the ship with this.
    ShipControl myShip;

    @Override
    public void init(ShipControl sC) {
        myShip = sC;
    }

    @Override
    public void think(Perceptions entities, double notUsed) {

        // From the collection of asteroids, most favorable to pursue
        AsteroidPerception targetAst = null;
        double chosenAstDist = Double.POSITIVE_INFINITY;
        AsteroidPerception[] asteroids = entities.asteroids();
        for (AsteroidPerception aP : asteroids) {
            double distToThisAst = myShip.pos().$minus(aP.pos()).sqLength();
            // prioritize smaller radii asteroids
            if (targetAst == null || aP.radius() <= targetAst.radius()) {
                if (distToThisAst < chosenAstDist) {
                    chosenAstDist = distToThisAst;
                    targetAst = aP;
                }
            }
        }

        // perform based on the intel we got in this method
        act(targetAst);
    }

    private void act(AsteroidPerception targetAst){
        // if we have a favorable asteroid to pursue
        if (targetAst != null) {
            Vector2d shipToAst = targetAst.pos().$minus(myShip.pos());
            Vector2d velToAst = myShip.v().$plus(shipToAst.normalize().$times(150.0D));
            double astTravel = shipToAst.length() / velToAst.length();
            Vector2d appliedVector = new Vector2d(shipToAst.x() + targetAst.v().x() * astTravel, shipToAst.y() + targetAst.v().y() * astTravel);
            double angleDiff = myShip.direction().cross(appliedVector);

            // determine the angle difference between the ship and the asteroid and steer accordingly
            if (angleDiff < 0.0D) {
                myShip.rotateLeft(true);
                myShip.rotateRight(false);
            } else {
                myShip.rotateLeft(false);
                myShip.rotateRight(true);
            }

            // decide to shoot if our aim is legit
            myShip.shooting(angleDiff < 1 || angleDiff > -1);

            // determine acceleration based on predicted possible collision with the asteroid
            if (this.willCollide(targetAst, shipToAst)) {
                if ((angleDiff <= 80.0D) && (angleDiff >= -80.0D)) {
                    myShip.thrustBackward(true);
                    myShip.thrustForward(false);
                } else {
                    myShip.thrustForward(true);
                    myShip.thrustBackward(false);
                }
            } else {
                myShip.thrustForward(true);
                myShip.thrustBackward(false);
            }
        }
    }

    // helper methods incorporated from JD_Ship_1
    public boolean willCollide(AsteroidPerception aP, Vector2d shipVel) {
        // attempt to scale this with the radius of the asteroid, but not to an overly drastic degree
        return this.isMovingToAst(aP) < 0.0D &&
                shipVel.sqLength() < 1750 * (aP.radius()/3F);
    }
    public double isMovingToAst(AsteroidPerception aP) {
        if (aP == null) {
            return 0;
        } else {
            double astToShipXDiff = aP.pos().x() - myShip.pos().x();
            double astToShipYDiff = aP.pos().y() - myShip.pos().y();
            double astToShipXVelDiff = aP.v().x() - myShip.v().x();
            double astToShipYVelDiff = aP.v().y() - myShip.v().y();
            return astToShipXDiff * astToShipXVelDiff + astToShipYDiff * astToShipYVelDiff;
        }
    }
}