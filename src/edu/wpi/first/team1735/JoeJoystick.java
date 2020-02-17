/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.team1735;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Administrator
 */
public class JoeJoystick extends Joystick {

    public boolean reverseDirection = true;

    public JoeJoystick(int channel) {
        super(channel);
    }

    public double getDirectionRadians() {
        if (reverseDirection) {
            return (super.getDirectionRadians() + Math.PI) % (2 * Math.PI);
        } else {
            return super.getDirectionRadians();
        }
    }
}
