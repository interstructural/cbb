package pl.zenit.cbb;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends javax.swing.JPanel {

    private Image image = null;

    public ImagePanel() {
        setLayout(new javax.swing.GroupLayout(this));
    }

    public void setImage(Image image) {
        this.image = image;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) return;

        Dimension target = new Dimension(getWidth(), getHeight());
        Dimension src = new Dimension(image.getWidth(null), image.getHeight(null));
        Dimension scaled = getScaledDimensionDown(src, target);

        Image resized = (image instanceof BufferedImage)
            ? resizeImage((BufferedImage)image, scaled.width, scaled.height)
            : resizeImage(image, scaled.width, scaled.height, BufferedImage.TYPE_INT_ARGB);

        int x = (target.width - resized.getWidth(null)) / 2;
        int y = (target.height - resized.getHeight(null)) / 2;
        g.drawImage(resized, x, y, null);
    }

    private static Dimension getScaledDimensionDown(Dimension imgSize, Dimension boundary) {
        int newWidth = imgSize.width > boundary.width ? boundary.width : imgSize.width;
        int newHeight = (newWidth * imgSize.height) / imgSize.width;
        if (newHeight > boundary.height) {
            newHeight = boundary.height;
            newWidth = (newHeight * imgSize.width) / imgSize.height;
        }
        return new Dimension(newWidth, newHeight);
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return resizeImageWithHints(originalImage, targetWidth, targetHeight, originalImage.getType());
    }

    private static Image resizeImage(Image originalImage, int width, int height, int bmp_type) {
        return resizeImageWithHints(originalImage, width, height, bmp_type);
    }

    private static BufferedImage resizeImageWithHints(Image originalImage, int width, int height, int type) {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }



}
