/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.team1735.drive;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SpeedController;

/**
 *
 * @author Administrator
 */
public class HolonomicRobotDrive {

    private SpeedController[] motors = new SpeedController[4];
    private int[] reversals = new int[4];
    private double anchorY = 1;

    public HolonomicRobotDrive(
            int frontLeft, boolean frontLeftForward,
            int backLeft, boolean backLeftForward,
            int frontRight, boolean frontRightForward,
            int backRight, boolean backRightForward) {
        motors[0] = new Jaguar(frontLeft);
        reversals[0] = frontLeftForward ? 1 : -1;
        motors[1] = new Jaguar(backLeft);
        reversals[1] = backLeftForward ? 1 : -1;
        motors[2] = new Jaguar(frontRight);
        reversals[2] = frontRightForward ? 1 : -1;
        motors[3] = new Jaguar(backRight);
        reversals[3] = backRightForward ? 1 : -1;
    }

    private void  setMotors(double[] speeds) {
//        double max = 1;
//        for (int i = 0; i < 4; i++) {
//            if (Math.abs(speeds[i]) > max) {
//                max = Math.abs(speeds[i]);
//            }
//        }
//        max = 1 / max;
        for (int i = 0; i < 4; i++) {
            //motors[i].set(speeds[i] * max * reversals[i]);
            motors[i].set(Math.max(Math.min(speeds[i] * reversals[i], 1), -1));
        }
    }

    public void holonomicDrive(double magnitude, double direction, double twist) {
        //Magnitude and Direction
        double[] speeds = new double[4];
        magnitude = Math.min(magnitude, 1);
        double cosD = Math.cos(Math.toRadians(direction + 45.0));
        double sinD = Math.sin(Math.toRadians(direction - 45.0));
        speeds[0] = sinD * magnitude;
        speeds[1] = cosD * magnitude;
        speeds[2] = cosD * magnitude;
        speeds[3] = sinD * magnitude;

        //Twist
        if (anchorY >= 0) {
            double frontTwist = twist * (anchorY - 1.0) / (anchorY + 1.0);
            speeds[0] -= frontTwist;
            speeds[1] += twist;
            speeds[2] += frontTwist;
            speeds[3] -= twist;
        } else {
            double backTwist = twist * (anchorY + 1.0) / (anchorY - 1.0);
            speeds[0] += twist;
            speeds[1] -= backTwist;
            speeds[2] -= twist;
            speeds[3] += backTwist;
        }

        setMotors(speeds);
    }

    public void setY(double y) {
        this.anchorY = y;
    }
}
