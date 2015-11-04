package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.BasicAppController;
import dao.AdminDAO;
import dao.UserDAO;
import dao.Utility;
import entity.Admin;
import entity.Breakdown;
import entity.User;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "BasicUsetimeReport", urlPatterns = {"/json/basic-usetime-report"})
public class BasicUsetime extends HttpServlet {

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		try (PrintWriter out = response.getWriter()) {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			JsonObject output = new JsonObject();

			ArrayList<String> errors = new ArrayList<String>();

			String token = request.getParameter("token");
			String startdate = request.getParameter("startdate");
			String enddate = request.getParameter("enddate");

			Date startDate = null;
			Date endDate = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
						UserDAO userDAO = new UserDAO();
						User user = userDAO.retrieve(username);
						AdminDAO adminDAO = new AdminDAO();
						Admin admin = adminDAO.retrieve(username);
						if (user == null && admin == null) {
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
				startdate = startdate.trim();
				if (startdate.length() != 10) {
					errors.add("invalid startdate");
				} else {
					startDate = dateFormat.parse(startdate + " " + "00:00:00", new ParsePosition(0));

					if (startDate == null) {
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
				enddate = enddate.trim();
				if (enddate.length() != 10) {
					errors.add("invalid enddate");
				} else {
					endDate = dateFormat.parse(enddate + " " + "23:59:59", new ParsePosition(0));

					if (endDate == null) {
						errors.add("invalid enddate");
					}
				}
			}

			if (startDate != null && endDate != null && startDate.after(endDate)) {
				errors.add("invalid startdate");
			}

			//PRINT ERROR AND EXIT IF ERRORS EXIST
			if (errors.size() > 0) {
				output.addProperty("status", "error");

				Collections.sort(errors);
				output.add("messages", gson.toJsonTree(errors));
				out.println(gson.toJson(output));
				return;
			}

			//PASSES ALL VALIDATION, proceed to report generation
			output.addProperty("status", "success");

			BasicAppController ctrl = new BasicAppController();

			Breakdown breakdown = ctrl.generateReport(startDate, endDate, null, -1);

			ArrayList<HashMap<String, Breakdown>> breakdownList = breakdown.getBreakdown();

			HashMap<String, Breakdown> intenseMap = breakdownList.get(0);
			HashMap<String, Breakdown> normalMap = breakdownList.get(1);
			HashMap<String, Breakdown> mildMap = breakdownList.get(2);

			JsonArray breakdownArr = new JsonArray();

			JsonObject intense = new JsonObject();
			intense.addProperty("intense-count", intenseMap.get("intense-count").getMessage());
			intense.addProperty("intense-percent", intenseMap.get("intense-percent").getMessage());

			JsonObject normal = new JsonObject();
			normal.addProperty("normal-count", normalMap.get("normal-count").getMessage());
			normal.addProperty("normal-percent", normalMap.get("normal-percent").getMessage());

			JsonObject mild = new JsonObject();
			mild.addProperty("mild-count", mildMap.get("mild-count").getMessage());
			mild.addProperty("mild-percent", mildMap.get("mild-percent").getMessage());

			breakdownArr.add(intense);
			breakdownArr.add(normal);
			breakdownArr.add(mild);

			output.add("breakdown", breakdownArr);

			out.println(gson.toJson(output));
		} catch (Exception e) {
			e.printStackTrace();
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
