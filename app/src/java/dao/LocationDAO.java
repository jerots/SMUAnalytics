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
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
        sc.nextLine(); //flush title

        while (sc.hasNext()) {

            //retrieving per row
            int locationId = -1;
            boolean err = false;

            locationId = Utility.parseInt(sc.next());
            if (locationId <= 0) {
                unsuccessful.add("invalid location id");
                err = true;
            }

            String semanticPl = Utility.parseString(sc.next());

            if (semanticPl == null) {
                unsuccessful.add("semantic place cannot be blank");
                err = true;
            }

            String school = semanticPl.substring(0, 7); //SMUSISL or SMUSISB
            int levelNum = Utility.parseInt(semanticPl.substring(7, 8));//1-5

            if (!((school.equals("SMUSISL") || school.equals("SMUSISB")) && (levelNum >= 1 || levelNum <= 5))) {
                unsuccessful.add("invalid sematic place");
                err = true;
            }

            if (!err) {
                //add to list
                Location location = new Location(locationId, semanticPl);
                locationList.add(location);
                //insert into tables
                String sql = "insert into location (location-id, semantic-place values(?,?))";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, +locationId);
                stmt.setString(2, "\"" + semanticPl + "\"");

            }

            //adding to batch
            stmt.addBatch();

        }

        //closing
        if (stmt != null) {
            stmt.executeBatch();
            conn.commit();
            stmt.close();
        }
        if (sc != null) {
            sc.close();
        }
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
