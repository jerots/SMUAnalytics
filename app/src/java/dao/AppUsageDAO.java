package dao;

import com.csvreader.CsvReader;
import entity.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * AppUsageDAO handles interactions between AppUsage and Controllers
 */
public class AppUsageDAO {

    private TreeMap<String, Integer> duplicate;
    private TreeMap<String, AppUsage> appList;

    /* Loads the list of App list and Duplicate list
     */
    public AppUsageDAO() {
        appList = new TreeMap<>();
        duplicate = new TreeMap<>();
    }

    /**
     * Inserts rows into AppUsage in the database
     *
     * @param reader The CSV reader used to read the csv file
     * @param errMap The map that will contain errors messages
     * @param conn The connection to the database
     * @param macList The list og mac address
     * @param appIdList The list of app id that is successfully uploaded to the
     * database
     * @throws IOException An error found
     * @return an array of int, anything above 0 is the row is success updated,
     * otherwise not successfully updated.
     */
    public int insert(CsvReader reader, TreeMap<Integer, String> errMap, Connection conn, HashMap<String, String> macList, HashMap<Integer, String> appIdList) throws IOException {
        try {
            int index = 2;
            String sql = "insert into appusage (timestamp, macaddress, appid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?) ON DUPLICATE KEY UPDATE appid "
                    + " = VALUES(appid);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            reader.readHeaders();
            String[] headers = reader.getHeaders();

            while (reader.readRecord()) {
                //retrieving per row
                String errorMsg = "";

                //Values declared
                String date = null;
                int appId = -1;
                String macAdd = null;

                for (String s : headers) {
                    switch (s) {
                        case "timestamp":
                            //check timestamp
                            date = Utility.parseString(reader.get("timestamp"));
                            if (date == null) {
                                errorMsg += ",blank timestamp";
                            } else {
                                if (!Utility.checkDate(date)) {
                                    errorMsg += ",invalid timestamp";
                                }
                            }
                            break;

                        case "mac-address":
                            //check macAdd
                            macAdd = Utility.parseString(reader.get("mac-address"));
                            if (macAdd == null) {
                                errorMsg += ",blank mac-address";
                            } else {
                                macAdd = macAdd.toLowerCase();
                                if (!Utility.checkHexadecimal(macAdd)) {
                                    errorMsg += ",invalid mac address";
                                } else if (!macList.containsKey(macAdd)) {
                                    errorMsg += ",no matching mac address";
                                }
                            }
                            break;

                        case "app-id":
                            //check appid
                            String appIdS = Utility.parseString(reader.get("app-id"));
                            if (appIdS == null) {
                                errorMsg += ",blank app-id";
                            } else {
                                appId = Utility.parseInt(appIdS);
                                if (appId <= 0) {
                                    errorMsg += ",invalid app id";
                                } else if (!appIdList.containsKey(appId)) {
                                    errorMsg += ",invalid app";
                                }
                            }
                            break;
                    }
                }
                if (errorMsg.length() == 0) {

                    if (duplicate.containsKey(date + macAdd)) {
                        errMap.put(duplicate.get(date + macAdd), "duplicate row");
                    }
                    duplicate.put(date + macAdd, index);

                    //add to list
                    stmt.setString(1, date);
                    stmt.setString(2, macAdd);
                    stmt.setInt(3, appId);
                    stmt.addBatch();
                    //insert into tables
                } else {

                    errMap.put(index, errorMsg.substring(1));
                }
                index++;
            }
            stmt.executeBatch();
            conn.commit();
            stmt.close();
        } catch (SQLException e) {

        }
        return duplicate.size();
    }

