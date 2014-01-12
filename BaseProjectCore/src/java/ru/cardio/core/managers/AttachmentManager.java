package ru.cardio.core.managers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Deflater;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import ru.cardio.core.jpa.entity.Attachment;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.utils.FileUtils;
import ru.cardio.core.utils.ImageUtils;
import ru.cardio.core.utils.UploadedFile;
import ru.cardio.exceptions.CardioException;
//import org.apache.tools.zip.ZipOutputStream;
//import org.apache.tools.zip.ZipOutputStream;

/**
 *
 * @author Danon
 */
@Stateless
public class AttachmentManager implements AttachmentManagerLocal {

    private static Logger log = Logger.getLogger(AttachmentManager.class);
    @PersistenceContext(unitName = "BaseProjectCorePU")
    EntityManager em;
    public static final long MAX_ZIP_SIZE = 1024 * 1024 * 40; // 40 Mb
    // TODO: default upload directory should be set in config!
    public static final String DEFAULT_UPLOAD_DIRECTORY = "uploads";
    @EJB
    UserManagerLocal userMan;

    /**
     * Stores uploaded file "as it is" and adds database entry.
     *
     * @return ID of attachment in the database.
     */
    @Override
    public Attachment uploadFile(String fileName, String contentType, User user, byte[] contents, String tags) {
        if (log.isTraceEnabled()) {
            log.trace(">> uploadFile()");
        }
        try {
            if (contents.length > MAX_ZIP_SIZE) {
                log.trace("File too large!");
                throw new IOException("File too large.");
            }

            if (!checkUploadRights(user)) {
                return null;
            }

            Attachment a = prepareAttachment(fileName, contentType, user, contents, tags);

            em.persist(a);

            Set<User> uset = new HashSet();
            uset.add(user);
            a.setUser(uset);
            em.merge(a);


            if (log.isTraceEnabled()) {
                log.trace("<< uploadFile(): " + a);
            }
            return a;
        } catch (Exception ex) {
            log.error("uploadFile(): Failed to upload file.", ex);
            return null;
        }
    }

    @Override
    public Attachment uploadAvatar(String fileName, String contentType, User user, byte[] contents, Integer width, Integer height) {
        if (log.isTraceEnabled()) {
            log.trace(">> uploadFile()");
        }
        try {
            if (contents.length > MAX_ZIP_SIZE) {
                log.trace("File too large!");
                throw new IOException("File too large.");
            }

            if (!checkUploadRights(user)) {
                return null;
            }

//            Attachment a = prepareAttachment(fileName, contentType, user, contents, tags);
            System.out.println("uploadAvatar: trying to upload avatar " + fileName);
            Attachment a = prepareAvatarAttachment(fileName, user, contents, 0, 0, width, height);

            em.persist(a);

            Set<User> uset = new HashSet();
            uset.add(user);
            a.setUser(uset);
            em.merge(a);


            if (log.isTraceEnabled()) {
                log.trace("<< uploadFile(): " + a);
            }
            return a;
        } catch (Exception ex) {
            log.error("uploadFile(): Failed to upload file.", ex);
            return null;
        }
    }

    /**
     * Stores uploaded file in database. If there are several files, they are
     * compressed into zip archive (with filename = user_login.zip).
     *
     * @param user Owner of the attachment
     * @param files List of uploaded files to be saved into database
     * @return ID of attachment in the database or null if operation failed.
     */
    @Override
    public Attachment uploadFiles(User user, List<UploadedFile> files, String tags) {

        System.out.println("att man: uploadfiles");

        if (log.isTraceEnabled()) {
            log.trace(">> uploadFiles(): " + files);
        }

        if (!checkUploadRights(user)) {
            return null;
        }

        System.out.println("after checking");

        if (files.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("List of files is empty! Nothing to compress.");
            }
            return null;
        }

        System.out.println("files are not empty");

