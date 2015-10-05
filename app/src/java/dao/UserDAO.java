/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import java.util.HashMap;

/**
 *
 * @author ASUS-PC
 */
public class UserDAO {

    private HashMap<String, User> userList;
    // private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> unsuccessful = new ArrayList<>();

    public UserDAO() {
        userList = new HashMap<>();
    }

    public void insert(CSVReader reader) throws IOException, SQLException {
        try{
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "insert into user (macaddress, name, password, email, gender) values(?,?,?,?,?) ON DUPLICATE KEY UPDATE name = "
                    + "VALUES(name), password = VALUES(password), email = VALUES(email), gender = VALUES(gender);";
            PreparedStatement stmt = conn.prepareStatement(sql);

            String arr[];
            
            while ((arr = reader.readNext()) != null) {
                //retrieving per row
                boolean err = false;

                String macAdd = Utility.parseString(arr[0]);
                if (macAdd == null) {
                    unsuccessful.add("mac add cannot be blank");
                    err = true;
                }

                if (!Utility.checkHexadecimal(macAdd)) {
                    unsuccessful.add("invalid macAddress");
                    err = true;
                }

                String name = Utility.parseString(arr[1]);

                if (name == null) {
                    unsuccessful.add("name cannot be blank");
                    err = true;
                }

                String password = Utility.parseString(arr[2]);
                if (password == null) {
                    unsuccessful.add("password cannot be blank");
                    err = true;
                }

                if (!Utility.checkPassword(password)) {
                    unsuccessful.add("invalid password");
                    err = true;
                }

                String email = Utility.parseString(arr[3]);
                if (email == null) {
                    unsuccessful.add("email cannot be blank");
                    err = true;
                }
                if (!Utility.checkEmail(email)) {
                    unsuccessful.add("invalid email");
                    err = true;
                }

                String g = Utility.parseString(arr[4]);
                if (g == null) {
                    unsuccessful.add("gender cannot be blank");
                    err = true;
                }

                String gender = g.toLowerCase();

                if (!gender.equals("f") && !gender.equals("m")) {
                    unsuccessful.add("invalid gender");
                    err = true;
                }

                if (!err) {
                    User user = new User(macAdd, name, password, email, gender);
                    userList.put(macAdd, user);
                    //add to list
                    //insert into tables
                    stmt.setString(1, macAdd);
                    stmt.setString(2, name);
                    stmt.setString(3, password);
                    stmt.setString(4, email);
                    stmt.setString(5, gender);
                    stmt.addBatch();
                }  
            }

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
	
	public User retrieve(String username, String password){
		String sql = "SELECT * FROM user WHERE email=? AND password=?";
		
		try {
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setString(1, username);
			ps.setString(2, password);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()){
				
				return new User(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5));
				
			}
			
			
			
		} catch (SQLException e){
			
		}
		
		
		return null;
	}
	
	
	public User retrieveByEmailId(String username, String password){
		String sql = "SELECT * FROM user WHERE email LIKE ? AND password=?";
		
		try {
			Connection conn = ConnectionManager.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setString(1, username + "@%.smu.edu.sg");
			ps.setString(2, password);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()){
				
				return new User(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5));
				
			}
			
			
			
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	
    
    public boolean hasMacAdd(String str){
        return userList.containsKey(str);
    }
}
