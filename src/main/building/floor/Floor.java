package main.building.floor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Floor {
	private int[][] squares;
	private List<Rectangle> rectangles = new ArrayList<>();
	
	public Floor(int radius, List<Rectangle> rectangles) {
		squares = new int[radius * 2][radius * 2];
		for (Rectangle rectangle : rectangles) {
			addRectangle(rectangle);
		}
	}

	public int[][] getSquares() {
		return squares;
	}
	public void setSquares(int[][] squares) {
		this.squares = squares;
	}
	private void addRectangle(Rectangle rectangle) {
		rectangles.add(rectangle);
		for (int i = rectangle.x; i < rectangle.x + rectangle.width; i++) {
			for (int j = rectangle.y; j < rectangle.y + rectangle.height; j++) {
				squares[i][j] = 1;
			}
		}
	}
	public BufferedImage generateImageForMap() {
		BufferedImage image = new BufferedImage(squares.length, squares[0].length, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.GRAY);
		for (Rectangle rectangle : rectangles) {
			graphics.fill(rectangle);
		}
		return image;
	}
}
