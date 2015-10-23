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
import controller.SmartphoneOveruseController;
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
import java.util.Iterator;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ASUS-PC
 */
@WebServlet(name = "SmartphoneOveruseReport", urlPatterns = {"/json/overuse-report"})
public class SmartphoneOveruseReport extends HttpServlet {

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
            String startTimeStr = request.getParameter("startTime");
            String endDateStr = request.getParameter("endDate");
            String endTimeStr = request.getParameter("endTime");

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
                    if (dateFormatted == null || !Utility.checkDate(startDateStr)) {
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
                    if (dateFormatted == null || !Utility.checkDate(endDateStr)) {
                        errors.add("invalid enddate");
                    }
                }
            }

            //START TIME VALIDATION
            if (startTimeStr == null) {
                errors.add("missing time");
            } else if (startTimeStr.length() == 0) {
                errors.add("blank time");
            } else {
                if (startTimeStr.length() != 8) {
                    errors.add("invalid time");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date dateFormatted = sdf.parse(startTimeStr, new ParsePosition(0));
                    if (dateFormatted == null) {
                        errors.add("invalid time");
                    }
                }
            }

            //END TIME VALIDATION
            if (endTimeStr == null) {
                errors.add("missing time");
            } else if (endTimeStr.length() == 0) {
                errors.add("blank time");
            } else {
                if (endTimeStr.length() != 8) {
                    errors.add("invalid time");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date dateFormatted = sdf.parse(endTimeStr, new ParsePosition(0));
                    if (dateFormatted == null) {
                        errors.add("invalid time");
                    }
                }
            }

            //PRINT ERROR AND EXIT IF ERRORS EXIST
            if (errors.size() > 0) {
                output.addProperty("status", "error");
                output.add("errors", errors);
                out.println(gson.toJson(output));
                return;
            }

            //PASSES ALL VALIDATION, proceed to report generation
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startdatetime = dateFormat.parse(startDateStr + " " + startTimeStr, new ParsePosition(0));
            Date enddatetime = dateFormat.parse(endDateStr + " " + endTimeStr, new ParsePosition(0));
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            SmartphoneOveruseController ctrl = new SmartphoneOveruseController();         

            output.addProperty("status", "success");
            TreeMap<String, String> results = ctrl.generateReport(user, startdatetime, enddatetime);            
            JsonArray metrics = new JsonArray();
            Iterator<String> iter = results.keySet().iterator();

            while (iter.hasNext()) {
                JsonObject category = new JsonObject();

                String index = iter.next();
                if (index.equals("usage")) {
                    category.addProperty("usage-category", index);
                    String usage = results.get(index);
                    category.addProperty("usage-duration", usage);
                } else if (index.equals("gaming")) {
                    category.addProperty("gaming-category", index);
                    String duration = results.get(index);
                    category.addProperty("gaming-duration", duration);
                } else {
                    category.addProperty("accessfrequency-category", index);
                    String frequency = results.get(index);
                    category.addProperty("accessfrequency", frequency);
                }

                metrics.add(category);
            }

            output.add("metrics", metrics);
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
