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

    public void insert(ZipInputStream zis) throws IOException, SQLException {
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement stmt = null;
            Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
            sc.nextLine(); //flush title

            String sql = "insert into app values(?,?,?);";
            stmt = conn.prepareStatement(sql);
            conn.setAutoCommit(false);

            while (sc.hasNextLine()) {
                //retrieving per row
                String currentLine = sc.nextLine();

                String[] arr = currentLine.split(",");

                boolean err = false;

                int appId = Utility.parseInt(arr[0]);
                if (appId <= 0) {
                    unsuccessful.add("invalid app id");
                    err = true;
                }

                String name = Utility.parseString(arr[1]);
                name = name.replace("\"","");
                if (name == null) {
                    unsuccessful.add("Name cannot be blank.");
                    err = true;
                }
                
                String cat = Utility.parseString(arr[2]);
                cat = cat.replace("\"","");

                if (cat == null) {
                    unsuccessful.add("category cannot be blank.");
                    err = true;

                }

                if (Utility.checkCategory(cat)) {
                    unsuccessful.add("invalid category.");
                    err = true;
                }

                if (!err) {
                    //insert into tables
                    stmt.setInt(1, appId);
                    stmt.setString(2, name);
                    stmt.setString(3, cat);
                    stmt.addBatch();
                }

            }

            //closing
            if (stmt != null) {
                stmt.executeBatch();
                conn.commit();
            }
            sc.close();
            ConnectionManager.close(conn,stmt);
        } catch (NullPointerException e) {
            e.printStackTrace();
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
