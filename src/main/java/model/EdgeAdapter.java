package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;

import views.CanvasObject;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

public class EdgeAdapter extends CanvasObject {

	private Edge e;

	private Point a;
	private Point b;

	public EdgeAdapter(Edge e) {
		super();
		this.e = e;
	}

	public Edge getEdge() {
		return e;
	}

	public VertexAdapter getOutVertex() {
		return new VertexAdapter(e.getVertex(Direction.OUT));
	}

	public VertexAdapter getInVertex() {
		return new VertexAdapter(e.getVertex(Direction.IN));
	}

	public String getWeight() {
		return e.getProperty("weight").toString();
	}

	public void setWeight(String w) {
		e.setProperty("weight", w);
		this.setLabel(w);

	}

	@Override
	public void initShape() {
		Line2D l = new Line2D.Double(a, b);
		this.setShape(l);

	}

	public void setPoints(Point p1, Point p2) {
		a = p1;
		b = p2;
		e.setProperty("startX", ""+a.x);
		e.setProperty("startY", ""+a.y);
		e.setProperty("endX", ""+b.x);
		e.setProperty("endY", ""+b.y);
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
