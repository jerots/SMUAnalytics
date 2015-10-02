package dao;

import entity.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

public class LocationDAO {

    private ArrayList<Location> locationList;
    private ArrayList<String> unsuccessful = new ArrayList<>();

    public LocationDAO() {
        locationList = new ArrayList<>();
    }

    public void insert(ZipInputStream zis) throws IOException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        PreparedStatement stmt = null;
        conn.setAutoCommit(false);
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
        sc.nextLine(); //flush title
        String sql = "insert into location (locationid, semanticplace) values(?,?);";
        stmt = conn.prepareStatement(sql);

        while (sc.hasNextLine()) {
            String currLine = sc.nextLine();
            String[] arr = currLine.split(",");
            //retrieving per row
            int locationId = -1;
            boolean err = false;

            locationId = Utility.parseInt(arr[0]);
            if (locationId <= 0) {
                unsuccessful.add("invalid location id");
                err = true;
            }

            String semanticPl = Utility.parseString(arr[1]);
            if (semanticPl == null) {
                unsuccessful.add("semantic place cannot be blank");
                err = true;
            }

            String school = semanticPl.substring(0, 7); //SMUSISL or SMUSISB
            int levelNum = Utility.parseInt(semanticPl.substring(7, 8));//1-5

            if (!(school.equals("SMUSISL") || school.equals("SMUSISB")) || levelNum < 1 || levelNum > 5) {
                unsuccessful.add("invalid sematic place");
                err = true;
            }

            if (!err) {
                //add to list
                Location location = new Location(locationId, semanticPl);
                locationList.add(location);
                //insert into tables
                stmt.setInt(1, locationId);
                stmt.setString(2, semanticPl);
                stmt.addBatch();
            }

        }
        //closing
        if (stmt != null) {
            stmt.executeBatch();
            conn.commit();
        }
        if (sc != null) {
            sc.close();
        }
        ConnectionManager.close(conn,stmt);
    }

    public boolean hasLocationId(int lId) {
        for (Location l : locationList) {
            if (l.getLocationId() == lId) {
                return true;
            }
        }
        return false;
    }

}
