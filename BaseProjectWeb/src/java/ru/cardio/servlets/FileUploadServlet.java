package ru.reshaka.web.servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import ru.cardio.core.jpa.entity.Attachment;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.managers.AttachmentManagerLocal;
import ru.cardio.core.managers.UserManagerLocal;
import ru.cardio.core.utils.FileUtils;
import ru.cardio.core.utils.UploadedFile;
import ru.cardio.json.additionals.JsonResponse;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.utils.StringUtils;
import ru.cardio.web.json.utils.SecureResponseWrapper;
import web.utils.SessionListener;

/**
 *
 * @author danon
 */
@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload"})
public class FileUploadServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(FileUploadServlet.class);
    
    private static final String SERVLET_ENCODING = "ISO-8859-1";
    
    public static final String FILES_PARAM = "files[]";
    public static final String COMPRESS_PARAM = "compress";
    
    @EJB
    private AttachmentManagerLocal am;
    
    @EJB
    private UserManagerLocal um;
    
    /**
     * Handles the HTTP
     * <code>POST</code> method.
     * <p/>
     * @param request  servlet request
     * @param response servlet response
     * <p/>
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long userId = (Long) SessionListener.getSessionAttribute(request.getSession(), "userId");
        User u = um.getUserById(userId);
        System.out.println("userId = " + userId);
//        User u = (User)SessionListener.getSessionAttribute(request.getSession(), "user");
         if(u == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            System.out.println("doPost(): unauthorized attempt to upload file(s).");
//            log.debug("doPost(): unauthorized attempt to upload file(s).");
            return;
        }
        Object o = request.getAttribute(FILES_PARAM);
        System.out.println("map = " + request.getParameterMap());
//        System.out.println("files[] = " + o.toString());
        
        if((o == null) || !(o instanceof FileItem) && !(o instanceof List)) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println("doPost(): failed to upload files or no files selected.");
//            log.debug("doPost(): failed to upload files or no files selected.");
            return;
        }
        List<FileItem> fileItems = new ArrayList<FileItem>();
        if(o instanceof FileItem) {
            fileItems.add((FileItem)o);
        } else {
            for(Object element : (List)o) {
                if(element instanceof FileItem)
                    fileItems.add((FileItem)element);
            }
        }
        System.out.println("fileItems = " + fileItems);
        
        if(fileItems.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("doPost(): error occured whule uploading files.");
//            log.debug("doPost(): error occured whule uploading files.");
            return;
        }
        
        boolean compress = StringUtils.isTrue(getValidParameter(request, COMPRESS_PARAM, (fileItems.size() > 1)+""));
        
        ServletOutputStream out = response.getOutputStream();
        try {
            response.setContentType("application/xml");
            processFiles(u.getId(), fileItems, compress, out);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println("");
//            log.error("doPost(): unable to process uploaded files.", ex);
        } finally {
            out.close();
        }
    }
    
    private String getValidParameter(HttpServletRequest request, String name) {
        return StringUtils.decode(StringUtils.getValidString(request.getParameter(name)), SERVLET_ENCODING);
    }
    
    private String getValidParameter(HttpServletRequest request, String name, String defualtValue) {
        final String param = getValidParameter(request, name);
        return param.isEmpty() ? defualtValue : param; 
    }    

    /**
     * Returns a short description of the servlet.
     * <p/>
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet for uploading files.";
    }// </editor-fold>

    private void processFiles(long userId, List<FileItem> fileItems, boolean compress, ServletOutputStream out) throws Exception {
        User u = um.getUserById(userId);
        if(u == null) {
            throw new IllegalArgumentException("processFiles(): there is no user with ID="+userId);
        }
        List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>(fileItems.size());
        for(FileItem fi : fileItems) {
            File tmpFile = File.createTempFile("upload_", ".tmp"+FileUtils.extractExtention(fi.getName()));
            fi.write(tmpFile);
            String contentType = fi.getContentType();
            if(StringUtils.isEmpty(contentType) || "application/octet-stream".equalsIgnoreCase(contentType))
                contentType = new MimetypesFileTypeMap().getContentType(fi.getName().toLowerCase());
            System.err.println("uploaded: "+contentType+" "+tmpFile.getName());
            uploadedFiles.add(new UploadedFile(fi.getName(), contentType, tmpFile));
        }
        System.out.println("uploadedFiles = " + uploadedFiles);
        List<Attachment> attachments = new ArrayList<Attachment>(uploadedFiles.size());
        if(compress) {
            Attachment attachment =  am.uploadFiles(u, uploadedFiles, null);
            if(attachment == null) {
                throw new IllegalStateException("Failed to store file in database.");
            }
            attachments.add(attachment);
        } else {
            for(UploadedFile uf : uploadedFiles) {
                Attachment attachment = am.uploadFiles(u, Collections.singletonList(uf), null);
                System.out.println("attachment = " + attachment);
                if(attachment == null) {
                    continue;
                }
                attachments.add(attachment);
            }
        }
//        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//        out.println("<files size=\""+attachments.size()+"\">");
        System.out.println("<files size=\""+attachments.size()+"\">");
        
        List<String> ids = new ArrayList();
        
        for(Attachment att: attachments) {
            ids.add(att.getId().toString());
            
//            out.println("  <file id=\""+att.getId()+"\">");
//            out.println("    <id>"+att.getId()+"</id>");
//            out.println("    <name>"+att.getName()+"</name>");
//            out.println("    <contentType>"+att.getMimeType()+"</contentType>");
//            out.println("    <size>"+att.getSize()+"</size>");
//            out.println("  </file>");
            System.out.println("  <file id=\""+att.getId()+"\">");
            System.out.println("    <id>"+att.getId()+"</id>");
            System.out.println("    <name>"+att.getName()+"</name>");
            System.out.println("    <contentType>"+att.getMimeType()+"</contentType>");
            System.out.println("    <size>"+att.getSize()+"</size>");
            System.out.println("  </file>");
        }
        
        JsonResponse<List<String>> js = new JsonResponse<List<String>>(ResponseConstants.OK, null, ids);
        
        System.out.println("</files>");
        out.print(SecureResponseWrapper.getJsonResponse(js));
//        out.println("</files>");
    }
}