        try {
            System.out.println("tryind to prepare attachment");
            Attachment att = prepareAttachment(user, files, tags);

            if (att.getSize() > MAX_ZIP_SIZE) {
                if (log.isTraceEnabled()) {
                    log.trace("File too large!");
                }
                throw new IOException("File too large.");
            }

            em.persist(att);

            Set<User> uset = new HashSet();
            uset.add(user);
            att.setUser(uset);
            em.merge(att);

            if (log.isTraceEnabled()) {
                log.trace("<< uploadFiles(): " + att);
            }
            return att;
        } catch (Exception ex) {
            log.error("uploadFiles(): Failed to upload files. ", ex);
            return null;
        }
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Attachment getUploadedFile(Long userId, long id) {
        if (log.isTraceEnabled()) {
            log.trace(">> getUploadedFile(): id=" + id);
        }
        try {
            System.out.println("getUploadedFile: userId = " + userId + " ; id = " + id);

            Attachment att = em.find(Attachment.class, id);
//            if (!checkDownloadRights(userId, att.getId())) {
//                return null;
//            }

            if (log.isTraceEnabled()) {
                log.trace("<< getUploadedFile(): " + att);
            }
            System.out.println("att = " + att);

            return att;

        } catch (Exception ex) {
            if (log.isTraceEnabled()) {
                log.trace("<< getUploadedFile()");
            }
        }
        return null;
    }

    @Override
    public void cropExistingFile(Long userId, Long attId, Integer x, Integer y, Integer w, Integer h) throws CardioException {
        try {
            User user = userMan.getUserById(userId);
            Attachment att = em.find(Attachment.class, attId);
            final File file = new File(AttachmentManager.DEFAULT_UPLOAD_DIRECTORY, att.getFileName());
            BufferedImage in = ImageIO.read(file);
            BufferedImage cropped = ImageUtils.cropImage(in, x, y, w, h);
            byte[] contents = ImageUtils.bufferedImageToBytes(cropped, extractTypeFromFileName(att.getName()));

            System.out.println("size after cropping = " + contents.length);

            File root = new File(DEFAULT_UPLOAD_DIRECTORY, "user" + user.getId().toString());


            System.out.println("root = " + root);

            root.mkdirs();

            System.out.println("after rootmkdirs");

            File tmpFile = File.createTempFile("upload_", ".bin", root);
            FileUtils.writeToFile(tmpFile, contents);
            att.setSize((long) contents.length);
            att.setMD5(FileUtils.getMD5(tmpFile));
            att.setFileName("user" + user.getId() + "/" + tmpFile.getName());
            em.merge(att);
        } catch (Exception e) {
            throw new CardioException(e.getMessage());
        }

    }

    @Override
    public void cropAvatar(Long userId, Integer x, Integer y, Integer w, Integer h) throws CardioException {
        Long avId = userMan.getUserAvatar(userId);
        System.out.println("try to crop avatar");
        try {
            cropExistingFile(userId, avId, x, y, w, h);
        } catch (Exception e) {
            throw new CardioException(e.getMessage());
        }
    }

    /**
     * Remove invalid entries from the database and remove redundant files
     */
    @Override
    public void removeInvalidEntries() {
        log.info("Checking validity of Attachments table");
        // TODO: Remove invalid entries from Attachments and remove redundant files
    }

    /**
     * Adds a single file into the ZipOutputStream with specified entry name.
     *
     * @throws IOException
     */
    private void addFileToZip(ZipOutputStream zipOut, UploadedFile file, String name) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace(">> addFileToZip(): " + file);
        }

        ZipEntry entry = new ZipEntry(name);
        zipOut.putNextEntry(entry);

        InputStream in = null;
        try {
            in = file.getInputstream();
            FileUtils.copyStream(file.getInputstream(), zipOut);
            zipOut.closeEntry();
        } finally {
            FileUtils.close(in);
        }

