package com.slivkam.graphdemonstrator.presenters;

import java.awt.Dialog;

import com.slivkam.graphdemonstrator.model.EdgeAdapter;
import com.slivkam.graphdemonstrator.model.VertexAdapter;
import com.slivkam.graphdemonstrator.views.EditEdge;
import com.slivkam.graphdemonstrator.views.EditVertex;
import com.slivkam.graphdemonstrator.views.GraphDemonstratorView;
import com.slivkam.graphdemonstrator.views.Message;

/**
 *
 * @author Miroslav
 *
 */
public abstract class Presenter {

	public interface PresenterFactory {
		DemoPresenter createDemoPresenter(GraphDemonstratorView view);
		GraphPresenter createGraphPresenter(GraphDemonstratorView view);
		EditVertexPresenter createVertexPresenter();
		EditEdgePresenter createEdgePresenter();
	}
	
	public interface View {};
	
	/**
	 *
	 * @author Miroslav
	 * Enumeration of dialogs that this presenter can show.
	 */
//	public enum Dialogs {
//		EDIT_VERTEX,
//		EDIT_EDGE,
//		MESSAGE
//	}

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
	public void populateDialog(Object o, Presenter presenter) {
		
		if (presenter instanceof EditVertexPresenter) {
			EditVertexPresenter p = (EditVertexPresenter) presenter;
			EditVertex veditView = new EditVertex(p);
			p.setView(veditView);
			p.setVertex((VertexAdapter)o);
			veditView.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			p.show();
			veditView.setVisible(true);
		} else if (presenter instanceof EditEdgePresenter) {
			EditEdgePresenter p = (EditEdgePresenter) presenter;
			EditEdge editView = new EditEdge(p);
			p.setView(editView);
			p.setEdge((EdgeAdapter) o);
			editView.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			p.show();
			editView.setVisible(true);
		} else {
			Message m = new Message(o.toString());
			m.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			m.setVisible(true);
		}
	}
	
	public abstract View getView();
}
