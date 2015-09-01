package com.slivkam.graphdemonstrator.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;

import com.slivkam.graphdemonstrator.views.CanvasObject;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

/**
 *
 * @author Miroslav
 * This class serves as adapter to real model.
 */
public class EdgeAdapter extends CanvasObject {

	private Edge e;

	private Point a;
	private Point b;

	/**
	 * Creates new edge based on models edge.
	 * @param e Models edge.
	 */
	public EdgeAdapter(Edge e) {
		super();
		this.e = e;
	}

	/**
	 * Gets models edge.
	 * @return edge from model.
	 */
	public Edge getEdge() {
		return e;
	}

	/**
	 * Gets vertex from which this edge originates.
	 * @return vertex adapter class.
	 */
	public VertexAdapter getOutVertex() {
		return new VertexAdapter(e.getVertex(Direction.OUT));
	}

	/**
	 * Gets vertex that this edge directs to.
	 * @return vertex adapter class.
	 */
	public VertexAdapter getInVertex() {
		return new VertexAdapter(e.getVertex(Direction.IN));
	}

	/**
	 * Gets weight property of the edge.
	 * @return string value of weight.
	 */
	public String getWeight() {
		return e.getProperty("weight").toString();
	}

	/**
	 * Sets weight property.
	 * @param w String value of weight. Have to be number.
	 */
	public void setWeight(String w) {
		e.setProperty("weight", w);
		this.setLabel(w);

	}

	@Override
	public void initShape() {
		//TODO if i->i
		Line2D l = new Line2D.Double(a, b);
		this.setShape(l);

	}

	/**
	 * Sets starting point and end point properties in the edge.
	 * @param p1 start point.
	 * @param p2 end point.
	 */
	public void setPoints(Point p1, Point p2) {
		a = p1;
		b = p2;
		e.setProperty("startX", ""+a.x);
		e.setProperty("startY", ""+a.y);
		e.setProperty("endX", ""+b.x);
		e.setProperty("endY", ""+b.y);
	}

	/**
	 * Sets starting point and end point based on origin vertex and destination vertex.
	 * @param out origin vertex.
	 * @param in destination vertex.
	 */
	public void setPoints(VertexAdapter out, VertexAdapter in) {
		Point start = new Point(Integer.parseInt(out.getAttribute("PositionX")),
				Integer.parseInt(out.getAttribute("PositionY")));
		Point end = new Point(Integer.parseInt(in.getAttribute("PositionX")),
				Integer.parseInt(in.getAttribute("PositionY")));
		setPoints(getTouchPoint(start,end,out,start),getTouchPoint(start,end,in,end));
	}

	/**
	 * Computes touch point of Vertex and Edge defined by two points.
	 * @param start Edge start point
	 * @param end Edge end point
	 * @param circle Vertex
	 * @param def Default value if no touch point found
	 * @return Touch point or default value.
	 */
	public static Point getTouchPoint(Point start, Point end, VertexAdapter circle,Point def) {

		// smernica usecky
		int y0 = Integer.parseInt(circle.getAttribute("PositionY"));
		int x0 = Integer.parseInt(circle.getAttribute("PositionX"));
		double k = (double)(end.y - start.y) / (double)(end.x - start.x);
		double q = k*(-1*start.x)+start.y;
		double a = 1+k*k;
		double b = 2*k*(q-y0)- 2*x0;
		// c = x0*x0 + (q-y0)^2 - r*r
		double c = x0 * x0 + (q-y0) * (q-y0)-100;
		double d = b*b-4*a*c;

		if(d < 0)
			return def;

		double x1 = (-1*b + Math.sqrt(d))/(2*a);
		double x2 = (-1*b - Math.sqrt(d))/(2*a);
		double y1 = k*x1+q;
		double y2 = k*x2+q;

		if (def == start) {
			// distance from end point to x1 and x2
			if (Math.sqrt(Math.pow(x1-end.x,2)+Math.pow(y1-end.y,2)) <
					Math.sqrt(Math.pow(x2-end.x,2)+Math.pow(y2-end.y,2)))
				return new Point((int)x1,(int)y1);
			return new Point((int)x2,(int)y2);
		}
		// distance from start point to x1 and x2
		if (Math.sqrt(Math.pow(x1-start.x,2)+Math.pow(y1-start.y,2)) <
				Math.sqrt(Math.pow(x2-start.x,2)+Math.pow(y2-start.y,2)))
			return new Point((int)x1,(int)y1);
		return new Point((int)x2,(int)y2);
	}

	@Override
	public boolean contains(Point p) {
		Line2D l = (Line2D) this.getShape();
		if (Line2D.ptLineDist(l.getX1(), l.getY1(),l.getX2(), l.getY2(), p.x, p.y) < 10.0)
			return true;
		return false;
	}

	@Override
	public void drawObject(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		if (this.getShape() != null) {

			g2.draw(this.getShape());
			double x1 = ((Line2D) this.getShape()).getX1();
			double x2 = ((Line2D) this.getShape()).getX2();
			double y1 = ((Line2D) this.getShape()).getY1();
			double y2 = ((Line2D) this.getShape()).getY2();
			int x = (int) (x1 + x2)/2;
			int y = (int) (y1 + y2)/2;

			// length of the line
			double length = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
			// angle with x
			double angle = Math.atan2(y2-y1, x2-x1);

			// arrow
			GeneralPath path = new GeneralPath();
			path.moveTo((float)length, 0);
			path.lineTo((float)length - 10, -5);
			path.lineTo((float)length - 7, 0);
			path.lineTo((float)length - 10, 5);
			path.lineTo((float)length, 0);
			path.closePath();

			try {
				AffineTransform af = AffineTransform.getTranslateInstance(x1, y1);
				af.concatenate(AffineTransform.getRotateInstance(angle));
				g2.transform(af);

				Area area = new Area(path);
				g2.fill(area);

				af.invert();
				g2.transform(af);
			} catch (NoninvertibleTransformException e) {
				// TODO: what to do here?
				e.printStackTrace();
			}

			g2.drawString(this.getLabel(),x , y );

		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof EdgeAdapter) {
			EdgeAdapter e = (EdgeAdapter) o;
			return e.getEdge().equals(this.e);
		}
		return false;

	}

}
