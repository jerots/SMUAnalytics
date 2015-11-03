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
import controller.SocialActivenessController;
import dao.UserDAO;
import dao.Utility;
import entity.Breakdown;
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

/**
 *
 * @author Boyofthefuture
 */
@WebServlet(name = "SocialActiveness", urlPatterns = {"/json/social-activeness-report"})
public class SocialActiveness extends HttpServlet {

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
			String macAdd = request.getParameter("macaddress");
			UserDAO userDAO = new UserDAO();
			User user = null;

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
						user = userDAO.retrieve(username);
						if (user == null) {
							errors.add("invalid token");
						}
					}
				} catch (JWTException e) {
					//failed
					errors.add("invalid token");
				}

			}
			user = null;

			//DATE VALIDATION
			if (date == null) {
				errors.add("missing date");
			} else if (date.length() == 0) {
				errors.add("blank date");
			} else {
				date = date.trim();
				if (date.length() != 10) {
					errors.add("invalid date");
				} else {
					Date dateFormatted = Utility.parseOnlyDate(date);
					if (dateFormatted == null) {
						errors.add("invalid date");
					} else {
						date = Utility.formatOnlyDate(dateFormatted);
						if (date == null) {
							errors.add("invalid date");
						}
					}
				}
			}

            //MACADD VALIDATION - since all the errors are the same, we can immediately retrieve the macadd from the system
			//Username has already been checked
			if (macAdd == null) {
				errors.add("missing macaddress");
			} else if (macAdd.length() == 0) {
				errors.add("blank macaddress");
			} else {
				macAdd = macAdd.toLowerCase().trim();
				user = userDAO.retrieveByMac(macAdd);
				if (user == null) {
					errors.add("invalid macaddress");
				}
			}

			//PRINT ERROR AND EXIT IF ERRORS EXIST
			if (errors.size() > 0) {
				output.addProperty("status", "error");
				output.add("errors", errors);
				out.println(gson.toJson(output));
				return;
			}

			//FROM HERE, FOR SUCCESS.
			SocialActivenessController cntrl = new SocialActivenessController();
			String error = "";
			HashMap<String, Breakdown> printMap = cntrl.generateAwarenessReport(date, macAdd, error);

			output.addProperty("status", "success");
			JsonObject results = new JsonObject();
			//Prints the total social app usage first
			results.addProperty("total-social-app-usage-duration", printMap.get("total-social-app-usage-duration").getMessage());
			//Starts printing the results for each app and the percentage
			ArrayList<HashMap<String, Breakdown>> appList = printMap.get("individual-social-app-usage").getBreakdown();
			JsonArray arr = new JsonArray();
			JsonObject eachApp = new JsonObject();
			for (HashMap<String, Breakdown> appDetails : appList) {
				eachApp.addProperty("app-name", appDetails.get("app-name").getMessage());
				eachApp.addProperty("percent", appDetails.get("percent").getMessage());
				arr.add(eachApp);
				eachApp = new JsonObject();
			}
			results.add("individual-social-app-usage", arr);
			results.addProperty("total-time-spent-in-sis", printMap.get("total-time-spent-in-sis").getMessage());
			results.addProperty("group-percent", printMap.get("group-percent").getMessage());
			results.addProperty("solo-percent", printMap.get("solo-percent").getMessage());
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
