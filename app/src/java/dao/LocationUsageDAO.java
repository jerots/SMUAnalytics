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
import java.util.TreeMap;

/**
 *
 * @author ASUS-PC
 */
public class LocationUsageDAO {

	private TreeMap<String, LocationUsage> locationList;
	private TreeMap<String, Integer> duplicate;

	public LocationUsageDAO() {
		duplicate = new TreeMap<>();
		locationList = new TreeMap<>();
	}

	public int[] insert(CSVReader reader, TreeMap<Integer, String> errMap, Connection conn, HashMap<Integer, String> locationIdList) throws IOException, SQLException {
		int index = 2;

		String sql = "insert into locationusage values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?) ON DUPLICATE KEY UPDATE locationid = "
				+ "VALUES(locationid);";
		PreparedStatement stmt = conn.prepareStatement(sql);

		String[] arr = null;
		while ((arr = reader.readNext()) != null) {
			//retrieving per row
			boolean err = false;
			String errorMsg = errMap.get(index);
			if (errorMsg == null) {
				errorMsg = "";
			}
			//check timestamp
			String date = Utility.parseString(arr[0]);
			if (date == null || !Utility.checkDate(date)) {
				errorMsg += ",invalid timestamp";
				err = true;
			}

			//check macAdd
			String macAdd = Utility.parseString(arr[1]);
			if (macAdd == null) {
				errorMsg += ",mac address cannot be blank";
				err = true;
			} else if (!Utility.checkHexadecimal(macAdd)) {

				errorMsg += ",invalid mac address";
				err = true;
			}

			//check appid
			int locationId = Utility.parseInt(arr[2]);
			if (locationId <= 0) {
				errorMsg += ",location id cannot be blank";
				err = true;
			} else if (!locationIdList.containsKey(locationId)) {

				errorMsg += ",invalid location";
				err = true;

			}

			if (!err) {
				String key = date + macAdd;
				Integer exisMac = duplicate.get(key);
				if (exisMac != null) {

					errMap.put(index, "duplicate row " + exisMac);

				}
				duplicate.put(key, index);
				//add to list
				stmt.setString(1, date);
				stmt.setString(2, macAdd);
				stmt.setInt(3, locationId);
				stmt.addBatch();
			} else {

				errMap.put(index, errorMsg.substring(1));
			}
			index++;
//			if (index % 10000 == 0){
//				stmt.executeBatch();
//			}

		}
		//insert into tables

		int[] updateCounts = stmt.executeBatch();
		conn.commit();

		return updateCounts;
	}

	public int add(CSVReader reader, TreeMap<Integer, String> errMap) throws IOException, SQLException {
		int updateCounts = 0;

		Connection conn = ConnectionManager.getConnection();
		conn.setAutoCommit(false);
		int index = 2;
		String sql = "insert into locationusage (timestamp, macaddress, locationid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
		PreparedStatement stmt = conn.prepareStatement(sql);

		String[] arr = null;
		while ((arr = reader.readNext()) != null) {
			//retrieving per row
			boolean err = false;

			//check timestamp
			String date = Utility.parseString(arr[0]);
			if (date == null || !Utility.checkDate(date)) {
				String errorMsg = errMap.get(index);
				if (errorMsg == null) {
					errMap.put(index, "invalid timestamp");
				} else {
					errMap.put(index, errorMsg + "," + "invalid timestamp");
				}
				err = true;
			}

			//check macAdd
			String macAdd = Utility.parseString(arr[1]);
			if (macAdd == null) {
				String errorMsg = errMap.get(index);
				if (errorMsg == null) {
					errMap.put(index, "mac address cannot be blank");
				} else {
					errMap.put(index, errorMsg + "," + "mac address cannot be blank");
				}
				err = true;
			} else if (!Utility.checkHexadecimal(macAdd)) {
				String errorMsg = errMap.get(index);
				if (errorMsg == null) {
					errMap.put(index, "invalid mac address");
				} else {
					errMap.put(index, errorMsg + "," + "invalid mac address");
				}
				err = true;
			}

			//check appid
			int locationId = Utility.parseInt(arr[2]);
			if (locationId == -1) {
				String errorMsg = errMap.get(index);
				if (errorMsg == null) {
					errMap.put(index, "location id cannot be blank");
				} else {
					errMap.put(index, errorMsg + "," + "location id cannot be blank");
				}
				err = true;

				//IF LOCATION ID NOT BLANK
			} else {

				String query = "select locationid from location where locationid = ?;";
				PreparedStatement pStmt = conn.prepareStatement(query);
				pStmt.setInt(1, locationId);
				ResultSet rs = pStmt.executeQuery();
				if (!rs.next()) {
					String errorMsg = errMap.get(index);
					if (errorMsg == null) {
						errMap.put(index, "invalid location");
					} else {
						errMap.put(index, errorMsg + "," + "invalid location");
					}
					err = true;
				}
				pStmt.close();
			}

			//IF ALL VALIDATIONS ARE PASSED
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
				locationList.put(date + macAdd, new LocationUsage(date, macAdd, locationId));
			}

			//row number increased
			index++;

		}

