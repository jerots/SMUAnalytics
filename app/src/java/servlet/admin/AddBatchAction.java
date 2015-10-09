/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.admin;

import com.opencsv.CSVReader;
import controller.AddBatchController;
import dao.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.RequestDispatcher;
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
@WebServlet(name = "AddBatchAction", urlPatterns = {"/AddBatchAction"})
public class AddBatchAction extends HttpServlet {

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
        HashMap<String, Integer> recordMap = null;
        try {
            Part filePart = request.getPart("zipFile"); 
            if(filePart != null && filePart.getSize() > 0){
                HashMap<Integer, String> userErrMap = new HashMap<Integer, String>();
                HashMap<Integer, String> auErrMap = new HashMap<Integer, String>();
                HashMap<Integer, String> luErrMap = new HashMap<Integer, String>();
                HashMap<Integer, String> delErrMap = new HashMap<Integer, String>();

                request.setAttribute("userErrMap", userErrMap);
                request.setAttribute("auErrMap", auErrMap);
                request.setAttribute("luErrMap", luErrMap);
                request.setAttribute("delErrMap", delErrMap);

                AddBatchController cntrl = new AddBatchController();
                recordMap = cntrl.addBatch(filePart, userErrMap, delErrMap, auErrMap, luErrMap);
            }
            request.setAttribute("recordMap", recordMap);
            RequestDispatcher rd = request.getRequestDispatcher("/admin/home.jsp");
            rd.forward(request, response);
            
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
