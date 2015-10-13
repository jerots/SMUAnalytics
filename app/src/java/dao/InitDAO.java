/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author jeremyongts92
 */
public class InitDAO {

//	public static void initAdmin() {
//		try {
//
//			Connection conn = ConnectionManager.getConnection();
//			Statement stmt = conn.createStatement();
//			conn.setAutoCommit(false);
//
//			stmt.addBatch("CREATE SCHEMA se IF NOT EXISTS;");
//			stmt.addBatch("USE se;");
//			stmt.addBatch("CREATE TABLE IF NOT EXISTS admin (\n"
//					+ "  username varchar(128) NOT NULL,\n"
//					+ "  password varchar(128) NOT NULL, \n"
//					+ "   CONSTRAINT admin_pk PRIMARY KEY(username)\n"
//					+ "   \n"
//					+ ");");
//
//			stmt.addBatch("INSERT INTO admin VALUES ('admin','123')");
//
//		} catch (SQLException e) {
//
//		}
//
//	}

	public static void createTable() throws SQLException {
		Connection conn = ConnectionManager.getConnection();
		Statement stmt = conn.createStatement();
		conn.setAutoCommit(false);

		stmt.addBatch("DROP SCHEMA IF EXISTS se;");
		stmt.addBatch("CREATE SCHEMA se CHARACTER SET utf8 COLLATE utf8_bin;");
		stmt.addBatch("USE se;");
		stmt.addBatch("CREATE TABLE IF NOT EXISTS admin (\n"
				+ "  username varchar(128) NOT NULL,\n"
				+ "  password varchar(128) NOT NULL, \n"
				+ "   CONSTRAINT admin_pk PRIMARY KEY(username)\n"
				+ "   \n"
				+ ");");

		stmt.addBatch("INSERT INTO admin VALUES ('admin','123')");

		stmt.addBatch("CREATE TABLE IF NOT EXISTS `user` (\n"
				+ "  macaddress varchar(40) NOT NULL,\n"
				+ "  name varchar(128) NOT NULL, \n"
				+ "  password varchar(128) NOT NULL, \n"
				+ "  email varchar(128) NOT NULL, \n"
				+ "  gender char(1) NOT NULL, \n"
				+ "  CONSTRAINT user_pk PRIMARY KEY (macaddress)\n"
				+ ");");

		stmt.addBatch("INSERT INTO user VALUES ('macadd','jeremy','321','jeremyong.2014@sis.smu.edu.sg','M')");
		stmt.addBatch("INSERT INTO user VALUES ('macadd2','zhihui','321','zhtan.2014@business.smu.edu.sg','F')");

		stmt.addBatch("CREATE TABLE IF NOT EXISTS app (\n"
				+ "  appid int NOT NULL,\n"
				+ "  appname varchar(128) NOT NULL, \n"
				+ "  appcategory varchar(20) NOT NULL, \n"
				+ "   CONSTRAINT app_pk PRIMARY KEY(appid)\n"
				+ "   \n"
				+ ");");

		stmt.addBatch("CREATE TABLE IF NOT EXISTS location (\n"
				+ "  locationid int(40) NOT NULL,\n"
				+ "  semanticplace varchar(128) NOT NULL, \n"
				+ "  CONSTRAINT location_pk PRIMARY KEY (locationid)\n"
				+ ");");

		stmt.addBatch("CREATE TABLE IF NOT EXISTS appusage (\n"
				+ "  timestamp datetime NOT NULL,\n"
				+ "  macaddress varchar(40) NOT NULL, \n"
				+ "  appid int(8) NOT NULL  , \n"
				+ "   CONSTRAINT appUsageID_pk PRIMARY KEY (timestamp,macaddress), \n"
				+ "   CONSTRAINT appUsageID_fk1 FOREIGN KEY (macaddress) REFERENCES user(macaddress), \n"
				+ "   CONSTRAINT appUsageID_fk2 FOREIGN KEY (appid) REFERENCES app(appid) \n"
				+ ");");

		stmt.addBatch("CREATE TABLE IF NOT EXISTS locationusage (\n"
				+ "  timestamp datetime NOT NULL,\n"
				+ "  macaddress varchar(40) REFERENCES user(macaddress), \n"
				+ "  locationid int(40) NOT NULL, \n"
				+ "  CONSTRAINT locationUsage_pk PRIMARY KEY (`timestamp`,`locationid`), \n"
				+ "   CONSTRAINT locationUsage_fk2 FOREIGN KEY (locationid) REFERENCES location(locationid) \n"
				+ ");");

        //int[] recordsAffected;
		//recordsAffected = stmt.executeBatch();
		//conn.commit();
		stmt.executeBatch();
		conn.commit();

		ConnectionManager.close(conn, stmt);
	}
}
