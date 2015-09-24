package dao;

import entity.App;
import entity.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.*;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ASUS-PC
 */
class AppUsageDAO {

    private ArrayList<App> appUsageList;

    public void insert(ZipInputStream zis) throws IOException {
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
        
        sc.nextLine(); //flush title
        while (sc.hasNext()) {
            sc.next();
        }
        

    }
}
