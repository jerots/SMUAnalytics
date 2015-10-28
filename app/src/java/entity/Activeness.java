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
public class Activeness{
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

    public long overlap(Activeness active){
        if(active.getEndTime() < startTime || active.getStartTime() > endTime){
            return 0;
        }
        //If there is an overlap, returns the millisecs that is overlapped
        if(active.getStartTime() <= startTime){
            long activeEnd = active.getEndTime();
            if(activeEnd <= endTime){
            //This means that it is within/end time is after this active's end time
                return activeEnd - startTime;
            }else{
                return endTime - startTime;
            }
        }else{
            long activeStart = active.getStartTime();
            long activeEnd = active.getEndTime();
            if(activeEnd <= endTime){
                return activeEnd - activeStart;
            }else{
                return endTime - activeStart;
            }
        }
    }
}
