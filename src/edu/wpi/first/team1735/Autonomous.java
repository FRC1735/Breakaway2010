/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.team1735;

import edu.wpi.first.team1735.drive.DeadDriveController;
import edu.wpi.first.team1735.drive.JoeDriveline;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Administrator
 */
public class Autonomous extends Thread {

    private int mode;
    private JoeDriveline driveline;
    private Joystick joystick;
    private Team1735 robot;
    public boolean autoModeBegun;
    public boolean kicking;

    public Autonomous(JoeDriveline driveline, Joystick joystick, Team1735 robot) {
        this.driveline = driveline;
        this.autoModeBegun = false;
        this.joystick = joystick;
        this.robot = robot;
        kicking = false;
        start();
    }

    //        switch (autoState) {
//            case 0:
//                Whiteboard.write("Beginning Autonomous");
//                //This will allow the Team1735 object to control the JoeDrive object
//                drive.setDriveController(new DeadDriveController());
//                autoState = 1;
//            case 1:
//                //This will drive the robot forward (and force it to face forward)
//                drive.fieldOrientedAngle(1, 0, 0);
//                //if(...) {autoState = 2}
//                break;
//            case 2:
//                //This will stop the robot but keep it facing forward
//                drive.fieldOrientedAngle(0, 0, 0);
//                break;
//        }
    public void run() {
        boolean flag = false;
        boolean flag2 = false;
        int autodelay = 0;
        boolean blinkflag = false;
        Whiteboard.write(1, "Team 1735");
        while (!this.autoModeBegun) {
            if (this.joystick.getTrigger()) {
                if (!flag) {
                    mode++;
                   // Whiteboard.write(1, "1");
                    flag = true;
                    if (mode > 5) {
                        mode = 0;
                    }
                }
            } else {
                flag = false;
            }
            if (this.joystick.getRawButton(2)) {
                if (!flag2) {
                    autodelay++;
                    if (autodelay > 12) {
                        autodelay = 0;
                    }
                    flag2 = true;
                } else {
                    flag2 = false;
                }
            }
            Whiteboard.write(2, "Auto Delay: " + autodelay);
            if (mode == 0) {
                if(blinkflag) {
                    Whiteboard.write(1, "No AUTONOMOUS");
                    //Whiteboard.write(2, "No AUTONOMOUS");
                    Whiteboard.write(3, "No AUTONOMOUS");
                    //Whiteboard.write(4, "No AUTONOMOUS");
                    Whiteboard.write(5, "No AUTONOMOUS");
                    //Whiteboard.write(6, "No AUTONOMOUS");
                    blinkflag = false;
                } else {
                    Whiteboard.write(1, "");
                   // Whiteboard.write(2, "");
                    Whiteboard.write(3, "");
                  //  Whiteboard.write(4, "");
                    Whiteboard.write(5, "");
                  //  Whiteboard.write(6, "");
                    blinkflag = true;
                }
            } else if (mode == 1) {
                Whiteboard.write(1, "Shoot Twice");
                Whiteboard.write(3, "");
                Whiteboard.write(5, "");
            } else if (mode == 2) {
                Whiteboard.write(1, "Shoot Once");
                Whiteboard.write(3, "");
                Whiteboard.write(5, "");
            } else if (mode == 3) {
                Whiteboard.write(1, "Shoot Twice - Long Drive");
                Whiteboard.write(3, "");
                Whiteboard.write(5, "");
            } else if (mode == 4) {
                Whiteboard.write(1, "Spin!");
                Whiteboard.write(3, "");
                Whiteboard.write(5, "");
            } else if (mode == 5) {
                Whiteboard.write(1, "Drive short distance");
                Whiteboard.write(3, "");
                Whiteboard.write(5, "");
            }
            Timer.delay(.05);
        }


        driveline.enable();
        driveline.setDriveController(new DeadDriveController());

        Timer.delay(autodelay);

        if (mode == 1) { //shoot twice
            Timer.delay(1);
            driveline.fieldOrientedAngle(.75, 0, 0);
            Timer.delay(.3);
            autoshoot();
            Timer.delay(.1);
            autoshoot();
            Timer.delay(.1);
            driveline.fieldOrientedAngle(0, 0, 0);
        }
        else if (mode == 2) { //shoot once
            driveline.fieldOrientedAngle(.75, 0, 0);
            Timer.delay(.2);
            autoshoot();
            Timer.delay(.3);
            driveline.fieldOrientedAngle(0, 0, 0);
        }
        else if (mode == 3) { //shoot twice long first drive
            Timer.delay(1);
            driveline.fieldOrientedAngle(.75, 0, 0);
            Timer.delay(.5);
            autoshoot();
            Timer.delay(.2);
            autoshoot();
            Timer.delay(.1);
            driveline.fieldOrientedAngle(0, 0, 0);
        }
        else if (mode == 4) { //spin
            driveline.fieldOrientedAngle(.75, 0, 0);
            Timer.delay(.7);
            driveline.fieldOriented(1, 0, 1);
            Timer.delay(1.5);
        } else if (mode == 5) {
            Timer.delay(1);
            driveline.fieldOrientedAngle(.75, 0, 0);
            Timer.delay(.7);
            driveline.fieldOrientedAngle(0, 0, 0);
        }
        driveline.fieldOrientedAngle(0, 0, 0);
    }

    public void autoshoot() {
        robot.openShooter();
        Timer.delay(.5);
        robot.unlockShooter();
        Timer.delay(.5);
        robot.lockShooter();
        Timer.delay(.25);
        robot.closeShooter();
    }
}
