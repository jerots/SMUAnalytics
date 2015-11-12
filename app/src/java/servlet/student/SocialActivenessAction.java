/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.student;

import controller.SocialActivenessController;
import dao.Utility;
import entity.Breakdown;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
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
@WebServlet(name = "SocialActivenessAction", urlPatterns = {"/SocialActivenessAction"})
public class SocialActivenessAction extends HttpServlet {

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
            String date = request.getParameter("date");
            String macAdd = request.getParameter("macadd");
            //This Error means NOTHING ELSE is printed
            String errors = "";
            //Checks Date
            //Places this outside to compare dates later
            Date dateFormatted = null;
            if(date == null){
                errors += ",missing date";
            }else if (date.length() == 0) {
                errors += ",blank date";
            } else {
                if(date.length() != 10){
                    errors += ",invalid date";
                }else{
                    dateFormatted = Utility.parseOnlyDate(date);
                    if (dateFormatted == null) {
                        errors += ",invalid date";
                    }else{
                        date = Utility.formatOnlyDate(dateFormatted);
                        if(date == null){
                            errors += ",invalid date";
                        }
                    }
                }
            }
            //check macAdd
            if (macAdd == null) {
                errors += ",missing mac-address";
            }else if(macAdd.length() == 0){
                errors += ",blank mac-address";
            }else if (!Utility.checkHexadecimal(macAdd)) {
                errors += "invalid mac-address";
            } 
            //If the previous few for Mac Address passes, the next will check from the inside to make sure the mac Address is accurate.
            HashMap<String, Breakdown> resultsMap = null;
            if(errors.length() == 0){
                SocialActivenessController cntrl = new SocialActivenessController();
                resultsMap = cntrl.generateAwarenessReport(date, macAdd, errors);
            }else{
                errors = errors.substring(1);
            }
            
            //Sends back the hashmap
            request.setAttribute("errors", errors); //These are the whole line of errors.
            request.setAttribute("date", date);
            request.setAttribute("macadd", macAdd);
            request.setAttribute("results", resultsMap);
            RequestDispatcher view = request.getRequestDispatcher("socialActiveness.jsp");
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
