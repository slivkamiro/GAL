package presenters;

import java.awt.Dialog;

import javax.swing.JApplet;

import model.EdgeAdapter;
import model.VertexAdapter;
import views.EditEdge;
import views.EditVertex;
import views.Message;

/**
 *
 * @author Miroslav
 *
 */
public abstract class Presenter {

	private JApplet view;

	/**
	 *
	 * @author Miroslav
	 * Enumeration of dialogs that this presenter can show.
	 */
	public enum Dialogs {
		EDIT_VERTEX,
		EDIT_EDGE,
		MESSAGE
	}

	/**
	 * Default constructor.
	 */
	public Presenter() {
	}

	/**
	 * Displays dialog specified.
	 * @param d Dialog to be displayed.
	 * @param o Object to be presented.
	 */
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
			p.show();
			eeditView.setVisible(true);
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

	/**
	 * Sets view to this presenter.
	 * @param view
	 */
	/*public void setView(JApplet view) {
		this.view = view;
	}
	 */
}
