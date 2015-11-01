/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author Boyofthefuture
 */
public class Activeness implements Comparable<Activeness>{
    //Why long? because long will make it very quick to calculate differences, and will help with the controller.
    //Activeness streamlines the problem of Social Activeness by just storing both start and end
    private long startTime;
    private long endTime;
    private String macAddress;
    private Location location;

    public Activeness(long startTime, long endTime, String macAddress, Location location) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.macAddress = macAddress;
        this.location = location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    //This method DOES return an overlap if it is less than 5 minutes
    public Activeness overlap(Activeness active){ //Puts the overlap into a new activeness
        //Checks for location initially as well to make sure that their locations are correct so that they can overlap
        if(!(active.endTime <= startTime || active.startTime >= endTime) && active.location.equals(location)){
            //If there is an overlap, returns the millisecs that is overlapped
            if(active.startTime <= startTime){
                long activeEnd = active.endTime;
                if(activeEnd <= endTime){
                //This means that it is within/end time is after this active's end time
                    return new Activeness(startTime, activeEnd, active.macAddress, location);
                }else{
                    return new Activeness(startTime, endTime, active.macAddress, location);
                }
            }else{
                long activeStart = active.startTime;
                long activeEnd = active.endTime;
                if(activeEnd <= endTime){
                    return new Activeness(activeStart, activeEnd, active.macAddress, location);
                }else{
                    return new Activeness(activeStart, endTime, active.macAddress, location);
                }
            }
        }
        return null;
    }
    
    public boolean continuation(Activeness overlap){
        //Purpose of this is to correlate a startdate with an end date to make sure two activeness are CONTINUOUS
        //The item here compared MUST BE THE ENDING ACTIVENESS.
        return endTime == overlap.startTime;
    }
    
    //This method returns the difference in time
    public long getTime(){
        return endTime - startTime;
    }
    
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
        return new Activeness(start, end, macAddress, location);
    }
    
    //This is a very special equals method. It equates activeness IF THEY OVERLAP. This is when checking the arraylist
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
    
    @Override
    public int compareTo(Activeness o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
