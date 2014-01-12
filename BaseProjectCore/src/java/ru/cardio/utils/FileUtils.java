package ru.cardio.utils;

import java.io.*;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class FileUtils {

    public static final int BUFFER_SIZE = 10 * 1024;

    public static String saveUploadedFile(InputStream is, String originalName, String uploadDirectory) {
        try {
            System.out.println("saveUploadedFile: originalName = " + originalName);
            File uploadDir = new File(uploadDirectory);
            uploadDir.mkdirs();
//            String name = (originalName.substring(originalName.lastIndexOf('.') > -1) ? 
//            File targetFile = File.createTempFile("upload", originalName.substring(originalName.lastIndexOf('.') ), uploadDir);
            File targetFile = File.createTempFile("upload", originalName, uploadDir);
            OutputStream os = new FileOutputStream(targetFile);
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                int read = 0;
                while ((read = is.read(buffer)) >= 0) {
                    os.write(buffer, 0, read);
                }
                return targetFile.getAbsolutePath();
            } finally {
                try {
                    os.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }
}
