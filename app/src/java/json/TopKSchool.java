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
import controller.TopkReportController;
import dao.UserDAO;
import dao.Utility;
import entity.User;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "TopKSchool", urlPatterns = {"/json/top-k-most-used-schools"})
public class TopKSchool extends HttpServlet {

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
                    } else {
                        UserDAO userDAO = new UserDAO();
                        User user = userDAO.retrieve(username);
                        if (user == null) {
                            errors.add("invalid token");
                        }
                    }
                } catch (JWTException e) {
                    //failed
                    errors.add("invalid token");
                }

            }
            //Gets the number of (top) K that the individual wants displayed
            String entry = request.getParameter("k");
            if (entry == null || entry.length() == 0) {
                entry = "3";
            }
            int entryInt = Utility.parseInt(entry);
            if (entryInt < 1 || entryInt > 10) {
                errors.add("invalid k");
            }
            

            //START DATE VALIDATION
            Date dateFormattedStart = null;
            if (startdate == null) {
                errors.add("missing startdate");
            } else if (startdate.length() == 0) {
                errors.add("blank startdate");
            } else {
                if (startdate.length() != 10) {
                    errors.add("invalid startdate");
                } else {
                    dateFormattedStart = Utility.parseDate(startdate + " 00:00:00");
                    if (dateFormattedStart == null) {
                        errors.add("invalid startdate");
                    }
                }
            }

            //END DATE VALIDATION
            Date dateFormattedEnd = null;
            if (enddate == null) {
                errors.add("missing enddate");
            } else if (enddate.length() == 0) {
                errors.add("blank enddate");
            } else {
                if (enddate.length() != 10) {
                    errors.add("invalid enddate");
                } else {
                    dateFormattedEnd = Utility.parseDate(enddate + " 23:59:59");
                    if (dateFormattedEnd == null) {
                        errors.add("invalid enddate");
                    }
                }
            }
            if (dateFormattedStart != null && dateFormattedEnd != null && dateFormattedStart.after(dateFormattedEnd)) {
                errors.add("invalid startdate");
            }
			//NOTE: SINCE IT IS A DROPDOWN, CATEGORY AND SCHOOL CAN NEVER BE WRONG. EVEN K. But we will check as well.
            //All the values are from the same select place. It only changes based on the report selected

            String selected = request.getParameter("appcategory");
            //Checks school/appcategory (Actually this is chosen)
            if (selected == null) {
                errors.add("missing appcategory");
            } else if (selected.length() == 0) {
                errors.add("blank appcategory");
            } else if (!Utility.checkCategory(selected)) {
                errors.add("invalid appcategory");
            }

            //PRINT ERROR AND EXIT IF ERRORS EXIST
            if (errors.size() > 0) {
                output.addProperty("status", "error");
                output.add("errors", errors);
                out.println(gson.toJson(output));
                return;
            }

            TopkReportController ctrl = new TopkReportController();

            //This error string is just passed in, but is meant for the UI and not the JSON.
            String error = "";

            //IF PASSED VALIDATION, PROCEED TO GENERATE REPORT
            output.addProperty("status", "success");

            //This parameter is only for those who select App Category and return School
            ArrayList<HashMap<String, String>> catValues = ctrl.getTopkSchool(entryInt, selected, dateFormattedStart, dateFormattedEnd, error);

            if (catValues != null) {
                Iterator<HashMap<String, String>> iter = catValues.iterator();
                JsonArray param = new JsonArray();
                while (iter.hasNext()) {
                    HashMap<String, String> map = iter.next();
                    JsonObject indiv = new JsonObject();
                    param.add(indiv);

                    indiv.addProperty("rank", map.get("rank"));
                    indiv.addProperty("school", map.get("school"));
                    indiv.addProperty("duration", map.get("duration"));
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