    /**
     * Add rows into AppUsage in the database
     *
     * @param reader The CSV reader used to read the csv file
     * @param errMap The map that will contain errors messages
     * @param conn The connection to the database
     * @return number of rows updated
     * @throws SQLException An error caused by SQL
     */
    public int add(CsvReader reader, TreeMap<Integer, String> errMap, Connection conn) throws IOException, SQLException {
        int updateCounts = 0;
        try {
            int index = 2;
            String sql = "insert into appusage (timestamp, macaddress, appid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            String query = null;
            PreparedStatement pStmt = null;
            ResultSet rs = null;
            reader.readHeaders();
            String[] headers = reader.getHeaders();
            while (reader.readRecord()) {
                //retrieving per row
                String errorMsg = "";

                //Declare values
                String date = null;
                String macAdd = null;
                int appId = -1;

                for (String s : headers) {
                    switch (s) {
                        case "timestamp":
                            //check timestamp
                            date = Utility.parseString(reader.get("timestamp"));
                            if (date == null) {
                                errorMsg += ",blank timestamp";
                            } else {
                                if (!Utility.checkDate(date)) {
                                    errorMsg += ",invalid timestamp";
                                }
                            }
                            break;

                        case "mac-address":
                            //check macAdd
                            macAdd = Utility.parseString(reader.get("mac-address"));
                            if (macAdd == null) {
                                errorMsg += ",blank mac-address";
                            } else {
                                macAdd = macAdd.toLowerCase();

                                if (!Utility.checkHexadecimal(macAdd)) {
                                    errorMsg += ",invalid mac address";
                                } else {
                                    query = "select macaddress from user where macaddress = ?;";
                                    pStmt = conn.prepareStatement(query);
                                    pStmt.setString(1, macAdd);

                                    rs = pStmt.executeQuery();
                                    if (!rs.next()) {
                                        errorMsg += ",no matching mac address";
                                    }
                                    pStmt.close();
                                }
                            }
                            break;

                        case "app-id":
                            //check appid
                            String appIdS = Utility.parseString(reader.get("app-id"));
                            if (appIdS == null) {
                                errorMsg += ",blank app-id";
                            } else {
                                appId = Utility.parseInt(appIdS);
                                if (appId <= 0) {
                                    errorMsg += ",invalid app id";
                                } else {
                                    query = "select appid from app where appid = ?;";
                                    pStmt = conn.prepareStatement(query);
                                    pStmt.setInt(1, appId);
                                    rs = pStmt.executeQuery();
                                    if (!rs.next()) {
                                        errorMsg += ",invalid app";
                                    }
                                    pStmt.close();
                                }
                            }
                            break;
                    }
                }

                if (errorMsg.length() == 0) {
                    if (duplicate.containsKey(date + macAdd)) {
                        errMap.put(duplicate.get(date + macAdd), "duplicate row");
                    }
                    duplicate.put(date + macAdd, index);
                    appList.put(date + macAdd, new AppUsage(date, macAdd, appId));
                } else {
                    errMap.put(index, errorMsg.substring(1));
                }
                index++;
            }
            ArrayList<AppUsage> appArray = new ArrayList<AppUsage>(appList.values());
            try {
                for (AppUsage app : appArray) {
                    stmt.setString(1, app.getTimestamp());
                    stmt.setString(2, app.getMacAddress());
                    stmt.setInt(3, app.getAppId());
                    stmt.addBatch();
                }
                //closing
                int[] updatedArr = stmt.executeBatch();
                for (int i : updatedArr) {
                    if (i >= 0) {
                        updateCounts += i;
                    }
                }

            } catch (BatchUpdateException e) {
                int[] updateArr = e.getUpdateCounts();
                for (int i = 0; i < updateArr.length; i++) {
                    if (updateArr[i] == Statement.EXECUTE_FAILED) {
                        // This method retrieves the row fail, and then searches the prikey corresponding and then uses the duplicate TreeMap to find the offending row.
                        int row = duplicate.get(appArray.get(i).getTimestamp() + appArray.get(i).getMacAddress());
                        String errorMsg = "";
                        if (errMap.containsKey(row)) {
                            errorMsg = errMap.get(row);
                        }
                        if (errorMsg != null && errorMsg.length() != 0) {
                            errorMsg += ",duplicate row";
                        } else {
                            errorMsg += "duplicate row";
                        }
                        errMap.put(row, errorMsg);
                    }

                    if (updateArr[i] >= 0) {
                        updateCounts += updateArr[i];
                    }
                }
            }
            conn.commit();
            reader.close();
            stmt.close();
        } catch (NullPointerException e) {
//            e.printStackTrace();
        }
        return updateCounts;
    }

