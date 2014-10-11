package presenters;

import javax.swing.JApplet;

import model.EdgeAdapter;
import model.GraphAdapter;
import model.VertexAdapter;
import views.EditEdge;
import views.EditVertex;

public abstract class Presenter {
	
	private JApplet view;
	
	public enum Dialogs {
		EDIT_VERTEX,
		EDIT_EDGE
	}
	
	public Presenter() {
	}
	
	public void populateDialog(Dialogs d,Object o) {
		switch(d) {
		case EDIT_VERTEX:
			EditVertexPresenter presenter = new EditVertexPresenter();
			EditVertex veditView = new EditVertex(presenter);
			presenter.setView(veditView);
			presenter.setVertex((VertexAdapter)o);
			veditView.setVisible(true);
			presenter.show();
			break;
		case EDIT_EDGE:
			EditEdgePresenter p = new EditEdgePresenter();
			EditEdge eeditView = new EditEdge(p);
			p.setView(eeditView);
			p.setEdge((EdgeAdapter) o);
			eeditView.setVisible(true);
			p.show();
			break;
		default:
			break;
		}
	}
	
	public void setView(JApplet view) {
		this.view = view;
	}
	
}
