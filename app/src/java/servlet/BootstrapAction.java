package servlet;

import java.io.*;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/dao";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

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
        System.out.println("MERRY xmas!");
        try {
            Part filePart = request.getPart("zipFile"); // Retrieves <input type="file" name="zipFile">
            InputStream fileContent = filePart.getInputStream();
            ZipInputStream zipInputStream = new ZipInputStream(fileContent);

            ZipInputStream appInputStream = null;
            ZipInputStream locationInputStream = null;

            boolean appEntered = false;
            boolean demoEntered = false;
            boolean locationEntered = false;
            //connection
System.out.println("Starthere!");
            Connection conn = ConnectionManager.getConnection();
            createTable(conn);
            System.out.println("STOP!");
            
            AppDAO appDao = new AppDAO();
            AppUsageDAO auDao = new AppUsageDAO();
            LocationUsageDAO luDao = new LocationUsageDAO();
            UserDAO uDao = new UserDAO();
            LocationDAO lDao = new LocationDAO();
            

            while (zipInputStream.available() == 1) {
                System.out.print("WHERE AM I?");
                ZipEntry entry = zipInputStream.getNextEntry();
                String fileName = entry.getName();
                switch (fileName) {

                    case "app.csv":
                        System.out.println("HIa");
                        if (appEntered && demoEntered) {
                            auDao.insert(appDao, uDao, zipInputStream, conn);
                        } else {
                            appInputStream = zipInputStream;
                        }
                        break;

                    case "app-lookup.csv":
                        System.out.println("HIb");
                        appEntered = true;
                        appDao.insert(zipInputStream, conn);
                        break;

                    case "demographics.csv":
                        System.out.println("HIc");
                        demoEntered = true;
                        uDao.insert(zipInputStream, conn);
                        break;

                    case "location-lookup.csv":
                        System.out.println("HId");
                        locationEntered = true;
                        lDao.insert(zipInputStream, conn);
                        break;

                    case "location.csv":
                        System.out.println("HIe");
                        if (locationEntered && demoEntered) {
                            luDao.insert(lDao, uDao, zipInputStream, conn);
                        } else {
                            locationInputStream = zipInputStream;
                        }
                        break;
                }

                if (appInputStream != null) {
                    //auDao.insert(appInputStream);
                }

                if (locationInputStream != null) {
                    //luDao.insert(locationInputStream);
                }
            }

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {

        }
    }

    public void createTable(Connection conn) throws SQLException {
        System.out.println("why WHYWHYYWYHYHWY");
        Statement stmt = conn.createStatement();
        System.out.println("why aint u appearing?");
        conn.setAutoCommit(false);
        System.out.println("why aint u appearing?");

        stmt.addBatch("CREATE TABLE IF NOT EXISTS app (\n"
                + "  appid int NOT NULL,\n"
                + "  appname varchar(128) NOT NULL, \n"
                + "  appcategory varchar(30) NOT NULL, \n"
                + "   CONSTRAINT app_pk PRIMARY KEY(appid)\n"
                + "   \n"
                + ");");
     
        
       
        stmt.addBatch("CREATE TABLE IF NOT EXISTS appUsage (\n"
                + "  timestamp date NOT NULL,\n"
                + "  macaddress varchar(128) NOT NULL, \n"
                + "  appid int(8) NOT NULL  , \n"
                + "   CONSTRAINT appUsageID_pk PRIMARY KEY (timestamp,macaddress), \n"
                + "   CONSTRAINT appUsageID_fk1 FOREIGN KEY (macaddress) REFERENCES user(macaddress), \n"
                + "   CONSTRAINT appUsageID_fk2 FOREIGN KEY (appid) REFERENCES app(appid) \n"
                + ");");

        stmt.addBatch("CREATE TABLE IF NOT EXISTS location (\n"
                + "  locationid varchar(100) NOT NULL,\n"
                + "  semanticplace varchar(100) NOT NULL, \n"
                + "  CONSTRAINT location_pk PRIMARY KEY (locationid)\n"
                + ");");

        stmt.addBatch("CREATE TABLE IF NOT EXISTS locationUsage (\n"
                + "  timestamp date NOT NULL,\n"
                + "  macaddress varchar(100), \n"
                + "  locationid varchar(100) NOT NULL, \n"
                + "  CONSTRAINT locationUsage_pk PRIMARY KEY (`timestamp`,`locationid`), \n"
                + "   CONSTRAINT locationUsage_fk1 FOREIGN KEY (macaddress) REFERENCES user(macaddress), \n"
                + "   CONSTRAINT locationUsage_fk2 FOREIGN KEY (locationid) REFERENCES location(locationid) \n"
                + ");");

        stmt.addBatch("CREATE TABLE IF NOT EXISTS `user` (\n"
                + "  macaddress varchar(100) NOT NULL,\n"
                + "  name varchar(100) NOT NULL, \n"
                + "  password varchar(100) NOT NULL, \n"
                + "  email varchar(100) NOT NULL, \n"
                + "  gender char(1) NOT NULL, \n"
                + "  CONSTRAINT user_pk PRIMARY KEY (macaddress)\n"
                + ");");
        
        //int[] recordsAffected;
        //recordsAffected = stmt.executeBatch();
 
        //conn.commit();
           
        stmt.executeBatch();
        conn.commit();
        System.out.println("Batch executed");
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
