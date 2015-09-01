package com.slivkam.graphdemonstrator.presenters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slivkam.graphdemonstrator.model.VertexAdapter;

/**
 *
 * @author Miroslav
 *
 */
public class EditVertexPresenter extends Presenter {

	/**
	 *
	 * @author Miroslav
	 * Interface that must implement every View that this presenter manages.
	 */
	public interface VertexEditor {

		/**
		 * Display properties of specified vertex.
		 * @param v vertex.
		 */
		public void showVertexProp(VertexAdapter v);

		/**
		 * Add property to list.
		 * @param s string property name and value separated by :.
		 */
		public void addToList(String s);

		/**
		 * Remove property from a list.
		 * @param s string property name and value separated by :.
		 */
		public void removeFromList(String s);
	}

	private VertexEditor editor;
	private VertexAdapter vertex;

	private Map<String,String> newAttrs;
	private List<String> toRemove;

	/**
	 * Default constructor.
	 */
	public EditVertexPresenter() {
		super();
		newAttrs = new HashMap<String,String>();
		toRemove = new ArrayList<String>();
	}

	/**
	 * Sets view to this presenter.
	 * @param view
	 */
	public void setView(VertexEditor view) {
		editor = view;
	}

	@Override
	public View getView() {
		return (View) this.editor;
	}
	
	/**
	 * Set vertex to be presented.
	 * @param o Vertex to be presented.
	 */
	public void setVertex(VertexAdapter o) {
		vertex = o;
	}

	/**
	 * Displays vertex properties.
	 */
	public void show() {
		editor.showVertexProp(vertex);
	}

	/**
	 * Adds new attribute to the vertex.
	 * @param name Name of new attribute.
	 * @param value Value of new attribute.
	 */
	public void addAttribute(String name, String value) {
		if (name != "" && value != "") {
			newAttrs.put(name, value);
			editor.addToList(name+" : "+value);
		}
	}

	/**
	 * Removes specified attribute.
	 * @param selectedValue Name of the attribute to be removed.
	 */
	public void removeAttribute(String selectedValue) {
		// TODO do not remove position attributes
		editor.removeFromList(selectedValue);

		String key = selectedValue.split(" : ")[0];
		String val = selectedValue.split(" : ")[1];
		for (String k : newAttrs.keySet()) {
			if (k == key && newAttrs.get(k) == val) {
				newAttrs.remove(k);
				return;
			}
		}

		toRemove.add(key);

	}

	/**
	 * Save changes made to this vertex.
	 */
	public void saveChanges() {
		for (String k : newAttrs.keySet()) {
			vertex.setAttribute(k, newAttrs.get(k));
		}
		for (String k : toRemove) {
			vertex.deleteAttribute(k);
		}

	}
}
