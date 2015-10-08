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
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
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
@WebServlet(name = "BasicUsetimeReport", urlPatterns = {"/json/basic-usetime-report"})
public class BasicUsetime extends HttpServlet {

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

			BasicAppController ctrl = new BasicAppController();

			HashMap<String, Integer> resultMap = ctrl.generateReport(startDate, endDate);
			int intenseCount = resultMap.get("intense-count");
			int normalCount = resultMap.get("normal-count");
			int mildCount = resultMap.get("mild-count");

			JsonArray breakdown = new JsonArray();

			double totalCount = intenseCount + normalCount + mildCount;
			JsonObject intense = new JsonObject();
			intense.addProperty("intense-count", intenseCount);
			intense.addProperty("intense-percent", Math.round((intenseCount / totalCount) * 100));

			JsonObject normal = new JsonObject();
			normal.addProperty("normal-count", normalCount);
			normal.addProperty("normal-percent", Math.round((normalCount / totalCount) * 100));

			JsonObject mild = new JsonObject();
			mild.addProperty("mild-count", mildCount);
			mild.addProperty("mild-percent", Math.round((mildCount / totalCount) * 100));

			breakdown.add(intense);
			breakdown.add(normal);
			breakdown.add(mild);

			output.add("breakdown", breakdown);

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
