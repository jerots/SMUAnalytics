/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.admin;

import controller.DeleteController;
import dao.Utility;
import entity.LocationUsage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
@WebServlet(name = "DeleteLocationAction", urlPatterns = {"/DeleteLocationAction"})
public class DeleteLocationAction extends HttpServlet {

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
		try {

			String macAdd = Utility.parseString(request.getParameter("macadd"));
			String startDate = Utility.parseString(request.getParameter("startdate"));
			String endDate = Utility.parseString(request.getParameter("enddate"));
                        String locationId = Utility.parseString(request.getParameter("locationid"));
                        String semanticPl = Utility.parseString(request.getParameter("semanticplace"));
                        String startTime = Utility.parseString(request.getParameter("starttime"));
                        String endTime = Utility.parseString(request.getParameter("endtime"));
                        ArrayList<String> errors = new ArrayList<>();

			DeleteController cntrl = new DeleteController();
			ArrayList<LocationUsage> lList = cntrl.delete(macAdd, startDate, endDate, startTime, endTime, locationId, semanticPl, errors);

			request.setAttribute("macadd", macAdd);
			request.setAttribute("startdate", startDate);
			request.setAttribute("enddate", endDate);
                        request.setAttribute("locationid", locationId);
                        request.setAttribute("semanticplace", semanticPl);
                        request.setAttribute("starttime", startTime);
                        request.setAttribute("endtime", endTime);
                        if(errors.size() != 0){
                            request.setAttribute("errors", errors.get(0));
                        }
			request.setAttribute("deleted", lList);
                        
			RequestDispatcher rd = request.getRequestDispatcher("delete-location.jsp");
			rd.forward(request, response);
			
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
