package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

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
		if(Line2D.ptLineDist(l.getX1(), l.getY1(),l.getX2(), l.getY2(), p.x, p.y) < 10.0) {
			return true;
		}
		return false;
	}
	
	@Override
	public void drawObject(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		if(this.getShape() != null) {
			g2.draw(this.getShape());
			int x = (int) (((Line2D) this.getShape()).getX1() + ((Line2D) this.getShape()).getX2())/2;
			int y = (int) (((Line2D) this.getShape()).getY1() + ((Line2D) this.getShape()).getY2())/2;
			g2.drawString(this.getLabel(),x , y );
			
		}		
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof EdgeAdapter) {
			EdgeAdapter e = (EdgeAdapter) o;
			return e.getEdge().equals(this.e);
		}
		return false;
		
	}

}
