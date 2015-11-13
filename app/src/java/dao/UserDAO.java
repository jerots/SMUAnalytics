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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class UserDAO {
    //NOTE: This method is ALSO used by addbatch because addbatch does the same things as bootstrap for demographics.csv, and clearing is in the servlet.
    public int[] insert(CsvReader reader, TreeMap<Integer, String> userMap, Connection conn, HashMap<String, String> macList) throws IOException {
        int[] updateCounts = {};
        try {
            String sql = "insert into user values(?,?,?,?,?,?);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            reader.readHeaders();
            String[] headers = reader.getHeaders();
            int index = 2;
            while (reader.readRecord()) {
                //retrieving per row
                String errorMsg = "";
                
                //Sets Values
                String macAdd = null;
                String name = null;
                String password = null;
                String email = null;
                String g = null;
                String cca = null;
                
                for(String s: headers){
                    switch(s){
                        case "mac-address":
                            macAdd = Utility.parseString(reader.get("mac-address"));
                            if (macAdd == null) {
                                errorMsg += ",blank mac-address";
                            } else {
                                macAdd = macAdd.toLowerCase();
                                if (!Utility.checkHexadecimal(macAdd)) {
                                    errorMsg += ",invalid mac address";
                                }
                            }
                            break;
                            
                        case "name":
                            name = Utility.parseString(reader.get("name"));
                            if (name == null) {
                                errorMsg += ",blank name";
                            }
                            break;
                            
                        case "password":
                            password = Utility.parseString(reader.get("password"));
                            if (password == null) {
                                errorMsg += ",blank password";
                            } else {
                                if (!Utility.checkPassword(password)) {
                                    errorMsg += ",invalid password";
                                }
                            }
                            break;
                            
                        case "email":
                            email = Utility.parseString(reader.get("email"));
                            if (email == null) {
                                errorMsg += ",blank email";
                            } else {
                                email = email.toLowerCase();
                                if (!Utility.checkEmail(email)) {
                                    errorMsg += ",invalid email";
                                }
                            }
                            break;
                            
                        case "gender":
                            g = Utility.parseString(reader.get("gender"));
                            if (g == null) {
                                errorMsg += ",blank gender";
                            } else {
                                g = g.toLowerCase();
                                if (!g.equals("f") && !g.equals("m")) {
                                    errorMsg += ",invalid gender";
                                }
                            }
                            break;
                        
                        case "cca":
                            cca = Utility.parseString(reader.get("cca"));
                            if (cca == null) {
                                errorMsg += ",blank cca";
                            } else if (cca.length() > 63) {
                                errorMsg += ",cca record too long";
                            } else {
                            }
                            break;
                        
                    }
                }

                if (errorMsg.length() == 0) {
                    //add to list
                    //insert into tables
                    macList.put(macAdd, "");
                    stmt.setString(1, macAdd);
                    stmt.setString(2, name);
                    stmt.setString(3, password);
                    stmt.setString(4, email);
                    stmt.setString(5, g);
                    stmt.setString(6, cca);
                    stmt.addBatch();
                }else{
                    userMap.put(index, errorMsg.substring(1));
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
            String[] headers = reader.getHeaders();
            int index = 2;
            while (reader.readRecord()) {
                //retrieving per row
                String errorMsg = "";
                
                //Sets Values
                String macAdd = null;
                String name = null;
                String password = null;
                String email = null;
                String g = null;
                String cca = null;
                
                for(String s: headers){
                    switch(s){
                        case "mac-address":
                            macAdd = Utility.parseString(reader.get("mac-address"));
                            if (macAdd == null) {
                                errorMsg += ",blank mac-address";
                            } else {
                                macAdd = macAdd.toLowerCase();
                                if (!Utility.checkHexadecimal(macAdd)) {
                                    errorMsg += ",invalid mac address";
                                }
                            }
                            break;
                            
                        case "name":
                            name = Utility.parseString(reader.get("name"));
                            if (name == null) {
                                errorMsg += ",blank name";
                            }
                            break;
                        
                        case "password":
                            password = Utility.parseString(reader.get("password"));
                            if (password == null) {
                                errorMsg += ",blank password";
                            } else {
                                if (!Utility.checkPassword(password)) {
                                    errorMsg += ",invalid password";
                                }
                            }
                            break;
                            
                        case "email":
                            email = Utility.parseString(reader.get("email"));
                            if (email == null) {
                                errorMsg += ",blank email";
                            } else {
                                email = email.toLowerCase();
                                if (!Utility.checkEmail(email)) {
                                    errorMsg += ",invalid email";
                                }
                            }
                            break;
                            
                        case "gender":
                            g = Utility.parseString(reader.get("gender"));
                            if (g == null) {
                                errorMsg += ",blank gender";
                            } else {
                                 g = g.toLowerCase();
                                if (!g.equals("f") && !g.equals("m")) {
                                    errorMsg += ",invalid gender";
                                }
                            }
                            break;
                        
                        case "cca":
                            cca = Utility.parseString(reader.get("cca"));
                            if (cca == null) {
                                errorMsg += ",blank cca";
                            } else if (cca.length() > 63) {
                                errorMsg += ",cca record too long";
                            } else {
                                cca = cca.toLowerCase();
                            }
                            break;
                    }
                }

                if (errorMsg.length() == 0) {
                    //add to list
                    //insert into tables
                    stmt.setString(1, macAdd);
                    stmt.setString(2, name);
                    stmt.setString(3, password);
                    stmt.setString(4, email);
                    stmt.setString(5, g);
                    stmt.setString(6, cca);
                    stmt.addBatch();
                }else{
                    userMap.put(index, errorMsg.substring(1));
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

            boolean result = rs.next();

            rs.close();
            ps.close();
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

    public ArrayList<String> getCCAs() {
        ArrayList<String> ccas = new ArrayList<String>();
        String sql = "select cca from user group by cca;";

        Connection conn = null;
        Statement s = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            s = conn.prepareStatement(sql);

            rs = s.executeQuery(sql);

            while (rs.next()) {

                ccas.add(rs.getString(1));
            }

        } catch (SQLException e) {

        } finally {
            ConnectionManager.close(conn, s, rs);

        }

        return ccas;

    }
}
