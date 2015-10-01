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
            ZipInputStream zipInputStream = null;
            //connection
            System.out.println("Starthere!");
            Connection conn = ConnectionManager.getConnection();
            System.out.println("STOP!");

            AppDAO appDao = new AppDAO();
            AppUsageDAO auDao = new AppUsageDAO();
            LocationUsageDAO luDao = new LocationUsageDAO();
            UserDAO uDao = new UserDAO();
            LocationDAO lDao = new LocationDAO();

            /*while (zipInputStream.available() == 1) {
             ZipEntry entry = zipInputStream.getNextEntry();
             String fileName = entry.getName();
             if (fileName.equals("app-lookup.csv")){
             appDao.insert(zipInputStream, conn);
             }
             zipInputStream.closeEntry();
             }
             zipInputStream.close();*/
            
            zipInputStream = new ZipInputStream(fileContent);
            ZipEntry entry = null;
            try{
                while ((entry = zipInputStream.getNextEntry())!=null) {
                    System.out.println("hww");
                    String fileName = entry.getName();
                    System.out.println(fileName);
                    if (fileName.equals("demographics.csv")) {
                        uDao.insert(zipInputStream, conn);
                    }else{
                        zipInputStream.closeEntry();
                        System.out.println("haha");
                    }
                }
            }catch (IOException e){
                
            }

            zipInputStream = new ZipInputStream(fileContent);
            entry = null;
            while ((entry = zipInputStream.getNextEntry())!=null) {
                String fileName = entry.getName();
                if (fileName.equals("app.csv")) {
                    auDao.insert(appDao, uDao, zipInputStream, conn);

                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();

            zipInputStream = new ZipInputStream(fileContent);
            entry = null;
            
            while ((entry = zipInputStream.getNextEntry())!=null) {
                String fileName = entry.getName();
                if (fileName.equals("location-lookup.csv")) {
                    lDao.insert(zipInputStream, conn);

                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();

            zipInputStream = new ZipInputStream(fileContent);
            entry = null;
            while ((entry = zipInputStream.getNextEntry())!=null) {
                String fileName = entry.getName();
                if (fileName.equals("location.csv")) {
                    luDao.insert(lDao, uDao, zipInputStream, conn);

                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("Exception Caught in bootstrap action.java");
            e.printStackTrace();
        }
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
