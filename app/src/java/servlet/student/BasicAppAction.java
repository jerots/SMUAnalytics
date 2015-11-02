/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.student;

import controller.BasicAppController;
import dao.Utility;
import entity.Breakdown;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Boyofthefuture
 */
@WebServlet(name = "BasicAppAction", urlPatterns = {"/BasicAppAction"})
public class BasicAppAction extends HttpServlet {

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
			
			
			//RETRIEVING PARAMETERS
			String startdate = request.getParameter("startdate");
			String enddate = request.getParameter("enddate");
			String filter1 = request.getParameter("filter1");
			String filter2 = request.getParameter("filter2");
			String filter3 = request.getParameter("filter3");
			String filter4 = request.getParameter("filter4");
			
			
			//VALIDATION
			ArrayList<String> errors = new ArrayList<String>();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startDate = dateFormat.parse(startdate + " " + "00:00:00", new ParsePosition(0));
			if (startDate == null) {
				errors.add("You have entered an invalid start date!");
			}
			Date endDate = dateFormat.parse(enddate + " " + "23:59:59", new ParsePosition(0));
			if (endDate == null) {
				errors.add("You have entered an invalid end date!");
			}
			if (startDate.after(endDate)){
				errors.add("Your start date should be before your end date!");
			}

			ArrayList<String> demoList = new ArrayList<String>();

			if (filter1 != null && filter1.length() > 0) {
				demoList.add(filter1);
			}
			if (filter2 != null && filter2.length() > 0) {
				if (!demoList.contains(filter2)) {
					demoList.add(filter2);
				} else {
					errors.add("Your second filter cannot be the same!");
				}

			}
			if (filter3 != null && filter3.length() > 0) {
				if (!demoList.contains(filter3)) {
					demoList.add(filter3);
				} else {
					errors.add("Your third filter cannot be the same!");
				}
			}
			if (filter4 != null && filter4.length() > 0) {
				if (!demoList.contains(filter4)) {
					demoList.add(filter4);
				} else {
					errors.add("Your fourth filter cannot be the same!");
				}
			}
			
			String[] demoArr = demoList.toArray(new String[demoList.size()]);
			
			//IF FAIL VALIDATION, SEND ERROR MESSAGE
			if (!errors.isEmpty()) {
				request.setAttribute("errors", errors);
				RequestDispatcher rd = request.getRequestDispatcher("student");
				rd.forward(request, response);
				return;
			}
			
			//IF PASS VALIDATION, CONTINUE
			Breakdown result = null;
			BasicAppController ctrl = new BasicAppController();

			if (demoArr.length == 0) {
				result = ctrl.generateReport(startDate, endDate, null, -1);
				

			} else {
				request.setAttribute("withDemo", "true");
				result = ctrl.generateReportByDemo(startDate, endDate, demoArr);

			}
			request.setAttribute("demoArr", demoArr);
			request.setAttribute("result", result);
			RequestDispatcher rd = request.getRequestDispatcher("student");
			rd.forward(request, response);
			
			

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
