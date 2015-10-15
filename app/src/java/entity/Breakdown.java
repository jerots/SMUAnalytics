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
public class Breakdown {
	ArrayList<HashMap<String, Breakdown>> breakdown = new ArrayList<HashMap<String,Breakdown>>();
	String message;
	String type;

	public Breakdown() {
	}

	public Breakdown(String message) {
		this.message = message;
	}

	public void setBreakdown(ArrayList<HashMap<String, Breakdown>> breakdown) {
		this.breakdown = breakdown;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<HashMap<String, Breakdown>> getBreakdown() {
		return breakdown;
	}

	public String getMessage() {
		return message;
	}

	public String getType() {
		return type;
	}
	
	public void addInList(HashMap<String,Breakdown> map){
		breakdown.add(map);
	}
	
	public String toString(){
		return message;
	}
	
}
