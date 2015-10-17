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
import controller.BasicAppController;
import dao.UserDAO;
import entity.Breakdown;
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
import java.util.TreeMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jeremyongts92
 */
@WebServlet(name = "BasicUsetimeDemo", urlPatterns = {"/json/basic-usetime-demographics-report"})
public class BasicUsetimeDemo extends HttpServlet {

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
            String order = request.getParameter("order");

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
            if (startdate == null) {
                errors.add("missing startdate");
            } else if (startdate.length() == 0) {
                errors.add("blank startdate");
            } else {
                if (startdate.length() != 10) {
                    errors.add("invalid startdate");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
                    Date dateFormatted = sdf.parse(startdate, new ParsePosition(0));
                    if (dateFormatted == null) {
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
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
                    Date dateFormatted = sdf.parse(enddate, new ParsePosition(0));
                    if (dateFormatted == null) {
                        errors.add("invalid enddate");
                    }
                }
            }

            //ORDER VALIDATION
            if (order == null) {
                errors.add("missing order");
            } else if (order.length() == 0) {
                errors.add("blank order");
            } else {

                String[] orderArr = order.split(",");

                if (orderArr.length > 3) {
                    //INVALID
                    errors.add("invalid order");
                } else {

                    ArrayList<String> toCheck = new ArrayList<String>();
                    toCheck.add("year");
                    toCheck.add("gender");
                    toCheck.add("school");

                    for (String s : orderArr) {
                        if (!toCheck.contains(s)) {
                            //INVALID
                            errors.add("invalid order");
                        } else {
                            toCheck.remove(s);
                        }
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
            output.addProperty("status", "success");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = dateFormat.parse(startdate + " " + "00:00:00", new ParsePosition(0));
            Date endDate = dateFormat.parse(enddate + " " + "23:59:59", new ParsePosition(0));
            String[] validArr = order.split(",");
//
            BasicAppController ctrl = new BasicAppController();
            Breakdown breakdown = null;
            try {
//            breakdown = ctrl.generateReportByDemo(startDate, endDate, validArr);
                breakdown = ctrl.generateReportByDemo(startDate, endDate, validArr);

            } catch (Exception e) {
                e.printStackTrace();
            }
            output.addProperty("status", "success");
            JsonArray arr = new JsonArray();
            output.add("breakdown", arr);

            ArrayList<HashMap<String, Breakdown>> breakdownList = breakdown.getBreakdown();

            generateJson(arr, breakdownList);

            out.println(gson.toJson(output));

        }
    }

    public void generateJson(JsonArray arr, ArrayList<HashMap<String, Breakdown>> breakdownList) {

        for (HashMap<String, Breakdown> map : breakdownList) {
            JsonObject obj = new JsonObject();
            if (map.get("gender") != null) {
                obj.addProperty("gender", map.get("gender").getMessage());
            } else if (map.get("year") != null) {
                obj.addProperty("year", map.get("year").getMessage());
            } else if (map.get("school") != null) {
                obj.addProperty("school", map.get("school").getMessage());
            }

            Breakdown count = map.get("count");
            if (count != null) {
                obj.addProperty("count", count.getMessage());

            }

            Breakdown percent = map.get("percent");
            if (percent != null) {
                obj.addProperty("percent", percent.getMessage());
            }

            Breakdown further = map.get("breakdown");
            if (further != null) {
                JsonArray newArr = new JsonArray();
                obj.add("breakdown", newArr);
                generateJson(newArr, further.getBreakdown());
            } else {
                Breakdown intenseCount = map.get("intense-count");
                Breakdown intensePercent = map.get("intense-percent");
                Breakdown normalCount = map.get("normal-count");
                Breakdown normalPercent = map.get("normal-percent");
                Breakdown mildCount = map.get("mild-count");
                Breakdown mildPercent = map.get("mild-percent");

                if (intenseCount != null) {
                    obj.addProperty("intense-count", intenseCount.getMessage());
                    obj.addProperty("intense-percent", intensePercent.getMessage());
                }
                if (normalCount != null) {
                    obj.addProperty("normal-count", normalCount.getMessage());
                    obj.addProperty("normal-percent", normalPercent.getMessage());
                }
                if (mildCount != null) {
                    obj.addProperty("mild-count", mildCount.getMessage());
                    obj.addProperty("mild-percent", mildPercent.getMessage());
                }
            }

            arr.add(obj);

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
