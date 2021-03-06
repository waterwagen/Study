package com.waterwagen.study.java8;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.waterwagen.Utilities;
import com.waterwagen.study.util.PojomaticClass;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.pojomatic.annotations.AutoProperty;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
      for (int y = 0; y < height; y++) {
        Color pixelColor = image.getPixelReader().getColor(x, y);
        out.getPixelWriter().setColor(x, y, colorTransformer.apply(pixelColor, x, y, image));
      }
    return out;
  }

  static void verifyImageBorderIsBorderColorAndCenterIsNot(Image image) {
    verifyImageBorderIsBorderColorAndCenterIsNot(image, DEFAULT_BORDER_SPEC);
  }

  static void verifyImageBorderIsBorderColorAndCenterIsNot(Image imageToVerify, BorderSpec borderSpec) {
    verifyImagePixels(imageToVerify, (point, image) -> {
      Color pixelColor = image.getPixelReader().getColor(point.x, point.y);
      if (isWithinImageBorder(point, image, borderSpec.thickness)) {
        verifyImageBorderPixelColor(borderSpec.color, pixelColor, point);
      } else {
        verifyNonImageBorderPixelIsNotColor(borderSpec.color, pixelColor, point);
      }
    });
  }

  static void verifyImageBorderColor(Image imageToVerify, BorderSpec borderSpec) {
    verifyImagePixels(imageToVerify, (point, image) -> {
      Color pixelColor = image.getPixelReader().getColor(point.x, point.y);
      if(isWithinImageBorder(point, image, borderSpec.thickness)) {
        verifyImageBorderPixelColor(borderSpec.color, pixelColor, point);
      }
    });
  }

  static void verifyNonBorderImageColor(Image imageToVerify, BorderSpec borderSpec, Color nonBorderColor) {
    verifyImagePixels(imageToVerify, (point, image) -> {
      Color pixelColor = image.getPixelReader().getColor(point.x, point.y);
      if(!isWithinImageBorder(point, image, borderSpec.thickness)) {
        verifyNonImageBorderPixelColor(nonBorderColor, pixelColor, point);
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
    verifyPixelIsColor(expectedBorderColor,
      pixelColor,
      pixel,
      "The color at pixel %d,%d in the image IS within the border but is NOT the border color %s.");
  }

  static void verifyPixelIsColor(Color expectedBorderColor,
                                 Color pixelColor,
                                 Point pixel,
                                 String errorMessage) {
    assertEquals(
      String.format(errorMessage, pixel.x, pixel.y, colorAsRgbString(expectedBorderColor)),
      colorAsRgbString(expectedBorderColor), colorAsRgbString(pixelColor));
  }

  private static void verifyNonImageBorderPixelColor(Color expectedNonBorderColor, Color pixelColor, Point pixel) {
    verifyPixelIsColor(
      expectedNonBorderColor,
      pixelColor,
      pixel,
      "The color at pixel %d,%d in the image is in the image center but is NOT the non-border color %s.");
  }

  private static void verifyNonImageBorderPixelIsNotColor(Color expectedBorderColor, Color pixelColor, Point pixel) {
    verifyPixelIsNotColor(expectedBorderColor,
      pixelColor,
      pixel,
      "The color at pixel %d,%d in the image is NOT within the border but IS the border color %s.");
  }

  static void verifyPixelIsNotColor(Color expectedBorderColor,
                                    Color pixelColor,
                                    Point pixel,
                                    String errorMessage) {
    assertFalse(
      String.format(errorMessage, pixel.x, pixel.y, colorAsRgbString(expectedBorderColor)),
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
    return (color, x, y, image) -> {
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

  static Point getCenterPointOfImage(Image originalImage) {
    return new Point((int) originalImage.getWidth() / 2, (int) originalImage.getHeight() / 2);
  }

  static Color getImageColorAtPoint(Image image, Point point) {
    return image.getPixelReader().getColor(point.x, point.y);
  }

  static ColorTransformer combineColorTransformers(ColorTransformer... transformers) {
    return (color, x, y, image) -> {
      Color result = color;
      for (ColorTransformer colorTransformer : transformers) {
        result = colorTransformer.apply(result, x, y, image);
      }
      return result;
    };
  }

  static ColorTransformer toColorTransformer(UnaryOperator<Color> brightenOperator) {
    return (color, x, y, image) -> brightenOperator.apply(color);
  }

  static Color blurPixelColor(Color color, int x, int y, Image image) {
    RgbAccumulation rgbAccumulation = Sets.newHashSet(AdjacentPixel.validValues(new Point(x, y), image)).stream()
      .map(ap -> ap.getColor(new Point(x, y), image))
      .map(RgbAccumulation::fromColor)
      .reduce(RgbAccumulation.fromColor(color), RgbAccumulation::add);
    return rgbAccumulation.toColor();
  }

  static void verifyColorsAreEqual(Color pixelColor, Color expectedColor) {
    assertEquals("Pixel color red element did not match the expected brightened then blurred color.", expectedColor.getRed(), pixelColor.getRed(), 0.01);
    assertEquals("Pixel color green element did not match the expected brightened then blurred color.", expectedColor.getGreen(), pixelColor.getGreen(), 0.01);
    assertEquals("Pixel color blue element did not match the expected brightened then blurred color.", expectedColor.getBlue(), pixelColor.getBlue(), 0.01);
  }

  static enum AdjacentPixel {

    NORTH(0, -1),
    NORTHEAST(1, -1),
    EAST(1, -1),
    SOUTHEAST(1, 1),
    SOUTH(0, 1),
    SOUTHWEST(-1, 1),
    WEST(-1, 0),
    NORTHWEST(-1, -1);

    private final int relativeX, relativeY;

    private AdjacentPixel(int relativeX, int relativeY) {
      this.relativeX = relativeX;
      this.relativeY = relativeY;
    }

    static Set<AdjacentPixel> validValues(Point pixel, Image image) {
      return Stream.of(values())
        .filter(ap -> ap.isValidFor(pixel, image))
        .collect(Collectors.toCollection(Sets::newHashSet));
    }

    boolean isValidFor(Point pixel, Image image) {
      int thisX = pixel.x + relativeX;
      int thisY = pixel.y + relativeY;
      return (thisX >= 0 && thisX < image.getWidth()) && (thisY >= 0 && thisY < image.getHeight());
    }

    Color getColor(Point pixel, Image image) {
      if(!isValidFor(pixel, image)) {
        throw new IllegalArgumentException("This adjacent pixel is at a position outside of the image boundary.");
      }

      return image.getPixelReader().getColor(pixel.x + relativeX, pixel.y + relativeY);
    }

  }

  @FunctionalInterface
  static interface ColorTransformer {
    Color apply(Color colorAtXY, int x, int y, Image image);
  }

  @FunctionalInterface
  interface PixelVerifier {
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

  static class LatentImage {

    private final List<ColorTransformer> pendingOperations = Lists.newArrayList();

    private Image in;

    private LatentImage(Image in) {
      this.in = in;
    }

    static LatentImage from(Image in) {
      return new LatentImage(in);
    }

    LatentImage transform(UnaryOperator<Color> unaryOperator) {
      return transform((color, x, y, image) -> unaryOperator.apply(color));
    }

    LatentImage transform(ColorTransformer colorTransformer) {
      pendingOperations.add(colorTransformer);
      return this;
    }

    LatentImage transformConvolutionFilter(ColorTransformer colorTransformer) {
      in = toImage();
      pendingOperations.clear();
      return transform(colorTransformer);
    }

    Image toImage() {
      int width = (int) in.getWidth();
      int height = (int) in.getHeight();
      WritableImage out = new WritableImage(width, height);
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          Color result = in.getPixelReader().getColor(x, y);
          for (ColorTransformer colorTransformer : pendingOperations) {
            result = colorTransformer.apply(result, x, y, in);
          }
          out.getPixelWriter().setColor(x, y, result);
        }
      }
      return out;
    }

  }

  static class RgbAccumulation {

    private final double redAccumulation;

    private final double greenAccumulation;

    private final double blueAccumulation;

    private final int instanceCount;

    private RgbAccumulation(Color color) {
      this(color.getRed(), color.getGreen(), color.getBlue(), 1);
    }

    private RgbAccumulation(double redAccumulation,
                            double greenAccumulation,
                            double blueAccumulation,
                            int instanceCount) {
      this.redAccumulation = redAccumulation;
      this.greenAccumulation = greenAccumulation;
      this.blueAccumulation = blueAccumulation;
      this.instanceCount = instanceCount;
    }

    static RgbAccumulation fromColor(Color color) {
      return new RgbAccumulation(color);
    }

    static RgbAccumulation add(RgbAccumulation... rgbAccumulations) {
      double redAccumulation = Stream.of(rgbAccumulations).mapToDouble(RgbAccumulation::getRedAccumulation).sum();
      double greenAccumulation = Stream.of(rgbAccumulations).mapToDouble(RgbAccumulation::getGreenAccumulation).sum();
      double blueAccumulation = Stream.of(rgbAccumulations).mapToDouble(RgbAccumulation::getBlueAccumulation).sum();
      int instanceCount = Stream.of(rgbAccumulations).mapToInt(RgbAccumulation::getInstanceCount).sum();

      return new RgbAccumulation(redAccumulation, greenAccumulation, blueAccumulation, instanceCount);
    }

    Color toColor() {
      double red = redAccumulation / instanceCount;
      double green = greenAccumulation / instanceCount;
      double blue = blueAccumulation / instanceCount;
      return Color.color(red, green, blue, 1);
    }

    private double getRedAccumulation() {
      return redAccumulation;
    }

    private double getGreenAccumulation() {
      return greenAccumulation;
    }

    private double getBlueAccumulation() {
      return blueAccumulation;
    }

    public int getInstanceCount() {
      return instanceCount;
    }

  }

}
