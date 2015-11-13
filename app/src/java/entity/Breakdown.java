/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jeremyongts92
 */
/**
 * Breakdown represents a JSON breakdown that can consist of further breakdowns
 */
public class Breakdown {

    ArrayList<HashMap<String, Breakdown>> breakdown;
    String message;
    String type;

    /**
     * Creates a Breakdown object
     */
    public Breakdown() {
        breakdown = new ArrayList<HashMap<String, Breakdown>>();
    }

    /**
     * Creates a Breakdown object with message
     *
     * @param message The message to display
     */
    public Breakdown(String message) {
        breakdown = new ArrayList<HashMap<String, Breakdown>>();
        this.message = message;
    }

    /**
     * Creates a Breakdown object further breakdowns
     *
     * @param breakdown The further breakdowns
     */
    public Breakdown(ArrayList<HashMap<String, Breakdown>> breakdown) {
        this.breakdown = breakdown;
    }

    /**
     * Set a Breakdown object
     *
     * @param breakdown The further breakdowns
     */
    public void setBreakdown(ArrayList<HashMap<String, Breakdown>> breakdown) {
        this.breakdown = breakdown;
    }

    /**
     * Set the message for the Breakdown object
     *
     * @param message The message to display
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Set the type for the Breakdown object
     *
     * @param type The type of the breakdown
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retrieve an arraylist of Breakdown
     *
     * @return Hashmap of further breakdowns
     */
    public ArrayList<HashMap<String, Breakdown>> getBreakdown() {
        return breakdown;
    }

    /**
     * Get the message in Breakdown Object
     *
     * @return the message in Breakdown Object
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the type in Breakdown Object
     *
     * @return the type in Breakdown Object
     */
    public String getType() {
        return type;
    }

    /**
     * Adds a map in the list
     * @param map Additional breakdowns
     */
    public void addInList(HashMap<String, Breakdown> map) {
        breakdown.add(map);
    }

    /**
     * Converts to String
     *
     */
    public String toString() {
        return message;
    }

}
