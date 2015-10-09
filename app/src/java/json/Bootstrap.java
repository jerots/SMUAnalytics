/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import controller.BootstrapController;
import controller.HeatmapController;
import dao.InitDAO;
import entity.LocationUsage;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 *
 * @author Boyofthefuture
 */
@WebServlet(name = "Bootstrap", urlPatterns = {"/Bootstrap"})
public class Bootstrap extends HttpServlet {

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
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            JSONObject output = new JSONObject();
            JSONArray errors = new JSONArray();
            
            String token = request.getParameter("token");
            String date = request.getParameter("date");
            String time = request.getParameter("time");
            String floor = request.getParameter("floor");
            
            try {
                String username = JWTUtility.verify(token, "nabjemzhdarrensw");
                if (username == null) {
                    //failed
                }
            } catch (JWTException e) {
                //failed
                e.printStackTrace();
            }

            try {
                InitDAO.createTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            HashMap<String, Integer> recordMap = null;
            String option = request.getParameter("option");
            Part filePart = request.getPart("zipFile"); // Retrieves <input type="file" name="zipFile">
            if(filePart != null && filePart.getSize() > 0){

                //Create ERROR MAPS - and pass to boostrapController to generate
                HashMap<Integer, String> userErrMap = new HashMap<Integer, String>();
                HashMap<Integer, String> appErrMap = new HashMap<Integer, String>();
                HashMap<Integer, String> locErrMap = new HashMap<Integer, String>();
                HashMap<Integer, String> auErrMap = new HashMap<Integer, String>();
                HashMap<Integer, String> luErrMap = new HashMap<Integer, String>();
                HashMap<Integer, String> delErrMap = new HashMap<Integer, String>();

                //SET FOR UI TO GET THE ATTRIBUTES.
                request.setAttribute("userErrMap", userErrMap);
                request.setAttribute("appErrMap", appErrMap);
                request.setAttribute("locErrMap", locErrMap);
                request.setAttribute("auErrMap", auErrMap);
                request.setAttribute("luErrMap", luErrMap);
                request.setAttribute("delErrMap", delErrMap);
                
                try{
                    BootstrapController ctrl = new BootstrapController();
                    recordMap = ctrl.bootstrap(filePart, userErrMap, appErrMap, locErrMap, auErrMap, luErrMap, delErrMap);
                }catch(SQLException e){
                    
                }
            }
            request.setAttribute("recordMap", recordMap);

            RequestDispatcher rd = request.getRequestDispatcher("/admin/home.jsp");
            rd.forward(request, response);

        } 
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
