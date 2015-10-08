/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import controller.BasicAppController;
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
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 *
 * @author jeremyongts92
 */
@WebServlet(name = "BasicUsetimeReport", urlPatterns = {"/json/basic-usetime-report"})
public class BasicUsetimeReport extends HttpServlet {

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
			JSONObject output = new JSONObject();
			JSONArray errors = new JSONArray();

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

			

			//PRINT ERROR AND EXIT IF ERRORS EXIST
			if (errors.size() > 0) {
				output.put("status", "error");
				output.put("errors", errors);
				out.println(output.toJSONString());
				return;
			}

			//PASSES ALL VALIDATION, proceed to report generation
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = dateFormat.parse(startdate, new ParsePosition(0));
			Date endDate = dateFormat.parse(enddate, new ParsePosition(0));
			
			BasicAppController ctrl = new BasicAppController();
			HashMap<String, String[]> resultMap = ctrl.generateReport(startDate, endDate);
			
			
//			output.put("status", "success");
//			JSONArray heatmap = new JSONArray();
//			Iterator<String> iter = luMap.keySet().iterator();
//
//			while (iter.hasNext()) {
//				JSONObject currLoc = new JSONObject();
//
//				String loc = iter.next();
//				currLoc.put("semanatic-place", loc);
//				int numUsers = luMap.get(loc).size();
//				currLoc.put("num-people-using-phone", numUsers);
//
//				int density = 0;
//				if (numUsers == 0) {
//
//				} else if (numUsers < 4) {
//					density = 1;
//				} else if (numUsers < 8) {
//					density = 2;
//				} else if (numUsers < 14) {
//					density = 3;
//				} else if (numUsers < 21) {
//					density = 4;
//				} else {
//					density = 5;
//				}
//				currLoc.put("crowd-density", density);
//				heatmap.add(currLoc);
//			}
//
//			output.put("heatmap", heatmap);
//			out.println(output.toJSONString());

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
