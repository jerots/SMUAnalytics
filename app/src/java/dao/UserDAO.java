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

    private ArrayList<String> unsuccessful;

    public UserDAO() {
        unsuccessful = new ArrayList<>();
    }

    //NOTE: This method is ALSO used by addbatch because addbatch does the same things as bootstrap for demographics.csv, and clearing is in the servlet.
    public void insert(CSVReader reader, HashMap<Integer, String> userMap) throws IOException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
        String sql = "insert into user (macaddress, name, password, email, gender) values(?,?,?,?,?);";
        PreparedStatement stmt = conn.prepareStatement(sql);

        String arr[];
        int index = 1;
        while ((arr = reader.readNext()) != null) {
            //retrieving per row
            boolean err = false;

            String errorMsg = userMap.get(index);
            String macAdd = Utility.parseString(arr[0]);
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

            String name = Utility.parseString(arr[1]);

            if (name == null) {
                if (errorMsg == null) {
                    userMap.put(index, "name cannot be blank");
                } else {
                    userMap.put(index, errorMsg + "," + "name cannot be blank");
                }
                err = true;
            }

            String password = Utility.parseString(arr[2]);
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

            String email = Utility.parseString(arr[3]);
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

            String g = Utility.parseString(arr[4]);
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

            if (!err) {
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

        stmt.executeBatch();
        conn.commit();

        //closing
        reader.close();
        ConnectionManager.close(conn, stmt);
    }

    public User retrieve(String username, String password) {
        String sql = "SELECT * FROM user WHERE email=? AND password=?";

        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                return new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));

            }

        } catch (SQLException e) {

        }

        return null;
    }

    public User retrieveByEmailId(String username, String password) {
        String sql = "SELECT * FROM user WHERE email LIKE ? AND password=?";

        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username + "@%.smu.edu.sg");
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                return new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
