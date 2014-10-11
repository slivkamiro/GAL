package presenters;

import model.VertexAdapter;

public class EditVertexPresenter extends Presenter {
	
	public interface VertexEditor {
		public void showVertexProp(VertexAdapter v);
		
	}
	
	private VertexEditor editor;
	private VertexAdapter vertex;
	
	public void setView(VertexEditor view) {
		editor = view;
	}

	public void setVertex(VertexAdapter o) {
		vertex = o;		
	}
	
	public void show() {
		editor.showVertexProp(vertex);
	}
}
