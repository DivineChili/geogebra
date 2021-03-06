package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.factories.AwtFactory;

/**
 * bounding box construction for selected elements
 * 
 * @author csilla
 *
 */
public class BoundingBox {
	private GRectangle2D rectangle;
	private ArrayList<GEllipse2DDouble> handlers;
	private ArrayList<GGeneralPath> cropHandlers;
	private int nrHandlers = 8;
	private boolean isCropBox = false;
	private boolean isImage = false;

	/**
	 * Make new bounding box
	 * 
	 * @param isImage
	 *            true if is boundingBox of image
	 */
	public BoundingBox(boolean isImage) {
		setHandlers(new ArrayList<GEllipse2DDouble>());
		if (isImage) {
			this.isImage = isImage;
			setCropHandlers(new ArrayList<GGeneralPath>());
		}
	}

	/**
	 * @return box part of bounding box construction
	 */
	public GRectangle2D getRectangle() {
		return rectangle;
	}

	/**
	 * @param rectangle
	 *            - box part of bounding box construction
	 */
	public void setRectangle(GRectangle2D rectangle) {
		this.rectangle = rectangle;
		if (rectangle != null) {
			createHandlers();
		}
	}

	/**
	 * @return handler points of bounding box construction
	 */
	public ArrayList<GEllipse2DDouble> getHandlers() {
		return handlers;
	}

	/**
	 * @param handlers
	 *            - points of bounding box construction
	 */
	public void setHandlers(ArrayList<GEllipse2DDouble> handlers) {
		this.handlers = handlers;
	}

	/**
	 * @return true if cropBox should be shown instead of boundingBox
	 */
	public boolean isCropBox() {
		return isCropBox;
	}

	/**
	 * @param isCropBox
	 *            set if boundingBox or cropBox should be shown
	 */
	public void setCropBox(boolean isCropBox) {
		this.isCropBox = isCropBox;
	}

	/**
	 * @return crop handlers
	 */
	public ArrayList<GGeneralPath> getCropHandlers() {
		return cropHandlers;
	}

	/**
	 * @param cropHandlers
	 *            list of crop handlers
	 */
	public void setCropHandlers(ArrayList<GGeneralPath> cropHandlers) {
		this.cropHandlers = cropHandlers;
	}

	/**
	 * @return number of needed handlers
	 */
	public int getNrHandlers() {
		return nrHandlers;
	}

	/**
	 * @param nrHandlers
	 *            - number of handlers
	 */
	public void setNrHandlers(int nrHandlers) {
		this.nrHandlers = nrHandlers;
	}

	private void createHandlers() {
		if (handlers == null) {
			handlers = new ArrayList<>();
		}
		handlers.clear();
		// init bounding box handler list
		for (int i = 0; i < /* = */nrHandlers; i++) {
			GEllipse2DDouble handler = AwtFactory.getPrototype()
					.newEllipse2DDouble();
			handlers.add(handler);
		}
		createBoundingBoxHandlers();
		if (isImage) {
			if (cropHandlers == null) {
				cropHandlers = new ArrayList<>();
			}
			cropHandlers.clear();
			for (int i = 0; i < /* = */nrHandlers; i++) {
				GGeneralPath cropHandler = AwtFactory.getPrototype()
						.newGeneralPath();
				cropHandlers.add(cropHandler);
			}
			createCropHandlers();
		}
	}

