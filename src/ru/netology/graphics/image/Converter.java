package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {
    private int maxWidth = 0;
    private int maxHeight = 0;
    private double maxRatio = 0.0;
    private Schema schema = new Schema();

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        double imgRatio = (double) img.getWidth() / img.getHeight();
        if (maxRatio != 0.0) {
            if (imgRatio > maxRatio) {
                throw new BadImageSizeException(imgRatio, maxRatio);
            }
        }

        int newImgWidth = 0;
        int newImgHeight = 0;

        if (maxWidth != 0 && maxHeight == 0) {
            newImgHeight = (int) (maxWidth / imgRatio);
        } else if (maxHeight != 0 & maxWidth == 0) {
            newImgWidth = (int) (maxHeight * imgRatio);
        } else if (maxWidth != 0 && maxHeight != 0) {
            newImgWidth = maxWidth;
            newImgHeight = maxHeight;
        } else {
            newImgWidth = img.getWidth();
            newImgHeight = img.getHeight();
        }

        Image scaledImage = img.getScaledInstance(newImgWidth, newImgHeight, BufferedImage.SCALE_SMOOTH);

        BufferedImage bwImg = new BufferedImage(newImgWidth, newImgHeight, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = bwImg.createGraphics();

        graphics.drawImage(scaledImage, 0, 0, null);

        WritableRaster bwRaster = bwImg.getRaster();

        StringBuilder sb = new StringBuilder();
        for (int h = 0; h < bwRaster.getHeight(); h++) {
            for (int w = 0; w < bwRaster.getWidth(); w++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                sb.append(c).append(c);//запоминаем символ c, например, в двумерном массиве или как-то ещё на ваше усмотрение
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.textColorSchema = schema;
    }
}
