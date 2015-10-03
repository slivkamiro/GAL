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
        this.newAttrs = new HashMap<String,String>();
        this.toRemove = new ArrayList<String>();
    }

    /**
     * Sets view to this presenter.
     * @param view
     */
    public void setView(VertexEditor view) {
        this.editor = view;
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
        this.vertex = o;
    }

    /**
     * Displays vertex properties.
     */
    public void show() {
        this.editor.showVertexProp(this.vertex);
    }

    /**
     * Adds new attribute to the vertex.
     * @param name Name of new attribute.
     * @param value Value of new attribute.
     */
    public void addAttribute(String name, String value) {

        if (this.vertex.getAttribute(name) != null || this.newAttrs.containsKey(name)) {
            this.populateDialog("Can't add duplicit attribute.", null);
            return;
        }

        if (name != "" && value != "") {
            this.newAttrs.put(name, value);
            this.editor.addToList(name+" : "+value);
        }
    }

    /**
     * Removes specified attribute.
     * @param selectedValue Name of the attribute to be removed.
     */
    public void removeAttribute(String selectedValue) {

        if (selectedValue.startsWith("PositionX") || selectedValue.startsWith("PositionY")) {
            this.populateDialog("Can't remove positional attributes!", null);
            return;
        }

        this.editor.removeFromList(selectedValue);

        String key = selectedValue.split(" : ")[0];
        String val = selectedValue.split(" : ")[1];
        for (String k : this.newAttrs.keySet()) {
            if (k == key && this.newAttrs.get(k) == val) {
                this.newAttrs.remove(k);
                return;
            }
        }

        this.toRemove.add(key);

    }

    /**
     * Save changes made to this vertex.
     */
    public void saveChanges() {
        for (String k : this.newAttrs.keySet()) {
            this.vertex.setAttribute(k, this.newAttrs.get(k));
        }
        for (String k : this.toRemove) {
            this.vertex.deleteAttribute(k);
        }

    }
}
