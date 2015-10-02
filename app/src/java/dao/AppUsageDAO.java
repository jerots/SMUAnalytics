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

    public void insert(AppDAO aDao, UserDAO uDao, ZipInputStream zis) throws IOException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        PreparedStatement stmt = null;
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
        String sql = "insert into appusage (timestamp, macaddress, appid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
        stmt = conn.prepareStatement(sql);
        conn.setAutoCommit(false);

        sc.nextLine(); //flush title

        while (sc.hasNextLine()) {

            String currLine = sc.nextLine();
            String[] arr = currLine.split(",");
            //retrieving per row
            boolean err = false;

            //check timestamp
            java.util.Date format = Utility.parseDate(arr[0]);
            
            if (format == null) {
                err = true;
                unsuccessful.add("invalid timestamp");
            }
            String date = Utility.formatDate(format);

            //check macAdd
            String macAdd = Utility.parseString(arr[1]);
            if (macAdd == null) {
                unsuccessful.add("mac add cannot be blank");
                err = true;
            }
            if (!Utility.checkHexadecimal(macAdd)) {
                unsuccessful.add("invalid mac address");
                err = true;
            }

            if (!uDao.hasMacAdd(macAdd)) {
                unsuccessful.add("no matching mac address");
                err = true;
            }

            //check appid
            int appId = Utility.parseInt(arr[2]);
            if (appId <= 0) {
                unsuccessful.add("app id cannot be blank");
                err = true;
            }

            if (aDao.hasAppId(appId)) {
                unsuccessful.add("invalid app");
                err = true;
            }

            if (!err) {
                System.out.println(date);
                //add to list
                stmt.setString(1, date);
                stmt.setString(2, macAdd);
                stmt.setInt(3, appId);
                stmt.addBatch();
                //insert into tables
            }
        }
        for(String s: unsuccessful){
            System.out.println(s);
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

}
