package presenters;

import java.awt.Dialog;

import javax.swing.JApplet;

import model.EdgeAdapter;
import model.VertexAdapter;
import views.EditEdge;
import views.EditVertex;
import views.Message;

public abstract class Presenter {

	private JApplet view;

	public enum Dialogs {
		EDIT_VERTEX,
		EDIT_EDGE,
		MESSAGE
	}

	public Presenter() {
	}

	public void populateDialog(Dialogs d,Object o) {
		switch (d) {
		case EDIT_VERTEX:
			EditVertexPresenter presenter = new EditVertexPresenter();
			EditVertex veditView = new EditVertex(presenter);
			presenter.setView(veditView);
			presenter.setVertex((VertexAdapter)o);
			veditView.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			presenter.show();
			veditView.setVisible(true);
			break;
		case EDIT_EDGE:
			EditEdgePresenter p = new EditEdgePresenter();
			EditEdge eeditView = new EditEdge(p);
			p.setView(eeditView);
			p.setEdge((EdgeAdapter) o);
			eeditView.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			eeditView.setVisible(true);
			p.show();
			break;
		case MESSAGE:
			Message m = new Message(o.toString());
			m.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			m.setVisible(true);
			break;
		default:
			break;
		}
	}

	public void setView(JApplet view) {
		this.view = view;
	}

}
