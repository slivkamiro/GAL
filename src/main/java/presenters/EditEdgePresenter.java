package presenters;

import model.Edge;

public class EditEdgePresenter extends Presenter {
	
	public interface EdgeEditor {
		public void showEdgeProp(Edge e);
	}
	
	private EdgeEditor editor;
	private Edge edge;
	
	public EditEdgePresenter() {
		
	}
	
	public void setView(EdgeEditor e) {
		editor = e;
	}
	
	public void setEdge(Edge e) {
		edge = e;
	}
	
	public void show() {
		editor.showEdgeProp(edge);
	}
	

}
