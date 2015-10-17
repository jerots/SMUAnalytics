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
import controller.AddBatchController;
import dao.AdminDAO;
import dao.UserDAO;
import entity.Admin;
import entity.User;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.Iterator;
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
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject output = new JsonObject();
            JsonArray arrayErr = new JsonArray();

            String token = request.getParameter("token");

            //TOKEN VALIDATION
            if (token == null) {
                arrayErr.add("missing token");
            } else if (token.length() == 0) {
                arrayErr.add("blank token");
            } else {
                try {
                    String username = JWTUtility.verify(token, "nabjemzhdarrensw");
                    if (username == null) {
                        //failed
                        arrayErr.add("invalid token");
                    } else {
                        AdminDAO adminDAO = new AdminDAO();
                        Admin admin = adminDAO.retrieve(username);
                        if (admin == null) {
                            arrayErr.add("invalid token");
                        }
                    }

                } catch (JWTException e) {
                    //failed
                    arrayErr.add("invalid token");
                }

            }

            Part filePart = request.getPart("zipFile"); // Retrieves <in addProperty type="file" name="zipFile">
            if (filePart != null && filePart.getSize() > 0 && arrayErr.size() <= 0) {
                //Create ERROR MAPS - and pass to boostrapController to generate
                TreeMap<Integer, String> userErrMap = new TreeMap<Integer, String>();
                TreeMap<Integer, String> auErrMap = new TreeMap<Integer, String>();
                TreeMap<Integer, String> luErrMap = new TreeMap<Integer, String>();
                TreeMap<Integer, String> delErrMap = new TreeMap<Integer, String>();

                TreeMap<String, Integer> recordMap = null;

                try {
                    AddBatchController ctrl = new AddBatchController();
                    recordMap = ctrl.addBatch(filePart, userErrMap, delErrMap, auErrMap, luErrMap);
                    //Returns success as the head of the JSON if it is a success.
                    boolean success = false;
                    if (userErrMap.isEmpty() && auErrMap.isEmpty() && luErrMap.isEmpty() && delErrMap.isEmpty()) {
                        success = true;
                        output.addProperty("status", "success");
                    } else {
                        output.addProperty("status", "error");
                    }

                    //Iterates through the main TreeMap to consolidate the number of rows that were updated.
                    Iterator<String> iter = recordMap.keySet().iterator();
                    JsonArray arr = new JsonArray();
                    JsonObject list = new JsonObject();
                    while (iter.hasNext()) {
                        String fileName = iter.next();
                        list.addProperty(fileName, recordMap.get(fileName));
                        arr.add(list);
                    }
                    output.add("num-record-uploaded", arr);

                    if (!success) { //This only occurs when there is an error in the upload

                        //Iterates through to find the unique row numbers that are affected. This is for UserDAO/demographics.csv
                        Iterator<Integer> iterInt = userErrMap.keySet().iterator();
                        arr = new JsonArray();
                        list = new JsonObject();
                        JsonArray errors = new JsonArray();
                        //Goes through the list to split all the error messages into a jsonarray
                        while (iterInt.hasNext()) {
                            int id = iterInt.next();
                            list.addProperty("file", "demographics.csv");
                            list.addProperty("line", id);
                            String[] messages = userErrMap.get(id).split(",");
                            for (String msg : messages) {
                                errors.add(msg);
                            }
                            list.add("message", arr);
                        }
                        arr.add(list);

                        //Iterates through to find the unique row numbers that are affected. This is for App-lookup.csv/AppUsageDAO
                        iterInt = auErrMap.keySet().iterator();
                        arr = new JsonArray();
                        list = new JsonObject();
                        errors = new JsonArray();
                        //Goes through the list to split all the error messages into a jsonarray
                        while (iterInt.hasNext()) {
                            int id = iterInt.next();
                            list.addProperty("file", "app.csv");
                            list.addProperty("line", id);
                            String[] messages = auErrMap.get(id).split(",");
                            for (String msg : messages) {
                                errors.add(msg);
                            }
                            list.add("message", arr);
                        }
                        arr.add(list);

                        //Iterates through to find the unique row numbers that are affected. This is for LocationUsageDAO/location.csv
                        iterInt = luErrMap.keySet().iterator();
                        arr = new JsonArray();
                        list = new JsonObject();
                        errors = new JsonArray();
                        //Goes through the list to split all the error messages into a jsonarray
                        while (iterInt.hasNext()) {
                            int id = iterInt.next();
                            list.addProperty("file", "location.csv");
                            list.addProperty("line", id);
                            String[] messages = luErrMap.get(id).split(",");
                            for (String msg : messages) {
                                errors.add(msg);
                            }
                            list.add("message", arr);
                        }
                        arr.add(list);

                        //Iterates through to find the unique row numbers that are affected. This is for UserDAO/demographics.csv
                        iterInt = delErrMap.keySet().iterator();
                        arr = new JsonArray();
                        list = new JsonObject();
                        errors = new JsonArray();
                        //Goes through the list to split all the error messages into a jsonarray
                        while (iterInt.hasNext()) {
                            int id = iterInt.next();
                            list.addProperty("file", "location-delete.csv");
                            list.addProperty("line", id);
                            String[] messages = delErrMap.get(id).split(",");
                            for (String msg : messages) {
                                errors.add(msg);
                            }
                            list.add("message", arr);
                        }
                        arr.add(list);

                        output.add("error", arr);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else {
                //For errors, will throw the errors back IMMEDIATELY without checking the files.
                output.addProperty("status", "error");
                output.add("errors", arrayErr);
                out.println(gson.toJson(output));
                return;
            }

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
