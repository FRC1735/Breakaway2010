/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.team1735;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.DriverStationLCD.Line;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Joe
 */
public class Whiteboard extends Thread {

    private static Whiteboard instance;
    private String[] messages;

    private Whiteboard() {
        this.messages = new String[6];
        for (int i = 0; i < 6; i++) {
            this.messages[i] = "";
        }
        start();
    }

    /**
     * Places a message at the top of the textbox, and pushes the others down
     * @param message the message to display
     */
    public static void write(String message) {
        //This makes sure that the program is updating the dashboard regularly
        if (instance == null) {
            instance = new Whiteboard();
        }

        //Shifts the messages down
        for (int i = 5; i > 0; i--) {
            instance.messages[i] = instance.messages[i - 1];
        }
        write(1, message);
    }

    /**
     * Saves a message to a given row on the dashboard
     * @param row the row to print to (values range from 1 to 6)
     * @param message the message to print out
     */
    public static void write(int row, String message) {
        //This makes sure that the program is updating the dashboard regularly
        if (instance == null) {
            instance = new Whiteboard();
        }

        System.out.println(message);

        //Fills the message with white space
        for (int i = message.length(); i <= DriverStationLCD.kLineLength; i++) {
            message += " ";
        }

        //Save the message until the thing updates
        if(row < 1) {
            row = 1;
        } else if (row > 6) {
            row = 6;
        }
        instance.messages[row - 1] = message;
    }

    /**
     * This will convert a row number into the format that WPILibJ requires
     * @param row the row to convert into WPILibJ Line object
     * @return the Line object to give to WPILibJ
     */
    private static Line determineLine(int row) {
        switch (row) {
            case 1:
                return DriverStationLCD.Line.kMain6;
            case 2:
                return DriverStationLCD.Line.kUser2;
            case 3:
                return DriverStationLCD.Line.kUser3;
            case 4:
                return DriverStationLCD.Line.kUser4;
            case 5:
                return DriverStationLCD.Line.kUser5;
            case 6:
                return DriverStationLCD.Line.kUser6;
        }
        System.out.println("There is no row " + row + " on the dashboard.  Printing to row 1");
        return DriverStationLCD.Line.kMain6;
    }

    /**
     * This will update the dashboard screen every 0.25 seconds
     */
    public void run() {
        while (true) {
            Timer.delay(.25);
            for (int i = 0; i < 6; i++) {
                DriverStationLCD.getInstance().println(determineLine(i + 1), 1, this.messages[i]);
            }
            DriverStationLCD.getInstance().updateLCD();
        }
    }
}
