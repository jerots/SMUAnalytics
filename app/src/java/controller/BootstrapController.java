/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.csvreader.CsvReader;
import dao.AppDAO;
import dao.AppUsageDAO;
import dao.ConnectionManager;
import dao.InitDAO;
import dao.LocationDAO;
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
 * @author Shuwen
 */

/**
 * Bootstrap controls all actions related TopkReport related functionality
 */
public class BootstrapController {

    
	//handles the number of rows updated 
	//and the error message of the different files - pass to DAO to handle
	//RETURNS A COMBINATION OF "DATA FILES & ROWS UPDATED"
    
      /**
     * Retrieves a TreeMap<String, Integer> object for the data bootstrapping of zip file
     *
     * @param filePart The zipped input file
     * @param userErrMap The map that contains error messages and its corresponding row from demographics.csv
     * @param appErrMap The map that contains error messages and its corresponding row from app-lookup.csv
     * @param locErrMap The map that contains error messages and its corresponding row from location-lookup.csv
     * @param auErrMap The map that contains error messages and its corresponding row from app.csv
     * @param luErrMap The map that contains error messages and its corresponding row from location.csv
     * @param errors The errors generated from the top-k apps
     * @return A sorted ArrayList of Hashmap objects that belongs contains the
     * Top-k most used apps(Given a school)
     */
	public TreeMap<String, Integer> bootstrap(Part filePart, TreeMap<Integer, String> userErrMap, TreeMap<Integer, String> appErrMap,
			TreeMap<Integer, String> locErrMap, TreeMap<Integer, String> auErrMap, TreeMap<Integer, String> luErrMap)
			throws IOException {

		InputStream fileContent = filePart.getInputStream();
		ZipEntry entry = null;

		CsvReader reader = null;
		Connection conn = null;
		try {
			conn = ConnectionManager.getConnection();
			conn.setAutoCommit(false);

			InitDAO.truncateTable(conn);

		} catch (SQLException e) {
		}

		// initialise the number of rows updated
		int userUpdated = -1;
		int appUpdated = -1;
		int locUpdated = -1;
		int auUpdated = -1;
		int luUpdated = -1;
		int delUpdated = -1;
                int notFound = -1;
		//put the results into aa hashmap to return to bootstrap action
		TreeMap<String, Integer> result = new TreeMap<String, Integer>();

		//initialised dao
		UserDAO uDao = new UserDAO();
		AppDAO appDao = new AppDAO();
		LocationDAO lDao = new LocationDAO();
		AppUsageDAO auDao = new AppUsageDAO();
		LocationUsageDAO luDao = new LocationUsageDAO();

		HashMap<String, String> macList = new HashMap<String, String>();
		HashMap<Integer, String> appIdList = new HashMap<Integer, String>();
		HashMap<Integer, String> locationIdList = new HashMap<Integer, String>();

		//app-lookup.csv
		ZipInputStream zipInputStream = new ZipInputStream(fileContent);
		InputStreamReader isr = new InputStreamReader(zipInputStream);
		BufferedReader br = new BufferedReader(isr);
		while ((entry = zipInputStream.getNextEntry()) != null) {
			String fileName = entry.getName();
			if (fileName.equals("app-lookup.csv")) {
				appUpdated = 0;
				reader = new CsvReader(br);
				//returns number of successfully entered entries
				//success >= 0;
				//unsuccess: anything other than a number;
				int[] updatedRecords = appDao.insert(reader, appErrMap, conn, appIdList);
				//count how many 1 = success. Sets updated records to empty so that in case theres nothing, the updated records return NOTHING.
                                appUpdated = updatedRecords.length;
				break;

			} else {
				zipInputStream.closeEntry();
			}
		}
		br.close();

		//demographics.csv
		fileContent = filePart.getInputStream();
		zipInputStream = new ZipInputStream(fileContent);
		isr = new InputStreamReader(zipInputStream);
		br = new BufferedReader(isr);
		entry = null;
		while ((entry = zipInputStream.getNextEntry()) != null) {
			String fileName = entry.getName();
			if (fileName.equals("demographics.csv")) {
				userUpdated = 0;
				reader = new CsvReader(br);
				int[] updatedRecords = uDao.insert(reader, userErrMap, conn, macList);
				userUpdated = updatedRecords.length;
				break;
			} else {
				zipInputStream.closeEntry();
			}
		}
		br.close();

		//app.csv
		fileContent = filePart.getInputStream();
		zipInputStream = new ZipInputStream(fileContent);
		isr = new InputStreamReader(zipInputStream);
		br = new BufferedReader(isr);
		entry = null;
		while ((entry = zipInputStream.getNextEntry()) != null) {
			String fileName = entry.getName();
			if (fileName.equals("app.csv")) {
				auUpdated = 0;
				reader = new CsvReader(br);
				auUpdated = auDao.insert(reader, auErrMap, conn, macList, appIdList);
				break;
			} else {
				zipInputStream.closeEntry();
			}
		}
		br.close();

		//location
		fileContent = filePart.getInputStream();
		zipInputStream = new ZipInputStream(fileContent);
		isr = new InputStreamReader(zipInputStream);
		br = new BufferedReader(isr);
		entry = null;
		while ((entry = zipInputStream.getNextEntry()) != null) {
			String fileName = entry.getName();
			if (fileName.equals("location-lookup.csv")) {
				locUpdated = 0;
				reader = new CsvReader(br);
                                int[] updatedRecords = lDao.insert(reader, locErrMap, conn, locationIdList);
				locUpdated = updatedRecords.length;
				break;
			} else {
				zipInputStream.closeEntry();
			}
		}
		br.close();

		//locationUsage
		fileContent = filePart.getInputStream();
		zipInputStream = new ZipInputStream(fileContent);
		isr = new InputStreamReader(zipInputStream);
		br = new BufferedReader(isr);
		entry = null;
		while ((entry = zipInputStream.getNextEntry()) != null) {
			String fileName = entry.getName();
			if (fileName.equals("location.csv")) {
				luUpdated = 0;
				reader = new CsvReader(br);
				luUpdated = luDao.insert(reader, luErrMap, conn, locationIdList);
				break;
			} else {
				zipInputStream.closeEntry();
			}

		}
		br.close();

		//location-delete.csv
		fileContent = filePart.getInputStream();
		zipInputStream = new ZipInputStream(fileContent);
		isr = new InputStreamReader(zipInputStream);
		br = new BufferedReader(isr);
		entry = null;
		while ((entry = zipInputStream.getNextEntry()) != null) {
			String fileName = entry.getName();
			if (fileName.equals("location-delete.csv")) {
				delUpdated = 0;
				reader = new CsvReader(br);
				int[] updatedRecords = luDao.delete(reader, conn);
                                delUpdated = updatedRecords[0];
                                notFound = updatedRecords[1];
				break;
			} else {
				zipInputStream.closeEntry();
			}
		}
		br.close();

		result.put("demographics.csv", userUpdated);
		result.put("app-lookup.csv", appUpdated);
		result.put("location-lookup.csv", locUpdated);
		result.put("app.csv", auUpdated);
		result.put("location.csv", luUpdated);
		result.put("location-delete.csv", delUpdated);
                result.put("deletenotfound", notFound);
		try {
			InitDAO.enableForeignKey(conn);

		} catch (SQLException e) {

		}
		ConnectionManager.close(conn);

		return result;
	}
}
