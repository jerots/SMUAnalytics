package entity;

import java.util.ArrayList;
import java.util.Date;

public class Activeness implements Comparable<Activeness>{
    //Why long? because long will make it very quick to calculate differences, and will help with the controller.
    //Activeness streamlines the problem of Social Activeness by just storing both start and end
    private long startTime;
    private long endTime;
    private Location location;
    private App app;
    private int members;

    public Activeness(long startTime, long endTime, Location location) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }
    
    public Activeness(long startTime, long endTime, App app) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.app = app;
    }

    public Activeness(long startTime, long endTime, int members, Location location) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.members = members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public int getMembers() {
        return members;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
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

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    //This method DOES return an overlap if it is less than 5 minutes
    public Activeness overlap(Activeness active){ //Puts the overlap into a new activeness
        //Checks for location initially as well to make sure that their locations are correct so that they can overlap
        if(!(active.endTime <= startTime || active.startTime >= endTime)){
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
    
    //THIS IS A SPECIAL METHOD ONLY FOR ADVANCED OVERUSE GROUP. THIS METHOD WILL RETURN NUMBERS.
    //This method DOES return an overlap if it is less than 5 minutes
    public Activeness memberOverlap(Activeness active){ //Puts the overlap into a new activeness
        //Checks for location initially as well to make sure that their locations are correct so that they can overlap
        if(!(active.endTime <= startTime || active.startTime >= endTime)){
            //If there is an overlap, returns the millisecs that is overlapped
            if(active.startTime <= startTime){
                long activeEnd = active.endTime;
                if(activeEnd <= endTime){
                //This means that it is within/end time is after this active's end time
                    return new Activeness(startTime, activeEnd, (active.members + members), location);
                }else{
                    return new Activeness(startTime, endTime, (active.members + members), location);
                }
            }else{
                long activeStart = active.startTime;
                long activeEnd = active.endTime;
                if(activeEnd <= endTime){
                    return new Activeness(activeStart, activeEnd, (active.members + members), location);
                }else{
                    return new Activeness(activeStart, endTime, (active.members + members), location);
                }
            }
        }
        return null;
    }
    
    //This special method will the split the activeness based on the overlaps.
    public ArrayList<Activeness> activenessSplit(Activeness active){
        ArrayList<Activeness> aList = new ArrayList<>();
        Activeness overlap = memberOverlap(active);
        //PART I checks if there is an overlap piece in front.
        if(active.startTime < overlap.startTime){
            aList.add(new Activeness(active.startTime, overlap.startTime, 1, location));
        }else if(startTime < overlap.startTime){
            aList.add(new Activeness(startTime, overlap.startTime, 1, location));
        }
        aList.add(overlap);
        if(active.endTime > overlap.endTime){
            aList.add(new Activeness(overlap.endTime, active.endTime, 1, location));
        }else if(endTime > overlap.endTime){
            aList.add(new Activeness(overlap.endTime, endTime, 1, location));
        }
        return aList;
    }

    @Override
    public String toString() {
        return "Activeness{" + "startTime=" + new Date(startTime) + ", endTime=" + new Date(endTime) + ", location=" + location + ", app=" + app + ", members=" + members + '}';
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
        return new Activeness(start, end, location);
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
        if(startTime <= o.startTime){
            return -1;
        }else if(o.startTime <= startTime){
            return 1;
        }else{
            return 0;
        }
    }
}
