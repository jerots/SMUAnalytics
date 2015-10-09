package dao;

import entity.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

public class AppUsageDAO {

    private HashMap<String, Integer> duplicate;
    private HashMap<String, AppUsage> appList;

    public AppUsageDAO() {
        appList = new HashMap<>();
        duplicate = new HashMap<>();
    }

    public int[] insert(CSVReader reader, HashMap<Integer, String> errMap) throws IOException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
        int index = 2;
        String sql = "insert into appusage (timestamp, macaddress, appid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?) ON DUPLICATE KEY UPDATE appid "
                + " = VALUES(appid);";
        PreparedStatement stmt = conn.prepareStatement(sql);

        String[] arr = null;
        while ((arr = reader.readNext()) != null) {
            //retrieving per row
            boolean err = false;
            boolean pass = false;

            //check timestamp
            String date = Utility.parseString(arr[0]);
            if (date == null || !Utility.checkDate(date)) {
                err = true;
                String errorMsg = errMap.get(index);
                if (errorMsg == null){
                    errMap.put(index, "invalid timestamp");
                } else {
                    errMap.put(index, errorMsg + "," + "invalid timestamp");
                }
            }

            //check macAdd
            String macAdd = Utility.parseString(arr[1]);
            if (macAdd == null) {
                String errorMsg = errMap.get(index);
                if (errorMsg == null){
                    errMap.put(index, "mac add cannot be blank");
                } else {
                    errMap.put(index, errorMsg + "," + "mac add cannot be blank");
                }
                err = true;
            }
            if (!Utility.checkHexadecimal(macAdd)) {
                String errorMsg = errMap.get(index);
                if (errorMsg == null){
                    errMap.put(index, "invalid mac add");
                } else {
                    errMap.put(index, errorMsg + "," + "invalid mac add");
                }
                err = true;
            }

            String query = "select macaddress from user where macaddress = ?;";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, macAdd);

            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("macaddress") == null) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null){
                        errMap.put(index, "no matching mac address");
                    } else {
                        errMap.put(index, errorMsg + "," + "no matching mac address");
                    }
                    err = true;
                }
            }
            pStmt.close();

            //check appid
            int appId = Utility.parseInt(arr[2]);
            if (appId <= 0) {
                String errorMsg = errMap.get(index);
                if (errorMsg == null){
                    errMap.put(index, "app id cannot be blank");
                } else {
                    errMap.put(index, errorMsg + "," + "app id cannot be blank");
                }
                err = true;
            }

            query = "select appid from app where appid = ?;";
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, appId);
            rs = pStmt.executeQuery();
            rs.next(); //pushes to the first line.
            int appIdReturn = rs.getInt("appid");
            if (appIdReturn <= 0) {
                String errorMsg = errMap.get(index);
                if (errorMsg == null){
                    errMap.put(index, "invalid app");
                } else {
                    errMap.put(index, errorMsg + "," + "invalid app");
                }
                err = true;
            }
            pStmt.close();

            if (!err) {
                
                if (duplicate.containsKey(date + macAdd)) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null){
                        errMap.put(index, "duplicate row " + duplicate.get(date + macAdd));
                    } else {
                        errMap.put(index, errorMsg + "," + "duplicate row " + duplicate.get(date + macAdd));
                    }
                }
                duplicate.put(date + macAdd, index);
                //add to list
                stmt.setString(1, date);
                stmt.setString(2, macAdd);
                stmt.setInt(3, appId);
                stmt.addBatch();
                //insert into tables
            }
            index++;
        }
        
        int[] updatedRecords = stmt.executeBatch();
        conn.commit();

        //closing
        reader.close();
        ConnectionManager.close(conn, stmt);
        return updatedRecords;
    }

    public int[] add(CSVReader reader, HashMap<Integer, String> errMap) throws IOException, SQLException {
        int[] updateCounts = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            int index = 2;
            String sql = "insert into appusage (timestamp, macaddress, appid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
            PreparedStatement stmt = conn.prepareStatement(sql);

            String[] arr = null;
            while ((arr = reader.readNext()) != null) {
                //retrieving per row
                boolean err = false;
                boolean pass = false;

                //check timestamp
                String date = Utility.parseString(arr[0]);
                if (date == null || !Utility.checkDate(date)) {
                    err = true;
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid timestamp");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid timestamp");
                    }
                }

                //check macAdd
                String macAdd = Utility.parseString(arr[1]);
                if (macAdd == null) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "mac add cannot be blank");
                    } else {
                        errMap.put(index, errorMsg + "," + "mac add cannot be blank");
                    }
                    err = true;
                }
                if (!Utility.checkHexadecimal(macAdd)) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid mac add");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid mac add");
                    }
                    err = true;
                }

                String query = "select macaddress from user where macaddress = ?;";
                PreparedStatement pStmt = conn.prepareStatement(query);
                pStmt.setString(1, macAdd);

                ResultSet rs = pStmt.executeQuery();
                while (rs.next()) {
                    if (rs.getString("macaddress") == null) {
                        String errorMsg = errMap.get(index);
                        if (errorMsg == null) {
                            errMap.put(index, "no matching mac address");
                        } else {
                            errMap.put(index, errorMsg + "," + "no matching mac address");
                        }
                        err = true;
                    }
                }
                pStmt.close();

                //check appid
                int appId = Utility.parseInt(arr[2]);
                if (appId <= 0) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "app id cannot be blank");
                    } else {
                        errMap.put(index, errorMsg + "," + "app id cannot be blank");
                    }
                    err = true;
                }

                query = "select appid from app where appid = ?;";
                pStmt = conn.prepareStatement(query);
                pStmt.setInt(1, appId);
                rs = pStmt.executeQuery();
                rs.next(); //pushes to the first line.
                int appIdReturn = rs.getInt("appid");
                if (appIdReturn <= 0) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid app");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid app");
                    }
                    err = true;
                }
                pStmt.close();

                if (!err) {
                    if (duplicate.containsKey(date + macAdd)) {
                        String errorMsg = errMap.get(index);
                        if (errorMsg == null) {
                            errMap.put(index, "duplicate row " + duplicate.get(date + macAdd));
                        } else {
                            errMap.put(index, errorMsg + "," + "duplicate row " + duplicate.get(date + macAdd));
                        }
                    }
                    duplicate.put(date + macAdd, index);
                    appList.put(date + macAdd, new AppUsage(date, macAdd, appId));
                }
                ArrayList<AppUsage> appArray = (ArrayList<AppUsage>) appList.values();
                try {
                    for (AppUsage app : appArray) {
                        stmt.setString(1, app.getTimestamp());
                        stmt.setString(2, app.getMacAddress());
                        stmt.setInt(3, app.getAppId());
                        stmt.addBatch();
                    }
                    //closing
                    if (stmt != null) {
                        stmt.executeBatch();
                        conn.commit();
                    }
                } catch (BatchUpdateException e) {
                    updateCounts = e.getUpdateCounts();
                    for (int i = 0; i < updateCounts.length; i++) {
                        if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                            // This method retrieves the row fail, and then searches the prikey corresponding and then uses the duplicate HashMap to find the offending row.
                            String errorMsg = errMap.get(index);
                            if (errorMsg == null) {
                                errMap.put(index, "duplicate row " + duplicate.get(appArray.get(i).getTimestamp() + appArray.get(i).getMacAddress()));
                            } else {
                                errMap.put(index, errorMsg + "," + "duplicate row " + duplicate.get(appArray.get(i).getTimestamp() + appArray.get(i).getMacAddress()));
                            }
                        }
                    }
                }
                index++;
            }
            reader.close();
            ConnectionManager.close(conn, stmt);
            
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return updateCounts;
    }
	
	public ArrayList<String> retrieveUsers(Date startDate, Date endDate) {

		ArrayList<String> result = new ArrayList<String>();
		
		try {
			
			Connection conn = ConnectionManager.getConnection();
			
			PreparedStatement ps = conn.prepareStatement("SELECT macaddress from appUsage where "
					+ "timestamp >= ? AND timestamp <= ? "
					+ "GROUP BY macaddress");
			ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
			ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()){
				String macAdd = rs.getString(1);
				result.add(macAdd);
			}
			
			
			
		} catch (SQLException e){
			
		}
		
		
		return result;
	}
	
	public ArrayList<String> retrieveUsers(Date startDate, Date endDate, String sql) {

		ArrayList<String> result = new ArrayList<String>();
		
		try {
			
			Connection conn = ConnectionManager.getConnection();
			
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
			ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()){
				String macAdd = rs.getString(1);
				result.add(macAdd);
			}
			
			
			
		} catch (SQLException e){
			
		}
		
		
		return result;
	}
	
	
	public ArrayList<AppUsage> retrieveByUser(String macAdd, Date startDate, Date endDate) {

		ArrayList<AppUsage> result = new ArrayList<AppUsage>();
		
		try {
			
			Connection conn = ConnectionManager.getConnection();
			
			PreparedStatement ps = conn.prepareStatement("SELECT * from appusage where "
					+ "timestamp >= ? AND timestamp <= ? "
					+ "AND macaddress = ?");
			ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
			ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());
			ps.setString(3, macAdd);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()){
				
				String timestamp = rs.getString(1);
				String macaddress = rs.getString(2);
				int appid = rs.getInt(3);
				result.add(new AppUsage(timestamp, macaddress, appid));
				
			}
			
			
			
		} catch (SQLException e){
			
		}
		
		
		return result;
	}
	
}
