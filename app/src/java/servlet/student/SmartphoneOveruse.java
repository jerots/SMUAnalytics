/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.student;

import controller.HeatmapController;
import controller.SmartphoneOveruseController;
import dao.Utility;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ASUS-PC
 */
@WebServlet(name = "SmartphoneOveruse", urlPatterns = {"/SmartphoneOveruse"})
public class SmartphoneOveruse extends HttpServlet {

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
            String errors = "";
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            
            TreeMap<String, String> result = new TreeMap<String, String>();
            HttpSession session = request.getSession();
            User loggedInUser = (User) session.getAttribute("user");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDatetime = dateFormat.parse(startDateStr + " 00:00:00", new ParsePosition(0));
            Date endDatetime = dateFormat.parse(endDateStr + " 23:59:59", new ParsePosition(0));
            
            if (startDatetime != null && endDatetime != null && startDatetime.after(endDatetime)) {
		errors += "Your start date should be before your end date!";
            }
            if (errors.length() != 0) {
		request.setAttribute("errors", errors);
            }
            SmartphoneOveruseController ctrl = new SmartphoneOveruseController();

            result = ctrl.generateReport(loggedInUser, startDatetime, endDatetime);
            
            request.setAttribute("result", result);
            RequestDispatcher view = request.getRequestDispatcher("smartphoneOveruse.jsp");
            view.forward(request, response);
            
            

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
