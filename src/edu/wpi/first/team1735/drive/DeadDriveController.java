/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.team1735.drive;

/**
 *
 * @author Administrator
 */
public class DeadDriveController extends DriveController {

    public DeadDriveController() {
        this.fieldOriented = false;
        this.name = "Dead";
    }

    public boolean drive(JoeDriveline driveline) {
        return true;
    }


}
