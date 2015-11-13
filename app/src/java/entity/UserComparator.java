/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import dao.Utility;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author Boyofthefuture
 */
/**
 * UserComparator compares and sort User objects
 */
public class UserComparator implements Comparator<HashMap<String, String>> {

    /**
     * Compare and sort the rank according to the HashMap of User related results given
     *
     * @param o1 The first object to compare with
     * @param o2 The second object to compare with
     * @return -1 if Integer less than the argument, 0 if Integer equals to the argument, 1 if Integer more than the argument
     */
    @Override
    public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
        if(o1 != null && o2 != null && o1.size() == 4 && o2.size() == 4){
            if(Utility.parseInt(o1.get("rank")) < Utility.parseInt(o2.get("rank"))){
                return -1;
            }else if(Utility.parseInt(o1.get("rank")) > Utility.parseInt(o2.get("rank"))){
                return 1;
            }
            return o1.get("name").compareTo(o2.get("name"));
        }
        return 0;
    }
    
}
