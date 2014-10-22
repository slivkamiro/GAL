package presenters;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.List;

import model.EdgeAdapter;
import model.GraphAdapter;
import model.VertexAdapter;
import views.CanvasObject;

public class GraphPresenter extends Presenter {
	
	public interface GraphEditor {
		public void drawObject(CanvasObject o);
		public void drawShape(final Shape s);
		//public void drawDirectedEdge(Point p1, Point p2);
		public void editObject(CanvasObject o);
		public void removeObjectCloseTo(Point p);
		public void removeLast();
		//public void removeVertexCloseTo(Point p);
		//public void removeEdgeCloseTo(Point p);
		public void moveVertex(Point p);
		public List<CanvasObject> getObjects();
		public void setObjects(List<CanvasObject> objects);
		public void clean();
	}
	
	public enum EditorOptions {
		VERTEX,
		EDGE,
		EDIT,
		REMOVE,
		NONE
	}
	
	private GraphEditor editor;
	
	protected GraphAdapter graph;
	
	private EditorOptions mode = EditorOptions.NONE;
	
	private Point start;
	private Point end;
	
	public GraphPresenter() {
		super();
		graph = new GraphAdapter();
	}
	
	public void setView(GraphEditor editor) {
		this.editor = editor;
	}

	public void endPoint(Point point) {
		switch(mode) {
		case EDGE:
			end = point;
			// add edge to model and draw it on canvas
			VertexAdapter out = getVertexOnPosition(start);
			VertexAdapter in = getVertexOnPosition(end);
			editor.removeLast();
			if(out != null && in != null) {
				EdgeAdapter e = graph.addEdge(out, in);
				e.setPoints(getTouchPoint(out,start), getTouchPoint(in,end));
				editor.drawObject(e);
			}
			break;
		case VERTEX:
			// add vertex to model
			VertexAdapter v = graph.addVertex();
			v.setAttribute("PositionX", String.valueOf(point.x));
			v.setAttribute("PositionY", String.valueOf(point.y));
			// draw vertex to canvas
			editor.drawObject(v);
			break;
		case EDIT:
		case REMOVE:
		case NONE:
			break;
		}
		
	}

	public void startPoint(Point point) {
		VertexAdapter v = null;
		EdgeAdapter e = null;
		switch(mode) {
		case EDGE:
			start = point;
			end = point;
			editor.drawShape(new Line2D.Double(start, end));
			break;
		case EDIT:
			v = getVertexOnPosition(point);
			if(v != null) {
				this.populateDialog(Presenter.Dialogs.EDIT_VERTEX,v);
				editor.editObject(v);
				break;
			}
			e = getEdgeCloseTo(point);
			if(e != null) {
				this.populateDialog(Presenter.Dialogs.EDIT_EDGE, e);
				editor.editObject(e);
			}
			break;
		case REMOVE:
			v = getVertexOnPosition(point);
			if(v != null && v.getEdges().size() == 0) {
				graph.removeVertex(v);
				editor.removeObjectCloseTo(point);
				break;
			} else if(v != null && v.getEdges().size() != 0) {
				// For simplicity user have to remove edges first
				this.populateDialog(Presenter.Dialogs.MESSAGE, "First remove edges!");
				break;
			}
			e = getEdgeCloseTo(point);
			if(e != null) {
				graph.removeEdge(e);
				editor.removeObjectCloseTo(point);
			}
			break;
		case VERTEX:
		case NONE:
			break;
		}
		
	}

	private EdgeAdapter getEdgeCloseTo(Point point) {
		Point2D p1 = null, p2 = null;
		for(CanvasObject o : editor.getObjects()) {
			if(o instanceof EdgeAdapter) {
				if(((EdgeAdapter) o).contains(point)) {
					p1 = ((Line2D)((EdgeAdapter) o).getShape()).getP1();
					p2 = ((Line2D)((EdgeAdapter) o).getShape()).getP2();
					break;
				}
			}
		}
		
		if(p1 == null || p2 == null )
			return null;
		
		VertexAdapter v1 = getVertexOnPosition(p1);
		VertexAdapter v2 = getVertexOnPosition(p2);
		
		if(v1 == null || v2 == null)
			return null;
		
		EdgeAdapter e = null;
		if((e = graph.getEdge(v1,v2)) == null)
			e = graph.getEdge(v2,v1);
		
		return e;
	}

	public void setEditor(EditorOptions mode) {
		this.mode = this.mode == mode ? EditorOptions.NONE : mode;
	}

	public void possibleEndPoint(Point point) {
		switch(mode) {
		case EDGE:
			end = point;
			VertexAdapter out = getVertexOnPosition(start);
			if(out != null) {
				editor.removeLast();
				editor.drawShape(new Line2D.Double(getTouchPoint(out,start), end));
			}
			break;
		case EDIT:
			// TODO: drag vertex, together with edges
		case REMOVE:
		case VERTEX:
		case NONE:
			break;
		}
		
	}
	
	public VertexAdapter getVertexOnPosition(Point2D point) {
		Point p = new Point();
		p.x = (int)point.getX();
		p.y = (int)point.getY();
		return getVertexOnPosition(p);
	}
	
	public VertexAdapter getVertexOnPosition(Point p) {
		for(VertexAdapter v : graph.getVertices()) {
			int x = Integer.parseInt(v.getAttribute("PositionX"));
			int y = Integer.parseInt(v.getAttribute("PositionY"));
			// some deviation given
			if(Math.pow(p.x-x,2)+Math.pow(p.y-y, 2) <= 200)
				return v;
		}
		return null;
	}

	private Point getTouchPoint(VertexAdapter circle,Point def) {
		
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
		
		if(d < 0) {
			return def;
		}
		
		double x1 = (-1*b + Math.sqrt(d))/(2*a);
		double x2 = (-1*b - Math.sqrt(d))/(2*a);
		double y1 = k*x1+q;
		double y2 = k*x2+q;
		
		if(def == start) {
			if (Math.sqrt(Math.pow(x1-end.x,2)+Math.pow(y1-end.y,2)) < 
					Math.sqrt(Math.pow(x2-end.x,2)+Math.pow(y2-end.y,2)))
				return new Point((int)x1,(int)y1);
			return new Point((int)x2,(int)y2);
		}
		if (Math.sqrt(Math.pow(x1-start.x,2)+Math.pow(y1-start.y,2)) < 
				Math.sqrt(Math.pow(x2-start.x,2)+Math.pow(y2-start.y,2)))
			return new Point((int)x1,(int)y1);
		return new Point((int)x2,(int)y2);
	}

	public GraphAdapter getGraph() {
		return graph;
	}

	public void setGraph(GraphAdapter graph) {
		this.graph = graph;
		editor.clean();
		editor.setObjects(graph.getAll());
		
	}

	public void setGraph(File f) {
		GraphAdapter s = graph;
		editor.clean();
		graph = new GraphAdapter();
		
		try {
			graph.read(f);
		} catch (IOException e) {
			// TODO populate dialog
			graph = s;
			e.printStackTrace();
		} finally {
			editor.setObjects(graph.getAll());
		}
		
	}

	public void saveGraph(File f) {
		try {
			graph.write(f);		
		} catch (IOException e){
			// TODO dialog
		}
	}

}
