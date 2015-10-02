package dao;

import entity.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.*;

public class AppUsageDAO {

    private ArrayList<String> unsuccessful = new ArrayList<>();

    public AppUsageDAO() {
    }

    public void insert(AppDAO aDao, UserDAO uDao, ZipInputStream zis, Connection conn) throws IOException, SQLException {
        PreparedStatement stmt = null;
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
        
        String sql = "insert into appusage (timestamp, macaddress, appid) values(?,?,?))";
        stmt = conn.prepareStatement(sql);
        conn.setAutoCommit(false);
        
        sc.nextLine(); //flush title

        while (sc.hasNext()) {
            //retrieving per row
            boolean err = false;

            //check timestamp
            Date date = Utility.parseDate(sc.next());
            if (date == null) {
                err = true;
                unsuccessful.add("invalid timestamp");
            }

            //check macAdd
            String macAdd = Utility.parseString(sc.next());
            if (macAdd == null) {
                unsuccessful.add("mac add cannot be blank");
                err = true;
            }
            if (!Utility.checkHexadecimal(macAdd)) {
                unsuccessful.add("invalid mac address");
                err = true;
            }

            if (uDao.hasMacAdd(macAdd)) {
                unsuccessful.add("no matching mac address");
                err = true;
            }

            //check appid
            int appId = Utility.parseInt(sc.next());
            if (appId <= 0) {
                unsuccessful.add("app id cannot be blank");
                err = true;
            }

            if (aDao.hasAppId(appId)) {
                unsuccessful.add("invalid app");
                err = true;
            }

            if (!err) {
                //add to list
                stmt.setDate(1, date);
                stmt.setString(2, macAdd);
                stmt.setInt(3, appId);
                stmt.addBatch();
                //insert into tables
            }
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

}
