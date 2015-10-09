package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import java.sql.ResultSet;
import java.util.HashMap;

public class LocationDAO {


    public LocationDAO() {
    }

    public int[] insert(CSVReader reader, HashMap<Integer, String> errMap) throws IOException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
        String sql = "insert into location (locationid, semanticplace) values(?,?) ON DUPLICATE KEY UPDATE semanticplace = "
                + "VALUES(semanticplace);";
        PreparedStatement stmt = conn.prepareStatement(sql);
        int index = 2;

        String[] arr = null;
        while ((arr = reader.readNext()) != null) {
            //retrieving per row
            boolean err = false;

            int locationId = Utility.parseInt(arr[0]);
            if (locationId <= 0) {
                String errorMsg = errMap.get(index);
                if (errorMsg == null) {
                    errMap.put(index, "invalid location id");
                } else {
                    errMap.put(index, errorMsg + "," + "invalid location id");
                }
                err = true;
            }

            String semanticPl = Utility.parseString(arr[1]);
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
                
                //insert into tables
                stmt.setInt(1, locationId);
                stmt.setString(2, semanticPl);
                stmt.addBatch();
            }
            index++;

        }
        
        int[] updateCounts = stmt.executeBatch();
        conn.commit();

        //closing
        reader.close();
        ConnectionManager.close(conn, stmt);
        return updateCounts;
    }

    public ArrayList<String> retrieve(String floor) {

        ArrayList<String> result = new ArrayList<String>();

        try {
            Connection conn = ConnectionManager.getConnection();

            PreparedStatement ps = conn.prepareStatement("SELECT distinct semanticplace FROM location "
                    + " where semanticplace like ? "
					+ " ORDER BY semanticplace");

            ps.setString(1, "SMUSIS" + floor + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String place = rs.getString(1);
                result.add(place);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<String> retrieveAll() {
        ArrayList<String> result = new ArrayList<String>();

        try {
            Connection conn = ConnectionManager.getConnection();

            PreparedStatement ps = conn.prepareStatement("SELECT semanticplace FROM location");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String place = rs.getString(1);
                result.add(place);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}
