/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.student;

import dao.AppUsageDAO;
import dao.LocationDAO;
import dao.LocationUsageDAO;
import entity.AppUsage;
import entity.Location;
import entity.LocationUsage;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jeremyongts92
 */
@WebServlet(name = "HeatmapAction", urlPatterns = {"/HeatmapAction"})
public class HeatmapAction extends HttpServlet {

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
			LocationUsageDAO luDAO = new LocationUsageDAO();

			String dateStr = request.getParameter("date");
			String timeStr = request.getParameter("time");
			out.println(dateStr + ",");
			out.println(timeStr);
			String floor = request.getParameter("floor");
			request.setAttribute("date", dateStr);
			request.setAttribute("time", timeStr);
			request.setAttribute("floor", floor);
			
			
			

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			Date datetime = dateFormat.parse(dateStr + " " + timeStr, new ParsePosition(0));
			
			//This list has all the usage of the floor (up to 15 mins prior given datetime, excluding datetime)
//			ArrayList<LocationUsage> luList = luDAO.retrieve(datetime, floor);

			//for each location, count unique users
			HashMap<String, ArrayList<LocationUsage>> result = new HashMap<String, ArrayList<LocationUsage>>();
			HashSet<String> peopleSet = new HashSet<String>();
			
			LocationDAO locDAO = new LocationDAO();
			ArrayList<String> floorLocationList = locDAO.retrieve(floor);
			out.println(floorLocationList.size());
			for (int i = 0; i < floorLocationList.size(); i++) { //for each location in the floor
				String loc = floorLocationList.get(i);
				
				ArrayList<LocationUsage> luList = luDAO.retrieve(datetime, loc);
				result.put(loc, luList);
				

			}
//			out.println("<br>");
//			Iterator iter = result.keySet().iterator();
//			while (iter.hasNext()){
//				String key = (String) iter.next();
//				out.println(key + ", ");
//				out.println(result.get(key).size() + "<br>");
//			}
//			
//			return HashMap<location,userlist>
			request.setAttribute("heatmap", result);
			RequestDispatcher rd = request.getRequestDispatcher("student/heatmap.jsp");
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
