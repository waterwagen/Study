package com.waterwagen.study.java8;

import com.waterwagen.study.util.PojomaticClass;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.pojomatic.annotations.AutoProperty;

import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import static org.junit.Assert.*;

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

  public static <T> Image transform(Image image, BiFunction<Color, T, Color> colorTransformer, T arg) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    WritableImage out = new WritableImage(width, height);
    for (int x = 0; x < width; x++)
      for (int y = 0; y < height; y++)
        out.getPixelWriter().setColor(x, y, colorTransformer.apply(image.getPixelReader().getColor(x, y), arg));
    return out;
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
    verifyImagePixels(image, Java8ImpatientLambdasChapter3ExercisesCompanion::verifyPixelColorBasedOnBorder);
  }

  static void verifyImagePixels(Image image, PixelVerifier pixelVerifier) {
    for(int x = 0; x < image.getWidth(); x++) {
      for(int y = 0; y < image.getHeight(); y++) {
        pixelVerifier.verify(new Point(x, y), image);
      }
    }
  }

  private static void verifyPixelColorBasedOnBorder(Point pixel, Image image) {
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

  static void verifyImageIsDarkerThanOtherImage(Image transformedImage, Image otherImage) {
    verifyImagePixels(transformedImage, (point, image) -> {
      double imageBrightness = image.getPixelReader().getColor(point.x, point.y).getBrightness();
      double otherImageBrightness = otherImage.getPixelReader().getColor(point.x, point.y).getBrightness();
      assertTrue(String.format("Expected transformed image to be darker than the other image but pixel at %s was not.", point),
        imageBrightness < otherImageBrightness);
    });
  }

  static Comparator<String> createStringComparator(UnaryOperator<String> stringTransformer) {
    return (string1, string2) -> stringTransformer.apply(string1).compareTo(stringTransformer.apply(string2));
  }

  @FunctionalInterface
  static interface ColorTransformer {
    Color apply(int x, int y, Color colorAtXY);
  }

  @FunctionalInterface
  private interface PixelVerifier {
    void verify(Point point, Image image);
  }

  @AutoProperty
  static final class Point extends PojomaticClass {

    final int x;

    final int y;

    Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

  }

}
