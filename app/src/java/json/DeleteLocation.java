/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.DeleteController;
import dao.AdminDAO;
import entity.Admin;
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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Boyofthefuture
 */
@WebServlet(name = "DeleteLocation", urlPatterns = {"/json/location-delete"})
public class DeleteLocation extends HttpServlet {

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
            /* TODO output your page here. You may use following sample code. */
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject output = new JsonObject();
            JsonArray errorArr = new JsonArray();

            String token = request.getParameter("token");
            String startDate = request.getParameter("startdate");
            String endDate = request.getParameter("enddate");
            String startTime = request.getParameter("starttime");
            String endTime = request.getParameter("endtime");
            String locationId = request.getParameter("locationid");
            String semanticPl = request.getParameter("semanticplace");
            String macAdd = request.getParameter("macaddress");
			
			if (semanticPl != null){
				semanticPl = semanticPl.toUpperCase();
			}
			if (macAdd != null){
				macAdd = macAdd.toLowerCase();
			}
			
            //TOKEN VALIDATION
            if (token == null) {
                errorArr.add("missing token");
            } else if (token.length() == 0) {
                errorArr.add("blank token");
            } else {
                try {
                    String username = JWTUtility.verify(token.trim(), "nabjemzhdarrensw");
                    if (username == null) {
                        //failed
                        errorArr.add("invalid token");
                    } else {
                        AdminDAO adminDAO = new AdminDAO();
                        Admin admin = adminDAO.retrieve(username);
                        if (admin == null) {
                            errorArr.add("invalid token");
                        }
                    }

                } catch (JWTException e) {
                    //failed
                    errorArr.add("invalid token");
                }

            }			
			
            //ALL VALIDATIONS ARE DONE BY THE CONTROLLER
            DeleteController dCntrl = new DeleteController();
            ArrayList<String> error = new ArrayList<String>();
            ArrayList<LocationUsage> lList = null;
            try {

                lList = dCntrl.delete(macAdd, startDate, endDate, startTime, endTime, locationId, semanticPl, error);
            } catch (SQLException e) {
            }

            if (error != null && error.size() != 0) {
                String[] errorsStr = error.get(0).split(",\\s");
                for (String str : errorsStr) {
                    errorArr.add(str);
                }
            }

            //PRINT ERROR AND EXIT IF ERRORS EXIST
            if (errorArr.size() > 0) {
                output.addProperty("status", "error");
                output.add("messages", errorArr);
                out.println(gson.toJson(output));
                return;
            }

            if (lList != null && lList.size() != 0) {
                output.addProperty("status", "success");
                output.addProperty("num-record-deleted", lList.size());
                JsonArray arr = new JsonArray();
                for (LocationUsage lu : lList) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("location-id", lu.getLocation().getLocationId());
                    obj.addProperty("mac-address", lu.getMacAddress());
                    obj.addProperty("timestamp", lu.getTimestamp());
                    arr.add(obj);
                }
                output.add("rows-deleted", arr);
                out.println(gson.toJson(output));
            } else {
                output.addProperty("status", "success");
                output.addProperty("num-records-deleted", 0);
                JsonArray arr = new JsonArray();
                output.add("rows-deleted", arr);
                out.println(gson.toJson(output));
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
