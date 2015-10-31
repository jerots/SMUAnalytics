package dao;

import com.csvreader.CsvReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.TreeMap;

public class LocationDAO {
	
	public LocationDAO() {
	}
	
	public int[] insert(CsvReader reader, TreeMap<Integer, String> errMap, Connection conn, HashMap<Integer, String> locationIdList) throws IOException {
		int[] updateCounts = {};
		try {
			String sql = "insert into location (locationid, semanticplace) values(?,?) ON DUPLICATE KEY UPDATE semanticplace = "
					+ "VALUES(semanticplace);";
			PreparedStatement stmt = conn.prepareStatement(sql);
			int index = 2;
			reader.readHeaders();
			while (reader.readRecord()) {
				//retrieving per row
				boolean err = false;
				
				int locationId = Utility.parseInt(reader.get("location-id"));
				if (locationId <= 0) {
					String errorMsg = errMap.get(index);
					if (errorMsg == null) {
						errMap.put(index, "invalid location id");
					} else {
						errMap.put(index, errorMsg + "," + "invalid location id");
					}
					err = true;
				}
				
				String semanticPl = Utility.parseString(reader.get("semantic-place"));
				if (semanticPl == null) {
					String errorMsg = errMap.get(index);
					if (errorMsg == null) {
						errMap.put(index, "semantic place cannot be blank");
					} else {
						errMap.put(index, errorMsg + "," + "semantic place cannot be blank");
					}
					err = true;
				}
				
				String school = semanticPl.substring(0, 7); //SMUSISL or SMUSISB
				int levelNum = Utility.parseInt(semanticPl.substring(7, 8));//1-5

				if (!(school.equals("SMUSISL") || school.equals("SMUSISB")) || levelNum < 1 || levelNum > 5) {
					String errorMsg = errMap.get(index);
					if (errorMsg == null) {
						errMap.put(index, "invalid semantic place");
					} else {
						errMap.put(index, errorMsg + "," + "invalid semantic place");
					}
					err = true;
				}
				
				if (!err) {
					locationIdList.put(locationId, "");
					//insert into tables
					stmt.setInt(1, locationId);
					stmt.setString(2, semanticPl);
					stmt.addBatch();
				}
				index++;
				
			}
			
			updateCounts = stmt.executeBatch();
			conn.commit();
			stmt.close();
		} catch (SQLException e) {
			
		}
		return updateCounts;
	}
	
	public ArrayList<String> retrieve(String floor) {
		
		ArrayList<String> result = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			
			ps = conn.prepareStatement("SELECT distinct semanticplace FROM location "
					+ " where semanticplace like ? "
					+ " ORDER BY semanticplace");
			
			ps.setString(1, "SMUSIS" + floor + "%");
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String place = rs.getString(1);
				result.add(place);
			}
			
		} catch (SQLException e) {
//            e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		
		return result;
	}
	
	public ArrayList<String> retrieveAll() {
		ArrayList<String> result = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			
			ps = conn.prepareStatement("SELECT semanticplace FROM location");
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String place = rs.getString(1);
				result.add(place);
			}
			
		} catch (SQLException e) {
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		
		return result;
	}
	
	public String checkLocationId(Connection conn, int locationId) {
		
		try {
			
			PreparedStatement ps = conn.prepareStatement("SELECT macaddress FROM location WHERE locationid = ?");
			
			ps.setInt(1, locationId);
			ResultSet rs = ps.executeQuery();
			
                        String location = null;
			while(rs.next()){
                            location = rs.getString(1);
                        }
                        ps.close();
                        rs.close();
                        return location;
			
		} catch (SQLException e) {
		}
            return null;
	}
	
}
