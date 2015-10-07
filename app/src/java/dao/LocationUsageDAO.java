/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import entity.*;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.HashMap;

/**
 *
 * @author ASUS-PC
 */
public class LocationUsageDAO {
    private HashMap<String, LocationUsage> locationList;
    private ArrayList<String> unsuccessful;
    private HashMap<String, Integer> duplicate;
    
    public LocationUsageDAO(){
        unsuccessful = new ArrayList<>();
        duplicate = new HashMap<>();
        locationList = new HashMap<>();
    }

    public void insert(CSVReader reader) throws IOException, SQLException {
        try{
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            int counter = 1;
            String sql = "insert into locationusage (timestamp, macaddress, locationid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?) ON DUPLICATE KEY UPDATE locationid = "
                    + "VALUES(locationid);";
            PreparedStatement stmt = conn.prepareStatement(sql);

            String[] arr = null;
            while ((arr = reader.readNext()) != null) {
                //retrieving per row
                boolean err = false;
                boolean pass = false;

                //check timestamp
                java.util.Date dateCheck = Utility.parseDate(arr[0]);
                if (dateCheck == null) {
                    err = true;
                    unsuccessful.add("invalid timestamp");
                }
                String date = Utility.formatDate(dateCheck);
                
                //check macAdd
                String macAdd = Utility.parseString(arr[1]);
                if (macAdd == null) {
                    unsuccessful.add("mac add cannot be blank");
                    err = true;
                }
                if (!Utility.checkHexadecimal(macAdd)) {
                    unsuccessful.add("invalid mac address");
                    err = true;
                }

                //check appid
                int locationId = Utility.parseInt(arr[2]);
                if (locationId <= 0) {
                    unsuccessful.add("location id cannot be blank");
                    err = true;
                }

                String query = "select locationid from location;";
                PreparedStatement pStmt = conn.prepareStatement(query);
                ResultSet rs = pStmt.executeQuery();
                while(rs.next()){
                    if(rs.getInt("locationid") == locationId){
                        pass = true;
                    }
                }
                if(!pass){
                    unsuccessful.add("invalid location");
                    err = true;
                }
                pStmt.close();

                if (!err) {
                    counter++;
                    if(duplicate.containsKey(date + macAdd)){
                        unsuccessful.add("duplicate row " + duplicate.get(date + macAdd));
                    }
                    duplicate.put(date + macAdd, counter);
                    //add to list
                    stmt.setString(1, date);
                    stmt.setString(2, macAdd);
                    stmt.setInt(3, locationId);
                    stmt.addBatch();
                }

            }
            //insert into tables

            //closing
            if (stmt != null) {
                stmt.executeBatch();
                conn.commit();
            }
            reader.close();
            ConnectionManager.close(conn,stmt);

        }catch(NullPointerException e){

        }
    }
    
