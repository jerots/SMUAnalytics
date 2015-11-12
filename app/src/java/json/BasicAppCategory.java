package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.BasicAppController;
import dao.AdminDAO;
import dao.UserDAO;
import entity.Admin;
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
import java.util.Iterator;
import java.util.TreeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "BasicAppCategory", urlPatterns = {"/json/basic-appcategory-report"})
public class BasicAppCategory extends HttpServlet {

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

			TreeMap<String, Integer[]> resultMap = ctrl.generateAppCategory(startDate, endDate);

			JsonArray basicAppCat = new JsonArray();
			Iterator<String> iter = resultMap.keySet().iterator();
			while (iter.hasNext()) {
				JsonObject currLoc = new JsonObject();

				String name = iter.next();
				currLoc.addProperty("category-name", name);
				Integer[] infoArr = resultMap.get(name);
				int duration = infoArr[0];
				//double duration = resultMap.get(name);
				currLoc.addProperty("category-duration", duration);
				int percent = infoArr[1];
				currLoc.addProperty("category-percent", percent);
				//currLoc.addProperty("crowd-density", density);
				basicAppCat.add(currLoc);
			}

			output.add("basicApp", basicAppCat);
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
