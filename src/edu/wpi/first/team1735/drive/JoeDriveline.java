/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.team1735.drive;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 *
 * @author Administrator
 */
public class JoeDriveline implements PIDOutput {
// Constants

    private static final double TWIST_MAX_RATE = 1;
    private static final double DEGREE_THRESHOLD = 2.0;
    // Pre-initialized Member Variables
    private double magnitude = 0;
    private double direction = 0;
    private double twist = 0;
    private boolean fieldOriented = false;
    private boolean enabled = false;
    // Member Variables
    //private HolonomicRobotDrive driveline;
    private RobotDrive driveline;
    PIDController anglePIDLoop;
    Gyro gyro;
    DriveController driver;

    public JoeDriveline(Gyro gyro) {
        //initializes a pilot that won't do anything
        this.driver = new DeadDriveController(); //change

        //initializes the Drive line object
        //determines if the robot is using CAN
        driveline = new RobotDrive(new Jaguar(5), //front-left
                new Jaguar(4), //back-left
                new Jaguar(2), //front-right
                new Jaguar(3));  //back-right
//        driveline.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
////        driveline.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        driveline.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        driveline.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        //driveline = new HolonomicRobotDrive(5, false, 4, true, 2, false, 3, false);

        //Saves the gyro
        this.gyro = gyro;
        gyro.reset();

        //initializes the PID Loop
        //NEVER DISABLE THIS! (unless through this class' disable method)
        anglePIDLoop = new PIDController(0.025, 0.0, 0, gyro, this, 0.01);
        anglePIDLoop.setContinuous(true);
        anglePIDLoop.setInputRange(0, 359.9);
        anglePIDLoop.setOutputRange(-TWIST_MAX_RATE, TWIST_MAX_RATE);
        anglePIDLoop.setTolerance(DEGREE_THRESHOLD / 360.0);
        anglePIDLoop.setSetpoint(gyro.getAngle());
        anglePIDLoop.disable();
    }

    /**
     * This is where all the driving is going to be happening.
     * It is called often by the internal PIDController Object.
     * @param twist the twist value given by the PID loop.
     */
    public synchronized void pidWrite(double twist) {
//        System.out.println("Goal Angle: " + anglePIDLoop.getSetpoint()
//                + " Current Angle: " + gyro.getAngle());
        twist = Math.max(Math.min(twist, TWIST_MAX_RATE), -TWIST_MAX_RATE);
        //System.out.println("Twist is "+ twist);
        //twist = 0;

        //Only do stuff if enabled
        if (enabled) {
            //Gets the Pilot's input
            if (this.driver.drive(this)) {
                if (this.twist != 0) {
                    //Sets the value of given twist to the member twist if the member twist
                    //is not zero; otherwise keep the given twist value.
                    twist = this.twist * TWIST_MAX_RATE;
                }

                //Modifies the direction to be field oriented if necessary.
                double direction = this.direction;
                if (fieldOriented) {
                    direction = (((direction - gyro.getAngle()) % 360) + 360) % 360;
                }

                //Actually drive the robot
                driveline.mecanumDrive_Polar(
                        this.magnitude,
                        direction,
                        twist);
            }
        }
    }

    /**
     * Sets the pilot that will be controlling this class
     * @param driver the new pilot
     */
    public void setDriveController(DriveController driver) {
        if (this.driver != driver) {
            System.out.println("Drive Controller Changed to " + driver.name());
            driver.initialize(this);
            this.driver = driver;
        }
    }

    /**
     * Sets the robot to drive in the given magnitude,
     * direction (relative to the field), and twist.
     * @param magnitude magnitude of the velocity of the robot (-1 to 1)
     * @param direction direction in degrees (0 to 360)
     * @param twist magnitude of angular velocity (-1 to 1)
     */
    public void fieldOriented(double magnitude, double direction, double twist) {
        //If the robot either is twisting or just became field oriented
        //then set the PID loop's angle to the current direction.
        if (twist != 0 || !fieldOriented || this.twist != 0) {
            this.anglePIDLoop.setSetpoint(gyro.getAngle());
        }

        //Set the member variables
        this.fieldOriented = true;
        this.magnitude = magnitude;
        this.direction = direction;
        this.twist = twist;
    }

    /**
     * Sets the robot to drive in the given magnitude, direction, and twist.
     * @param magnitude magnitude of the velocity of the robot (-1 to 1)
     * @param direction direction in degrees (0 to 360)
     * @param twist magnitude of angular velocity (-1 to 1)
     */
    public void robotOriented(double magnitude, double direction, double twist) {
        this.magnitude = magnitude;
        this.direction = direction;
        this.twist = twist;
        if (twist != 0 || this.fieldOriented) {
            this.anglePIDLoop.setSetpoint(gyro.getAngle());
        }
        this.fieldOriented = false;
    }

    /**
     * Sets the robot to drive in the given magnitude
     * and direction (relative to the field).
     * It keeps the robot facing at the given angle.
     * @param magnitude magnitude of the velocity of the robot (-1 to 1)
     * @param direction direction in degrees (0 to 360)
     * @param angle the angle to keep the robot pointed at (0 to 360)
     */
    public void fieldOrientedAngle(double magnitude, double direction, double angle) {
        this.fieldOriented = true;
        this.magnitude = magnitude;
        this.direction = direction;
        this.twist = 0;
        this.anglePIDLoop.setSetpoint(angle);
    }

    /**
     * Sets the robot to drive in the given magnitude and direction.
     * It keeps the robot facing at the given angle.
     * @param magnitude magnitude of the velocity of the robot (-1 to 1)
     * @param direction direction in degrees (0 to 360)
     * @param angle the angle to keep the robot pointed at (0 to 360)
     */
    public void robotOrientedAngle(double magnitude, double direction, double angle) {
        this.fieldOriented = false;
        this.magnitude = magnitude;
        this.direction = direction;
        this.twist = 0;
        this.anglePIDLoop.setSetpoint(angle);
    }

    /**
     * Enables the driving
     * This will enable but will clear out any saved motor values.
     */
    public void enable() {
        //This will prevent multiple calls to the PID Loop's enable method.
        if (!enabled) {
            System.out.println("Driveline Enabled");
            enabled = true;
            robotOriented(0, 0, 0);
            anglePIDLoop.enable();
        }
    }

    /**
     * Disables the driving
     * This will turn all motors off before it disables.
     */
    public void disable() {
        //This will prevent multiple calls to the PID Loop's disable method.
        if (enabled) {
            System.out.println("Driveline Disabled");
            enabled = false;
            robotOriented(0, 0, 0);
            anglePIDLoop.reset();
        }
    }

    /**
     * Resets the robot gyro to think it is facing away from the driver.
     */
    public synchronized void resetGyro() {
        gyro.reset();
        anglePIDLoop.setSetpoint(0);
    }

    public void setBallCentric(boolean ballCentric) {
//        if(ballCentric) {
//            driveline.setY(1.2);
//        } else {
//            driveline.setY(0);
//        }
    }
}