//        try (InputStream in = file.getInputstream()) {
//            FileUtils.copyStream(file.getInputstream(), zipOut);
//            zipOut.closeEntry();
//        }

        if (log.isTraceEnabled()) {
            log.trace("<< addFileToZip()");
        }
    }

    /**
     * Checks if user can upload files
     */
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    private boolean checkUploadRights(User u) {
        if (log.isTraceEnabled()) {
            log.trace(">> checkDownloadRights(): " + u);
        }

        if (u == null) {
            if (log.isDebugEnabled()) {
                log.debug("<< checkDownloadRights(): false - User is null");
            }
            return false;
        }

        // TODO: Check upload rights

        if (log.isTraceEnabled()) {
            log.trace("<< checkUploadRigths(): true");
        }
        return true;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Attachment> getAttachmentsByUserId(Long userId) {
        User user = em.find(User.class, userId);
        List<Attachment> alist = new ArrayList(user.getAttachments());
        return alist;
    }

    @Override
    public Attachment shareFile(Long attachmentId, long who, Long with) {
        if (log.isTraceEnabled()) {
            log.trace(">> shareFile(): attachmentId=" + attachmentId + ", who=" + who + ", with=" + with);
        }

        Attachment att = em.find(Attachment.class, attachmentId);
        if (att == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< shareFile(): null - no such attachment");
            }
            return null;
        }
        boolean canShare = false;
        try {
            //canShare |= um.isAdmin(who);
            User actor = em.find(User.class, who);
            if (actor != null && actor.getUserGroup() == 1) {
                canShare = true;
            }
        } catch (Exception ex) {
        }
        if (!canShare) {
            for (User u : att.getUser()) {
                if (u.getId() == who) {
                    canShare = true;
                    break;
                }
            }
        }
        if (!canShare) {
            if (log.isTraceEnabled()) {
                log.trace("<< shareFile(): null - operation is not permitted");
            }
            return null;
        }
        User w = em.find(User.class, with);
        if (w == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< shareFile(): cannot share with nobody, and owners list was not modified");
            }
            return att;
        }
        att.getUser().add(w);
        em.persist(att);

        return att;
    }

    @Override
    public Attachment reuploadFiles(User user, Long attachmentId, List<UploadedFile> files, String tags) {
        if (attachmentId == null) {
            return uploadFiles(user, files, tags);
        }
        if (!checkUploadRights(user)) {
            return null;
        }
        Attachment original = em.find(Attachment.class, attachmentId);
        if (original == null) {
            return null;
        }

        Set<User> u = original.getUser();
        removeAttachmentFromDisk(original);
        original = prepareAttachment(user, files, tags);
        original.setId(attachmentId);
        original.setUser(u);
        return em.merge(original);
    }

    private void removeAttachmentFromDisk(Attachment a) {
        // TODO: removeAttachmentFromDisk()
        return;
    }

    private Attachment prepareAttachment(String fileName, String contentType, User user, byte[] contents, String tags) {
        System.out.println("prepare attachment of single file");
        try {
            Attachment a = new Attachment();
            a.setName(fileName);
            a.setMimeType(contentType);
            System.out.println("tryin to new File(DEFAULT_UPLOAD_DIRECTORY, user.getLogin());");
            System.out.println("user = " + user);

            File root = new File(DEFAULT_UPLOAD_DIRECTORY, "user" + user.getId().toString());


            System.out.println("root = " + root);

            root.mkdirs();

            System.out.println("after rootmkdirs");

            File tmpFile = File.createTempFile("upload_", ".bin", root);
            FileUtils.writeToFile(tmpFile, contents);
            a.setSize((long) contents.length);
            a.setMD5(FileUtils.getMD5(tmpFile));
            a.setFileName("user" + user.getId() + "/" + tmpFile.getName());
            return a;
        } catch (Exception ex) {
            System.out.println("exception occured : exc = " + ex);
            return null;
        }

    }

    private String extractTypeFromFileName(String fileName) {
        String type = "png";
        System.out.println("try to extract type from filename = " + fileName);
        int b = fileName.indexOf(".") + 1;
        if (b >= 0) {
            type = fileName.substring(b);
        }
        System.out.println("extractTypeFromFileName: filename = " + fileName + " -> type = " + type);
        return type;
    }

    private Attachment prepareAvatarAttachment(String fileName, User user, byte[] contents, Integer x, Integer y, Integer width, Integer height) {
        System.out.println("prepare attachment of single file");
        try {
            Attachment a = new Attachment();
            a.setName(fileName);
            String type = extractTypeFromFileName(fileName);
            System.out.println("prepareAvatarAttachment: filename = " + fileName + "; type = " + type);
//            a.setMimeType(contentType);
            System.out.println("trying to new File(DEFAULT_UPLOAD_DIRECTORY, user.getLogin());");
            System.out.println("user = " + user);

            File root = new File(DEFAULT_UPLOAD_DIRECTORY, "user" + user.getId().toString());


            System.out.println("root = " + root);

            root.mkdirs();

            System.out.println("after rootmkdirs");

            File tmpFile = File.createTempFile("upload_", ".bin", root);

            //resizing
            System.out.println("before resizing: bite array length = " + contents.length);
            contents = ImageUtils.resizeImage(contents, width, height, type);
            System.out.println("after resizing: bite array length = " + contents.length);

            FileUtils.writeToFile(tmpFile, contents);
            a.setSize((long) contents.length);
            a.setMD5(FileUtils.getMD5(tmpFile));
            a.setFileName("user" + user.getId() + "/" + tmpFile.getName());
            return a;
        } catch (Exception ex) {
            System.out.println("exception occured : exc = " + ex);
            return null;
        }

    }

    private Attachment prepareAttachment(User user, List<UploadedFile> files, String tags) {
        System.out.println("prepare attachment...");
        if (files.isEmpty()) {
            System.out.println("files are empty");
            if (log.isDebugEnabled()) {
                log.debug("prepareAttachment() : List of files is empty! Nothing to compress.");
            }
            return null;
        }
        if (files.size() == 1) {
            if (log.isTraceEnabled()) {
                log.trace("prepareAttachment() : Single file is being uploaded. Delegating to uploadFile()");
            }
            try {
                return prepareAttachment(files.get(0).getFileName(), files.get(0).getContentType(), user, files.get(0).getContents(), tags);
            } catch (IOException ex) {
                if (log.isTraceEnabled()) {
                    log.trace("prepareAttachment() : I/O exception" + ex);
                }
                return null;
            }
        }

        try {
            // create zip file
            log.trace("prepareAttachment(): Creating zip-file");

            File root = new File(DEFAULT_UPLOAD_DIRECTORY, user.getLogin());
            root.mkdirs();
            File file = File.createTempFile("upload_", ".zip", root);


            ZipOutputStream zos = null;

            try {
//                zos = new ZipOutputStream
                zos = new ZipOutputStream(file);
                zos.setEncoding("utf-8");
                zos.setMethod(ZipOutputStream.DEFLATED);
                zos.setLevel(Deflater.BEST_COMPRESSION);

                for (UploadedFile uf : files) {
                    addFileToZip(zos, uf, uf.getFileName());
                }
            } finally {
                FileUtils.close(zos);
            }

//            try (ZipOutputStream zos = new ZipOutputStream(file)) {
//                zos.setEncoding("utf-8");
//                zos.setMethod(ZipOutputStream.DEFLATED);
//                zos.setLevel(Deflater.BEST_COMPRESSION);
//
//                for (UploadedFile uf : files) {
//                    addFileToZip(zos, uf, uf.getFileName());
//                }
//            }

            if (log.isDebugEnabled()) {
                log.debug("prepareAttachment(): Files are saved at " + file);
            }

            if (file.length() > MAX_ZIP_SIZE) {
                file.delete();
                throw new IOException("File too large.");
            }

            // Create attachment
            Attachment att = new Attachment();
            att.setName(file.getName());
            att.setMimeType("application/zip");
            att.setSize(file.length());
            att.setMD5(FileUtils.getMD5(file));
            att.setFileName(user.getLogin() + "/" + file.getName());

            if (log.isTraceEnabled()) {
                log.trace("<< prepareAttachment()");
            }
            return att;
        } catch (IOException ex) {
            log.error("prepareAttachment(): Failed to upload files. ", ex);
            return null;
        }
    }

    @Override
    public Attachment renameAttachment(Long userId, long attachmentId, String name) {
        if (log.isDebugEnabled()) {
            log.debug(">> renameAttachment() : userId =" + userId + ", attachmentId=" + attachmentId);
        }
        if (userId == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< renameAttachment() : null // operation is not permitted");
            }
            return null;
        }

        User u = em.find(User.class, userId);
        if (u == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< renameAttachment() : null // no such user! operation is not permitted");
            }
            return null;
        }
        Attachment att = em.find(Attachment.class, attachmentId);
        if (att == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< renameAttachment() : null // invalid attachmentId=" + attachmentId);
            }
            return null;
        }
        if (!isOwner(u, att) && u.getUserGroup() != User.ADMIN) {
            if (log.isTraceEnabled()) {
                log.trace("<< renameAttachment() : null // operation is not permitted");
            }
            return null;
        }
        att.setName(name);
        if (log.isDebugEnabled()) {
            log.debug(">> renameAttachment() : userId =" + userId + ", attachmentId=" + attachmentId);
        }
        return em.merge(att);
    }

    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    private boolean isOwner(User u, Attachment a) {
        for (User usr : a.getUser()) {
            if (usr.getId().equals(u.getId())) {
                if (log.isTraceEnabled()) {
                    log.trace("<< isOwner(): true // owner of the file");
                }
                return true;
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("<< isOwner(): false");
        }
        return false;
    }
}
