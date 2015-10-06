/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;

/**
 *
 * @author jeremyongts92
 */
@WebServlet(name = "UsageHeatmap", urlPatterns = {"/usage-heatmap"})
public class UsageHeatmap extends HttpServlet {

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
			ArrayList<String> errors = new ArrayList<String>();
			
			
			String token = request.getParameter("token");
			String date = request.getParameter("date");
			String time = request.getParameter("time");
			String floor = request.getParameter("floor");
			
			try {
				String username = JWTUtility.verify(token, "nabjemzhdarrensw");
				if (username == null){
					//failed
				}
			} catch (JWTException e){
				//failed
				e.printStackTrace();
			}
			
			
			//FLOOR VALIDATION
			if (floor == null){
				errors.add("missing floor");
			} else {
				try {
					int floorInt = Integer.parseInt(floor);
					if (floorInt < 0 || floorInt > 5){
						errors.add("invalid floor");
					}
				} catch (NumberFormatException e){
					errors.add("invalid floor");
				}
				
			}
			
			//DATE VALIDATION
			
			
			
			//TIME VALIDATION
			
			
			
			
			
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