    /**
     * Retrieve users who have appusage between the input startdate and enddate
     *
     * @param startDate The startdate of interest
     * @param endDate The enddate of interest
     * @return an arraylist of users
     */
    public ArrayList<User> retrieveUsers(Date startDate, Date endDate) {

        ArrayList<User> result = new ArrayList<User>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("SELECT au.macaddress, name, password, email, gender,cca from appusage au, user u where "
                    + "au.macaddress = u.macaddress "
                    + "AND timestamp >= ? AND timestamp <= ? "
                    + "GROUP BY macaddress");
            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                String macAdd = rs.getString(1);
                String name = rs.getString(2);
                String password = rs.getString(3);
                String email = rs.getString(4);
                String gender = rs.getString(5);
                String cca = rs.getString(6);
                result.add(new User(macAdd, name, password, email, gender, cca));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    /**
     * Retrieve users who have appusage between the input startdate and enddate
     * and the given sql statement
     *
     * @param startDate The startdate of interest
     * @param endDate The enddate of interest
     * @param sql The sql statement to be executed
     * @return an arraylist of mac addresses in String
     */
    public ArrayList<String> retrieveUsers(Date startDate, Date endDate, String sql) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        ArrayList<String> result = new ArrayList<String>();

        try {

            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement(sql);
            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                String macAdd = rs.getString(1);
                result.add(macAdd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    /**
     * Retrieve users who have appusage between the input startdate and enddate
     * and for the specific mac address
     *
     * @param macAdd The mac address of a user
     * @param startDate The startdate of interest
     * @param endDate The enddate of interest
     * @return an arraylist of AppUsage
     */
    public ArrayList<AppUsage> retrieveByUser(String macAdd, Date startDate, Date endDate) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<AppUsage> result = new ArrayList<AppUsage>();

        try {

            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("SELECT * from appusage au, app a where "
                    + "timestamp >= ? AND timestamp <= ? "
                    + "AND au.appid = a.appid "
                    + "AND macaddress = ? order by timestamp");
            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());
            ps.setString(3, macAdd);

            rs = ps.executeQuery();

            while (rs.next()) {

                String timestamp = rs.getString(1);
                String macaddress = rs.getString(2);
                int appid = rs.getInt(3);
                String appName = rs.getString(5);
                String appCat = rs.getString(6);

                result.add(new AppUsage(timestamp, macaddress, appid, new App(appid, appName, appCat)));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    /**
     * Retrieve users who have appusage between the input startHour and endHour
     * and for the specific mac address
     *
     * @param macAdd The mac address of a user
     * @param startHour The starting time in hours of interest
     * @param endHour The ending time in hours of interest
     * @return an arraylist of AppUsage
     */
    public ArrayList<AppUsage> retrieveByUserHourly(String macAdd, Date startHour, Date endHour) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<AppUsage> result = new ArrayList<AppUsage>();

        try {
            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("SELECT * from appusage where "
                    + "timestamp >= ? AND timestamp < ? "
                    + "AND macaddress = ? "
                    + "ORDER BY timestamp");

            ps.setString(1, new java.sql.Timestamp(startHour.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endHour.getTime()).toString());
            ps.setString(3, macAdd);

            rs = ps.executeQuery();

            while (rs.next()) {

                String timestamp = rs.getString(1);
                String macaddress = rs.getString(2);
                int appid = rs.getInt(3);
                result.add(new AppUsage(timestamp, macaddress, appid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    /**
     * Retrieve Users who have appusage between the input startHour and endHour
     *
     * @param startHour The starting time in hours of interest
     * @param endHour The ending time in hours of interest
     * @param demoArr The arraylist containing year,gender and school of User
     * @return an arraylist of User
     */
    public ArrayList<User> retrieveUserByDemo(Date startHour, Date endHour, String[] demoArr) {

        ArrayList<User> result = new ArrayList<User>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("SELECT au.macaddress, name, password, email, gender,cca from appusage au, user u \n"
                    + "where au.macaddress = u.macaddress\n"
                    + "AND timestamp >= ? AND timestamp < ?\n"
                    + "AND email like ? \n"
                    + "AND gender like ?\n"
                    + "AND email like ?\n"
                    + "GROUP BY macaddress;");
            ps.setString(1, new java.sql.Timestamp(startHour.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endHour.getTime()).toString());

            String year = demoArr[0];
            String gender = demoArr[1];
            String school = demoArr[2];

            if (year.toLowerCase().equals("na")) {
                ps.setString(3, "%");
            } else {
                ps.setString(3, "%." + year + "@%.smu.edu.sg");
            }

            if (gender.toLowerCase().equals("na")) {
                ps.setString(4, "%");
            } else {
                ps.setString(4, gender.toLowerCase());
            }

            if (school.toLowerCase().equals("na")) {
                ps.setString(5, "%");
            } else {
                ps.setString(5, "%@" + school + ".smu.edu.sg");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                String macAdd = rs.getString(1);
                String name = rs.getString(2);
                String password = rs.getString(3);
                String email = rs.getString(4);
                String genderRes = rs.getString(5);
                String cca = rs.getString(6);
                result.add(new User(macAdd, name, password, email, genderRes, cca));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    /**
     * Retrieve AppUsage who have appusage between the input startHour and
     * endHour for a specific macAdd with the specific appCat
     *
     * @param startHour The starting time in hours of interest
     * @param endHour The ending time in hours of interest
     * @param macAdd The mac address of a user
     * @param appCat The app category of an app
     * @return an arraylist of AppUsage
     */
    public ArrayList<AppUsage> retrieveByAppCat(Date startHour, Date endHour, String macAdd, String appCat) {

        ArrayList<AppUsage> result = new ArrayList<AppUsage>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("select * from appusage au, app a\n"
                    + "WHERE au.appid = a.appid\n"
                    + "AND timestamp >= ? AND timestamp <= ? \n"
                    + "AND appcategory = ? \n"
                    + "AND macaddress = ? \n"
                    + "ORDER BY timestamp;");

            ps.setString(1, new java.sql.Timestamp(startHour.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endHour.getTime()).toString());
            ps.setString(3, appCat);
            ps.setString(4, macAdd);

            rs = ps.executeQuery();

            while (rs.next()) {
                String timestamp = rs.getString(1);
                String macAddress = rs.getString(2);
                int appId = rs.getInt(3);
                result.add(new AppUsage(timestamp, macAddress, appId));
            }
            ConnectionManager.close(conn, ps, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    /**
     * Retrieve AppUsage by the given school
     *
     * @param school The school of interest
     * @param startDate The start date of interest
     * @param endDate The end date of interest
     * @return an arraylist of AppUsage
     */
    public ArrayList<AppUsage> getAppsBySchool(String school, Date startDate, Date endDate) {
        ArrayList<AppUsage> aList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //This has been changed to take into account that the next update is calculated as well.
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("SELECT timestamp, appname, a.appid, u.macaddress, appcategory\n"
                    + "FROM appusage au, user u, app a\n"
                    + "WHERE timestamp >= ?\n"
                    + "AND timestamp <= ?\n"
                    + "AND au.macaddress = u.macaddress\n"
                    + "AND a.appid = au.appid\n"
                    + "AND u.email LIKE ? \n"
                    + "ORDER BY u.macaddress, timestamp;");

            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());
            ps.setString(3, "%" + school + "%");

            rs = ps.executeQuery();
            while (rs.next()) {
                String timestamp = rs.getString(1);
                String appName = rs.getString(2);
                int appId = rs.getInt(3);
                String macAdd = rs.getString(4);
                String appCat = rs.getString(5);

                aList.add(new AppUsage(timestamp, macAdd, appId, new App(appId, appName, appCat)));
            }
            ConnectionManager.close(conn, ps);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return aList;
    }

    /**
     * Retrieve AppUsage of a student by the given category
     *
     * @param priKMac The hashmap of mac address as the key, and username as the
     * value
     * @param startDate The start date of interest
     * @param endDate The end date of interest
     * @return an arraylist of AppUsage
     */
    public ArrayList<AppUsage> getStudentsByCategory(HashMap<String, String> priKMac, Date startDate, Date endDate) {
        ArrayList<AppUsage> aList = new ArrayList<>();

        String sql = "SELECT timestamp, u.name, u.macaddress, a.appid, a.appcategory, appname\n"
                + "FROM appusage au, user u, app a\n"
                + "WHERE timestamp >= ?\n"
                + "AND timestamp <= ?\n"
                + "AND au.macaddress = u.macaddress\n"
                + "AND a.appid = au.appid\n"
                + "ORDER BY u.macaddress, timestamp;";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //Cannot send in category as must minus from the previous amount.
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                String timeStamp = rs.getString(1);
                String name = rs.getString(2);
                String macAdd = rs.getString(3);
                int appId = rs.getInt(4);
                String category = rs.getString(5);
                String appName = rs.getString(6);

                aList.add(new AppUsage(timeStamp, macAdd, appId, new App(appId, appName, category)));
                priKMac.put(macAdd, name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return aList;
    }

    /**
     * Retrieve AppUsage of a school by the given category
     *
     * @param priKSch The hashmap of mac address as the key, and username as the
     * value
     * @param startDate The start date of interest
     * @param endDate The end date of interest
     * @return an arraylist of AppUsage
     */
    public ArrayList<AppUsage> getSchoolsByCategory(HashMap<String, String> priKSch, Date startDate, Date endDate) {
        ArrayList<AppUsage> aList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT timestamp, u.macaddress, a.appid, appcategory, email, appname\n"
                + "FROM appusage au, user u, app a\n"
                + "WHERE au.macaddress = u.macaddress\n"
                + "AND timestamp >= ?\n"
                + "AND timestamp <= ?\n"
                + "AND a.appid = au.appid\n"
                + "ORDER BY CASE\n"
                + "WHEN u.email LIKE '%accountancy%' THEN 1\n"
                + "WHEN u.email LIKE '%economics%' THEN 2\n"
                + "WHEN u.email LIKE '%business%' THEN 3\n"
                + "WHEN u.email LIKE '%sis%' THEN 4\n"
                + "WHEN u.email LIKE '%law%' THEN 5\n"
                + "WHEN u.email LIKE '%socsc%' THEN 6\n"
                + "end,macaddress, timestamp;";

        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());

            rs = ps.executeQuery();
            //Need to retrieve category as you must take the first time of every new app to be accurate
            while (rs.next()) {
                String timeStamp = rs.getString(1);
                String macAdd = rs.getString(2);
                int appId = rs.getInt(3);
                String category = rs.getString(4);
                String email = rs.getString(5);
                String school = Utility.getSchool(email);
                String appName = rs.getString(6);

                aList.add(new AppUsage(timeStamp, macAdd, appId, new App(appId, appName, category)));
                priKSch.put(macAdd, school);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return aList;
    }

    /**
     * Retrieve AppUsage of a user for the given date
     *
     * @param date The date of interest
     * @param macAdd The macAddress of the user
     * @return an arraylist of AppUsage
     */
    public ArrayList<AppUsage> getUserAppsForSocial(String date, String macAdd) {
        ArrayList<AppUsage> sList = new ArrayList<>();

        String query = "SELECT appname, timestamp, a.appid, appcategory\n"
                + "FROM appusage au, user u, app a\n"
                + "WHERE au.macaddress = u.macaddress\n"
                + "AND a.appid = au.appid\n"
                + "AND date(timestamp) = ?\n"
                + "AND au.macaddress = ?\n"
                + "ORDER BY timestamp, a.appid;";

        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement pStmt = conn.prepareStatement(query);
            //Assumes that checks have been done prior already
            pStmt.setString(1, date);
            pStmt.setString(2, macAdd);

            ResultSet rs = pStmt.executeQuery();

            //NOTE: WHY DO WE NOT SET CATEGORY = SOCIAL. BECAUSE YOU NEED TO MINUS THE NEXT TO CHECK THE TOTAL USAGE TIME. Category check later
            while (rs.next()) {
                String appName = rs.getString(1);
                String timeStamp = rs.getString(2);
                int appId = rs.getInt(3);
                String category = rs.getString(4);

                sList.add(new AppUsage(timeStamp, macAdd, appId, new App(appId, appName, category)));
            }
            rs.close();
            pStmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sList;
    }
}
