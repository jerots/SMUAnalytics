/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import com.opencsv.CSVReader;
import dao.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author Boyofthefuture
 */
@WebServlet(name = "AddBatch", urlPatterns = {"/AddBatch"})
public class AddBatch extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try {
            Part filePart = request.getPart("zipFile");
            InputStream fileContent = filePart.getInputStream();
            ZipEntry entry = null;

            CSVReader reader = null;        
            
            ZipInputStream zipInputStream = new ZipInputStream(fileContent);
            InputStreamReader isr = new InputStreamReader(zipInputStream);
            BufferedReader br = new BufferedReader(isr);
            try {
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    if (fileName.equals("app-lookup.csv")) {
                        AppUsageDAO appUDao = new AppUsageDAO();
                        reader = new CSVReader(br);
                        reader.readNext();
                        appUDao.add(reader);
                    }
                    if (fileName.equals("location-lookup.csv")) {
                        LocationUsageDAO luDao = new LocationUsageDAO();
                        reader = new CSVReader(br);
                        reader.readNext();
                        luDao.add(reader);
                    }
                    if (fileName.equals("demographics.csv")) {
                        UserDAO uDao = new UserDAO();
                        reader = new CSVReader(br);
                        reader.readNext();
                        uDao.insert(reader);
                    }
                    if (fileName.equals("location-delete.csv")) {
                        LocationUsageDAO luDao = new LocationUsageDAO();
                        reader = new CSVReader(br);
                        reader.readNext();
                        luDao.add(reader);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            response.sendRedirect("admin/home.jsp");
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
