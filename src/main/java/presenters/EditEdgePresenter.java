package presenters;

import model.EdgeAdapter;

public class EditEdgePresenter extends Presenter {
	
	public interface EdgeEditor {
		public void showEdgeProp(EdgeAdapter e);
	}
	
	private EdgeEditor editor;
	private EdgeAdapter edge;
	
	public void setView(EdgeEditor e) {
		editor = e;
	}
	
	public void setEdge(EdgeAdapter e) {
		edge = e;
	}
	
	public void show() {
		editor.showEdgeProp(edge);
	}
	

}
