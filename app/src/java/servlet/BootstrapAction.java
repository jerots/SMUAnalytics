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
    private static final String DB_URL = "jdbc:mysql://localhost/is203";
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
            throws ServletException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {

        Part filePart = request.getPart("zipFile"); // Retrieves <input type="file" name="zipFile">
        InputStream fileContent = filePart.getInputStream();
        ZipInputStream zipInputStream = new ZipInputStream(fileContent);

        ZipInputStream appInputStream = null;
        ZipInputStream locationInputStream = null;

        boolean appEntered = false;
        boolean locationEntered = false;

        //connection
        Class.forName(JDBC_DRIVER).newInstance();
        Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

        while (zipInputStream.available() == 1) {
            ZipEntry entry = zipInputStream.getNextEntry();
            String fileName = entry.getName();
            switch (fileName) {

                case "app.csv":
                    if (appEntered) {
                        AppUsageDAO auDao = new AppUsageDAO();
                        auDao.insert(zipInputStream);
                    } else {
                        appInputStream = zipInputStream;
                    }
                    break;

                case "app-lookup.csv":
                    appEntered = true;
                    AppDAO appDao = new AppDAO();
                    appDao.insert(zipInputStream);
                    break;

                case "demographics.csv":
                  //  UserDAO uDao = new UserDAO();
                    //uDao.insert(zipInputStream);
                    break;

                case "location-lookup.csv":
                   // locationEntered = true;
                   // LocationDAO lDao = new LocationDAO();
                   // lDao.insert(zipInputStream);
                    break;

                case "location.csv":
                    if (appEntered) {
                        LocationUsageDAO luDao = new LocationUsageDAO();
                        //luDao.insert(zipInputStream);
                    } else {
                        locationInputStream = zipInputStream;
                    }
                    break;
            }

            if (appInputStream != null) {
               // AppUsageDAO auDao = new AppUsageDAO();
                //auDao.insert(appInputStream);
            }

            if (locationInputStream != null) {
               // LocationUsageDAO luDao = new LocationUsageDAO();
                //luDao.insert(locationInputStream);
            }
        }
    }

    public void createTable(Connection conn) throws SQLException {

        Statement stmt = conn.createStatement();
        conn.setAutoCommit(false);

        String appLookUpSQL = "CREATE TABLE IF NOT EXISTS app (\n"
                + "  app-id int(8) NOT NULL,\n"
                + "  app-name varchar(128) NOT NULL, \n"
                + "  app-category varchar(30) NOT NULL, \n"
                + "   CONSTRAINT app_pk PRIMARY KEY (app-id)\n"
                + "   \n"
                + ");";
        stmt.addBatch(appLookUpSQL);

        String appUsageSQL = "CREATE TABLE IF NOT EXISTS appUsage (\n"
                + "  timestamp date NOT NULL,\n"
                + "  mac-address varchar(128) NOT NULL, \n"
                + "  app-id int(8) NOT NULL  , \n"
                + "   CONSTRAINT AppUsageID_pk PRIMARY KEY (timestamp,mac-address)\n"
                + "   CONSTRAINT AppUsageID_fk1 FOREIGN KEY (mac-address) REFERENCES user(mac-address)\n"
                + "   CONSTRAINT AppUsageID_fk2 FOREIGN KEY (app-id) REFERENCES app(app-id) \n"
                + ");";
        stmt.addBatch(appUsageSQL);

        String locationLookUpSQL = "CREATE TABLE IF NOT EXISTS location (\n"
                + "  location-id varchar(100) NOT NULL PRIMARY KEY,\n"
                + "  semantic-place varchar(100) NOT NULL, \n"
                + "  CONSTRAINT location_pk PRIMARY KEY (location-id)\n"
                + ");";
        stmt.addBatch(locationLookUpSQL);

        String locationUsageSQL = "CREATE TABLE IF NOT EXISTS locationUsage (\n"
                + "  timestamp date NOT NULL,\n"
                + "  mac-address varchar(100), \n"
                + "  location-id varchar(100) NOT NULL, \n"
                + "  CONSTRAINT locationUsage_pk PRIMARY KEY (`timestamp`,`location-id`)\n"
                + "   CONSTRAINT locationUsage_fk1 FOREIGN KEY (mac-address) REFERENCES user(mac-address)\n"
                + "   CONSTRAINT locationUsage_fk2 FOREIGN KEY (location-id) REFERENCES location(location-id) \n"
                + ");";
        stmt.addBatch(locationUsageSQL);

        String userSQL = "CREATE TABLE IF NOT EXISTS `user` (\n"
                + "  mac-address varchar(100) NOT NULL,\n"
                + "  name varchar(100) NOT NULL, \n"
                + "  password varchar(100) NOT NULL, \n"
                + "  email varchar(100) NOT NULL, \n"
                + "  gender varchar(100) NOT NULL, \n"
                + "  CONSTRAINT user_pk PRIMARY KEY (mac-address)\n"
                + ");";

        stmt.addBatch(userSQL);

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
