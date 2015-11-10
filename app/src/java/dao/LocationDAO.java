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
                String errorMsg = "";
                
                int locationId = Utility.parseInt(reader.get("location-id"));
                if (locationId <= 0) {
                    errorMsg += ",invalid location-id";
                }

                String semanticPl = Utility.parseString(reader.get("semantic-place"));
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

                if (errorMsg.length() == 0) {
                    locationIdList.put(locationId, "");
                    //insert into tables
                    stmt.setInt(1, locationId);
                    stmt.setString(2, semanticPl);
                    stmt.addBatch();
                }else{
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
