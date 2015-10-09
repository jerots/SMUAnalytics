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
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.Part;

/**
 *
 * @author Boyofthefuture
 */
public class AddBatchController {
    
    public HashMap<String, Integer> addBatch(Part filePart, HashMap<Integer, String> userErrMap, HashMap<Integer, String> delErrMap,
            HashMap<Integer, String> auErrMap, HashMap<Integer, String> luErrMap) throws SQLException, IOException {

        InputStream fileContent = filePart.getInputStream();
        ZipEntry entry = null;

        CSVReader reader = null;

        // initialise the number of rows updated
        int userUpdated = 0;
        int delUpdated = 0;
        int auUpdated = 0;
        int luUpdated = 0;
        //put the results into aa hashmap to return to bootstrap action
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        //initialised dao
        
        LocationUsageDAO luDao = new LocationUsageDAO();

        //
        ZipInputStream zipInputStream = new ZipInputStream(fileContent);
        InputStreamReader isr = new InputStreamReader(zipInputStream);
        BufferedReader br = new BufferedReader(isr);
        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.equals("app.csv")) {
                    AppUsageDAO auDao = new AppUsageDAO();
                    reader = new CSVReader(br);
                    reader.readNext();
                    int[] updatedRecords = auDao.insert(reader, auErrMap);

                    //count how many 1 = success
                    for (int i : updatedRecords) {
                        auUpdated+= i;
                    }
                }
                if (fileName.equals("location.csv")) {

                    reader = new CSVReader(br);
                    reader.readNext();
                    int[] updatedRecords = luDao.insert(reader, luErrMap);

                    //count how many 1 = success
                    for (int i : updatedRecords) {
                        luUpdated+= i;
                    }
                }
                if (fileName.equals("demographics.csv")) {
                    UserDAO uDao = new UserDAO();
                    reader = new CSVReader(br);
                    reader.readNext();
                    int[] updatedRecords = uDao.insert(reader, userErrMap);

                    for (int i : updatedRecords) {
                        userUpdated+= i;
                    }
                }
                if (fileName.equals("location-delete.csv")) {

                    reader = new CSVReader(br);
                    reader.readNext();
                    int[] updatedRecords = luDao.delete(reader, delErrMap);

                    //count how many 1 = success
                    for (int i : updatedRecords) {
                        delUpdated+= i;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //
        result.put("userUpdated", userUpdated);
        result.put("delUpdated", delUpdated);
        result.put("auUpdated", auUpdated);
        result.put("luUpdated", luUpdated);

        return result;
    }
}
