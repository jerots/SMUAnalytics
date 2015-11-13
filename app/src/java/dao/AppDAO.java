package dao;

import com.csvreader.CsvReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import entity.App;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ASUS-PC
 */
/**
 * AppDAO handles interactions between App and Controllers
 */
public class AppDAO {

    /**
     * Inserts rows into app in the database
     *
     * @param reader The CSV reader used to read the csv file
     * @param errMap The map that will contain errors messages
     * @param conn The connection to the database
     * @param appIdList The list of app id that is successfully uploaded to the
     * database
     * @throws IOException An error found
     * @return an array of 0 or 1, 1 is a successfully updated record, otherwise
     * 
     */
    public int[] insert(CsvReader reader, TreeMap<Integer, String> errMap, Connection conn, HashMap<Integer, String> appIdList) throws IOException {
        int[] updatedRecords = {};
        try {
            String sql = "insert into app values(?,?,?) ON DUPLICATE KEY UPDATE appname = appname, appcategory = appcategory;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            //Reads the headers to decide where to go!
            reader.readHeaders();
            String[] headers = reader.getHeaders();
            //index starts at 2 because the headers count as a row.
            int index = 2;
            while (reader.readRecord()) {
                String errorMsg = "";

                //Values declared
                int appId = -1;
                String name = null;
                String cat = null;

                for (String s : headers) {
                    switch (s) {
                        case "app-id":
                            //APPID VALIDATION
                            String appIdS = reader.get("app-id");
                            if (appIdS == null || appIdS.length() == 0) {
                                errorMsg += ",blank app-id";
                            } else {
                                appId = Utility.parseInt(appIdS);
                                if (appId <= 0) {
                                    errorMsg += ",invalid app id";
                                }
                            }
                            break;

                        case "app-name":
                            name = Utility.parseString(reader.get("app-name"));
                            if (name == null) {
                                errorMsg += ",blank app-name";
                            } else {
                                name = name.replace("\"", "");
                            }
                            break;

                        case "app-category":
                            cat = Utility.parseString(reader.get("app-category"));
                            if (cat == null) {
                                errorMsg += ",blank app-category";
                            } else {
                                cat = cat.toLowerCase();
                                cat = cat.replace("\"", "");
                                if (!Utility.checkCategory(cat)) {
                                    errorMsg += ",invalid app category";
                                }
                            }
                    }
                }

                if (errorMsg.length() == 0) {
                    //insert into tables
                    appIdList.put(appId, "");
                    stmt.setInt(1, appId);
                    stmt.setString(2, name);
                    stmt.setString(3, cat);
                    stmt.addBatch();
                } else {
                    errMap.put(index, errorMsg.substring(1));
                }
                index++;
            }
            //closing

            updatedRecords = stmt.executeBatch();
            stmt.close();
            conn.commit();
        } catch (SQLException e) {

        }

        return updatedRecords;
    }

    /**
     * Retrieve an app by the given app id
     *
     * @param appId The app id that uniquely identifies an app
     * @return App object
     */
    public App retrieveAppbyId(int appId) {

        String sql = "SELECT * FROM app WHERE appid = ? ";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, appId);
            rs = ps.executeQuery();
            while (rs.next()) {

                int appid = rs.getInt(1);
                String appname = rs.getString(2);
                String category = rs.getString(3);
                return new App(appid, appname, category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return null;
    }

    /**
     * Retrieve a TreeMap of each app category with its corresponding count
     *
     * @return a treemap of app category with its corresponding count
     */
    public TreeMap<String, ArrayList<Integer>> retrieveByCategory() {

        TreeMap<String, ArrayList<Integer>> result = new TreeMap<String, ArrayList<Integer>>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("SELECT appid, appcategory from app");

            rs = ps.executeQuery();

            while (rs.next()) {

                int appid = rs.getInt(1);
                String appcategory = rs.getString(2);
                if (result.containsKey(appcategory)) {
                    ArrayList<Integer> value = result.get(appcategory);
                    value.add(appid);
                    result.put(appcategory, value);
                } else {
                    ArrayList<Integer> value = new ArrayList<Integer>();
                    value.add(appid);
                    result.put(appcategory, value);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }
}
