/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.student;

import controller.*;
import dao.Utility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "TopKAction", urlPatterns = {"/TopKAction"})
public class TopKAction extends HttpServlet {

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

		//Gets the number of (top) K that the individual wants displayed
		String entry = request.getParameter("entries");

		//This is the choice selection of which of the 3 option the user wants to be processed.
		String selection = request.getParameter("category");
		//Gets the start and end dates as necessary.
		String startDate = request.getParameter("startdate");
		String endDate = request.getParameter("enddate");
		TopkReportController ctrl = new TopkReportController();
		//This Error means NOTHING ELSE is printed
		String errors = "";
		//This error means that data is still printed
		String error = "";
            //Checks startDate
		//Places this outside to compare dates later
		Date dateFormattedStart = null;
		if (startDate == null) {
			errors += ", invalid startdate";
		} else if (startDate.length() == 0) {
			errors += ", invalid startdate";
		} else {
			dateFormattedStart = Utility.parseOnlyDate(startDate);
			if (dateFormattedStart == null && Utility.formatOnlyDate(dateFormattedStart) != null) {
				errors += ", invalid startdate";
			}
		}
		System.out.println("TESTa");

            //Checks endDate
		//Places this outside to compare dates later
		Date dateFormattedEnd = null;
		if (endDate == null) {
			errors += ", invalid enddate";
		} else if (endDate.length() == 0) {
			errors += ", invalid enddate";
		} else {
			dateFormattedEnd = Utility.parseOnlyDate(endDate);
			if (dateFormattedEnd == null && Utility.formatOnlyDate(dateFormattedEnd) != null) {
				errors += ", invalid enddate";
			}
		}
		System.out.println("TESTb");

		//Finally, makes sure the start date if after to add error, as if it is before or similar, no error
		if (dateFormattedStart != null && dateFormattedEnd != null && dateFormattedStart.after(dateFormattedEnd)) {
			errors += ", end date is after start date";
		}

		//All the values are from the same select place. It only changes based on the report selected
		String selected = request.getParameter("choice");
		//Checks school/appcategory (Actually this is chosen)
		if (selection.equals("schoolapps")) {
			if (!Utility.checkSchools(selected)) {
				errors += ", invalid school";
			}
		} else {
			if (!Utility.checkCategory(selected)) {
				errors += ", invalid app category";
			}
		}
		System.out.println("TESTc");

		//Checks for K
		int topK = Utility.parseInt(entry);
		if (topK > 10 || topK < 1) {
			errors += ", invalid k";
		}

		//Delcares the values to return. Declares both in case of 
		ArrayList<HashMap<String, String>> catValues = null;
		System.out.println("TEST");

		//If all checks are passed:
		if (errors.length() == 0) {
			//The switch case divides the chosen choice into the three categories as necessary
			switch (selection) {
				case "schoolapps":
					//This parameter is only for the school function
					catValues = ctrl.getTopkApp(topK, selected, startDate, endDate, error);
					break;
				case "appstudents":
					//This parameter is only for those who select App Category and return Students
					catValues = ctrl.getTopkStudents(topK, selected, startDate, endDate, error);
					break;
				default:
					//This parameter is only for those who select App Category and return School
					catValues = ctrl.getTopkSchool(topK, selected, startDate, endDate, error);
					break;
			}
		} else {
			//Need to substring for multiple errors
			errors = errors.substring(2);
		}
		System.out.println("TEST2");

		request.setAttribute("catvalues", catValues);
		request.setAttribute("choice", selected);
		request.setAttribute("error", errors);
		request.setAttribute("errors", error);
		request.setAttribute("entries", entry);
		RequestDispatcher rd = null;
		//Divides back into where the request came from.
		switch (selection) {
			case "schoolapps":
				//This parameter is only for the school function
				rd = request.getRequestDispatcher("top-kreport.jsp");
				break;
			case "appstudents":
				//This parameter is only for those who select App Category and return Students
				rd = request.getRequestDispatcher("top-kstudent.jsp");
				break;
			default:
				//This parameter is only for those who select App Category and return School
				rd = request.getRequestDispatcher("top-kschool.jsp");
				break;
		}
		rd.forward(request, response);
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
