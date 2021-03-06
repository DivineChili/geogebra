package org.geogebra.common.kernel.discrete.delaunay;

import java.util.Comparator;

import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

/**
 * This class represents a 3D point, with some simple geometric methods
 * (pointLineTest).
 */
public class Point_dt {
	public final static int ONSEGMENT = 0;

	/**
	 * + <br>
	 */
	public final static int LEFT = 1;

	/**
	 * +
	 */
	public final static int RIGHT = 2;
	public final static int INFRONTOFA = 3;
	public final static int BEHINDB = 4;
	public final static int ERROR = 5;

	double x, y, z;

	@Override
	public int hashCode() {

		double tempArray[] = { x, y, z };

		return java.util.Arrays.hashCode(tempArray);
	}

	/**
	 * Default Constructor. <br>
	 * constructs a 3D point at (0,0,0).
	 */
	public Point_dt() {
		this(0, 0);
	}

	/**
	 * constructs a 3D point
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public Point_dt(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * constructs a 3D point with a z value of 0.
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public Point_dt(double x, double y) {
		this(x, y, 0);
	}

	/**
	 * simple copy constructor
	 * 
	 * @param p
	 *            point
	 */
	public Point_dt(Point_dt p) {
		x = p.x;
		y = p.y;
		z = p.z;
	}

	/** @return the x-coordinate of this point. */
	public double x() {
		return x;
	}

	/**
	 * Sets the x coordinate.
	 * 
	 * @param x
	 *            The new x coordinate.
	 */
	public void setX(double x) {
		this.x = x;
	}

	/** @return the y-coordinate of this point. */
	public double y() {
		return y;
	}

	/**
	 * Sets the y coordinate.
	 * 
	 * @param y
	 *            The new y coordinate.
	 */
	public void setY(double y) {
		this.y = y;
	}

	/** @return the z-coordinate of this point. */
	public double z() {
		return z;
	}

	/**
	 * Sets the z coordinate.
	 * 
	 * @param Z
	 *            The new z coordinate.
	 */
	public void setZ(double Z) {
		this.z = Z;
	}

	/**
	 * @param p
	 *            point
	 * @return distance
	 */
	double distance2(Point_dt p) {
		return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
	}

	/**
	 * @param px
	 *            x-coord
	 * @param py
	 *            y-coord
	 * @return distance
	 */
	double distance2(double px, double py) {
		return (px - x) * (px - x) + (py - y) * (py - y);
	}

	/**
	 * @param p
	 *            point
	 * @return true if less than this
	 */
	boolean isLess(Point_dt p) {
		return Compare.lessThan(x, p.x)
				|| (Compare.equals(x, p.x) && Compare.lessThan(y, p.y));
	}

	/**
	 * @param p
	 *            point
	 * @return true if greater than this
	 */
	boolean isGreater(Point_dt p) {
		return Compare.greaterThan(x, p.x)
				|| (Compare.equals(x, p.x) && Compare.greaterThan(y, p.y));
	}

	/**
	 * @return true iff this point [x,y] coordinates are the same as p [x,y]
	 *         coordinates. (the z value is ignored).
	 */
	@Override
	public boolean equals(Object p) {

		if (!(p instanceof Point_dt)) {
			return false;
		}

		return Compare.equals(x, ((Point_dt) p).x)
				&& Compare.equals(y, ((Point_dt) p).y);
	}

	/** @return a String in the [x,y,z] format */
	@Override
	public String toString() {
		return " Pt[" + x + "," + y + "," + z + "]";
	}

	/**
	 * @param p
	 *            point
	 * @return the L2 distance NOTE: 2D only!!!
	 */
	public double distance(Point_dt p) {
		return MyMath.length(p.x() - x, p.y() - y);
		//double temp = Math.pow(p.x() - x, 2) + Math.pow(p.y() - y, 2);
		//return Math.sqrt(temp);
	}

	/**
	 * @param p
	 *            point
	 * @return the L2 distance NOTE: 3D only!!!
	 */
	public double distance3D(Point_dt p) {
		return MyMath.length(p.x() - x, p.y() - y, p.z() - z);
		// double temp = Math.pow(p.x() - x, 2) + Math.pow(p.y() - y, 2)
		// + Math.pow(p.z() - z, 2);
		// return Math.sqrt(temp);
	}

	/**
	 * @return a String: x y z (used by the save to file - write_tsin method).
	 */
	public String toFile() {
		return ("" + x + " " + y + " " + z);
	}

	String toFileXY() {
		return ("" + x + " " + y);
	}

	// pointLineTest
	// ===============
	// simple geometry to make things easy!

