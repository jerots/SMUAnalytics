package dao;

import com.csvreader.CsvReader;
import entity.Location;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.TreeMap;

public class LocationDAO {

    /**
     * LocationDAO handles interactions between Location and Controllers
     */
    public LocationDAO() {
    }

    /**
     * Inserts rows into Location in the database
     *
     * @param reader The CSV reader used to read the csv file
     * @param errMap The map that will contain errors messages
     * @param conn The connection to the database
     * @param locationIdList The list of location id that is successfully
     * uploaded to the database
     * @throws IOException An error found
     * @return an array of int, any number above 0 is the row is success
     * updated, otherwise not successfully updated.
     */
    public int[] insert(CsvReader reader, TreeMap<Integer, String> errMap, Connection conn, HashMap<Integer, String> locationIdList) throws IOException {
        int[] updateCounts = {};
        try {
            String sql = "insert into location (locationid, semanticplace) values(?,?) ON DUPLICATE KEY UPDATE semanticplace = "
                    + "VALUES(semanticplace);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            int index = 2;
            reader.readHeaders();
            String[] headers = reader.getHeaders();
            while (reader.readRecord()) {
                //retrieving per row
                String errorMsg = "";

                //Sets Values
                int locationId = -1;
                String semanticPl = null;

                for (String s : headers) {
                    switch (s) {
                        case "location-id":
                            String locId = Utility.parseString(reader.get("location-id"));
                            if (locId == null) {
                                errorMsg += ",blank location-id";
                            } else {
                                locationId = Utility.parseInt(locId);
                                if (locationId <= 0) {
                                    errorMsg += ",invalid location id";
                                }
                            }
                            break;

                        case "semantic-place":
                            semanticPl = Utility.parseString(reader.get("semantic-place"));
                            if (semanticPl == null) {
                                errorMsg += ",semantic place cannot be blank";
                            } else {
                                semanticPl = semanticPl.toUpperCase();
                                String school = semanticPl.substring(0, 7); //SMUSISL or SMUSISB
                                int levelNum = Utility.parseInt(semanticPl.substring(7, 8));//1-5

                                if (!(school.equals("SMUSISL") || school.equals("SMUSISB")) || levelNum < 1 || levelNum > 5) {
                                    errorMsg += ",invalid semantic place";
                                }
                            }
                            break;
                    }
                }

                if (errorMsg.length() == 0) {
                    locationIdList.put(locationId, "");
                    //insert into tables
                    stmt.setInt(1, locationId);
                    stmt.setString(2, semanticPl);
                    stmt.addBatch();
                } else {
                    errMap.put(index, errorMsg.substring(1));
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

    /**
     * Retrieve a list of semantic place given the floor of the location
     *
     * @param floor The floor of the location
     * @return an arraylist of semantic place
     */
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

    /**
     * Retrieve a list of semantic place
     * @return an arraylist of semantic place
     */
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

    /**
     * Retrieve semanticplace given a locationId and connection
     *
     * @param conn The connection to the database
     * @param locationId The unique id that identifies a semantic place
     * @return The corresponding semantic place
     */
    public String checkLocationId(Connection conn, int locationId) {

        try {

            PreparedStatement ps = conn.prepareStatement("SELECT semanticplace FROM location WHERE locationid = ?");

            ps.setInt(1, locationId);
            ResultSet rs = ps.executeQuery();

            String location = null;
            while (rs.next()) {
                location = rs.getString(1);
            }

            rs.close();
            ps.close();
            return location;

        } catch (SQLException e) {
        }
        return null;
    }

    /**
     * Retrieve Location object given a semantic Place
     *
     * @param semanticPlace The Semantic Place of interest
     * @return The Location object
     */
    public Location retrieveSemPl(String semanticPlace) {

        String sql = "SELECT * FROM location WHERE semanticplace=?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, semanticPlace);

            rs = ps.executeQuery();

            while (rs.next()) {
                return new Location(rs.getInt(1), rs.getString(2));
            }

        } catch (SQLException e) {

        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return null;
    }

}
