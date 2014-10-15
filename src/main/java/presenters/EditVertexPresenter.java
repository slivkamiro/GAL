package presenters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.VertexAdapter;

public class EditVertexPresenter extends Presenter {
	
	public interface VertexEditor {
		public void showVertexProp(VertexAdapter v);
		public void addToList(String s);
		public void removeFromList(String s);
	}
	
	private VertexEditor editor;
	private VertexAdapter vertex;
	
	private Map<String,String> newAttrs;
	private List<String> toRemove;
	
	public EditVertexPresenter() {
		super();
		newAttrs = new HashMap<String,String>();
		toRemove = new ArrayList<String>();
	}
	
	public void setView(VertexEditor view) {
		editor = view;
	}

	public void setVertex(VertexAdapter o) {
		vertex = o;		
	}
	
	public void show() {
		editor.showVertexProp(vertex);
	}

	public void addAttribute(String name, String value) {
		if(name != "" && value != "") {
			newAttrs.put(name, value);
			editor.addToList(name+" : "+value);
		}
	}

	public void removeAttribute(String selectedValue) {
		editor.removeFromList(selectedValue);
		
		String key = selectedValue.split(" : ")[0];
		String val = selectedValue.split(" : ")[1];
		for(String k : newAttrs.keySet()) {
			if(k == key && newAttrs.get(k) == val) {
				newAttrs.remove(k);
				return;
			}
		}
		
		toRemove.add(key);
		
	}

	public void saveChanges() {
		for(String k : newAttrs.keySet()) {
			vertex.setAttribute(k, newAttrs.get(k));
		}
		for(String k : toRemove) {
			vertex.deleteAttribute(k);
		}
		
	}
}
