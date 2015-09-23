import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;




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
        PrintWriter out = response.getWriter();

        Part filePart = request.getPart("zipFile"); // Retrieves <input type="file" name="zipFile">
//        String fileName = filePart.getSubmittedFileName();
        InputStream fileContent = filePart.getInputStream();
        ZipInputStream zipInputStream = new ZipInputStream(fileContent);

        while (zipInputStream.available() == 1) {
            ZipEntry entry = zipInputStream.getNextEntry();
            String fileName = entry.getName();
            out.println(fileName);
            switch (fileName) {
                case "app.csv":
                    //AppUsageDAO auDao = new AppUsageDAO();
//					auDao.insert(zipInputStream);
                    out.println("hii1");
                    break;
                case "app-lookup.csv":
                    //AppDAO appDao = new AppDAO();
//					appDao.insert(zipInputStream);
                    out.println("hii2");
                    break;
                case "demographics.csv":
                    //UserDAO uDao = new UserDAO();
//					uDao.insert(zipInputStream);
                    out.println("hii3");
                    break;
                case "location-lookup.csv":
                    //LocationDAO lDao = new LocationDAO();
//					lDao.insert(zipInputStream);
                    out.println("hii4");
                    break;
                case "location.csv":
                    //LocationUsageDAO luDao = new LocationUsageDAO();
//					luDao.insert(zipInputStream);
                    out.println("hii5");
                    break;

            }

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
