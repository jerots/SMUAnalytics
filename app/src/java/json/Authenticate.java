package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dao.AdminDAO;
import dao.UserDAO;
import entity.Admin;
import entity.User;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/json/authenticate"})
public class Authenticate extends HttpServlet {

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		try (PrintWriter out = response.getWriter()) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonObject result = new JsonObject();
			
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			if (username != null){
				username = username.toLowerCase().trim();
			}
			
			String token = JWTUtility.sign("nabjemzhdarrensw", username);

			//CHECK WHETHER ADMIN LOGIN SUCCESS
			AdminDAO adminDAO = new AdminDAO();
			Admin admin = adminDAO.retrieve(username, password.trim());
			if (admin != null) {
				result.addProperty("status", "success");
				result.addProperty("token", token);
				out.println(gson.toJson(result));
				return;
				//redirect to admin page

			}
//			out.println(admin);

			//IF NOT, CHECK WHETHER STUDENT LOGIN SUCCESS
			UserDAO userDAO = new UserDAO();
			User user = userDAO.retrieveByEmailId(username, password);
			if (user != null) {
				result.addProperty("status", "success");
				result.addProperty("token", token);
				out.println(gson.toJson(result));
				return;
				//redirect to student page

			}

//			out.println(user);
			//IF ALL FAIL.
			result.addProperty("status", "error");
			result.addProperty("messages", "invalid username/password");
			out.println(gson.toJson(result));
			

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
		//processRequest(request, response);
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
