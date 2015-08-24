package com.slivkam.graphdemonstrator.presenters;

import com.slivkam.graphdemonstrator.model.EdgeAdapter;

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
		 * Publish edge properties. Weight to be specific.
		 * @param e edge to be published.
		 */
		public void showEdgeProp(Integer w);
	}

	private EdgeEditor editor;
	private EdgeAdapter edge;

	private Integer weight;

	/**
	 * Sets view to this presenter.
	 * @param e
	 */
	public void setView(EdgeEditor e) {
		editor = e;
	}

	@Override
	public View getView() {
		return (View) this.editor;
	}
	
	/**
	 * Set edge to be presented.
	 * @param e
	 */
	public void setEdge(EdgeAdapter e) {
		edge = e;
		weight = Integer.parseInt(edge.getWeight());
	}

	/**
	 * Display edges properties.
	 */
	public void show() {
		editor.showEdgeProp(weight);
	}

	/**
	 * Sets new weight to edge.
	 * @param w weight to be set.
	 */
	public void setWeight(String w) {
		edge.setWeight(w);

	}

	public void resetWeight() {
		edge.setWeight(weight.toString());
	}


}
