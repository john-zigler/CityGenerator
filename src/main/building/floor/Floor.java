package main.building.floor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Floor {
	private int[][] squares;
	private List<Shape> shapes = new ArrayList<>();
	
	public Floor(int radius, List<Rectangle> rectangles) {
		squares = new int[radius * 2][radius * 2];
		for (Rectangle rectangle : rectangles) {
			addRectangle(rectangle);
		}
	}
	
	public Floor(int radius, int floorRadius) {
		squares = new int[radius * 2][radius * 2];
		shapes.add(new Ellipse2D.Double(radius - floorRadius, radius - floorRadius, floorRadius * 2, floorRadius * 2));
		for (int x = 0; x < radius * 2; x++) {
			for (int y = 0; y < radius * 2; y++) {
				if (Math.pow(x - radius, 2) + Math.pow(y - radius, 2) <= floorRadius * floorRadius) {
					squares[x][y] = 1;
				}
			}
		}
	}

	public int[][] getSquares() {
		return squares;
	}
	public void setSquares(int[][] squares) {
		this.squares = squares;
	}
	private void addRectangle(Rectangle rectangle) {
		shapes.add(rectangle);
		for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
			for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
				squares[x][y] = 1;
			}
		}
	}
	public BufferedImage generateImageForMap(Color color) {
		BufferedImage image = new BufferedImage(squares.length, squares[0].length, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(color);
		for (Shape shape : shapes) {
			graphics.fill(shape);
		}
		return image;
	}
}
