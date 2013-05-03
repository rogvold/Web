package ru.cardio.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import ru.cardio.core.jpa.entity.Attachment;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.AttachmentManager;
import ru.cardio.core.managers.AttachmentManagerLocal;
import ru.cardio.core.utils.FileUtils;
import ru.cardio.utils.StringUtils;
import web.utils.SessionListener;

/**
 *
 * @author danon
 */
@WebServlet(name = "DownloadServlet", urlPatterns = {"/download"})
public class DownloadServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(DownloadServlet.class);
    @EJB
    private AttachmentManagerLocal am;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     * <p/>
     * @param request servlet request
     * @param response servlet response
     * <p/>
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        try {
            long id = StringUtils.getValidLong(getValidParameter(request, "id"));
            if (id == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

//            User u = (User) request.getSession(true).getAttribute("user");
//            Long userId = u == null ? null : u.getId();
            Long userId = (Long) SessionListener.getSessionAttribute(request.getSession(), "userId");


            boolean inline = StringUtils.isTrue(getValidParameter(request, "inline", "true"));
            System.out.println("inline: " + inline);
            
            //TODO: create additional servlt for avatar downloadings
//            if (userId==null) {
//                response.sendError(HttpServletResponse.SC_FORBIDDEN);
//                return;
//            }

            Attachment f = am.getUploadedFile(userId, id);
            System.out.println("f = " + f);
            
            if (f != null) {
                sendFile(response, out, f, inline);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Exception ex) {
            System.out.println("ex = " + ex);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            log.error("Failed to download file.", ex);
        } finally {
            out.close();
        }
    }

    public String getValidParameter(HttpServletRequest request, String name) {
        String param = request.getParameter(name);
        return param == null ? "" : param;
    }

    public String getValidParameter(HttpServletRequest request, String name, String defaultValue) {
        final String param = getValidParameter(request, name);
        return param.isEmpty() ? defaultValue : param;
    }

    private void sendFile(HttpServletResponse response, OutputStream out, Attachment f, boolean inline) throws IOException {
        System.out.println("sendFile");
        
        final File file = new File(AttachmentManager.DEFAULT_UPLOAD_DIRECTORY, f.getFileName());
        
        System.out.println("file = " + file);
        
        response.setContentType(f.getMimeType());
        if (inline) {
            response.setHeader("Content-Disposition", "inline; filename=\"" + f.getName() + "\"");
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"");
        }
        InputStream in = new FileInputStream(file);
        FileUtils.copyStream(in, out);
        in.close();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     * <p/>
     * @param request servlet request
     * @param response servlet response
     * <p/>
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     * <p/>
     * @param request servlet request
     * @param response servlet response
     * <p/>
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
     * <p/>
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
