package presenters;

import java.awt.Point;

import model.Graph;
import model.Vertex;

public class GraphPresenter extends Presenter {
	
	public interface GraphEditor {
		public void drawVertex(Point p);
		public void drawEdge(Point x, Point y);
		public void drawDirectedEdge(Point p1, Point p2);
		public void editObjectColseTo(Point p);
		public void removeLast();
		public void removeObjectCloseTo(Point p);
		public void moveVertex(Point p);
	}
	
	public enum EditorOptions {
		VERTEX,
		EDGE,
		EDIT,
		REMOVE,
		NONE
	}
	
	private GraphEditor editor;
	
	private EditorOptions mode = EditorOptions.NONE;
	
	private Graph graph;
	
	private Point start;
	private Point end;
	
	public GraphPresenter() {
		graph = new Graph("new");
	}
	
	public void setView(GraphEditor editor) {
		this.editor = editor;
	}

	public void endPoint(Point point) {
		switch(mode) {
		case EDGE:
			end = point;
			// add edge to model and draw it on canvas
			Vertex out = graph.getVertexOnPosition(start);
			Vertex in = graph.getVertexOnPosition(end);
			editor.removeLast();
			if(out != null && in != null) {
				graph.addEdge(out, in);
				editor.drawEdge(getTouchPoint(out,start), getTouchPoint(in,end));
			}
			break;
		case EDIT:
			// TODO: update model
			// TODO: get object from model with coords specified
			this.populateDialog(Presenter.Dialogs.EDIT_VERTEX);
			editor.editObjectColseTo(point);
			break;
		case REMOVE:
			// TODO: update model
			editor.removeObjectCloseTo(point);
			break;
		case VERTEX:
			// add vertex to model
			Vertex v = graph.addVertex();
			v.setAttribute("PositionX", String.valueOf(point.x));
			v.setAttribute("PositionY", String.valueOf(point.y));
			// draw vertex to canvas
			editor.drawVertex(point);
			break;
		case NONE:
			break;
		}
		
	}

	public void startPoint(Point point) {
		switch(mode) {
		case EDGE:
			start = point;
			end = point;
			editor.drawEdge(start, end);
			break;
		case EDIT:
		case REMOVE:
		case VERTEX:
		case NONE:
			break;
		}
		
	}

	public void setEditor(EditorOptions mode) {
		this.mode = this.mode == mode ? EditorOptions.NONE : mode;
	}

	public void possibleEndPoint(Point point) {
		switch(mode) {
		case EDGE:
			end = point;
			Vertex out = graph.getVertexOnPosition(start);
			if(out != null) {
				editor.removeLast();
				editor.drawEdge(getTouchPoint(out,start), end);
			}
			break;
		case EDIT:
		case REMOVE:
		case VERTEX:
		case NONE:
			break;
		}
		
	}

	private Point getTouchPoint(Vertex circle,Point def) {
		
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

}
