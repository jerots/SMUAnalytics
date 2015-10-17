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
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jeremyongts92
 */
@WebServlet(name = "BasicDiurnalPattern", urlPatterns = {"/json/basic-diurnalpattern-report"})
public class BasicDiurnalPattern extends HttpServlet {

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
			String yearFilter = request.getParameter("yearfilter");
			String genderFilter = request.getParameter("genderfilter");
			String schoolFilter = request.getParameter("schoolfilter");

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

			//DATE VALIDATION
			if (date == null) {
				errors.add("missing startdate");
			} else if (date.length() == 0) {
				errors.add("blank startdate");
			} else {
				if (date.length() != 10) {
					errors.add("invalid startdate");
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
					Date dateFormatted = sdf.parse(date, new ParsePosition(0));
					if (dateFormatted == null) {
						errors.add("invalid startdate");
					}
				}
			}

			//YEAR FILTER VALIDATION
			UserDAO userDAO = new UserDAO();
			ArrayList<String> years = userDAO.getYears();
			if (yearFilter == null) {
				errors.add("missing yearfilter");
			} else if (yearFilter.length() == 0) {
				errors.add("blank yearfilter");
			} else {

				if (!years.contains(yearFilter) && !yearFilter.equals("NA")) {
					errors.add("invalid yearfilter");
				}
			}

			//GENDER FILTER VALIDATION
			ArrayList<String> genders = userDAO.getGenders();
			if (genderFilter == null) {
				errors.add("missing genderfilter");
			} else if (genderFilter.length() == 0) {
				errors.add("blank genderfilter");
			} else {

				if (!years.contains(genderFilter) && !genderFilter.equals("NA")) {
					errors.add("invalid genderfilter");
				}
			}

			//SCHOOL FILTER VALIDATION
			ArrayList<String> schools = userDAO.getSchools();
			if (schoolFilter == null) {
				errors.add("missing schoolfilter");
			} else if (schoolFilter.length() == 0) {
				errors.add("blank schoolfilter");
			} else {

				if (!schools.contains(schoolFilter) && !schoolFilter.equals("NA")) {
					errors.add("invalid schoolfilter");
				}
			}

			String[] demoArr = new String[3];
			demoArr[0] = yearFilter;
			demoArr[1] = genderFilter;
			demoArr[2] = schoolFilter;

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
			Date startDate = dateFormat.parse(date + " " + "00:00:00", new ParsePosition(0));
//			Date endDate = dateFormat.parse(date + " " + "23:59:59", new ParsePosition(0));
//
			BasicAppController ctrl = new BasicAppController();
			Breakdown breakdown = null;
			try {
				breakdown = ctrl.generateDiurnalReport(startDate, demoArr);

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
			if (map.get("period") != null) {
				obj.addProperty("period", map.get("period").getMessage());
			}
			if (map.get("duration") != null) {
				obj.addProperty("duration", map.get("duration").getMessage());
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