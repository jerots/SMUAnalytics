/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.opencsv.CSVReader;
import dao.AppDAO;
import dao.AppUsageDAO;
import dao.InitDAO;
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
import java.util.TreeMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.Part;

/**
 *
 * @author Shuwen
 */
public class BootstrapController {

    //handles the number of rows updated 
    //and the error message of the different files - pass to DAO to handle
    //RETURNS A COMBINATION OF "DATA FILES & ROWS UPDATED"
    public TreeMap<String, Integer> bootstrap(Part filePart, TreeMap<Integer, String> userErrMap, TreeMap<Integer, String> appErrMap,
            TreeMap<Integer, String> locErrMap, TreeMap<Integer, String> auErrMap, TreeMap<Integer, String> luErrMap, TreeMap<Integer, String> delErrMap) 
            throws SQLException, IOException {

        InputStream fileContent = filePart.getInputStream();
        ZipEntry entry = null;

        CSVReader reader = null;
        
        //Clears the tables before loading.
        try {
            InitDAO.createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // initialise the number of rows updated
        int userUpdated = 0;
        int appUpdated = 0;
        int locUpdated = 0;
        int auUpdated = 0;
        int luUpdated = 0;
        int delUpdated = 0;
        //put the results into aa hashmap to return to bootstrap action
        TreeMap<String, Integer> result = new TreeMap<String, Integer>();

        //initialised dao
        UserDAO uDao = new UserDAO();
        AppDAO appDao = new AppDAO();
        LocationDAO lDao = new LocationDAO();
        AppUsageDAO auDao = new AppUsageDAO();
        LocationUsageDAO luDao = new LocationUsageDAO();

        //app-lookup.csv
        ZipInputStream zipInputStream = new ZipInputStream(fileContent);
        InputStreamReader isr = new InputStreamReader(zipInputStream);
        BufferedReader br = new BufferedReader(isr);
        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.equals("app-lookup.csv")) {
                    reader = new CSVReader(br);
                    reader.readNext();
                    //returns number of successfully entered entries
                    //success >= 0;
                    //unsuccess: anything other than a number;
                    int[] updatedRecords = appDao.insert(reader, appErrMap);

                    //count how many 1 = success
                    for (int i : updatedRecords) {
                        appUpdated+= i;
                    }
                }else {
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }

        //demographics.csv
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
                    int[] updatedRecords = uDao.insert(reader, userErrMap);

                    for (int i : updatedRecords) {
                        userUpdated+= i;
                    }
                }else {
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }

        //app.csv
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
                    int[] updatedRecords = auDao.insert(reader, auErrMap);

                    for (int i : updatedRecords) {
                        auUpdated++;
                    }
                }else {
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }

        //location
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
                    int[] updatedRecords = lDao.insert(reader, locErrMap);

                    for (int i : updatedRecords) {
                        locUpdated++;
                    }
                }else {
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
		
		//locationUsage
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
                    int[] updatedRecords = luDao.insert(reader, luErrMap);

                    for (int i : updatedRecords) {
                        luUpdated++;
                    }
                }else {
                    zipInputStream.closeEntry();
                }

            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
		
		//location-delete.csv
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
                    int[] updatedRecords = luDao.delete(reader, delErrMap);
					System.out.println("LOOK BELOW");
//                    for (int i : updatedRecords) {
//						System.out.println(i);
//                        delUpdated+= i;
//                    }
					delUpdated = updatedRecords[0];
					System.out.println("LOOK ABOVE");
                } else {
                    zipInputStream.closeEntry();
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }

        result.put("demographics.csv", userUpdated);
        result.put("app-lookup.csv", appUpdated);
        result.put("location-lookup.csv", locUpdated);
        result.put("app.csv", auUpdated);
        result.put("location.csv", luUpdated);
        result.put("location-delete.csv", delUpdated);

        return result;
    }
}
