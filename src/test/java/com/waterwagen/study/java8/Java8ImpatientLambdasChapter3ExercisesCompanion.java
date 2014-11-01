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

  static final int DEFAULT_IMAGE_BORDER_WIDTH = 10;

  static final Color DEFAULT_IMAGE_BORDER_COLOR = Color.BLACK;

  static final BorderSpec DEFAULT_BORDER_SPEC = new BorderSpec(DEFAULT_IMAGE_BORDER_WIDTH, DEFAULT_IMAGE_BORDER_COLOR);

  static void withLock(Lock lock, Runnable action) {
    lock.lock();
    try {
      action.run();
    }
    finally {
      lock.unlock();
    }
  }

  static <T> Image transform(Image image, BiFunction<Color, T, Color> colorTransformer, T arg) {
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
    verifyImageBorderIsBorderColorAndCenterIsNot(image, DEFAULT_BORDER_SPEC);
  }

  static void verifyImageBorderIsBorderColorAndCenterIsNot(Image imageToVerify, BorderSpec borderSpec) {
    verifyImagePixels(imageToVerify, (point, image) -> {
      Color pixelColor = image.getPixelReader().getColor(point.x, point.y);
      if(isWithinImageBorder(point, image, borderSpec.thickness)) {
        verifyImageBorderPixelColor(borderSpec.color, pixelColor, point);
      }
      else {
        verifyNonImageBorderPixelColor(borderSpec.color, pixelColor, point);
      }
    });
  }

  static void verifyImagePixels(Image image, PixelVerifier pixelVerifier) {
    for(int x = 0; x < image.getWidth(); x++) {
      for(int y = 0; y < image.getHeight(); y++) {
        pixelVerifier.verify(new Point(x, y), image);
      }
    }
  }

  static boolean isWithinImageBorder(Point pixel, Image image) {
    return isWithinImageBorder(pixel, image, DEFAULT_IMAGE_BORDER_WIDTH);
  }

  static boolean isWithinImageBorder(Point pixel, Image image, int borderThickness) {
    int imageWidth = (int) image.getWidth();
    int imageHeight = (int) image.getHeight();

    return ((pixel.x <= borderThickness - 1 || imageWidth - pixel.x <= borderThickness)
        || (pixel.y <= borderThickness - 1 || imageHeight - pixel.y <= borderThickness));
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

  static ColorTransformer createColorTransformerForBorder(Image originalImage, BorderSpec borderSpec) {
    return (x,y,color) -> {
      if (isWithinImageBorder(new Point(x, y), originalImage, borderSpec.thickness)) {
        return borderSpec.color;
      }
      return color;
    };
  }

  static <T> Comparator<T> lexicographicComparator(String... fieldsToCompare) {
    return (T object1, T object2) -> {
      for(String field : fieldsToCompare) {
        try {
          String field1 = (String) object1.getClass().getField(field).get(object1);
          String field2 = (String) object2.getClass().getField(field).get(object2);
          int result = field1.compareTo(field2);
          if (result != 0) {
            return result;
          }
        }
        catch(Exception exc) {
          throw new RuntimeException(exc);
        }
      }
      return 0;
    };
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

  static final class BorderSpec extends PojomaticClass {

    final int thickness;

    final Color color;

    BorderSpec(int thickness, Color color) {
      this.thickness = thickness;
      this.color = color;
    }

  }

  static final class Name extends PojomaticClass {

    public final String firstName;

    public final String middleName;

    public final String lastName;

    Name(String firstName, String middleName, String lastName) {
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
    }

  }
}
