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
import controller.TopKController;
import dao.Utility;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Boyofthefuture
 */
@WebServlet(name = "TopK", urlPatterns = {"/json/top-k-most-used-apps"})
public class TopK extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
           Gson gson = new GsonBuilder().setPrettyPrinting().create();
           JsonObject output = new JsonObject();
           JsonArray errors = new JsonArray();
           
            String token = request.getParameter("token");
            String startdate = request.getParameter("startdate");
            String enddate = request.getParameter("enddate");

            //TOKEN VALIDATION
            if (token == null) {
                errors.add("missing token");
            } else if (token.length() == 0) {
                errors.add("blank token");
            } else {
                try {
                    String username = JWTUtility.verify(token, "nabjemzhdarrensw");
                    if (username == null) {
                        //failed
                        errors.add("invalid token");
                    }

                } catch (JWTException e) {
                    //failed
                    errors.add("invalid token");
                }

            }
            
            //Gets the number of (top) K that the individual wants displayed
            String entry = request.getParameter("entries");
            
            //This is the choice selection of which of the 3 option the user wants to be processed.
            String selection = request.getParameter("category");
            //Gets the start and end dates as necessary.
            String startDate = request.getParameter("startdate");
            String endDate = request.getParameter("endDate");
            output.addProperty("status", "success");
            
            //START DATE VALIDATION
            if (startdate == null) {
                errors.add("missing startdate");
            } else if (startdate.length() == 0) {
                errors.add("blank startdate");
            } else {
                if (startdate.length() != 10) {
                    errors.add("invalid startdate");
                } else {
                    Date dateFormatted = Utility.parseOnlyDate(startdate);
                    if (dateFormatted == null || Utility.checkOnlyDate(startdate)) {
                        errors.add("invalid startdate");
                    }
                }
            }

            //END DATE VALIDATION
            if (enddate == null) {
                errors.add("missing enddate");
            } else if (enddate.length() == 0) {
                errors.add("blank enddate");
            } else {
                if (enddate.length() != 10) {
                    errors.add("invalid enddate");
                } else {
                    Date dateFormatted = Utility.parseOnlyDate(startdate);
                    if (dateFormatted == null || Utility.checkOnlyDate(enddate)) {
                        errors.add("invalid enddate");
                    }
                }
            }
            //NOTE: SINCE IT IS A DROPDOWN, CATEGORY AND SCHOOL CAN NEVER BE WRONG. EVEN K. But we will check as well.
            //All the values are from the same select place. It only changes based on the report selected
            String selected = request.getParameter("choices");
            //Checks school/appcategory (Actually this is chosen)
            if(selection.equals("schoolapps")){
                if(!Utility.checkSchools(selected)){
                    errors.add("invalid school");
                }
            }else{
                if(!Utility.checkCategory(selected)){
                    errors.add("invalid app category");
                }
            }
            
            
            int topK = Utility.parseInt(entry);
            if(topK > 10 || topK < 1){
                errors.add("invalid k");
            }
            
            //PRINT ERROR AND EXIT IF ERRORS EXIST
            if (errors.size() > 0) {
                output.addProperty("status", "error");
                output.add("errors", errors);
                out.println(gson.toJson(output));
                return;
            }
            
            TopKController ctrl = new TopKController();
            
            //This error string is just passed in, but is meant for the UI and not the JSON.
            String error = "";

            //This parameter is only for the school function
            ArrayList<HashMap<String, String>>  catValues = ctrl.getTopkSchool(topK, selected, startDate, endDate, error);
            
            if(catValues != null){
                Iterator<HashMap<String, String>> iter = catValues.iterator();
                JsonArray param = new JsonArray();
                while(iter.hasNext()){
                    HashMap<String, String> map = iter.next();
                    Iterator<String> iterKey = map.keySet().iterator();
                    Iterator<String> iterValue = map.values().iterator();
                    JsonObject indiv = new JsonObject();
                    while(iterKey.hasNext()){
                        indiv.addProperty(iterKey.next(), iterValue.next());
                    }
                     param.add(indiv);
                }
                output.add("results", param);
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
