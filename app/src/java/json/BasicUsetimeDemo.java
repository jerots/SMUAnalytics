package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.BasicAppController;
import dao.AdminDAO;
import dao.UserDAO;
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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "BasicUsetimeDemo", urlPatterns = {"/json/basic-usetime-demographics-report"})
public class BasicUsetimeDemo extends HttpServlet {

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
			String order = request.getParameter("order");

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

			//ORDER VALIDATION
			if (order == null) {
				errors.add("missing order");
			} else if (order.length() == 0) {
				errors.add("blank order");
			} else {
				order = order.toLowerCase().trim();
				String[] orderArr = order.split(",");

				if (orderArr.length > 4) {
					//INVALID
					errors.add("invalid order");
				} else {

					ArrayList<String> toCheck = new ArrayList<String>();
					toCheck.add("year");
					toCheck.add("gender");
					toCheck.add("school");
					toCheck.add("cca");

					for (String s : orderArr) {
						if (!toCheck.contains(s)) {
							//INVALID
							errors.add("invalid order");
						} else {
							toCheck.remove(s);
						}
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

			String[] validArr = order.split(",");
//
			BasicAppController ctrl = new BasicAppController();
			Breakdown breakdown = null;
			try {
//            breakdown = ctrl.generateReportByDemo(startDate, endDate, validArr);
				breakdown = ctrl.generateReportByDemo(startDate, endDate, validArr);

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
			if (map.get("gender") != null) {
				obj.addProperty("gender", map.get("gender").getMessage().toUpperCase());
			} else if (map.get("year") != null) {
				obj.addProperty("year", Integer.parseInt(map.get("year").getMessage()));
			} else if (map.get("school") != null) {
				obj.addProperty("school", map.get("school").getMessage());
			} else if (map.get("cca") != null) {
				obj.addProperty("cca", map.get("cca").getMessage());
			}

			Breakdown count = map.get("count");
			if (count != null) {
				obj.addProperty("count", Integer.parseInt(count.getMessage()));

			}

			Breakdown percent = map.get("percent");
			if (percent != null) {
				obj.addProperty("percent", Integer.parseInt(percent.getMessage()));
			}

			Breakdown further = map.get("breakdown");
			if (further != null) {
				JsonArray newArr = new JsonArray();
				obj.add("breakdown", newArr);
				generateJson(newArr, further.getBreakdown());
			} else {
				Breakdown intenseCount = map.get("intense-count");
				Breakdown intensePercent = map.get("intense-percent");
				Breakdown normalCount = map.get("normal-count");
				Breakdown normalPercent = map.get("normal-percent");
				Breakdown mildCount = map.get("mild-count");
				Breakdown mildPercent = map.get("mild-percent");

				if (intenseCount != null) {
					obj.addProperty("intense-count", Integer.parseInt(intenseCount.getMessage()));
					obj.addProperty("intense-percent", Integer.parseInt(intensePercent.getMessage()));
				}
				if (normalCount != null) {
					obj.addProperty("normal-count", Integer.parseInt(normalCount.getMessage()));
					obj.addProperty("normal-percent", Integer.parseInt(normalPercent.getMessage()));
				}
				if (mildCount != null) {
					obj.addProperty("mild-count", Integer.parseInt(mildCount.getMessage()));
					obj.addProperty("mild-percent", Integer.parseInt(mildPercent.getMessage()));
				}
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
