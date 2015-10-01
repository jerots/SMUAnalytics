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

    private ArrayList<AppUsage> appUsageList;

    private ArrayList<String> unsuccessful = new ArrayList<>();

    public AppUsageDAO() {
        this.appUsageList = appUsageList;
    }

    public void insert(AppDAO aDao, UserDAO uDao, ZipInputStream zis) throws IOException, SQLException {
        Connection conn = ConnectionManager.getConnection();
		PreparedStatement stmt = null;
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
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
                
                AppUsage appU = new AppUsage(date, macAdd, appId);
                appUsageList.add(appU);
                //insert into tables
            }
        }
        for (AppUsage aUsage : appUsageList) {
            String sql = "insert into app (timestamp, mac-address, app-id values(?,?,?))";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, aUsage.getTimestamp());
            stmt.setString(2, "\"" + aUsage.getMacAddress() + "\"");
            stmt.setInt(3, aUsage.getAppId());
        }

        //adding to batch
        stmt.addBatch();

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
