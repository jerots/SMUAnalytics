package servlet;

import java.io.*;
import java.sql.SQLException;
import java.util.zip.ZipInputStream;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import dao.*;
import com.opencsv.CSVReader;
import java.util.zip.ZipEntry;

@WebServlet(urlPatterns = {"/BootstrapAction"})
@MultipartConfig

public class BootstrapAction extends HttpServlet {

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

            try {
                InitDAO.createTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Part filePart = request.getPart("zipFile"); // Retrieves <input type="file" name="zipFile">
            InputStream fileContent = filePart.getInputStream();
            ZipEntry entry = null;

            CSVReader reader = null;

//            while ((entry = zipInputStream.getNextEntry()) != null) {
//                String fileName = entry.getName();
//                
//            }
            UserDAO uDao = new UserDAO();
            AppDAO appDao = new AppDAO();
            LocationDAO lDao = new LocationDAO();
            AppUsageDAO auDao = new AppUsageDAO();
            LocationUsageDAO luDao = new LocationUsageDAO();

            ZipInputStream zipInputStream = new ZipInputStream(fileContent);
            InputStreamReader isr = new InputStreamReader(zipInputStream);
            BufferedReader br = new BufferedReader(isr);
            try {
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    if (fileName.equals("app-lookup.csv")) {
                        reader = new CSVReader(br);
                        reader.readNext();
                        appDao.insert(reader);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            fileContent = filePart.getInputStream();
            zipInputStream = new ZipInputStream(fileContent);
            isr = new InputStreamReader(zipInputStream);
            br = new BufferedReader(isr);
            entry = null;
            try {
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    if (fileName.equals("demographics.csv")) {
                        reader = new CSVReader(br);
                        reader.readNext();
                        uDao.insert(reader);
                    } else {
                        zipInputStream.closeEntry();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            fileContent = filePart.getInputStream();
            zipInputStream = new ZipInputStream(fileContent);
            isr = new InputStreamReader(zipInputStream);
            br = new BufferedReader(isr);
            entry = null;
            try {
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    if (fileName.equals("app.csv")) {
                        reader = new CSVReader(br);
                        reader.readNext();
                        auDao.insert(reader);
                    } else {
                        zipInputStream.closeEntry();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            fileContent = filePart.getInputStream();
            zipInputStream = new ZipInputStream(fileContent);
            isr = new InputStreamReader(zipInputStream);
            br = new BufferedReader(isr);
            entry = null;
            try {
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    if (fileName.equals("location-lookup.csv")) {
                        reader = new CSVReader(br);
                        reader.readNext();
                        lDao.insert(reader);
                    } else {
                        zipInputStream.closeEntry();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileContent = filePart.getInputStream();
            zipInputStream = new ZipInputStream(fileContent);
            isr = new InputStreamReader(zipInputStream);
            br = new BufferedReader(isr);
            entry = null;
            try {
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    if (fileName.equals("location.csv")) {
                        reader = new CSVReader(br);
                        reader.readNext();
                        luDao.insert(reader);
                    } else {
                        zipInputStream.closeEntry();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileContent = filePart.getInputStream();
            zipInputStream = new ZipInputStream(fileContent);
            isr = new InputStreamReader(zipInputStream);
            br = new BufferedReader(isr);
            entry = null;
            try {
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    if (fileName.equals("location-delete.csv")) {
                        reader = new CSVReader(br);
                        reader.readNext();
                        luDao.insert(reader);
                    } else {
                        zipInputStream.closeEntry();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Exception Caught in bootstrap action.java");
            e.printStackTrace();
        } finally {

            response.sendRedirect("admin/home.jsp");
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
