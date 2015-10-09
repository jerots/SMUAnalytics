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

	public static void initAdmin() {
		try {

			Connection conn = ConnectionManager.getConnection();
			Statement stmt = conn.createStatement();
			conn.setAutoCommit(false);

			stmt.addBatch("CREATE SCHEMA g3t3 IF NOT EXISTS;");
			stmt.addBatch("USE g3t3;");
			stmt.addBatch("CREATE TABLE IF NOT EXISTS admin (\n"
					+ "  username varchar(128) NOT NULL,\n"
					+ "  password varchar(128) NOT NULL, \n"
					+ "   CONSTRAINT admin_pk PRIMARY KEY(username)\n"
					+ "   \n"
					+ ");");

			stmt.addBatch("INSERT INTO admin VALUES ('admin','123')");

		} catch (SQLException e) {

		}

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
