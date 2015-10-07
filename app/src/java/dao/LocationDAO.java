package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import java.sql.ResultSet;

public class LocationDAO {
    private ArrayList<String> unsuccessful;

    public LocationDAO() {
        unsuccessful = new ArrayList<>();
    }

    public void insert(CSVReader reader) throws IOException, SQLException {
        try{
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "insert into location (locationid, semanticplace) values(?,?) ON DUPLICATE KEY UPDATE semanticplace = "
                    + "VALUES(semanticplace);";
            PreparedStatement stmt = conn.prepareStatement(sql);

            String[] arr = null;
            while ((arr = reader.readNext()) != null) {
                //retrieving per row
                boolean err = false;

                int locationId = Utility.parseInt(arr[0]);
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
            reader.close();
            ConnectionManager.close(conn,stmt);
        }catch(NullPointerException e){
    
        }
    }
	public ArrayList<String> retrieve(String floor) {

		ArrayList<String> result = new ArrayList<String>();

		try {
			Connection conn = ConnectionManager.getConnection();

			PreparedStatement ps = conn.prepareStatement("SELECT distinct semanticplace FROM location"
					+ " where semanticplace like ?");

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
	
	public ArrayList<String> retrieveAll(){
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
