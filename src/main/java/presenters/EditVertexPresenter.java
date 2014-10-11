package presenters;

import model.Vertex;

public class EditVertexPresenter extends Presenter {
	
	public interface VertexEditor {
		public void showVertexProp(Vertex v);
		
	}
	
	private VertexEditor editor;
	private Vertex vertex;
	
	public void setView(VertexEditor view) {
		editor = view;
	}

	public void setVertex(Vertex o) {
		vertex = o;		
	}
	
	public void show() {
		editor.showVertexProp(vertex);
	}
}