		//CHECK FOR DUPLICATES IN DATABASE
		ArrayList<LocationUsage> locList = new ArrayList<LocationUsage>(locationList.values());

		try {
			for (LocationUsage loc : locList) {
				stmt.setString(1, loc.getTimestamp());
				stmt.setString(2, loc.getMacAddress());
				stmt.setInt(3, loc.getLocationId());
				stmt.addBatch();
			}
			int[] updatedArr = stmt.executeBatch();
			for (int i : updatedArr) {
				updateCounts += i;
			}
			conn.commit();

			//CATCH WHEN THERE IS DUPLICATE
		} catch (BatchUpdateException e) {
			int[] updatedArr = e.getUpdateCounts();

			for (int i = 0; i < updatedArr.length; i++) {
				if (updatedArr[i] == Statement.EXECUTE_FAILED) {
					// This method retrieves the row fail, and then searches the locationid corresponding and then uses the duplicate TreeMap to find the offending row.
					int row = duplicate.get(locList.get(i).getTimestamp() + locList.get(i).getMacAddress());
					String errorMsg = errMap.get(row);
					if (errorMsg == null) {
						errMap.put(row, "duplicate row ");
					} else {
						errMap.put(row, errorMsg + "," + "duplicate row ");
					}
				}
				if (updatedArr[i] >= 0) {

					updateCounts += updatedArr[i];
				}
			}
		}
		reader.close();
		ConnectionManager.close(conn, stmt);

