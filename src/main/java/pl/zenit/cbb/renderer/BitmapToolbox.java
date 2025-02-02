package pl.zenit.cbb.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;

public class BitmapToolbox {

      public static BufferedImage deepCopy(BufferedImage originalImage) {
            ColorModel colorModel = originalImage.getColorModel();
            boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
            WritableRaster raster = originalImage.copyData(null);
            return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
      }

      public static BufferedImage scaleFast(BufferedImage original, double scaleBy) {            
            Map<Key, Object> hints = new HashMap<>();
            hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            return scale(original, scaleBy, hints);
      }
      public static BufferedImage scaleNice(BufferedImage original, double scaleBy) {
            Map<Key, Object> hints = new HashMap<>();
            hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            return scale(original, scaleBy, hints);
      }
      public static BufferedImage scale(BufferedImage original, double scaleBy, Map<Key, Object> hints) {
            int scaledWidth = (int) ((double) original.getWidth() * scaleBy);
            int scaledHeight = (int) ((double) original.getHeight() * scaleBy);

            BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = scaledImage.createGraphics();            
            hints.forEach(g2d::setRenderingHint);            
            g2d.drawImage(original, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();
            return scaledImage;
      }

      public static BufferedImage adjustContrast(BufferedImage image, double contrastFactor) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        int rgb = image.getRGB(x, y);

                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        red = (int) (contrastFactor * (red - 128) + 128);
                        green = (int) (contrastFactor * (green - 128) + 128);
                        blue = (int) (contrastFactor * (blue - 128) + 128);

                        red = Math.min(255, Math.max(0, red));
                        green = Math.min(255, Math.max(0, green));
                        blue = Math.min(255, Math.max(0, blue));

                        int adjustedRGB = (alpha << 24) | (red << 16) | (green << 8) | blue;

                        adjustedImage.setRGB(x, y, adjustedRGB);
                  }
            }

            return adjustedImage;
      }

      public static BufferedImage adjustSaturation(BufferedImage image, double saturationFactor) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));

                        float[] hsl = new float[3];
                        Color.RGBtoHSB(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), hsl);

                        // Adjust the saturation based on lightness
                        hsl[1] += (1.0 - hsl[2]) * saturationFactor;
                        hsl[1] = Math.max(0.0f, Math.min(1.0f, hsl[1]));

                        int adjustedRGB = Color.HSBtoRGB(hsl[0], hsl[1], hsl[2]);
                        adjustedImage.setRGB(x, y, adjustedRGB);
                  }
            }

            return adjustedImage;
      }

      public static BufferedImage adjustSaturationLuminosity(BufferedImage image, double saturationFactor) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));

                        float[] hsl = new float[3];
                        Color.RGBtoHSB(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), hsl);

                        // Calculate luminosity (brightness) of the pixel
                        float luminosity = (Math.max(Math.max(hsl[0], hsl[1]), hsl[2]) + Math.
                                            min(Math.min(hsl[0], hsl[1]), hsl[2])) / 2;

                        // Adjust the saturation based on luminosity
                        hsl[1] += (1.0 - luminosity) * saturationFactor;
                        hsl[1] = Math.max(0.0f, Math.min(1.0f, hsl[1]));

                        int adjustedRGB = Color.HSBtoRGB(hsl[0], hsl[1], hsl[2]);
                        adjustedImage.setRGB(x, y, adjustedRGB);
                  }
            }

            return adjustedImage;
      }

      public static BufferedImage adjustLightness(BufferedImage image, double brightnessFactor) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));

                        float[] hsl = new float[3];
                        Color.RGBtoHSB(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), hsl);

                        // Calculate luminosity (brightness) of the pixel
                        float luminosity = (Math.max(Math.max(hsl[0], hsl[1]), hsl[2]) + Math.
                                            min(Math.min(hsl[0], hsl[1]), hsl[2])) / 2;

                        // Adjust the brightness based on luminosity
                        luminosity = Math.max(0.0f, Math.min(1.0f, luminosity + (float) brightnessFactor));

                        int adjustedRGB = Color.HSBtoRGB(hsl[0], hsl[1], luminosity);
                        adjustedImage.setRGB(x, y, adjustedRGB);
                  }
            }

            return adjustedImage;
      }

      public static BufferedImage adjustBrightness(BufferedImage image, double brightnessFactor) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));

                        int red = (int) (originalColor.getRed() * brightnessFactor);
                        int green = (int) (originalColor.getGreen() * brightnessFactor);
                        int blue = (int) (originalColor.getBlue() * brightnessFactor);

                        // Ensure the values are in the valid range (0-255)
                        red = Math.min(255, Math.max(0, red));
                        green = Math.min(255, Math.max(0, green));
                        blue = Math.min(255, Math.max(0, blue));

                        Color adjustedColor = new Color(red, green, blue);
                        adjustedImage.setRGB(x, y, adjustedColor.getRGB());
                  }
            }

            return adjustedImage;
      }

      public static BufferedImage adjustWhiteLevel(BufferedImage image, double whiteLevelFactor) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Find the maximum RGB value in the original image (white point)
            int maxRGB = 0;

            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));

                        int rgbSum = originalColor.getRed() + originalColor.getGreen() + originalColor.getBlue();
                        if ( rgbSum > maxRGB ) {
                              maxRGB = rgbSum;
                        }
                  }
            }

            // Calculate the scaling factor to adjust the white point
            double scaleFactor = 255.0 / maxRGB;

            // Apply the white level adjustment to each pixel
            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));

                        int red = (int) (originalColor.getRed() * scaleFactor * whiteLevelFactor);
                        int green = (int) (originalColor.getGreen() * scaleFactor * whiteLevelFactor);
                        int blue = (int) (originalColor.getBlue() * scaleFactor * whiteLevelFactor);

                        // Ensure the values are in the valid range (0-255)
                        red = Math.min(255, Math.max(0, red));
                        green = Math.min(255, Math.max(0, green));
                        blue = Math.min(255, Math.max(0, blue));

                        Color adjustedColor = new Color(red, green, blue);
                        adjustedImage.setRGB(x, y, adjustedColor.getRGB());
                  }
            }

            return adjustedImage;
      }

      public static BufferedImage adjustBlackLevel(BufferedImage image, double blackLevelFactor) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Find the minimum RGB value in the original image (black point)
            int minRGB = Integer.MAX_VALUE;

            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));

                        int rgbSum = originalColor.getRed() + originalColor.getGreen() + originalColor.getBlue();
                        if ( rgbSum < minRGB ) {
                              minRGB = rgbSum;
                        }
                  }
            }

            // Calculate the scaling factor to adjust the black point
            double scaleFactor = 255.0 / minRGB;

            // Apply the black level adjustment to each pixel
            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));

                        int red = (int) (originalColor.getRed() * scaleFactor * blackLevelFactor);
                        int green = (int) (originalColor.getGreen() * scaleFactor * blackLevelFactor);
                        int blue = (int) (originalColor.getBlue() * scaleFactor * blackLevelFactor);

                        // Ensure the values are in the valid range (0-255)
                        red = Math.min(255, Math.max(0, red));
                        green = Math.min(255, Math.max(0, green));
                        blue = Math.min(255, Math.max(0, blue));

                        Color adjustedColor = new Color(red, green, blue);
                        adjustedImage.setRGB(x, y, adjustedColor.getRGB());
                  }
            }

            return adjustedImage;
      }

      public static BufferedImage adjustGrayPoint(BufferedImage image, int grayPoint) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Calculate the gray point difference
            int deltaGray = grayPoint - 128; // Assuming 128 as the default gray point

            // Apply the gray point adjustment
            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));

                        int red = originalColor.getRed() + deltaGray;
                        int green = originalColor.getGreen() + deltaGray;
                        int blue = originalColor.getBlue() + deltaGray;

                        // Ensure the values are in the valid range (0-255)
                        red = Math.min(255, Math.max(0, red));
                        green = Math.min(255, Math.max(0, green));
                        blue = Math.min(255, Math.max(0, blue));

                        Color adjustedColor = new Color(red, green, blue);
                        adjustedImage.setRGB(x, y, adjustedColor.getRGB());
                  }
            }

            return adjustedImage;
      }

      public static BufferedImage adjustHue(BufferedImage image, float hueShift) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));
                        float[] hsl = new float[3];

                        // Convert RGB to HSL
                        Color.RGBtoHSB(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue(), hsl);

                        // Shift the hue component
                        float adjustedHue = (hsl[0] + hueShift) % 1.0f;
                        if ( adjustedHue < 0 ) {
                              adjustedHue += 1.0f;
                        }

                        // Convert back to RGB
                        int rgb = Color.HSBtoRGB(adjustedHue, hsl[1], hsl[2]);

                        adjustedImage.setRGB(x, y, rgb);
                  }
            }

            return adjustedImage;
      }

      public static BufferedImage tightenOutputLevels(BufferedImage image, int outputMin, int outputMax) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int inputMin = Integer.MAX_VALUE;
            int inputMax = Integer.MIN_VALUE;

            // Find the minimum and maximum values in the original image
            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));
                        int grayValue = (originalColor.getRed() + originalColor.getGreen() + originalColor.getBlue()) / 3;

                        if ( grayValue < inputMin ) {
                              inputMin = grayValue;
                        }
                        if ( grayValue > inputMax ) {
                              inputMax = grayValue;
                        }
                  }
            }

            // Calculate the scaling factors
            double inputRange = inputMax - inputMin;
            double outputRange = outputMax - outputMin;
            double scale = outputRange / inputRange;

            // Apply the output levels tightening
            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));
                        int grayValue = (originalColor.getRed() + originalColor.getGreen() + originalColor.getBlue()) / 3;

                        // Scale the gray value to the desired output range
                        int adjustedValue = (int) (outputMin + (grayValue - inputMin) * scale);
                        adjustedValue = Math.min(outputMax, Math.max(outputMin, adjustedValue));

                        Color adjustedColor = new Color(adjustedValue, adjustedValue, adjustedValue);
                        adjustedImage.setRGB(x, y, adjustedColor.getRGB());
                  }
            }

            return adjustedImage;
      }

      public static BufferedImage adjustTemperature(BufferedImage image, int temperatureChange) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Apply the temperature adjustment
            for ( int y = 0; y < height; y++ ) {
                  for ( int x = 0; x < width; x++ ) {
                        Color originalColor = new Color(image.getRGB(x, y));

                        int red = originalColor.getRed() + temperatureChange;
                        int green = originalColor.getGreen();
                        int blue = originalColor.getBlue() - temperatureChange;

                        // Ensure the values are in the valid range (0-255)
                        red = Math.min(255, Math.max(0, red));
                        green = Math.min(255, Math.max(0, green));
                        blue = Math.min(255, Math.max(0, blue));

                        Color adjustedColor = new Color(red, green, blue);
                        adjustedImage.setRGB(x, y, adjustedColor.getRGB());
                  }
            }

            return adjustedImage;
      }

} //end of class BitmapLoader
