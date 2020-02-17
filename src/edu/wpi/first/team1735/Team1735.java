/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.team1735;

import edu.wpi.first.team1735.drive.DeadDriveController;
import edu.wpi.first.team1735.drive.DriveController;
import edu.wpi.first.team1735.drive.JoeDriveline;
import edu.wpi.first.team1735.drive.JoystickDrive;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.camera.AxisCamera;
//import java.io.PrintStream;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Team1735 extends IterativeRobot {

    Joystick driveJS = new JoeJoystick(1);
    Joystick twistJS = new JoeJoystick(2);
    Joystick operatorJS = new Joystick(3);
    //RobotDrive drive = new RobotDrive(5, 4, 2, 3);
    Jaguar spinner = new Jaguar(1);
    Compressor compressor = new Compressor(1, 1);
    Solenoid kickerIn = new Solenoid(1);
    Solenoid kickerOut = new Solenoid(2);
    Solenoid lock = new Solenoid(4);
    Solenoid unlock = new Solenoid(3);
    Servo servo = new Servo(8);
    Jaguar fp1 = new Jaguar(6);
    Jaguar fp2 = new Jaguar(7);
    Gyro gyro = new JoeGyro(1);
    DigitalInput magneticReedSwitch = new DigitalInput(2);
    JoeDriveline drive;
    DriveController joystickDrive;
    long timeKicked = Timer.getUsClock();
    boolean kicking = false;
    int kickingState = 0;
    Autonomous autonomous;
    //AxisCamera camera = new AxisCamera(1);

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        //drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        //drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        gyro.setSensitivity(.007);
        gyro.reset();
        compressor.start();
        AxisCamera cam = AxisCamera.getInstance();
        cam.writeResolution(AxisCamera.ResolutionT.k320x240);
        cam.writeBrightness(0);
        drive = new JoeDriveline(gyro);
        this.joystickDrive = new JoystickDrive(driveJS, twistJS);
        drive.setDriveController(joystickDrive);
        joystickDrive.fieldOriented = true;
        this.autonomous = new Autonomous(drive, operatorJS, this);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        //Feed the watchdog and make sure the driveline is enabled
        boolean started = true;
        Watchdog.getInstance().feed();
        if(started) {
            drive.resetGyro();
            started=false;
        }
        drive.enable();
        this.autonomous.autoModeBegun = true;
    }

    public void disabledPeriodic() {
        drive.disable();
        super.disabledPeriodic();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Watchdog.getInstance().feed();
        drive.enable();
        drive.setDriveController(joystickDrive);

        if (driveJS.getRawButton(10)) {
            drive.resetGyro();
        }

        //drive.holonomicDrive(driveJS.getMagnitude(), driveJS.getDirectionDegrees() , twistJS.getX());
//        PrintStream gyro.getAngle();

        if (driveJS.getTrigger()) {
            spinner.set(-1.0);
        } else if (driveJS.getRawButton(2)) {
            spinner.set(1.0);
        } else {
            spinner.set(0.0);
        }


        if (twistJS.getTrigger()) {
            if (kicking == false) {
                kicking = true;
                kickingState = 0;
            }
        } else if (!kicking) {
            lockShooter();
            closeShooter();
        }

        if (kicking) {
            shoot();
        }

        if (operatorJS.getTrigger()) {
            //servo.setAngle(45.0);
            servo.set(1.0);
        } else {
            servo.set(0.3);
        }

        //if (operatorJS.getAxis(Joystick.AxisType.kX) > 0.0) {
        if (operatorJS.getY() < 0) {
            System.out.println("roller");
            fp1.set(-1*operatorJS.getY());
            fp2.set(-1*operatorJS.getY());
        } else {
            fp1.set(0.0);
            fp2.set(0.0);
        }

    }

    /* lockShooter()
     * Puts the latch out - i.e. small piston out
     */
    public void lockShooter() {
        lock.set(true);
        unlock.set(false);
    }

    /* unlcokShooter()
     * Pulls the latch - i.e. small piston in
     */
    public void unlockShooter() {
        lock.set(false);
        unlock.set(true);
    }

    /* openShooter()
     * Pushes Kicker - i.e. Big piston out
     */
    public void openShooter() {
        kickerOut.set(true);
        kickerIn.set(false);
    }

    /* closeShooter()
     * Pulls kicker in - i.e. big piston in
     */
    public void closeShooter() {
        kickerOut.set(false);
        kickerIn.set(true);
    }

    /* shoot()
     * A state machine to kick the ball
     */
    public void shoot() {
        //System.out.println("State: " + kickingState + " Time Kicked: " + timeKicked + " MicroClock: " + Timer.getUsClock() + " Reed: " + magneticReedSwitch.get());
        if (kickingState == 0) {
            timeKicked = Timer.getUsClock();
            openShooter();
            kickingState = 1;
        } else if (kickingState == 1) {
            if (Timer.getUsClock() > timeKicked + 500000.0) {
                unlockShooter();
                timeKicked = Timer.getUsClock();
                kickingState = 2;
            }
        } else if (kickingState == 2) {
            if (Timer.getUsClock() > timeKicked + 500000.0 || !magneticReedSwitch.get()) {
                lockShooter();
                kickingState = 3;
            }
        } else if (kickingState == 3) {
            if (Timer.getUsClock() > timeKicked + 750000.0 || !magneticReedSwitch.get()) {
                closeShooter();
                kickingState = 0;
                kicking = false;
            }
        }
    }
}
