package com.waterwagen.study.java8;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.locks.Lock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Java8ImpatientLambdasChapter3ExercisesCompanion {

  static final String GRAY_SQUARE_IMAGE_FILE_NAME = "./src/test/resources/grayRectangle.png";

  static final int IMAGE_BORDER_WIDTH = 10;

  static final Color IMAGE_BORDER_COLOR = Color.BLACK;

  static void withLock(Lock lock, Runnable action) {
    lock.lock();
    try {
      action.run();
    }
    finally {
      lock.unlock();
    }
  }

  static Image transform(Image image, ColorTransformer colorTransformer) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    WritableImage out = new WritableImage(width, height);
    for (int x = 0; x < width; x++)
      for (int y = 0; y < height; y++)
        out.getPixelWriter().setColor(x, y, colorTransformer.apply(x, y, image.getPixelReader().getColor(x, y)));
    return out;
  }

  static void verifyImageBorderIsBorderColorAndCenterIsNot(Image image) {
    for(int x = 0; x < image.getWidth(); x++) {
      for(int y = 0; y < image.getHeight(); y++) {
        verifyPixelColor(new Point(x, y), image);
      }
    }
  }

  private static void verifyPixelColor(Point pixel, Image image) {
    Color pixelColor = image.getPixelReader().getColor(pixel.x, pixel.y);
    if(isWithinImageBorder(pixel, image)) {
      verifyImageBorderPixelColor(IMAGE_BORDER_COLOR, pixelColor, pixel);
    }
    else {
      verifyNonImageBorderPixelColor(IMAGE_BORDER_COLOR, pixelColor, pixel);
    }
  }

  static boolean isWithinImageBorder(Point pixel, Image image) {
    int imageWidth = (int) image.getWidth();
    int imageHeight = (int) image.getHeight();

    return ((pixel.x <= IMAGE_BORDER_WIDTH - 1 || imageWidth - pixel.x <= IMAGE_BORDER_WIDTH)
        || (pixel.y <= IMAGE_BORDER_WIDTH - 1 || imageHeight - pixel.y <= IMAGE_BORDER_WIDTH));
  }

  private static void verifyImageBorderPixelColor(Color expectedBorderColor, Color pixelColor, Point pixel) {
    assertEquals(
        String.format("The color at pixel %d,%d in the image IS within the border but is NOT the border color %s.",
            pixel.x, pixel.y, colorAsRgbString(expectedBorderColor)),
        colorAsRgbString(expectedBorderColor), colorAsRgbString(pixelColor));
  }

  private static void verifyNonImageBorderPixelColor(Color expectedBorderColor, Color pixelColor, Point pixel) {
    assertFalse(
      String.format("The color at pixel %d,%d in the image is NOT within the border but IS the border color %s.",
        pixel.x, pixel.y, colorAsRgbString(expectedBorderColor)),
      expectedBorderColor.equals(pixelColor));
  }

  static String colorAsRgbString(Color color) {
    return color.getRed() + "/" + color.getGreen() + "/" + color.getBlue();
  }

  @FunctionalInterface
  static interface ColorTransformer {
    Color apply(int x, int y, Color colorAtXY);
  }

  static final class Point {

    final int x;

    final int y;

    Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

  }

}
