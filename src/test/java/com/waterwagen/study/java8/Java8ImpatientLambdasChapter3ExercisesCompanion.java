package com.waterwagen.study.java8;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.locks.Lock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Java8ImpatientLambdasChapter3ExercisesCompanion {

  static final String GRAY_SQUARE_IMAGE_FILE_NAME = "./src/test/resources/grayRectangle.png";

  static final int IMAGE_BORDER_WIDTH = 10;

  static void withLock(Lock lock, Runnable action) {
    lock.lock();
    try {
      action.run();
    }
    finally {
      lock.unlock();
    }
  }

  static void verifyImageBorderIsBlackAndCenterIsNot(Image image) {
    PixelReader pixelReader = image.getPixelReader();
    int imageWidth = (int) image.getWidth();
    int imageHeight = (int) image.getHeight();
    Color borderColor = Color.BLACK;

    verifyColorOfCorners(pixelReader, imageWidth, imageHeight, borderColor);
    verifyColorOfBorderEdges(pixelReader, imageWidth, imageHeight, borderColor);
    verifyCenterIsNotColor(pixelReader, imageWidth, imageHeight, borderColor);
  }

  static void verifyColorOfCorners(PixelReader pixelReader,
                                   int imageWidth,
                                   int imageHeight,
                                   Color expectedColor) {
    assertEquals("The corner pixel was not the expected border color.",
        colorAsRgbString(expectedColor), colorAsRgbString(pixelReader.getColor(0, 0)));
    assertEquals("The corner pixel was not the expected border color.",
        colorAsRgbString(expectedColor), colorAsRgbString(pixelReader.getColor(imageWidth - 1, 0)));
    assertEquals("The corner pixel was not the expected border color.",
        colorAsRgbString(expectedColor), colorAsRgbString(pixelReader.getColor(imageWidth - 1, imageHeight - 1)));
    assertEquals("The corner pixel was not the expected border color.",
        colorAsRgbString(expectedColor), colorAsRgbString(pixelReader.getColor(0, imageHeight - 1)));
  }

  static void verifyColorOfBorderEdges(PixelReader pixelReader,
                                       int imageWidth,
                                       int imageHeight,
                                       Color expectedColor) {
    verifyTopBorderEdge(pixelReader, imageWidth, expectedColor);
    verifyRightBorderEdge(pixelReader, imageWidth, imageHeight, expectedColor);
    verifyBottomBorderEdge(pixelReader, imageWidth, imageHeight, expectedColor);
    verifyLeftBorderEdge(pixelReader, imageHeight, expectedColor);
  }

  private static void verifyTopBorderEdge(PixelReader pixelReader, int imageWidth, Color expectedBorderColor) {
    int topBorderEdgeX = imageWidth / 2;
    int topBorderEdgeY = IMAGE_BORDER_WIDTH - 1;
    Point borderEdgePoint = new Point(topBorderEdgeX, topBorderEdgeY);
    Point adjacentPoint = new Point(topBorderEdgeX, topBorderEdgeY + 1);
    String borderLabel = "top";

    verifyBorderEdge(pixelReader, expectedBorderColor, borderEdgePoint, adjacentPoint, borderLabel);
  }

  private static void verifyBorderEdge(PixelReader pixelReader,
                                       Color expectedBorderColor,
                                       Point borderEdgePoint,
                                       Point adjacentPoint,
                                       String borderLabel) {
    Color borderEdgeColor = pixelReader.getColor(borderEdgePoint.getX(), borderEdgePoint.getY());
    Color adjacentColor = pixelReader.getColor(adjacentPoint.getX(), adjacentPoint.getY());
    verifyColorOfBorderEdge(expectedBorderColor, borderEdgeColor, borderLabel);
    verifyColorAdjacentToBorderEdge(expectedBorderColor, adjacentColor, borderLabel);
  }

  private static void verifyColorAdjacentToBorderEdge(Color expectedColor,
                                                      Color adjacentColor,
                                                      String borderLabel) {
    assertFalse(
      String.format("The pixel adjacent to the %s border edge should not have been the border color.", borderLabel),
      expectedColor.equals(adjacentColor));
  }

  private static void verifyColorOfBorderEdge(Color expectedColor,
                                              Color borderColor,
                                              String borderLabel) {
    assertEquals(String.format("The %s border edge pixel was not the expected border color.", borderLabel),
      colorAsRgbString(expectedColor), colorAsRgbString(borderColor));
  }

  private static void verifyRightBorderEdge(PixelReader pixelReader,
                                            int imageWidth,
                                            int imageHeight,
                                            Color expectedBorderColor) {
    int rightBorderEdgeX = imageWidth - IMAGE_BORDER_WIDTH;
    int rightBorderEdgeY = imageHeight / 2;
    Point borderEdgePoint = new Point(rightBorderEdgeX, rightBorderEdgeY);
    Point adjacentPoint = new Point(rightBorderEdgeX - 1, rightBorderEdgeY);
    String borderLabel = "right";

    verifyBorderEdge(pixelReader, expectedBorderColor, borderEdgePoint, adjacentPoint, borderLabel);
  }


  private static void verifyBottomBorderEdge(PixelReader pixelReader,
                                             int imageWidth,
                                             int imageHeight,
                                             Color expectedBorderColor) {
    int bottomBorderEdgeX = imageWidth / 2;
    int bottomBorderEdgeY = imageHeight - IMAGE_BORDER_WIDTH;
    Point borderEdgePoint = new Point(bottomBorderEdgeX, bottomBorderEdgeY);
    Point adjacentPoint = new Point(bottomBorderEdgeX, bottomBorderEdgeY - 1);
    String borderLabel = "bottom";

    verifyBorderEdge(pixelReader, expectedBorderColor, borderEdgePoint, adjacentPoint, borderLabel);
  }

  private static void verifyLeftBorderEdge(PixelReader pixelReader, int imageHeight, Color expectedBorderColor) {
    int leftBorderEdgeX = IMAGE_BORDER_WIDTH - 1;
    int leftBorderEdgeY = imageHeight / 2;
    Point borderEdgePoint = new Point(leftBorderEdgeX, leftBorderEdgeY);
    Point adjacentPoint = new Point(leftBorderEdgeX + 1, leftBorderEdgeY);
    String borderLabel = "left";

    verifyBorderEdge(pixelReader, expectedBorderColor, borderEdgePoint, adjacentPoint, borderLabel);
  }

  static void verifyCenterIsNotColor(PixelReader pixelReader,
                                      int imageWidth,
                                      int imageHeight,
                                      Color borderColor) {
    assertFalse("Expected center(ish) pixel to NOT be color of border.",
      pixelReader.getColor(imageWidth / 2, imageHeight / 2).equals(borderColor));
  }

  static String colorAsRgbString(Color color) {
    return color.getRed() + "/" + color.getGreen() + "/" + color.getBlue();
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

  static boolean isWithinImageBorder(int x, int y, Image image) {
    int imageWidth = (int) image.getWidth();
    int imageHeight = (int) image.getHeight();

    return ((x <= IMAGE_BORDER_WIDTH - 1 || imageWidth - x <= IMAGE_BORDER_WIDTH)
      || (y <= IMAGE_BORDER_WIDTH - 1 || imageHeight - y <= IMAGE_BORDER_WIDTH));
  }

  @FunctionalInterface
  static interface ColorTransformer {
    Color apply(int x, int y, Color colorAtXY);
  }

  static final class Point {

    private final int x;

    private final int y;

    Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    int getX() {
      return x;
    }

    int getY() {
      return y;
    }

  }

}
