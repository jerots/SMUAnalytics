/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.opencsv.CSVReader;
import dao.AppDAO;
import dao.AppUsageDAO;
import dao.LocationDAO;
import dao.LocationUsageDAO;
import dao.UserDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.Part;

/**
 *
 * @author Shuwen
 */
public class BootstrapController {

    public HashMap<String, Integer> bootstrap(Part filePart, HashMap<Integer,String> userErrMap, HashMap<Integer,String> appErrMap,
            HashMap<Integer,String> locErrMap, HashMap<Integer,String> auErrMap, HashMap<Integer,String> luErrMap) throws SQLException, IOException {

        
        
        InputStream fileContent = filePart.getInputStream();
        ZipEntry entry = null;

        CSVReader reader = null;

//            while ((entry = zipInputStream.getNextEntry()) != null) {
//                String fileName = entry.getName();
//                
//            }
        
        
        
        int userUpdated = 0;
        int appUpdated = 0;
        int locUpdated = 0;
        int auUpdated = 0;
        int luUpdated = 0;
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        
        UserDAO uDao = new UserDAO();
        AppDAO appDao = new AppDAO();
        LocationDAO lDao = new LocationDAO();
        AppUsageDAO auDao = new AppUsageDAO();
        LocationUsageDAO luDao = new LocationUsageDAO();

        ZipInputStream zipInputStream = new ZipInputStream(fileContent);
        InputStreamReader isr = new InputStreamReader(zipInputStream);
        BufferedReader br = new BufferedReader(isr);
        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.equals("app-lookup.csv")) {
                    reader = new CSVReader(br);
                    reader.readNext();
                    int[] updatedRecords = appDao.insert(reader, appErrMap);
                    
                    
                    for (int i : updatedRecords){
                        if (i == 1){
                            appUpdated++;
                        }
                    }
                    
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileContent = filePart.getInputStream();
        zipInputStream = new ZipInputStream(fileContent);
        isr = new InputStreamReader(zipInputStream);
        br = new BufferedReader(isr);
        entry = null;
        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.equals("demographics.csv")) {
                    reader = new CSVReader(br);
                    reader.readNext();
                    uDao.insert(reader,userErrMap);
                } else {
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileContent = filePart.getInputStream();
        zipInputStream = new ZipInputStream(fileContent);
        isr = new InputStreamReader(zipInputStream);
        br = new BufferedReader(isr);
        entry = null;
        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.equals("app.csv")) {
                    reader = new CSVReader(br);
                    reader.readNext();
                    auDao.insert(reader);
                } else {
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileContent = filePart.getInputStream();
        zipInputStream = new ZipInputStream(fileContent);
        isr = new InputStreamReader(zipInputStream);
        br = new BufferedReader(isr);
        entry = null;
        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.equals("location-lookup.csv")) {
                    reader = new CSVReader(br);
                    reader.readNext();
                    lDao.insert(reader,locErrMap);
                } else {
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileContent = filePart.getInputStream();
        zipInputStream = new ZipInputStream(fileContent);
        isr = new InputStreamReader(zipInputStream);
        br = new BufferedReader(isr);
        entry = null;
        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.equals("location.csv")) {
                    reader = new CSVReader(br);
                    reader.readNext();
                    luDao.insert(reader);
                } else {
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileContent = filePart.getInputStream();
        zipInputStream = new ZipInputStream(fileContent);
        isr = new InputStreamReader(zipInputStream);
        br = new BufferedReader(isr);
        entry = null;
        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.equals("location-delete.csv")) {
                    reader = new CSVReader(br);
                    reader.readNext();
                    luDao.insert(reader);
                } else {
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        result.put("userUpdated", userUpdated);
        result.put("appUpdated", appUpdated);
        result.put("locUpdated", locUpdated);
        result.put("auUpdated", auUpdated);
        result.put("luUpdated", luUpdated);
        
        return result;
    }

}
