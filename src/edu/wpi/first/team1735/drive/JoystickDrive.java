/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.team1735.drive;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Administrator
 */
public class JoystickDrive extends DriveController {

    private Joystick driveJS;
    private Joystick twistJS;

    public JoystickDrive(Joystick driveJS, Joystick twistJS) {
        this.driveJS = driveJS;
        this.twistJS = twistJS;
    }

    public boolean drive(JoeDriveline driveline) {
        double twist = twistJS.getX();
        //System.out.println("Joystick Twist Value: " + twist);
        if(Math.abs(twist) < .2) {
            twist = 0;
        }
        if (fieldOriented) {
            driveline.fieldOriented(driveJS.getMagnitude(), driveJS.getDirectionDegrees(), twist);
        } else {
            driveline.robotOriented(driveJS.getMagnitude(), driveJS.getDirectionDegrees(), twist);
        }

        driveline.setBallCentric(twistJS.getRawButton(2));

        return true;
    }
}
