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
			String startDateStr = request.getParameter("startdate");
			String endDateStr = request.getParameter("enddate");
			String macAdd = request.getParameter("macaddress");
			Date dateFormattedStart = null;
			Date dateFormattedEnd = null;
			User user = null;

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

			//MACADDRESS VALIDATION
			if (macAdd == null) {
				errors.add("missing macaddress");
			} else if (macAdd.length() == 0) {
				errors.add("blank macaddress");
			} else {
				macAdd = macAdd.toLowerCase();
				UserDAO userDAO = new UserDAO();
				user = userDAO.retrieveByMac(macAdd);
				if (user == null) {
					errors.add("invalid macaddress");
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
					dateFormattedStart = Utility.parseDate(startDateStr + " 00:00:00");
					if (dateFormattedStart == null) {
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
					dateFormattedEnd = Utility.parseDate(endDateStr + " 23:59:59");
					if (dateFormattedEnd == null) {
						errors.add("invalid enddate");
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

			SmartphoneOveruseController ctrl = new SmartphoneOveruseController();

			output.addProperty("status", "success");
			TreeMap<String, String> results = ctrl.generateReport(user, dateFormattedStart, dateFormattedEnd);

			JsonArray metrics = new JsonArray();

			
			output.addProperty("overuse-index", results.get("overuse-index"));
			
			JsonObject usageObj = new JsonObject();
			usageObj.addProperty("usage-category", results.get("usage-category"));
			usageObj.addProperty("usage-duration", results.get("usage-duration"));
			metrics.add(usageObj);

			JsonObject gamingObj = new JsonObject();
			gamingObj.addProperty("gaming-category", results.get("gaming-category"));
			gamingObj.addProperty("gaming-duration", results.get("gaming-duration"));
			metrics.add(gamingObj);

			JsonObject accessObj = new JsonObject();
			accessObj.addProperty("accessfrequency-category", results.get("accessfrequency-category"));
			accessObj.addProperty("accessfrequency", results.get("accessfrequency"));
			metrics.add(accessObj);

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
