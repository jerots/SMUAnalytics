package dao;

import entity.App;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ASUS-PC
 */
public class AppDAO {

    private ArrayList<App> appList;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> unsuccessful = new ArrayList<>();

    public AppDAO() {
        appList = new ArrayList<>();
    }

    public void insert(ZipInputStream zis, Connection conn) throws IOException, SQLException {
        PreparedStatement stmt = null;
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
        sc.nextLine(); //flush title

        while (sc.hasNext()) {
            //retrieving per row
            boolean err = false;

            int appId = Utility.parseInt(sc.next());
            if (appId <= 0) {
                unsuccessful.add("invalid app id");
                err = true;
            }

            String name = Utility.parseString(sc.next());
            if (name == null) {
                unsuccessful.add("Name cannot be blank.");
                err = true;
            }
            String category = Utility.parseString(sc.next());
            if (category == null) {
                unsuccessful.add("category cannot be blank.");
                err = true;
            }
            
            if (Utility.checkCategory(category)) {
                unsuccessful.add("invalid category.");
                err = true;
            }
            System.out.println(conn.isClosed());

            if (!err) {
                //insert into tables
                String sql = "insert into app (appid, appname, appcategory) values(?,?,?))";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, appId);
                stmt.setString(2, "\"" + name + "\"");
                stmt.setString(3, "\"" + category + "\"");
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

    public boolean hasAppId(int aId) {
        for (App a : appList) {
            if (a.getAppId() == aId) {
                return true;
            }
        }
        return false;
    }
}