		return updateCounts;
	}

	public int[] delete(CSVReader reader, TreeMap<Integer, String> errMap) throws IOException, SQLException {
		int[] toReturn = new int[2];
		int index = 2; //counts the row of the record.
		int notFound = 0;
		int found = 0;
		try {

			Connection conn = ConnectionManager.getConnection();
			conn.setAutoCommit(false);
			String sql = "delete from locationusage where timestamp = STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and macaddress = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			String[] arr = null;
			while ((arr = reader.readNext()) != null) {
				//retrieving per row
				boolean err = false;

				//check timestamp
				String date = Utility.parseString(arr[0]);
				if (date == null || !Utility.checkDate(date)) {
					String errorMsg = errMap.get(index);
					if (errorMsg == null) {
						errMap.put(index, "invalid timestamp");
					} else {
						errMap.put(index, errorMsg + "," + "invalid timestamp");
					}
					err = true;
				}

				//check macAdd
				String macAdd = Utility.parseString(arr[1]);
				if (macAdd == null) {
					String errorMsg = errMap.get(index);
					if (errorMsg == null) {
						errMap.put(index, "mac address cannot be blank");
					} else {
						errMap.put(index, errorMsg + "," + "mac address cannot be blank");
					}
					err = true;
				}
				if (macAdd != null && !Utility.checkHexadecimal(macAdd)) {
					String errorMsg = errMap.get(index);
					if (errorMsg == null) {
						errMap.put(index, "invalid mac address");
					} else {
						errMap.put(index, errorMsg + "," + "invalid mac address");
					}
					err = true;
				}

				if (!err) {
					found++;
					stmt.setString(1, date);
					stmt.setString(2, macAdd);
					stmt.addBatch();
				}
				if (stmt != null) {
					int[] updateCounts = stmt.executeBatch();
					conn.commit();
					for (int i : updateCounts) {
						if (i == 0) {
							notFound++;
						}
					}
				}
				index++;
			}

			reader.close();
			ConnectionManager.close(conn, stmt);

		} catch (NullPointerException e) {

		}
		toReturn[0] = found - notFound; //Valid Records which have successfully deleted rows in the database
		toReturn[1] = notFound; //Valid Records which are succesful but have not deleted rows in the database
		return toReturn;
	}

	public int delete(String macAdd, String startDate, String endDate) throws IOException, SQLException {
		int updated = 0;
		try {
			Connection conn = ConnectionManager.getConnection();
			conn.setAutoCommit(false);
//			System.out.println(startDate);
//			System.out.println(endDate);
//			System.out.println(macAdd);
			String sql = "delete from locationusage where timestamp BETWEEN ? AND ?"
					+ " AND macaddress = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);

			stmt.setString(1, startDate + " 00:00:00");
			stmt.setString(2, endDate + " 23:59:59");
			stmt.setString(3, macAdd);
			stmt.addBatch();

			updated = stmt.executeUpdate();
			conn.commit();
			ConnectionManager.close(conn, stmt);

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return updated;
	}

	public ArrayList<LocationUsage> retrieve(java.util.Date date, String loc) {
		ArrayList<LocationUsage> result = new ArrayList<LocationUsage>();
		try {
			String sql = "SELECT timestamp, macaddress, lu.locationid \n"
					+ "FROM (\n"
					+ "SELECT MAX(TIMESTAMP) as timestamp, macaddress, locationid FROM locationusage\n"
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
			ConnectionManager.close(conn, ps, rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public HashMap<String,LocationUsage> retrieveByFloor(java.util.Date date, String floor) {
		HashMap<String,LocationUsage> result = new HashMap<String,LocationUsage>();
		try {
			String sql = "select * \n"
					+ "    from appusage au, locationusage lu,location l \n"
					+ "    where au.macaddress = lu.macaddress\n"
					+ "    and lu.locationid = l.locationid \n"
					+ "    and semanticplace like ?\n"
					+ "    AND lu.timestamp >= ? and lu.timestamp <= ?\n"
					+ "    and au.timestamp >= ? and au.timestamp <= ?\n"
					+ "    order by lu.macaddress, lu.timestamp;";

			Connection conn = ConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);

			Date before = new java.sql.Date(date.getTime() - (15 * 60 * 1000));
			Date after = new java.sql.Date(date.getTime());
			ps.setString(1, "SMUSIS" + floor + "%");
			ps.setString(2, Utility.formatDate(before)); //15 minutes before
			ps.setString(3, Utility.formatDate(after));
			ps.setString(4, Utility.formatDate(before)); //15 minutes before
			ps.setString(5, Utility.formatDate(after));
			

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Timestamp timestamp = rs.getTimestamp(4);
				String macAddress = rs.getString(5);
				int locationId = rs.getInt(6);
				String semanticplace = rs.getString(8);
				LocationUsage curr = new LocationUsage(Utility.formatDate(new Date(timestamp.getTime())), macAddress, locationId, new Location(locationId, semanticplace));
				result.put(macAddress, curr);

			}
			ConnectionManager.close(conn, ps, rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<LocationUsage> retrieveByUser(String macAdd, java.util.Date startDate, java.util.Date endDate) {

		ArrayList<LocationUsage> result = new ArrayList<LocationUsage>();

		try {
			String sql = " select * from locationusage\n"
					+ " WHERE macaddress = ? \n"
					+ " AND timestamp >= ? AND timestamp <= ? \n"
					+ " ORDER BY timestamp;";

			Connection conn = ConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, macAdd);
			ps.setString(2, new java.sql.Timestamp(startDate.getTime()).toString());
			ps.setString(3, new java.sql.Timestamp(endDate.getTime()).toString());

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Timestamp timestamp = rs.getTimestamp(1);
				String macAddress = rs.getString(2);
				String locationId = rs.getString(3);

				LocationUsage curr = new LocationUsage(Utility.formatDate(new Date(timestamp.getTime())), macAddress, Integer.parseInt(locationId));
				result.add(curr);

			}
			ConnectionManager.close(conn, ps, rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;

	}

	public void retrieve(java.util.Date startInLocation, java.util.Date endInLocation, int prevLocationId, String macaddress, ArrayList<LocationUsage> totalAUList) {

		try {
			String sql = "select * from locationusage\n"
					+ " WHERE timestamp >= ? AND timestamp < ? \n"
					+ " AND locationid = ? \n"
					+ " AND macaddress != ? \n"
					+ " ORDER BY timestamp\n"
					+ " ;";

			Connection conn = ConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, new java.sql.Timestamp(startInLocation.getTime()).toString());
			ps.setString(2, new java.sql.Timestamp(endInLocation.getTime()).toString());
			ps.setInt(3, prevLocationId);
			ps.setString(4, macaddress);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Timestamp timestamp = rs.getTimestamp(1);
				String macAddress = rs.getString(2);
				String locationId = rs.getString(3);

				LocationUsage curr = new LocationUsage(Utility.formatDate(new Date(timestamp.getTime())), macAddress, Integer.parseInt(locationId));
				totalAUList.add(curr);

			}
			ConnectionManager.close(conn, ps, rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}