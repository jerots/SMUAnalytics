package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.AddBatchController;
import dao.AdminDAO;
import entity.Admin;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "AddBatch", urlPatterns = {"/json/update"})
@MultipartConfig
public class AddBatch extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject output = new JsonObject();
            JsonArray errors = new JsonArray();

            InputStream fis = null;
            String token = request.getParameter("token");
            Part filePart = null;
            try {
                filePart = request.getPart("addbatch-file");

            } catch (Exception e) {
                e.printStackTrace();
            }

            //TOKEN VALIDATION
            if (token == null) {
                errors.add("missing token");
            } else if (token.length() == 0) {
                errors.add("blank token");
            } else {
                try {
                    String username = JWTUtility.verify(token.trim(), "nabjemzhdarrensw");
                    if (username == null) {
                        //failed
                        errors.add("invalid token");
                    } else {
                        AdminDAO adminDAO = new AdminDAO();
                        Admin admin = adminDAO.retrieve(username);
                        if (admin == null) {
                            errors.add("invalid token");
                        }
                    }

                } catch (JWTException e) {
                    //failed
                    errors.add("invalid token");
                }

                //IF bootstrap-file field not found
                if (filePart == null && filePart.getSize() < 0) {
                    errors.add("missing file");
                }

                //If not zip file
            }

            if (errors.size() > 0) {
                output.addProperty("status", "error");
                output.add("errors", errors);
                out.println(gson.toJson(output));
                return;
            }

            if (filePart != null && filePart.getSize() > 0 && errors.size() <= 0) {
                //Create ERROR MAPS - and pass to boostrapController to generate
                TreeMap<Integer, String> userErrMap = new TreeMap<Integer, String>();
                TreeMap<Integer, String> auErrMap = new TreeMap<Integer, String>();
                TreeMap<Integer, String> luErrMap = new TreeMap<Integer, String>();

                TreeMap<String, Integer> recordMap = null;

                try {
                    AddBatchController ctrl = new AddBatchController();
                    recordMap = ctrl.addBatch(filePart, userErrMap, auErrMap, luErrMap);
                    //Returns success as the head of the JSON if it is a success.
                    boolean success = false;
                    if (userErrMap.isEmpty() && auErrMap.isEmpty() && luErrMap.isEmpty()) {
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
                    }
                    arr.add(list);
                    output.add("num-record-uploaded", arr);
                    if(recordMap.get("location-delete.csv") >= 0){
                        output.addProperty("num-record-deleted", recordMap.get("location-delete.csv"));
                        output.addProperty("num-record-not-found", recordMap.get("deletenotfound"));
                    }
                    if (!success) { //This only occurs when there is an error in the upload
                        if(userErrMap != null && userErrMap.size() != 0 ){
                            //Iterates through to find the unique row numbers that are affected. This is for UserDAO/demographics.csv
                            Iterator<Integer> iterInt = userErrMap.keySet().iterator();
                            arr = new JsonArray();
                            list = new JsonObject();
                            errors = new JsonArray();
                            //Goes through the list to split all the error messages into a jsonarray
                            while (iterInt.hasNext()) {
                                int id = iterInt.next();
                                list.addProperty("file", "demographics.csv");
                                list.addProperty("line", id);
                                String[] messages = userErrMap.get(id).split(",");
                                for (String msg : messages) {
                                    errors.add(msg);
                                }
                                list.add("message", errors);
                                errors = new JsonArray();
                                arr.add(list);
                                list = new JsonObject();
                            }
                        }
                        
                        if(auErrMap != null && auErrMap.size() != 0){
                            //Iterates through to find the unique row numbers that are affected. This is for App-lookup.csv/AppUsageDAO
                            Iterator<Integer> iterInt = auErrMap.keySet().iterator();
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
                                list.add("message", errors);
                                errors = new JsonArray();
                                arr.add(list);
                                list = new JsonObject();
                            }
                        }
                        if(luErrMap != null && luErrMap.size() != 0){
                            //Iterates through to find the unique row numbers that are affected. This is for LocationUsageDAO/location.csv
                            Iterator<Integer> iterInt = luErrMap.keySet().iterator();
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
                                list.add("message", errors);
                                errors = new JsonArray();
                                arr.add(list);
                                list = new JsonObject();
                            }
                            
                        }
                        
//                        if(delErrMap != null && delErrMap.size() != 0){
//                            //Iterates through to find the unique row numbers that are affected. This is for UserDAO/demographics.csv
//                            Iterator<Integer> iterInt = delErrMap.keySet().iterator();
//                            arr = new JsonArray();
//                            list = new JsonObject();
//                            errors = new JsonArray();
//                            //Goes through the list to split all the error messages into a jsonarray
//                            while (iterInt.hasNext()) {
//                                int id = iterInt.next();
//                                list.addProperty("file", "location-delete.csv");
//                                list.addProperty("line", id);
//                                String[] messages = delErrMap.get(id).split(",");
//                                for (String msg : messages) {
//                                    errors.add(msg);
//                                }
//                                list.add("message", errors);
//                                errors = new JsonArray();
//                                arr.add(list);
//                                list = new JsonObject();
//                            }
//                        }
                        
                        output.add("error", arr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                //For errors, will throw the errors back IMMEDIATELY without checking the files.
                output.addProperty("status", "error");
                output.add("errors", errors);
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