	/**
	 * tests the relation between this point (as a 2D [x,y] point) and a 2D
	 * segment a,b (the Z values are ignored), returns one of the following:
	 * LEFT, RIGHT, INFRONTOFA, BEHINDB, ONSEGMENT
	 * 
	 * @param a
	 *            the first point of the segment.
	 * @param b
	 *            the second point of the segment.
	 * @return the value (flag) of the relation between this point and the a,b
	 *         line-segment.
	 */
	public int pointLineTest(Point_dt a, Point_dt b) {

		double dx = b.x - a.x;
		double dy = b.y - a.y;
		double res = dy * (x - a.x) - dx * (y - a.y);

		if (res < 0) {
			return LEFT;
		}
		if (res > 0) {
			return RIGHT;
		}

		if (dx > 0) {
			if (x < a.x) {
				return INFRONTOFA;
			}
			if (b.x < x) {
				return BEHINDB;
			}
			return ONSEGMENT;
		}
		if (dx < 0) {
			if (x > a.x) {
				return INFRONTOFA;
			}
			if (b.x > x) {
				return BEHINDB;
			}
			return ONSEGMENT;
		}
		if (dy > 0) {
			if (y < a.y) {
				return INFRONTOFA;
			}
			if (b.y < y) {
				return BEHINDB;
			}
			return ONSEGMENT;
		}
		if (dy < 0) {
			if (y > a.y) {
				return INFRONTOFA;
			}
			if (b.y > y) {
				return BEHINDB;
			}
			return ONSEGMENT;
		}
		Log.error("Error, pointLineTest with a=b");
		return ERROR;
	}

	boolean areCollinear(Point_dt a, Point_dt b) {
		double dx = b.x - a.x;
		double dy = b.y - a.y;
		double res = dy * (x - a.x) - dx * (y - a.y);
		return res == 0;
	}

	/*
	 * public ajSegment Bisector( ajPoint b) { double sx = (x+b.x)/2; double sy
	 * = (y+b.y)/2; double dx = b.x-x; double dy = b.y-y; ajPoint p1 = new
	 * ajPoint(sx-dy,sy+dx); ajPoint p2 = new ajPoint(sx+dy,sy-dx); return new
	 * ajSegment( p1,p2 ); }
	 */

	Point_dt circumcenter(Point_dt a, Point_dt b) {

		double u = ((a.x - b.x) * (a.x + b.x) + (a.y - b.y) * (a.y + b.y))
				/ 2.0f;
		double v = ((b.x - x) * (b.x + x) + (b.y - y) * (b.y + y)) / 2.0f;
		double den = (a.x - b.x) * (b.y - y) - (b.x - x) * (a.y - b.y);
		if (den == 0) {
			Log.debug("circumcenter, degenerate case");
		}
		return new Point_dt((u * (b.y - y) - v * (a.y - b.y)) / den,
				(v * (a.x - b.x) - u * (b.x - x)) / den);
	}

	public static Comparator<Point_dt> getComparator(int flag) {
		return new Compare(flag);
	}

	public static Comparator<Point_dt> getComparator() {
		return new Compare(0);
	}
}

class Compare implements Comparator<Point_dt> {
	private int _flag;

	public Compare(int i) {
		_flag = i;
	}

	/** compare between two points. */
	@Override
	public int compare(Point_dt d1, Point_dt d2) {
		int ans = 0;
		if (d1 != null && d2 != null) {
			if (_flag == 0) {
				if (greaterThan(d1.x, d2.x)) {
					return 1;
				}
				if (lessThan(d1.x, d2.x)) {
					return -1;
				}
				// x1 == x2
				if (greaterThan(d1.y, d2.y)) {
					return 1;
				}
				if (lessThan(d1.y, d2.y)) {
					return -1;
				}
			} else if (_flag == 1) {
				if (greaterThan(d1.x, d2.x)) {
					return -1;
				}
				if (lessThan(d1.x, d2.x)) {
					return 1;
				}
				// x1 == x2
				if (greaterThan(d1.y, d2.y)) {
					return -1;
				}
				if (lessThan(d1.y, d2.y)) {
					return 1;
				}
			} else if (_flag == 2) {
				if (greaterThan(d1.y, d2.y)) {
					return 1;
				}
				if (lessThan(d1.y, d2.y)) {
					return -1;
				}
				// y1 == y2
				if (greaterThan(d1.x, d2.x)) {
					return 1;
				}
				if (d1.x < d2.x) {
					return -1;
				}

			} else if (_flag == 3) {
				if (greaterThan(d1.y, d2.y)) {
					return -1;
				}
				if (d1.y < d2.y) {
					return 1;
				}
				// y1 == y2
				if (greaterThan(d1.x, d2.x)) {
					return -1;
				}
				if (lessThan(d1.x, d2.x)) {
					return 1;
				}
			}
		} else {
			if (d1 == null && d2 == null) {
				return 0;
			}
			if (d1 == null && d2 != null) {
				return 1;
			}
			if (d1 != null && d2 == null) {
				return -1;
			}
		}
		return ans;
	}

	public static boolean greaterThan(double x, double y) {
		return DoubleUtil.isGreater(x, y);
	}

	public static boolean lessThan(double x, double y) {
		return DoubleUtil.isGreater(y, x);
	}

	public static boolean equals(double x, double y) {
		return DoubleUtil.isEqual(x, y);
	}

}