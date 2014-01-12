package ru.cardio.core.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class ImageUtils {

    private static final int DEFAULT_IMG_WIDTH = 100;
    private static final int DEFAULT_IMG_HEIGHT = 100;

    public static BufferedImage resizeImage(byte[] contents, Integer w, Integer h) throws IOException {
        InputStream in = new ByteArrayInputStream(contents);
        BufferedImage bImageFromConvert = ImageIO.read(in);
        System.out.println("resizeImage: height " + bImageFromConvert.getHeight());
        return resizeImage(bImageFromConvert, w, h);
    }

    public static byte[] resizeImage(byte[] contents, Integer w, Integer h, String iType) throws IOException {
        BufferedImage image = resizeImage(contents, w, h);
//        System.out.println("resized image size = " + image.get);

        return bufferedImageToBytes(image, iType);
    }

    public static byte[] bufferedImageToBytes(BufferedImage img, String type) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, type, baos);
        System.out.println("ImageIO.write(image, iType, baos); baos size = " + baos.size());
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        System.out.println("resized byte length = " + imageInByte.length);
        baos.close();
        return imageInByte;
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, Integer w, Integer h) {
        if (w <= 0 || h <= 0) {
            return originalImage;
        }
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        BufferedImage resizedImage = new BufferedImage(w, h, type);

        System.out.println("resized image height = " + resizedImage.getHeight());

        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w, h, null);
        g.dispose();
        return resizedImage;
    }

    public static BufferedImage resizeImage(File image, Integer width, Integer height) throws IOException {
        BufferedImage originalImage = ImageIO.read(image);


        Integer w = (width == null) ? DEFAULT_IMG_WIDTH : width;
        Integer h = (height == null) ? DEFAULT_IMG_HEIGHT : height;

        return resizeImage(originalImage, w, h);
    }

    private static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        BufferedImage dest = src.getSubimage(0, 0, rect.width, rect.height);
        return dest;
    }

    public static BufferedImage cropImage(BufferedImage src, Integer x, Integer y, Integer w, Integer h) {
        System.out.println("utils: cropImage: x=" + x + ";y=" + y + ";w=" + w + ";h=" + h);
        BufferedImage dest = src.getSubimage(x, y, w, h);
        return dest;
    }
}
