package presenters;

import model.EdgeAdapter;

/**
 *
 * @author Miroslav
 *
 */
public class EditEdgePresenter extends Presenter {

	/**
	 *
	 * @author Miroslav
	 * Interface that must implement every View that this presenter manages.
	 */
	public interface EdgeEditor {

		/**
		 * Publish edge properties.
		 * @param e edge to be published.
		 */
		public void showEdgeProp(EdgeAdapter e);
	}

	private EdgeEditor editor;
	private EdgeAdapter edge;

	/**
	 * Sets view to this presenter.
	 * @param e
	 */
	public void setView(EdgeEditor e) {
		editor = e;
	}

	/**
	 * Set edge to be presented.
	 * @param e
	 */
	public void setEdge(EdgeAdapter e) {
		edge = e;
	}

	/**
	 * Display edges properties.
	 */
	public void show() {
		editor.showEdgeProp(edge);
	}

	/**
	 * Sets new weight to edge.
	 * @param w weight to be set.
	 */
	public void setWeight(String w) {
		edge.setWeight(w);

	}


}
