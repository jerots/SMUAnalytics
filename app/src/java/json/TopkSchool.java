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
import controller.HeatmapController;
import controller.TopkController;
import dao.UserDAO;
import dao.Utility;
import entity.LocationUsage;
import entity.User;
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
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ASUS-PC
 */
@WebServlet(name = "TopkSchool", urlPatterns = {"/json/top-k-most-used-schools"})
public class TopkSchool extends HttpServlet {

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
            /* TODO output your page here. You may use following sample code. */

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject output = new JsonObject();
            JsonArray errors = new JsonArray();

            String token = request.getParameter("token");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String school = request.getParameter("school");
            String k = request.getParameter("k");

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
            //START DATE VALIDATION
            if (startDateStr == null) {
                errors.add("missing startdate");
            } else if (startDateStr.length() == 0) {
                errors.add("blank startdate");
            } else {
                if (startDateStr.length() != 10) {
                    errors.add("invalid startdate");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
                    Date dateFormatted = sdf.parse(startDateStr, new ParsePosition(0));
                    if (dateFormatted == null) {
                        errors.add("invalid startdate");
                    }
                }
            }

            //END DATE VALIDATION
            if (endDateStr == null) {
                errors.add("missing enddate");
            } else if (endDateStr.length() == 0) {
                errors.add("blank enddate");
            } else {
                if (endDateStr.length() != 10) {
                    errors.add("invalid enddate");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
                    Date dateFormatted = sdf.parse(endDateStr, new ParsePosition(0));
                    if (dateFormatted == null) {
                        errors.add("invalid enddate");
                    }
                }
            }

            //School Validation
            if (school == null) {
                errors.add("missing school");
            } else if (school.length() == 0) {
                errors.add("blank school");
            } else {
                ArrayList<String> schoolList = Utility.getSchoolist();
                if (!schoolList.contains(school)) {
                    errors.add("invalid school");
                }
            }

            //validate K
            if (k == null) {
                errors.add("missing k");
            } else if (k.length() == 0) {
                errors.add("blank k");
            } else {
                try {
                    int kNum = Integer.parseInt(k);
                    if (kNum < 0 || kNum > 10) {
                        errors.add("invalid k");
                    }
                } catch (NumberFormatException e) {
                    errors.add("invalid k");
                }
            }

            //PRINT ERROR AND EXIT IF ERRORS EXIST
            if (errors.size() > 0) {
                output.addProperty("status", "error");
                output.add("errors", errors);
                out.println(gson.toJson(errors));
                return;
            }

            //PASSES ALL VALIDATION, proceed to report generation
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            TopkController ctrl = new TopkController();
            ArrayList<HashMap<String, String>> uList = ctrl.getTopkSchool(k, school, startDateStr, endDateStr);

            output.addProperty("status", "success");
            JsonArray results = new JsonArray();

            for (HashMap<String, String> kDetails : uList) {
                Iterator<String> iter = kDetails.keySet().iterator();
                while (iter.hasNext()) {
                    JsonObject currK = new JsonObject();
                    String key = iter.next();
                    if(key.equals("rank")){
                        currK.addProperty("rank", key);
                    }else if(key.equals("school")){
                        currK.addProperty("school", key);
                    }else{
                        currK.addProperty("duration", key);
                    }

                    results.add(currK);
                }
            }

            output.add("results", results);
            out.println(gson.toJson(output));

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
