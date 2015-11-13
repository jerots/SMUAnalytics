/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.csvreader.CsvReader;
import dao.AppUsageDAO;
import dao.ConnectionManager;
import dao.LocationUsageDAO;
import dao.UserDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.Part;

/**
 *
 * @author Boyofthefuture
 */
/**
 * AddBatchController controls all actions related to adding of additional data to the original
 * data that a user Bootstrapped
 */
public class AddBatchController {

    
     /**
     * Retrieves a TreeMap<String, Integer> object for the data add additional data to the bootstrapped files
     *
     * @param filePart The zipped input file
     * @param userErrMap The map that contains error messages and its corresponding row from demographics.csv
     * @param delErrMap The map that contains error messages and its corresponding row from location-delete.csv
     * @param auErrMap The map that contains error messages and its corresponding row from app.csv
     * @param luErrMap The map that contains error messages and its corresponding row from location.csv
     * @param delMap The map that contains uploaded successful row and non successful rows from location-delete.csv
     * @return A treemap objects that belongs contains the
     * records successfully updated for each csv file in the input Zipped File
     */
    public TreeMap<String, Integer> addBatch(Part filePart, TreeMap<Integer, String> userErrMap,
            TreeMap<Integer, String> auErrMap, TreeMap<Integer, String> luErrMap, HashMap<String, Integer> delMap) throws SQLException, IOException {
        Connection conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
        InputStream fileContent = filePart.getInputStream();
        ZipEntry entry = null;

        CsvReader reader = null;

        // initialise the number of rows updated
        int userUpdated = -1;
        int delUpdated = -1;
        int auUpdated = -1;
        int luUpdated = -1;
        int notFound = -1;
        //put the results into aa hashmap to return to bootstrap action
        TreeMap<String, Integer> result = new TreeMap<String, Integer>();

        //initialised dao
        LocationUsageDAO luDao = new LocationUsageDAO();
        UserDAO uDao = new UserDAO();
        AppUsageDAO auDao = new AppUsageDAO();

        //demographics.csv
        ZipInputStream zipInputStream = new ZipInputStream(fileContent);
        InputStreamReader isr = new InputStreamReader(zipInputStream);
        BufferedReader br = new BufferedReader(isr);
        entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            String fileName = entry.getName();
            if (fileName.equals("demographics.csv")) {
                reader = new CsvReader(br);
                int[] updatedRecords = uDao.add(reader, userErrMap, conn);
                userUpdated = updatedRecords.length;
                break;
            } else {
                zipInputStream.closeEntry();
            }
        }

        //app.csv
        fileContent = filePart.getInputStream();
        zipInputStream = new ZipInputStream(fileContent);
        isr = new InputStreamReader(zipInputStream);
        br = new BufferedReader(isr);
        entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            String fileName = entry.getName();
            if (fileName.equals("app.csv")) {
                reader = new CsvReader(br);
                auUpdated = auDao.add(reader, auErrMap, conn);
                break;
            } else {
                zipInputStream.closeEntry();
            }
        }

        //locationUsage
        fileContent = filePart.getInputStream();
        zipInputStream = new ZipInputStream(fileContent);
        isr = new InputStreamReader(zipInputStream);
        br = new BufferedReader(isr);
        entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            String fileName = entry.getName();
            if (fileName.equals("location.csv")) {
                reader = new CsvReader(br);
                luUpdated = luDao.add(reader, luErrMap, conn);
                break;
            } else {
                zipInputStream.closeEntry();
            }
        }

        //location-delete.csv
        fileContent = filePart.getInputStream();
        zipInputStream = new ZipInputStream(fileContent);
        isr = new InputStreamReader(zipInputStream);
        br = new BufferedReader(isr);
        entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            String fileName = entry.getName();
            if (fileName.equals("location-delete.csv")) {
                reader = new CsvReader(br);
                int[] updatedRecords = luDao.delete(reader, conn);
                delUpdated = updatedRecords[0];
                notFound = updatedRecords[1];
                break;
            } else {
                zipInputStream.closeEntry();
            }
        }

        ConnectionManager.close(conn);
        //LOADS UNNECESSARY THINGS FOR THE UI CHECKING TO CORRESP WITH BOOTSTRAP
        result.put("app-lookup.csv", -1);
        result.put("location-lookup.csv", -1);
        result.put("demographics.csv", userUpdated);
        result.put("app.csv", auUpdated);
        result.put("location.csv", luUpdated);
        delMap.put("location-delete.csv", delUpdated);
        delMap.put("deletenotfound", notFound);
        return result;
    }
}
