/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import controller.BootstrapController;
import dao.InitDAO;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import com.google.gson.JsonArray;
import dao.AdminDAO;
import entity.Admin;
import java.io.InputStream;
import javax.servlet.annotation.MultipartConfig;

/**
 *
 * @author Boyofthefuture
 */
@WebServlet(name = "Bootstrap", urlPatterns = {"/json/bootstrap"})
@MultipartConfig
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
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonObject output = new JsonObject();
			JsonArray errors = new JsonArray();

			InputStream fis = null;
			String token = request.getParameter("token");
			Part filePart = null;
			try {
				filePart = request.getPart("bootstrap-file");
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
				if (filePart == null || filePart.getSize() < 0) {
					errors.add("missing file");
				}else{
                                    String name = filePart.getName();
                                    if(!name.substring(name.length()-4 ,name.length()).equals(".zip")){
                                        errors.add("invalid file");
                                    }
                                }

				//If not zip file
			}

			//PRINT ERROR AND EXIT IF ERRORS EXIST
			if (errors.size() > 0) {
				output.addProperty("status", "error");
				output.add("messages", errors);
				out.println(gson.toJson(output));
				return;
			}

			//IF PASS VALIDATION, READ FILES IN BOOTSTRAP
			//Create ERROR MAPS - and pass to boostrapController to generate
			TreeMap<Integer, String> userErrMap = new TreeMap<Integer, String>();
			TreeMap<Integer, String> appErrMap = new TreeMap<Integer, String>();
			TreeMap<Integer, String> locErrMap = new TreeMap<Integer, String>();
			TreeMap<Integer, String> auErrMap = new TreeMap<Integer, String>();
			TreeMap<Integer, String> luErrMap = new TreeMap<Integer, String>();

			TreeMap<String, Integer> recordMap = null;

			BootstrapController ctrl = new BootstrapController();
			recordMap = ctrl.bootstrap(filePart, userErrMap, appErrMap, locErrMap, auErrMap, luErrMap);
			//Returns success as the head of the JSON if it is a success.
			boolean success = false;
			if (userErrMap.isEmpty() && appErrMap.isEmpty() && locErrMap.isEmpty() && auErrMap.isEmpty() && luErrMap.isEmpty()) {
				success = true;
				output.addProperty("status", "success");
			} else {
				output.addProperty("status", "error");
			}

			//Iterates through the main TreeMap to consolidate the number of rows that were updated.
			Iterator<String> iter = recordMap.keySet().iterator();
			JsonArray updatedArr = new JsonArray();

			while (iter.hasNext()) {

				String fileName = iter.next();

				//print location-delete.csv only if it exists
				if (recordMap.get(fileName) > -1) {
					JsonObject list = new JsonObject();
					list.addProperty(fileName, recordMap.get(fileName));
					updatedArr.add(list);
				}

			}
			output.add("num-record-loaded", updatedArr);
                        
                        if(recordMap.get("location-delete.csv") >= 0){
                            output.addProperty("num-record-deleted", recordMap.get("location-delete.csv"));
                            output.addProperty("num-record-not-found", recordMap.get("deletenotfound"));
                        }
                        
			//PRINTING OF ERRORS
			if (!success) { //This only occurs when there is an error
				//Iterates through to find the unique row numbers that are affected. This is for AppDAO or app-lookup.csv
				Iterator<Integer> iterInt = appErrMap.keySet().iterator();
				JsonArray errorArr = new JsonArray();

				//Goes through the list to split all the error messages into a jsonarray
				
				//app-lookup.csv
				while (iterInt.hasNext()) {
					JsonObject errorObj = new JsonObject();
					JsonArray msgArr = new JsonArray();
					int id = iterInt.next();
					errorObj.addProperty("file", "app-lookup.csv");
					errorObj.addProperty("line", id);
					String[] messages = appErrMap.get(id).split(",");
					for (String msg : messages) {
						msgArr.add(msg);
					}
					errorObj.add("message", msgArr);
					errorArr.add(errorObj);
				}

//				//Iterates through to find the unique row numbers that are affected. This is for UserDAO/demographics.csv
				iterInt = userErrMap.keySet().iterator();
				while (iterInt.hasNext()) {
					JsonObject errorObj = new JsonObject();
					JsonArray msgArr = new JsonArray();
					int id = iterInt.next();
					errorObj.addProperty("file", "demographics.csv");
					errorObj.addProperty("line", id);
					String[] messages = userErrMap.get(id).split(",");
					for (String msg : messages) {
						msgArr.add(msg);
					}
					errorObj.add("message", msgArr);
					errorArr.add(errorObj);
				}
//
//				//Iterates through to find the unique row numbers that are affected. This is for App-lookup.csv/AppUsageDAO
				iterInt = auErrMap.keySet().iterator();
				while (iterInt.hasNext()) {
					JsonObject errorObj = new JsonObject();
					JsonArray msgArr = new JsonArray();
					int id = iterInt.next();
					errorObj.addProperty("file", "app.csv");
					errorObj.addProperty("line", id);
					String[] messages = auErrMap.get(id).split(",");
					for (String msg : messages) {
						msgArr.add(msg);
					}
					errorObj.add("message", msgArr);
					errorArr.add(errorObj);
				}
//				//Iterates through to find the unique row numbers that are affected. This is for LocationDAO/demographics.csv
				iterInt = locErrMap.keySet().iterator();
				while (iterInt.hasNext()) {
					JsonObject errorObj = new JsonObject();
					JsonArray msgArr = new JsonArray();
					int id = iterInt.next();
					errorObj.addProperty("file", "location-lookup.csv");
					errorObj.addProperty("line", id);
					String[] messages = locErrMap.get(id).split(",");
					for (String msg : messages) {
						msgArr.add(msg);
					}
					errorObj.add("message", msgArr);
					errorArr.add(errorObj);
				}
//
//				//Iterates through to find the unique row numbers that are affected. This is for LocationUsageDAO/demographics.csv
				iterInt = luErrMap.keySet().iterator();
				while (iterInt.hasNext()) {
					JsonObject errorObj = new JsonObject();
					JsonArray msgArr = new JsonArray();
					int id = iterInt.next();
					errorObj.addProperty("file", "location.csv");
					errorObj.addProperty("line", id);
					String[] messages = luErrMap.get(id).split(",");
					for (String msg : messages) {
						msgArr.add(msg);
					}
					errorObj.add("message", msgArr);
					errorArr.add(errorObj);
				}
//
//				//Iterates through to find the unique row numbers that are affected. This is for UserDAO/demographics.csv
				

				output.add("error", errorArr);
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
