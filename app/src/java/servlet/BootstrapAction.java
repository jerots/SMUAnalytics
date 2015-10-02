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
        try {
            Part filePart = request.getPart("zipFile"); // Retrieves <input type="file" name="zipFile">
            InputStream fileContent = filePart.getInputStream();
            ZipInputStream zipInputStream = new ZipInputStream(fileContent);
            //connection

            AppDAO appDao = new AppDAO();
            AppUsageDAO auDao = new AppUsageDAO();
            LocationUsageDAO luDao = new LocationUsageDAO();
            UserDAO uDao = new UserDAO();
            LocationDAO lDao = new LocationDAO();

            ZipEntry entry = null;
            try{
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    if (fileName.equals("app-lookup.csv")) {
                        appDao.insert(zipInputStream);
                    } else {
                        zipInputStream.closeEntry();
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }catch(BatchUpdateException e){
                e.printStackTrace();
                zipInputStream.close();
            }

            fileContent = filePart.getInputStream();
            zipInputStream = new ZipInputStream(fileContent);
            entry = null;
            try{
                while ((entry = zipInputStream.getNextEntry())!=null) {
                    String fileName = entry.getName();
                    if (fileName.equals("demographics.csv")) {
                        uDao.insert(zipInputStream);
                    }else{
                        zipInputStream.closeEntry();
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch(BatchUpdateException e){
                e.printStackTrace();
                zipInputStream.close();
            }
            
            fileContent = filePart.getInputStream();
            zipInputStream = new ZipInputStream(fileContent);
            entry = null;
            try{
                while ((entry = zipInputStream.getNextEntry())!=null) {
                    String fileName = entry.getName();
                    if (fileName.equals("app.csv")) {
                        auDao.insert(appDao, uDao, zipInputStream);
                    }else{
                        zipInputStream.closeEntry();
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch(BatchUpdateException e){
                e.printStackTrace();
                zipInputStream.close();
            }

            fileContent = filePart.getInputStream();
            zipInputStream = new ZipInputStream(fileContent);
            entry = null;
            try{
                while ((entry = zipInputStream.getNextEntry())!=null) {
                    String fileName = entry.getName();
                    if (fileName.equals("location-lookup.csv")) {
                        lDao.insert(zipInputStream);
                    }else{
                        zipInputStream.closeEntry();
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch(BatchUpdateException e){
                e.printStackTrace();
                zipInputStream.close();
            }
            
            fileContent = filePart.getInputStream();
            zipInputStream = new ZipInputStream(fileContent);
            entry = null;
            try{
                while ((entry = zipInputStream.getNextEntry())!=null) {
                    String fileName = entry.getName();
                    if (fileName.equals("location.csv")) {
                        luDao.insert(lDao, uDao, zipInputStream);
                    }else{
                        zipInputStream.closeEntry();
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch(BatchUpdateException e){
                e.printStackTrace();
                zipInputStream.close();
            }
        }catch (Exception e) { 
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
