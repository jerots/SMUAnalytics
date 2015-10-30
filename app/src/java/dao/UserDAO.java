/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.csvreader.CsvReader;
import entity.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class UserDAO {
	
	private ArrayList<String> schools;
	private ArrayList<String> years;
	private ArrayList<String> genders;
	
	public UserDAO() {
		schools = new ArrayList<String>();
		schools.add("business");
		schools.add("accountancy");
		schools.add("sis");
		schools.add("economics");
		schools.add("law");
		schools.add("socsc");
		
		years = new ArrayList<String>();
		years.add("2011");
		years.add("2012");
		years.add("2013");
		years.add("2014");
		years.add("2015");
		
		genders = new ArrayList<String>();
		genders.add("M");
		genders.add("F");
	}
	
	public ArrayList<String> getSchools() {
		return schools;
	}
	
	public ArrayList<String> getYears() {
		return years;
	}
	
	public ArrayList<String> getGenders() {
		return genders;
	}

	//NOTE: This method is ALSO used by addbatch because addbatch does the same things as bootstrap for demographics.csv, and clearing is in the servlet.
	public int[] insert(CsvReader reader, TreeMap<Integer, String> userMap, Connection conn, HashMap<String, String> macList) throws IOException {
		int[] updateCounts = {};
		try {
			String sql = "insert into user values(?,?,?,?,?,?);";
			PreparedStatement stmt = conn.prepareStatement(sql);
			reader.readHeaders();
			int index = 2;
			while (reader.readRecord()) {
				//retrieving per row
				boolean err = false;
				
				String errorMsg = userMap.get(index);
				String macAdd = Utility.parseString(reader.get("mac-address"));
				if (macAdd == null) {
					if (errorMsg == null) {
						userMap.put(index, "mac add cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "mac add cannot be blank");
					}
					err = true;
				}
				
				if (!Utility.checkHexadecimal(macAdd)) {
					if (errorMsg == null) {
						userMap.put(index, "invalid mac address");
					} else {
						userMap.put(index, errorMsg + "," + "invalid mac address");
					}
					err = true;
				}
				
				String name = Utility.parseString(reader.get("name"));
				
				if (name == null) {
					if (errorMsg == null) {
						userMap.put(index, "name cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "name cannot be blank");
					}
					err = true;
				}
				
				String password = Utility.parseString(reader.get("password"));
				if (password == null) {
					if (errorMsg == null) {
						userMap.put(index, "password cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "password cannot be blank");
					}
					err = true;
				}
				
				if (!Utility.checkPassword(password)) {
					if (errorMsg == null) {
						userMap.put(index, "invalid password");
					} else {
						userMap.put(index, errorMsg + "," + "invalid password");
					}
					err = true;
				}
				
				String email = Utility.parseString(reader.get("email"));
				if (email == null) {
					if (errorMsg == null) {
						userMap.put(index, "email cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "email cannot be blank");
					}
					err = true;
				}
				if (!Utility.checkEmail(email)) {
					if (errorMsg == null) {
						userMap.put(index, "invalid email");
					} else {
						userMap.put(index, errorMsg + "," + "invalid email");
					}
					err = true;
				}
				
				String g = Utility.parseString(reader.get("gender"));
				if (g == null) {
					if (errorMsg == null) {
						userMap.put(index, "gender cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "gender cannot be blank");
					}
					err = true;
				}
				
				String gender = g.toLowerCase();
				
				if (!gender.equals("f") && !gender.equals("m")) {
					if (errorMsg == null) {
						userMap.put(index, "invalid gender");
					} else {
						userMap.put(index, errorMsg + "," + "invalid gender");
					}
					err = true;
				}
				
				String cca = Utility.parseString(reader.get("cca"));
				if (cca == null) {
					if (errorMsg == null) {
						userMap.put(index, "blank cca");
					} else {
						userMap.put(index, errorMsg + "," + "cca cannot be blank");
					}
					err = true;
				} else if (cca.length() > 63) {
					if (errorMsg == null) {
						userMap.put(index, "cca record too long");
					} else {
						userMap.put(index, errorMsg + "," + "cca record too long");
					}
					err = true;
				}
				
				if (!err) {
					//add to list
					//insert into tables
					macList.put(macAdd,"");
					stmt.setString(1, macAdd);
					stmt.setString(2, name);
					stmt.setString(3, password);
					stmt.setString(4, email);
					stmt.setString(5, gender);
					stmt.setString(6, cca);
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
	
	public int[] add(CsvReader reader, TreeMap<Integer, String> userMap, Connection conn) throws IOException {
		int[] updateCounts = {};
		try {
			String sql = "insert into user values(?,?,?,?,?,?);";
			PreparedStatement stmt = conn.prepareStatement(sql);
			reader.readHeaders();
			
			int index = 2;
			while (reader.readRecord()) {
				//retrieving per row
				boolean err = false;
				
				String errorMsg = userMap.get(index);
				String macAdd = Utility.parseString(reader.get("mac-address"));
				if (macAdd == null) {
					if (errorMsg == null) {
						userMap.put(index, "mac add cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "mac add cannot be blank");
					}
					err = true;
				}
				
				if (!Utility.checkHexadecimal(macAdd)) {
					if (errorMsg == null) {
						userMap.put(index, "invalid mac address");
					} else {
						userMap.put(index, errorMsg + "," + "invalid mac address");
					}
					err = true;
				}
				
				String name = Utility.parseString(reader.get("name"));
				
				if (name == null) {
					if (errorMsg == null) {
						userMap.put(index, "name cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "name cannot be blank");
					}
					err = true;
				}
				
				String password = Utility.parseString(reader.get("password"));
				if (password == null) {
					if (errorMsg == null) {
						userMap.put(index, "password cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "password cannot be blank");
					}
					err = true;
				}
				
				if (!Utility.checkPassword(password)) {
					if (errorMsg == null) {
						userMap.put(index, "invalid password");
					} else {
						userMap.put(index, errorMsg + "," + "invalid password");
					}
					err = true;
				}
				
				String email = Utility.parseString(reader.get("email"));
				if (email == null) {
					if (errorMsg == null) {
						userMap.put(index, "email cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "email cannot be blank");
					}
					err = true;
				}
				if (!Utility.checkEmail(email)) {
					if (errorMsg == null) {
						userMap.put(index, "invalid email");
					} else {
						userMap.put(index, errorMsg + "," + "invalid email");
					}
					err = true;
				}
				
				String g = Utility.parseString(reader.get("gender"));
				if (g == null) {
					if (errorMsg == null) {
						userMap.put(index, "gender cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "gender cannot be blank");
					}
					err = true;
				}
				
				String gender = g.toLowerCase();
				
				if (!gender.equals("f") && !gender.equals("m")) {
					if (errorMsg == null) {
						userMap.put(index, "invalid gender");
					} else {
						userMap.put(index, errorMsg + "," + "invalid gender");
					}
					err = true;
				}
				
				String cca = Utility.parseString(reader.get("cca"));
				if (cca == null) {
					if (errorMsg == null) {
						userMap.put(index, "cca cannot be blank");
					} else {
						userMap.put(index, errorMsg + "," + "cca cannot be blank");
					}
					err = true;
				} else if (cca.length() > 63) {
					if (errorMsg == null) {
						userMap.put(index, "cca record too long");
					} else {
						userMap.put(index, errorMsg + "," + "cca record too long");
					}
					err = true;
				}
				
				if (!err) {
					//add to list
					//insert into tables
					stmt.setString(1, macAdd);
					stmt.setString(2, name);
					stmt.setString(3, password);
					stmt.setString(4, email);
					stmt.setString(5, gender);
					stmt.setString(6, cca);
					stmt.addBatch();
				}
				index++;
			}
			
			updateCounts = stmt.executeBatch();
			conn.commit();
			stmt.close();
			//closing
			reader.close();
		} catch (SQLException e) {
			
		}
		return updateCounts;
	}
	
	public User retrieve(String username, String password) {
		String sql = "SELECT * FROM user WHERE email=? AND password=?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, username);
			ps.setString(2, password);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				return new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
				
			}
			
		} catch (SQLException e) {
			
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		
		return null;
	}
	
	public User retrieveByEmailId(String username, String password) {
		String sql = "SELECT * FROM user WHERE email LIKE ? AND password=?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, username + "@%.smu.edu.sg");
			ps.setString(2, password);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				return new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
				
			}
			
		} catch (SQLException e) {
//            e.printStackTrace();
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		
		return null;
	}
	
	public User retrieve(String username) {
		String sql = "SELECT * FROM user WHERE email like ?";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, username + "@%.smu.edu.sg");
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				return new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
				
			}
			
		} catch (SQLException e) {
			
		} finally {
			ConnectionManager.close(conn, ps, rs);
		}
		
		return null;
	}
	
	public boolean checkMacAdd(Connection conn, String macAdd) {
		String sql = "SELECT macaddress FROM user WHERE macaddress = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, macAdd);
			
			rs = ps.executeQuery();
			
			ps.close();
			boolean result = rs.next();
			rs.close();
			return result;
		} catch (SQLException e) {
			
		}
		
		return false;
	}
	
	public User retrieveByMac(String macaddress) {
		String sql = "SELECT * FROM user WHERE macaddress = ?";
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, macaddress);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				return new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
				
			}
			
		} catch (SQLException e) {
			
		} finally {
			ConnectionManager.close(conn, ps, rs);
			
		}
		
		return null;
	}
}
