package views;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import presenters.GraphPresenter;
import presenters.GraphPresenter.GraphEditor;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener, GraphEditor {
	
	private List<CanvasObject> objects;
	
	GraphPresenter presenter;
	/**
	 * 
	 */
	
	public Canvas(GraphPresenter presenter) {
		super();
		this.presenter = presenter;
		objects = new ArrayList<CanvasObject>();
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

		for(CanvasObject o : objects) {
			o.drawObject(g2);
		}
		
	}

	public void drawObject(CanvasObject o) {	
		o.initShape();
		objects.add(o);
		this.repaint();
	}
	
	//public void drawDirectedEdge(Point p1, Point p2) {
		//drawEdge(p1,p2);
		
	//}
	
	public void removeLast() {
		objects.remove(objects.size()-1);
		
		this.repaint();
	}

	public void editObject(CanvasObject n) {
		for(CanvasObject o : objects) {
			if(o.equals(n)) {
				objects.remove(o);
				// TODO: implement vertex replacing
				n.setShape(o.getShape());
				objects.add(n);
				break;
			}
		}
		repaint();
		
	}
	
	public void removeObjectCloseTo(Point p) {
		// TODO: when object is vertex I need to remove edges too
		for(CanvasObject o : objects) {
			if(o.contains(p)) {
				objects.remove(o);
				break;
			}
		}
		repaint();
		
	}

	// This is kind of heavy computation for view, isnt't it?
	/*public void removeVertexCloseTo(Point p) {
		Ellipse2D vertex = null;
		// find vertex at first
		for(Shape s : objects) {
			if(s instanceof Ellipse2D) {
				vertex = (Ellipse2D) s;
				if(Math.pow(p.x-vertex.getCenterX(),2)+
						Math.pow(p.y-vertex.getCenterY(), 2) < 100) {
					break;
				}
				vertex = null;
			}
		}
		if(vertex == null) {
			return;
		}
		// find lines whose some end is touching this ellipse
		List<Line2D> lines = new ArrayList<Line2D>();
		for(Shape s : objects) {
			if(s instanceof Line2D) {
				Line2D l = (Line2D) s;
				// (x-x0)^2+(y-y0)^2 <= r^2
				// there could be some edges left when the touch point was rounded
				if( (Math.pow(l.getX1()-vertex.getCenterX(),2)+
						Math.pow(l.getY1()-vertex.getCenterY(), 2) <= 100) ||
						(Math.pow(l.getX2()-vertex.getCenterX(),2)+
						Math.pow(l.getY2()-vertex.getCenterY(), 2) <= 100)) {
							lines.add(l);
						}
					
			}
		}
		objects.remove(vertex);
		objects.removeAll(lines);
		
		repaint();
	}*/

	public void moveVertex(Point x) {
		// TODO Auto-generated method stub
		
	}

	public List<CanvasObject> getObjects() {
		return objects;
	}
	
	public void setObjects(List<CanvasObject> objects) {
		this.objects = objects;
		for(CanvasObject o : this.objects) {
			o.initShape();
		}
		repaint();
		
	}

	public void drawShape(final Shape s) {
		CanvasObject o = new CanvasObject(){

			@Override
			public boolean contains(Point p) {
				return s.contains(p);
			}

			@Override
			public void initShape() {
				this.setShape(s);
				
			}
			
		};
		o.initShape();
		objects.add(o);
		repaint();
	}

	public void clean() {
		objects = new ArrayList<CanvasObject>();
		repaint();
	}
}
