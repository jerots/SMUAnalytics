package json;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import dao.AdminDAO;
import dao.UserDAO;
import entity.Admin;
import entity.User;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.minidev.json.JSONObject;

/**
 *
 * @author jeremyongts92
 */
@WebServlet(urlPatterns = {"/authenticate"})
public class Authenticate extends HttpServlet {

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
			JSONObject result = new JSONObject();

			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			String token = JWTUtility.sign("zhihui", username);

			//CHECK WHETHER ADMIN LOGIN SUCCESS
			AdminDAO adminDAO = new AdminDAO();
			Admin admin = adminDAO.retrieve(username, password);
			if (admin != null) {
				result.put("status", "success");
				result.put("token", token);
				out.println(result.toJSONString());
				return;
				//redirect to admin page

			}
//			out.println(admin);

			//IF NOT, CHECK WHETHER STUDENT LOGIN SUCCESS
			UserDAO userDAO = new UserDAO();
			User user = userDAO.retrieveByEmailId(username, password);
			System.out.println(user);
			if (user != null) {
				result.put("status", "success");
				result.put("token", token);
				out.println(result.toJSONString());
				return;
				//redirect to student page

			}

//			out.println(user);
			//IF ALL FAIL.
			result.put("status", "error");
			out.println(result.toJSONString());
			

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
