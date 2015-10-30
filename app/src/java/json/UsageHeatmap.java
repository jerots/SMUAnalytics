/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import controller.HeatmapController;
import entity.LocationUsage;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import dao.UserDAO;
import dao.Utility;
import entity.User;
import java.util.Collections;
import java.util.TreeMap;

/**
 *
 * @author jeremyongts92
 */
@WebServlet(name = "UsageHeatmap", urlPatterns = {"/json/usage-heatmap"})
public class UsageHeatmap extends HttpServlet {

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
            String date = request.getParameter("date");
            String time = request.getParameter("time");
            String floor = request.getParameter("floor");

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

            //FLOOR VALIDATION
            if (floor == null) {
                errors.add("missing floor");
            } else if (floor.length() == 0) {
                errors.add("blank floor");
            } else {
                try {
                    int floorInt = Integer.parseInt(floor);
                    if (floorInt < 0 || floorInt > 5) {
                        errors.add("invalid floor");
                    } else {
                        //parse floor to be controller-readable format e.g. B1, L1, L2...
                        if (floorInt == 0) {
                            floor = "B1";
                        } else {
                            floor = "L" + floor;
                        }
                    }
                } catch (NumberFormatException e) {
                    errors.add("invalid floor");
                }

            }

            //DATE VALIDATION
            if (date == null) {
                errors.add("missing date");
            } else if (date.length() == 0) {
                errors.add("blank date");
            } else {
                if (date.length() != 10) {
                    errors.add("invalid date");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
                    Date dateFormatted = sdf.parse(date, new ParsePosition(0));
                    if (dateFormatted == null) {
                        errors.add("invalid date");
                    }
                }
            }

            //TIME VALIDATION
            if (time == null) {
                errors.add("missing time");
            } else if (time.length() == 0) {
                errors.add("blank time");
            } else {
                if (time.length() != 8) {
                    errors.add("invalid time");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date dateFormatted = sdf.parse(time, new ParsePosition(0));
                    if (dateFormatted == null) {
                        errors.add("invalid time");
                    }
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date datetime = dateFormat.parse(date + " " + time, new ParsePosition(0));

            HeatmapController ctrl = new HeatmapController();
            TreeMap<String, ArrayList<LocationUsage>> luMap = ctrl.generateHeatmap(datetime, floor);

            output.addProperty("status", "success");
            JsonArray heatmap = new JsonArray();
            Iterator<String> iter = luMap.keySet().iterator();

            while (iter.hasNext()) {
                JsonObject currLoc = new JsonObject();

                String loc = iter.next();
                currLoc.addProperty("semanatic-place", loc);
                int numUsers = luMap.get(loc).size();
                currLoc.addProperty("num-people-using-phone", numUsers);

                int density = 0;
                if (numUsers == 0) {

                } else if (numUsers < 4) {
                    density = 1;
                } else if (numUsers < 8) {
                    density = 2;
                } else if (numUsers < 14) {
                    density = 3;
                } else if (numUsers < 21) {
                    density = 4;
                } else {
                    density = 5;
                }
                currLoc.addProperty("crowd-density", density);
                heatmap.add(currLoc);
            }

            output.add("heatmap", heatmap);
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