	private void createCropHandlers() {
		if (nrHandlers == 8) {
			// corner crop handlers
			cropHandlers.get(0).moveTo(rectangle.getX(), rectangle.getY() + 10);
			cropHandlers.get(0).lineTo(rectangle.getX(), rectangle.getY());
			cropHandlers.get(0).lineTo(rectangle.getX() + 10, rectangle.getY());
			cropHandlers.get(1).moveTo(rectangle.getX(),
					rectangle.getMaxY() - 10);
			cropHandlers.get(1).lineTo(rectangle.getX(), rectangle.getMaxY());
			cropHandlers.get(1).lineTo(rectangle.getX() + 10,
					rectangle.getMaxY());
			cropHandlers.get(2).moveTo(rectangle.getMaxX() - 10,
					rectangle.getMaxY());
			cropHandlers.get(2).lineTo(rectangle.getMaxX(),
					rectangle.getMaxY());
			cropHandlers.get(2).lineTo(rectangle.getMaxX(),
					rectangle.getMaxY() - 10);
			cropHandlers.get(3).moveTo(rectangle.getMaxX(),
					rectangle.getY() + 10);
			cropHandlers.get(3).lineTo(rectangle.getMaxX(), rectangle.getY());
			cropHandlers.get(3).lineTo(rectangle.getMaxX() - 10,
					rectangle.getY());
			// side handlers
			cropHandlers.get(4).moveTo(
					(rectangle.getMinX() + rectangle.getMaxX()) / 2 - 5,
					rectangle.getMinY());
			cropHandlers.get(4).lineTo(
					(rectangle.getMinX() + rectangle.getMaxX()) / 2 + 5,
					rectangle.getMinY());
			cropHandlers.get(5).moveTo(rectangle.getMinX(),
					(rectangle.getMinY() + rectangle.getMaxY()) / 2 - 5);
			cropHandlers.get(5).lineTo(rectangle.getMinX(),
					(rectangle.getMinY() + rectangle.getMaxY()) / 2 + 5);
			cropHandlers.get(6).moveTo(
					(rectangle.getMinX() + rectangle.getMaxX()) / 2 - 5,
					rectangle.getMaxY());
			cropHandlers.get(6).lineTo(
					(rectangle.getMinX() + rectangle.getMaxX()) / 2 + 5,
					rectangle.getMaxY());
			cropHandlers.get(7).moveTo(rectangle.getMaxX(),
					(rectangle.getMinY() + rectangle.getMaxY()) / 2 - 5);
			cropHandlers.get(7).lineTo(rectangle.getMaxX(),
					(rectangle.getMinY() + rectangle.getMaxY()) / 2 + 5);
		}
	}

	private void createBoundingBoxHandlers() {
		if (nrHandlers == 8) {
			// corner handlers
			handlers.get(0).setFrameFromCenter(rectangle.getX(),
					rectangle.getY(),
					rectangle.getX() + 5, rectangle.getY() + 5);
			handlers.get(1).setFrameFromCenter(rectangle.getX(),
					rectangle.getMaxY(), rectangle.getX() + 5,
					rectangle.getMaxY() + 5);
			handlers.get(2).setFrameFromCenter(
				rectangle.getMaxX(), rectangle.getMaxY(),
					rectangle.getMaxX() + 5, rectangle.getMaxY() + 5);
			handlers.get(3).setFrameFromCenter(
					rectangle.getMaxX(), rectangle.getY(),
					rectangle.getMaxX() + 5, rectangle.getY() + 5);

			// side handlers
			// top
			handlers.get(4).setFrameFromCenter(
				(rectangle.getMinX() + rectangle.getMaxX()) / 2,
				rectangle.getMinY(),
					(rectangle.getMinX() + rectangle.getMaxX()) / 2 + 5,
					rectangle.getMinY() + 5);
			// left
			handlers.get(5).setFrameFromCenter(rectangle.getMinX(),
				(rectangle.getMinY() + rectangle.getMaxY()) / 2,
					rectangle.getMinX() + 5,
					(rectangle.getMinY() + rectangle.getMaxY()) / 2 + 5);
			// bottom
			handlers.get(6).setFrameFromCenter(
				(rectangle.getMinX() + rectangle.getMaxX()) / 2,
				rectangle.getMaxY(),
					(rectangle.getMinX() + rectangle.getMaxX()) / 2 + 5,
					rectangle.getMaxY() + 5);
			// right
			handlers.get(7).setFrameFromCenter(rectangle.getMaxX(),
				(rectangle.getMinY() + rectangle.getMaxY()) / 2,
					rectangle.getMaxX() + 5,
					(rectangle.getMinY() + rectangle.getMaxY()) / 2 + 5);
			// handler for rotation
			// handlers.get(8).setFrameFromCenter(
			// (rectangle.getMinX() + rectangle.getMaxX()) / 2,
			// rectangle.getMaxY() + 15,
			// (rectangle.getMinX() + rectangle.getMaxX()) / 2 + 3,
			// rectangle.getMaxY() + 15 + 3);
		}
	}

