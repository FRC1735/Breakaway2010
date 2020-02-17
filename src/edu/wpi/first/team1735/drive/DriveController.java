/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.team1735.drive;

/**
 *
 * @author Administrator
 */
public abstract class DriveController {
    //Member Variables

    public boolean fieldOriented;
    protected String name;

    /**
     * This is to be called when the robot is given this pilot.
     * @param driveline the object to modify
     */
    public void initialize(JoeDriveline driveline) {
        return;
    }

    /**
     * This is the method that will update the given object's settings.
     * If this returns false, then the driving object will not continue it's
     * current loop.
     * @param driveline the object to modify
     */
    public abstract boolean drive(JoeDriveline driveline);

    /**
     * This will allow for quick printing.
     * @return The name of the mode.
     */
    public String name() {
        return name + " "
                + (fieldOriented ? "with" : "without")
                + " Field Orientation";
    }
}
