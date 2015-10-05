/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 *
 * @author ASUS-PC
 */
public class LocationUsageDAO {

	private ArrayList<String> unsuccessful = new ArrayList<>();
	private ArrayList<LocationUsage> locationUsageList = new ArrayList<>();

	public void insert(LocationDAO lDao, UserDAO uDao, CSVReader reader) throws IOException, SQLException {
		try {
			Connection conn = ConnectionManager.getConnection();
			conn.setAutoCommit(false);
			String sql = "insert into locationusage (timestamp, macaddress, locationid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
			PreparedStatement stmt = conn.prepareStatement(sql);

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

				//check appid
				int locationId = Utility.parseInt(arr[2]);
				if (locationId <= 0) {
					unsuccessful.add("location id cannot be blank");
					err = true;
				}

				if (!lDao.hasLocationId(locationId)) {
					unsuccessful.add("invalid location");
					err = true;
				}

				if (!err) {
					//add to list
					stmt.setString(1, date);
					stmt.setString(2, macAdd);
					stmt.setInt(3, locationId);
					stmt.addBatch();
				}

			}
			//insert into tables
			for (String s : unsuccessful) {
				System.out.println(s);
			}

			//closing
			if (stmt != null) {
				stmt.executeBatch();
				conn.commit();
			}
			reader.close();
			ConnectionManager.close(conn, stmt);

		} catch (NullPointerException e) {

		}
	}

	public ArrayList<LocationUsage> retrieve(java.util.Date date, String floor) {
		ArrayList<LocationUsage> result = new ArrayList<LocationUsage>();
		try {
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT lu.timestamp, lu.macaddress, lu.locationid FROM locationUsage lu, location l"
					+ "WHERE lu.locationid = l.locationid"
					+ "AND timestamp < ?"
					+ "AND semanticplace LIKE '?'");

			ps.setDate(1, new java.sql.Date(date.getTime()));
			ps.setString(2, "%" + floor + "%");

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Timestamp timestamp = rs.getTimestamp(1);
				String macAddress = rs.getString(2);
				String locationId = rs.getString(3);
				System.out.println(timestamp + macAddress + locationId);

				LocationUsage curr = new LocationUsage(new Date(timestamp.getTime()), macAddress, Integer.parseInt(locationId));
				result.add(curr);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<LocationUsage> retrieve(java.util.Date date, Location loc) {
		ArrayList<LocationUsage> result = new ArrayList<LocationUsage>();
		try {
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT timestamp,distinct macaddress FROM `locationUsage`"
					+ "WHERE timestamp >= ? AND timestamp < ?"
					+ "AND locationid = ?");

			ps.setDate(1, new java.sql.Date(date.getTime() - 900000)); //15 minutes before
			ps.setDate(2, new java.sql.Date(date.getTime()));
			ps.setInt(3, loc.getLocationId());

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Timestamp timestamp = rs.getTimestamp(1);
				String macAddress = rs.getString(2);
				String locationId = rs.getString(3);
				System.out.println(timestamp + macAddress + locationId);

				LocationUsage curr = new LocationUsage(new Date(timestamp.getTime()), macAddress, Integer.parseInt(locationId));
				result.add(curr);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

}
