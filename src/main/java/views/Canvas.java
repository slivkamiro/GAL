package views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import presenters.GraphPresenter;
import presenters.GraphPresenter.GraphEditor;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener, GraphEditor {
	
	private List<Shape> objects;
	
	GraphPresenter presenter;
	/**
	 * 
	 */
	
	public Canvas(GraphPresenter presenter) {
		super();
		this.presenter = presenter;
		objects = new ArrayList<Shape>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	private static final long serialVersionUID = 4259767900446651940L;

	public void mouseClicked(MouseEvent me) {
		// pass
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent me) {
		presenter.startPoint(me.getPoint());
		
	}

	public void mouseReleased(MouseEvent me) {
		presenter.endPoint(me.getPoint());
		
	}
	
	public void mouseDragged(MouseEvent me) {
		presenter.possibleEndPoint(me.getPoint());
		
	}

	public void mouseMoved(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawObjects((Graphics2D) g);
		
	}

	private void drawObjects(Graphics2D g2) {
		
		g2.setColor(Color.BLACK);

		for(Shape s : objects) {
			g2.draw(s);
		}
		
	}

	public void drawVertex(Point p) {
		Ellipse2D e = new Ellipse2D.Double(p.x-10.0, p.y-10.0, 20.0, 20.0);
		
		objects.add(e);
		this.repaint();
	}

	public void drawEdge(Point x, Point y) {
		Line2D l = new Line2D.Double(x, y);
		
		objects.add(l);
		this.repaint();
		
	}
	
	public void drawDirectedEdge(Point p1, Point p2) {
		drawEdge(p1,p2);
		
	}
	
	public void removeLast() {
		objects.remove(objects.size()-1);
		
		this.repaint();
	}

	public void editObjectColseTo(Point p) {
		// TODO Auto-generated method stub
		
	}

	public void removeObjectCloseTo(Point p) {
		// TODO Auto-generated method stub
		
	}

	public void moveVertex(Point x) {
		// TODO Auto-generated method stub
		
	}
}