    public void add(CSVReader reader) throws IOException, SQLException {
        try{
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            int counter = 1;
            String sql = "insert into locationusage (timestamp, macaddress, locationid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
            PreparedStatement stmt = conn.prepareStatement(sql);

            String[] arr = null;
            while ((arr = reader.readNext()) != null) {
                //retrieving per row
                boolean err = false;
                boolean pass = false;

                //check timestamp
                java.util.Date dateCheck = Utility.parseDate(arr[0]);
                String date = Utility.formatDate(dateCheck);
                if (date == null) {
                    err = true;
                    unsuccessful.add("invalid timestamp");
                }

                //check macAdd
                String macAdd = Utility.parseString(arr[1]);
                if (macAdd == null) {
                    unsuccessful.add("mac add cannot be blank");
                    err = true;
                }
                if (!Utility.checkHexadecimal(macAdd)) {
                    unsuccessful.add("invalid mac address");
                    err = true;
                }

                //check appid
                int locationId = Utility.parseInt(arr[2]);
                if (locationId <= 0) {
                    unsuccessful.add("location id cannot be blank");
                    err = true;
                }
                
                String query = "select locationid from location;";
                PreparedStatement pStmt = conn.prepareStatement(query);
                ResultSet rs = pStmt.executeQuery();
                while(rs.next()){
                    if(rs.getInt("locationid") == locationId){
                        pass = true;
                    }
                }
                if(!pass){
                    unsuccessful.add("invalid location");
                    err = true;
                }
                pStmt.close();

                if (!err) {
                    counter++;
                    if(duplicate.containsKey(date + macAdd)){
                        unsuccessful.add("duplicate row " + duplicate.get(date + macAdd));
                    }
                    duplicate.put(date + macAdd, counter);
                    locationList.put(date + macAdd, new LocationUsage(date, macAdd, locationId));
                }

            }
            ArrayList<LocationUsage> locList = (ArrayList<LocationUsage>) locationList.values();
            try{
                for(LocationUsage loc: locList){
                    stmt.setString(1, loc.getTimestamp());
                    stmt.setString(2, loc.getMacAddress());
                    stmt.setInt(3, loc.getLocationId());
                    stmt.addBatch();
                }
                //closing
                if (stmt != null) {
                    stmt.executeBatch();
                    conn.commit();
                }
            }catch(BatchUpdateException e){
                int[] updateCounts = e.getUpdateCounts();
                for (int i = 0; i < updateCounts.length; i++) {
                    if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                        // This method retrieves the row fail, and then searches the locationid corresponding and then uses the duplicate HashMap to find the offending row.
                        unsuccessful.add("duplicate row " + duplicate.get(locList.get(i).getTimestamp() + locList.get(i).getMacAddress())); 
                    }
                }
            }
            reader.close();
            ConnectionManager.close(conn,stmt);

        }catch(NullPointerException e){

        }
    }
    
    public void delete(CSVReader reader) throws IOException, SQLException {
        try{
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "delete from locationusage where timestamp = STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and macaddress = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            int counter = 0; //counts total number of records.
            int notFound = 0; //counts number of records not found in database.
            String[] arr = null;
            while ((arr = reader.readNext()) != null) {
                //retrieving per row
                boolean err = false;

                //check timestamp
                java.util.Date dateCheck = Utility.parseDate(arr[0]);
                String date = Utility.formatDate(dateCheck);
                if (date == null) {
                    err = true;
                    unsuccessful.add("invalid timestamp");
                }

                //check macAdd
                String macAdd = Utility.parseString(arr[1]);
                if (macAdd == null) {
                    unsuccessful.add("mac add cannot be blank");
                    err = true;
                }
                if (!Utility.checkHexadecimal(macAdd)) {
                    unsuccessful.add("invalid mac address");
                    err = true;
                }

                if (!err) {
                    counter++;
                    stmt.setString(1, date);
                    stmt.setString(2, macAdd);
                    stmt.addBatch();
                }
                if (stmt != null) {
                    int[] updateCounts = stmt.executeBatch();
                    conn.commit();
                    for(int i: updateCounts){
                        if(i == 0){
                            notFound++;
                        }
                    }
                }
                unsuccessful.add("Number of Valid records deleted: " + (counter - notFound));
                unsuccessful.add("Number of Valid records not found in the database: " + notFound);
            }
            reader.close();
            ConnectionManager.close(conn,stmt);

        }catch(NullPointerException e){

        }
    }
    
    public void delete(String macAdd, String startDate, String endDate) throws IOException, SQLException {
        try{
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "delete from locationusage where timestamp BETWEEN STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s')"
                    + " and macaddress = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
 
            boolean err = false;
            //check timestamp
            java.util.Date dateCheck = Utility.parseDate(startDate);
            startDate = Utility.formatDate(dateCheck);
            if (startDate == null) {
                err = true;
                unsuccessful.add("invalid start timestamp");
            }

            dateCheck = Utility.parseDate(endDate);
            endDate = Utility.formatDate(dateCheck);
            if (endDate == null) {
                err = true;
                unsuccessful.add("invalid end timestamp");
            }

            //check macAdd
            macAdd = Utility.parseString(macAdd);
            if (macAdd == null) {
                unsuccessful.add("mac add cannot be blank");
                err = true;
            }
            if (!Utility.checkHexadecimal(macAdd)) {
                unsuccessful.add("invalid mac address");
                err = true;
            }

            if (!err) {
                stmt.setString(1, startDate + "00:00:00");
                stmt.setString(2, endDate + "23:59:59");
                stmt.setString(3, macAdd);
                stmt.addBatch();
            }
            if (stmt != null) {
                int[] errors = stmt.executeBatch();
                conn.commit();
                if(errors[0] == 0){
                    unsuccessful.add("No records have been deleted.");
                }
            }
            ConnectionManager.close(conn,stmt);

        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }
    
	public ArrayList<LocationUsage> retrieve(java.util.Date date, String loc) {
		ArrayList<LocationUsage> result = new ArrayList<LocationUsage>();
		try {
			String sql = "SELECT timestamp, macaddress, lu.locationid \n"
					+ "FROM (\n"
					+ "SELECT MAX(TIMESTAMP) as timestamp, macaddress, locationid FROM locationUsage\n"
					+ "WHERE timestamp >= STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') \n"
					+ "AND timestamp < STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') \n"
					+ "group by macaddress\n"
					+ ") as lu,\n"
					+ "location l\n"
					+ "WHERE \n"
					+ "lu.locationid = l.locationid\n"
					+ "AND semanticplace = ? \n"
					+ ";";
			
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);

			Date before = new java.sql.Date(date.getTime() - 900000);
			Date after = new java.sql.Date(date.getTime());
			ps.setString(1, Utility.formatDate(before)); //15 minutes before
			ps.setString(2, Utility.formatDate(after));
			ps.setString(3, loc);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Timestamp timestamp = rs.getTimestamp(1);
				String macAddress = rs.getString(2);
				String locationId = rs.getString(3);

				LocationUsage curr = new LocationUsage(Utility.formatDate(new Date(timestamp.getTime())), macAddress, Integer.parseInt(locationId));
				result.add(curr);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(result.size());
		return result;
	}

}
