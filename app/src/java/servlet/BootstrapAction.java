package servlet;



import java.io.*;
import java.sql.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import dao.*;

@WebServlet(urlPatterns = {"/BootstrapAction"})
@MultipartConfig

public class BootstrapAction extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {

			Part filePart = request.getPart("zipFile"); // Retrieves <input type="file" name="zipFile">
			InputStream fileContent = filePart.getInputStream();
			


			boolean appEntered = false;
			boolean demoEntered = false;
			boolean locationEntered = false;
			createTable();
			System.out.println("STOP!");

			AppDAO appDao = new AppDAO();
			AppUsageDAO auDao = new AppUsageDAO();
			LocationUsageDAO luDao = new LocationUsageDAO();
			UserDAO uDao = new UserDAO();
			LocationDAO lDao = new LocationDAO();

			
			ZipInputStream zipInputStream = new ZipInputStream(fileContent);
			while (zipInputStream.available() == 1) {
				ZipEntry entry = zipInputStream.getNextEntry();
				String fileName = entry.getName();
				if (fileName.equals("app-lookup.csv")){
					appDao.insert(zipInputStream);
				
				}
				zipInputStream.closeEntry();
			}
			zipInputStream.close();
//			zipInputStream = new ZipInputStream(fileContent);
//			while (zipInputStream.available() == 1) {
//				ZipEntry entry = zipInputStream.getNextEntry();
//				String fileName = entry.getName();
//				if (fileName.equals("demographics.csv")){
//					uDao.insert(zipInputStream);
//				
//				}
//				zipInputStream.closeEntry();
//			}
//			zipInputStream.close();
//			zipInputStream = new ZipInputStream(fileContent);
//			while (zipInputStream.available() == 1) {
//				ZipEntry entry = zipInputStream.getNextEntry();
//				String fileName = entry.getName();
//				if (fileName.equals("app.csv")){
//					auDao.insert(appDao, uDao, zipInputStream);
//				
//				}
//				zipInputStream.closeEntry();
//			}
//			zipInputStream.close();
//			zipInputStream = new ZipInputStream(fileContent);
//			while (zipInputStream.available() == 1) {
//				ZipEntry entry = zipInputStream.getNextEntry();
//				String fileName = entry.getName();
//				if (fileName.equals("location-lookup.csv")){
//					lDao.insert(zipInputStream);
//				
//				}
//				zipInputStream.closeEntry();
//			}
//			zipInputStream.close();
//			zipInputStream = new ZipInputStream(fileContent);
//			while (zipInputStream.available() == 1) {
//				ZipEntry entry = zipInputStream.getNextEntry();
//				String fileName = entry.getName();
//				if (fileName.equals("location.csv")){
//					luDao.insert(lDao, uDao, zipInputStream);
//				
//				}
//				zipInputStream.closeEntry();
//			}
			zipInputStream.close();
			
			
			
		} catch (Exception e) {
			System.out.println("Exception caught");
			e.printStackTrace();
		}

	}

	public static void createTable() throws SQLException {
		Connection conn = ConnectionManager.getConnection();
		Statement stmt = conn.createStatement();
		conn.setAutoCommit(false);

		stmt.addBatch("CREATE TABLE IF NOT EXISTS app (\n"
				+ "  appid int NOT NULL,\n"
				+ "  appname varchar(128) NOT NULL, \n"
				+ "  appcategory varchar(30) NOT NULL, \n"
				+ "   CONSTRAINT app_pk PRIMARY KEY(appid)\n"
				+ "   \n"
				+ ");");

		stmt.addBatch("CREATE TABLE IF NOT EXISTS location (\n"
				+ "  locationid varchar(100) NOT NULL,\n"
				+ "  semanticplace varchar(100) NOT NULL, \n"
				+ "  CONSTRAINT location_pk PRIMARY KEY (locationid)\n"
				+ ");");

		stmt.addBatch("CREATE TABLE IF NOT EXISTS `user` (\n"
				+ "  macaddress varchar(100) NOT NULL,\n"
				+ "  name varchar(100) NOT NULL, \n"
				+ "  password varchar(100) NOT NULL, \n"
				+ "  email varchar(100) NOT NULL, \n"
				+ "  gender char(1) NOT NULL, \n"
				+ "  CONSTRAINT user_pk PRIMARY KEY (macaddress)\n"
				+ ");");

		stmt.addBatch("CREATE TABLE IF NOT EXISTS appUsage (\n"
				+ "  timestamp date NOT NULL,\n"
				+ "  macaddress varchar(128) NOT NULL, \n"
				+ "  appid int(8) NOT NULL  , \n"
				+ "   CONSTRAINT appUsageID_pk PRIMARY KEY (timestamp,macaddress), \n"
				+ "   CONSTRAINT appUsageID_fk1 FOREIGN KEY (macaddress) REFERENCES user(macaddress), \n"
				+ "   CONSTRAINT appUsageID_fk2 FOREIGN KEY (appid) REFERENCES app(appid) \n"
				+ ");");

		stmt.addBatch("CREATE TABLE IF NOT EXISTS locationUsage (\n"
				+ "  timestamp date NOT NULL,\n"
				+ "  macaddress varchar(100), \n"
				+ "  locationid varchar(100) NOT NULL, \n"
				+ "  CONSTRAINT locationUsage_pk PRIMARY KEY (`timestamp`,`locationid`), \n"
				+ "   CONSTRAINT locationUsage_fk1 FOREIGN KEY (macaddress) REFERENCES user(macaddress), \n"
				+ "   CONSTRAINT locationUsage_fk2 FOREIGN KEY (locationid) REFERENCES location(locationid) \n"
				+ ");");

        //int[] recordsAffected;
		//recordsAffected = stmt.executeBatch();
		//conn.commit();
		stmt.executeBatch();
		conn.commit();
		ConnectionManager.close(conn,stmt);
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
