package dao;

import entity.App;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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

    public void insert(ZipInputStream zis) throws IOException {
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
        sc.nextLine(); //flush title
        while (sc.hasNext()) {
            String appId = sc.next();
            String name = sc.next();
            String category = sc.next();
        }
    }
}