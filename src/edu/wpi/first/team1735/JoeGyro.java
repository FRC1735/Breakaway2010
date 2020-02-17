/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.team1735;

import edu.wpi.first.wpilibj.Gyro;

/**
 *
 * @author Administrator
 */
public class JoeGyro extends Gyro {
    public JoeGyro(int channel) {
        super(channel);
    }

    public double getAngle() {
        return ((super.getAngle() % 360) + 360) % 360;
    }
}