	/**
	 * method to draw the bounding box construction for selected geo
	 * 
	 * @param g2
	 *            - graphics
	 */
	public void draw(GGraphics2D g2) {
		// draw bounding box
		if (rectangle != null) {
			g2.setColor(GColor.newColor(192, 192, 192, 0.0));
			g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f,
					GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER));
			g2.fill(rectangle);
			g2.setColor(GColor.MOW_MEBIS_TEAL);
			g2.draw(rectangle);
		}
		if (handlers != null && !handlers.isEmpty() && !isCropBox) {
			// join rotation handler and bounding box
			// GLine2D line = AwtFactory.getPrototype().newLine2D();
			// line.setLine((rectangle.getMinX() + rectangle.getMaxX()) / 2,
			// rectangle.getMaxY(),
			// (rectangle.getMinX() + rectangle.getMaxX()) / 2,
			// rectangle.getMaxY() + 15);
			// g2.setColor(GColor.GEOGEBRA_GRAY);
			// g2.draw(line);
			for (int i = 0; i < /* = */nrHandlers; i++) {
				g2.setPaint(GColor.MOW_MEBIS_TEAL);
				g2.fill(handlers.get(i));
				g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f,
						GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER));
				g2.setColor(GColor.GEOGEBRA_GRAY);
				g2.draw(handlers.get(i));
			}
		}
		if (cropHandlers != null && !cropHandlers.isEmpty() && isCropBox) {
			g2.setColor(GColor.WHITE);
			g2.setStroke(AwtFactory.getPrototype().newBasicStroke(6.0f,
					GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_ROUND));
			for (int i = 0; i < nrHandlers; i++) {
				g2.draw(cropHandlers.get(i));
			}
			g2.setStroke(AwtFactory.getPrototype().newBasicStroke(4.0f,
					GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_ROUND));
			g2.setColor(GColor.BLACK);
			for (int i = 0; i < nrHandlers; i++) {
				g2.draw(cropHandlers.get(i));
			}
		}

	}

	/**
	 * reset the parts of bounding box construction
	 */
	public void resetBoundingBox() {
		rectangle = null;
		handlers.clear();
	}

	/**
	 * 
	 * @param threshold
	 *            controller threshold
	 * @return distance threshold to select a point
	 */
	public static final int getSelectionThreshold(int threshold) {
		return threshold + 12;
	}

	/**
	 * @param x
	 *            - mouse event x
	 * @param y
	 *            - mouse event y
	 * @param hitThreshold
	 *            - threshold
	 * @return number of handler which was hit
	 */
	public int hitHandlers(int x, int y, int hitThreshold) {
		int index = -1;
		if (!handlers.isEmpty()) {
			for (int i = 0; i < handlers.size(); i++) {
				GEllipse2DDouble point = handlers.get(i);
				int r = getSelectionThreshold(hitThreshold);
				double dx = point.getBounds().getX()
						+ point.getBounds().getWidth() / 2 - x;
				double dy = point.getBounds().getY()
						+ point.getBounds().getHeight() / 2 - y;
				if (dx < r && dx > -r && dx * dx + dy * dy <= r * r) {
					return i;
				}
			}
		}
		return index;
	}

	/**
	 * @param x
	 *            - x coord of hit
	 * @param y
	 *            - y coord of hit
	 * @param hitThreshold
	 *            - threshold
	 * @return true if hits any side of boundingBox
	 */
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		if (rectangle == null) {
			return false;
		}
		return
		// left side
		onSegment(rectangle.getMinX(), rectangle.getMinY(), x, y,
				rectangle.getMinX(), rectangle.getMaxY(), hitThreshold)
				// top side
				|| onSegment(rectangle.getMinX(), rectangle.getMinY(), x, y,
						rectangle.getMaxX(), rectangle.getMinY(), hitThreshold)
				// bottom side
				|| onSegment(rectangle.getMinX(), rectangle.getMaxY(), x, y,
						rectangle.getMaxX(), rectangle.getMaxY(), hitThreshold)
				// right side
				|| onSegment(rectangle.getMaxX(), rectangle.getMinY(), x, y,
						rectangle.getMaxX(), rectangle.getMaxY(), hitThreshold);
	}

	// check if intersection point is on segment
	private static boolean onSegment(double segStartX, double segStartY,
			int hitX, int hitY, double segEndX, double segEndY,
			int hitThreshold) {
		if (hitX <= Math.max(segStartX, segEndX) + 2 * hitThreshold
				&& hitX >= Math.min(segStartX, segEndX) - 2 * hitThreshold
				&& hitY <= Math.max(segStartY, segEndY) + 2 * hitThreshold
				&& hitY >= Math.min(segStartY, segEndY) - 2 * hitThreshold) {
			return true;
		}
		return false;
	}
}
