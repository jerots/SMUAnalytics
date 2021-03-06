package entity;

/**
 *
 * @author Boyofthefuture
 */
/**
 * Activeness represents a user's social activeness in a location between a time
 * interval
 */
import java.util.ArrayList;
import java.util.Date;

public class Activeness implements Comparable<Activeness> {

    //Why long? because long will make it very quick to calculate differences, and will help with the controller.
    //Activeness streamlines the problem of Social Activeness by just storing both start and end
    private long startTime;
    private long endTime;
    private Location location;
    private App app;

    /**
     * Creates a Activeness object with startTime, endTime, location
     *
     * @param startTime The given start time of interest
     * @param endTime The given end time of interest
     * @param location The location object
     */
    public Activeness(long startTime, long endTime, Location location) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    /**
     * Set the app of a social activeness
     *
     * @param app The App of a social activeness
     */
    public void setApp(App app) {
        this.app = app;
    }

    /**
     * Get the App object of a user's social activeness
     *
     * @return the App object
     */
    public App getApp() {
        return app;
    }

    /**
     * Set the location of a user's social activeness
     *
     * @param location The location object
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Get the location object of a user's social activeness
     *
     * @return the Location object
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get the start time of User's usage of Social Activeness
     *
     * @return the start time of User's usage of Social Activeness
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Get the end time of Usage in the Activeness
     *
     * @return the end time of Usage in the Activeness
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Set the Start Time of the User's Usage in the Social Activeness
     *
     * @param startTime The given start time of interest
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Set the End Time of the User's Usage in the Social Activeness
     *
     * @param endTime The given end time of interest
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * Check if two Activeness overlaps
     *
     * @param active The other Activeness object for comparison
     * @return an Activeness object
     */
    public Activeness overlap(Activeness active) { //Puts the overlap into a new activeness
             //Checks for location initially as well to make sure that their locations are correct so that they can overlap
        if(!(active.endTime <= startTime || active.startTime >= endTime) && active.location.equals(location)){
            //If there is an overlap, returns the millisecs that is overlapped
            if(active.startTime <= startTime){
                long activeEnd = active.endTime;
                if(activeEnd <= endTime){
                //This means that it is within/end time is after this active's end time
                    return new Activeness(startTime, activeEnd, location);
                }else{
                    return new Activeness(startTime, endTime, location);
                }
            }else{
                long activeStart = active.startTime;
                long activeEnd = active.endTime;
                if(activeEnd <= endTime){
                    return new Activeness(activeStart, activeEnd, location);
                }else{
                    return new Activeness(activeStart, endTime, location);
                }
            }
        }
        return null;
    }
               

    /**
     * Check if two Activeness are continuous
     *
     * @param overlap The other Activeness object for comparison
     * @return true if they are continuous, false otherwise
     */
                         
     public boolean continuation(Activeness overlap){
        //Purpose of this is to correlate a startdate with an end date to make sure two activeness are CONTINUOUS
        //The item here compared MUST BE THE ENDING ACTIVENESS.
        return endTime == overlap.startTime;
    }

    /**
     * Get the time in long
     *
     * @return the time
     */
    public long getTime() {
        return endTime - startTime;
    }

    /**
     * Combine activeness that are overlapping
     *
     * @param active The Activeness Object
     * @return Activeness object
     */
    public Activeness combine(Activeness active){
        //The purpose of this method is to combine activeness that are overlapping and return a new activeness
        long start = 0;
        if(active.startTime < startTime){
            start = active.startTime;
        }else{
            start = startTime;
        }
        long end = 0;
        if(active.endTime < endTime){
            end = endTime;
        }else{
            end = active.endTime;
        }
        return new Activeness(start, end, location);
    }

    /**
     * Equates Activeness if they overlap
     *
     * @param obj An object
     * @return true if they overlap, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Activeness other = (Activeness) obj;
        return overlap(other) != null;
    }

    /**
     * Compare if two Activeness are equal
     *
     * @param o The object to compare
     * @return -1 if Integer less than the argument, 0 if Integer equals to the
     * argument, 1 if Integer more than the argument
     */
    @Override
    public int compareTo(Activeness o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
