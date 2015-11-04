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
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.Part;

/**
 *
 * @author Boyofthefuture
 */
public class AddBatchController {

    public TreeMap<String, Integer> addBatch(Part filePart, TreeMap<Integer, String> userErrMap, TreeMap<Integer, String> delErrMap,
            TreeMap<Integer, String> auErrMap, TreeMap<Integer, String> luErrMap) throws SQLException, IOException {
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
        System.out.println("e");
        while ((entry = zipInputStream.getNextEntry()) != null) {
            System.out.println("d");
            String fileName = entry.getName();
            System.out.println("c");
            if (fileName.equals("app.csv")) {
                System.out.println("b");
                reader = new CsvReader(br);
                System.out.println("A");
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
                int[] updatedRecords = luDao.delete(reader, delErrMap, conn);
                delUpdated = updatedRecords[0];
            } else {
                zipInputStream.closeEntry();
            }
        }

        ConnectionManager.close(conn);
        if (userUpdated >= 0) {
            result.put("demographics.csv", userUpdated);
        }
        if (auUpdated >= 0) {
            result.put("app.csv", auUpdated);
        }
        if (luUpdated >= 0) {
            result.put("location.csv", luUpdated);
        }
        if (delUpdated >= 0) {
            result.put("location-delete.csv", delUpdated);
        }
        return result;
    }
}
