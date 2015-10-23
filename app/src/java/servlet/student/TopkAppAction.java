/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.student;

import controller.TopkAppController;
import java.io.IOException;
import java.io.PrintWriter;
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
 * @author ASUS-PC
 */
@WebServlet(name = "topkApp", urlPatterns = {"/topkApp"})
public class TopkAppAction extends HttpServlet {

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
            /* TODO output your page here. You may use following sample code. */
            //Gets the number of (top) K that the individual wants displayed
            String entry = request.getParameter("entries");

            //This is the choice selection of which of the 3 option the user wants to be processed.
            String selection = request.getParameter("category");
            //Gets the start and end dates as necessary.
            String startDate = request.getParameter("startdate");
            String endDate = request.getParameter("enddate");
            TopkAppController ctrl = new TopkAppController();

            //Delcares the values to return. Declares both in case of 
            ArrayList<HashMap<String, String>> catValues = null;
            //All the values are from the same select place. It only changes based on the report selected
            String selected = request.getParameter("choices");

            //The switch case divides the chosen choice into the three categories as necessary
            switch (selection) {
                case "schoolapps":
                    //This parameter is only for the school function
                    catValues = ctrl.getTopKApp(entry, selected, startDate, endDate);
                    break;
                case "appstudents":
                    //This parameter is only for those who select App Category and return Students
                    catValues = ctrl.getTopkStudents(entry, selected, startDate, endDate);
                    //break;
                default:
                    //This parameter is only for those who select App Category and return School
                    catValues = ctrl.getTopkSchool(entry, selected, startDate, endDate);
                    //break;
            }
            request.setAttribute("catValues", catValues);
            request.setAttribute("category", selection);
            RequestDispatcher rd = request.getRequestDispatcher("topK.jsp");
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
