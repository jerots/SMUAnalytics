/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.opencsv.CSVReader;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
			throws IOException {

		InputStream fileContent = filePart.getInputStream();
		ZipEntry entry = null;

		CSVReader reader = null;
		Connection conn = null;
		try {
			conn = ConnectionManager.getConnection();
			conn.setAutoCommit(false);

			InitDAO.truncateTable(conn);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// initialise the number of rows updated
		int userUpdated = -1;
		int appUpdated = -1;
		int locUpdated = -1;
		int auUpdated = -1;
		int luUpdated = -1;
		int delUpdated = -1;
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
				reader = new CSVReader(br);
				reader.readNext();
				//returns number of successfully entered entries
				//success >= 0;
				//unsuccess: anything other than a number;
				int[] updatedRecords = null;
				try {
					updatedRecords = appDao.insert(reader, appErrMap, conn, appIdList);

				} catch (SQLException e) {
					e.printStackTrace();
				}

				//count how many 1 = success
				for (int i : updatedRecords) {
					appUpdated += i;
				}
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
				reader = new CSVReader(br);
				reader.readNext();
				int[] updatedRecords = null;
				try {
					updatedRecords = uDao.insert(reader, userErrMap, conn, macList);

				} catch (SQLException e) {
					e.printStackTrace();
				}

				for (int i : updatedRecords) {
					userUpdated += i;
				}
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
				reader = new CSVReader(br);
				reader.readNext();
				int[] updatedRecords = null;
				try {
					updatedRecords = auDao.insert(reader, auErrMap, conn, macList, appIdList);

				} catch (SQLException e) {
					e.printStackTrace();
				}

//				for (int i : updatedRecords) {
//					auUpdated++;
//				}
				auUpdated = updatedRecords.length;
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
				reader = new CSVReader(br);
				reader.readNext();
				int[] updatedRecords = null;
				try {
					updatedRecords = lDao.insert(reader, locErrMap, conn, locationIdList);

				} catch (SQLException e) {
					e.printStackTrace();
				}

//				for (int i : updatedRecords) {
//					locUpdated++;
//				}
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
				reader = new CSVReader(br);
				reader.readNext();
				int[] updatedRecords = null;
				try {
					updatedRecords = luDao.insert(reader, luErrMap, conn, locationIdList);

				} catch (SQLException e) {
					e.printStackTrace();
				}

//				for (int i : updatedRecords) {
//					luUpdated++;
//				}
				luUpdated = updatedRecords.length;
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
				reader = new CSVReader(br);
				reader.readNext();
				int[] updatedRecords = null;
				try {
					updatedRecords = luDao.delete(reader, delErrMap);

				} catch (SQLException e) {

				}
//                    for (int i : updatedRecords) {
//						System.out.println(i);
//                        delUpdated+= i;
//                    }
				delUpdated = updatedRecords[0];
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
		try {
			InitDAO.enableForeignKey(conn);

		} catch (SQLException e) {

		}
		ConnectionManager.close(conn);

		return result;
	}
}
