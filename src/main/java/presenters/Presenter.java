package presenters;

import javax.swing.JApplet;

public abstract class Presenter {
	
	private JApplet view;
	
	public enum Dialogs {
		EDIT_VERTEX,
		EDIT_EDGE
	}
	
	public void populateDialog(Dialogs d) {
		// TODO: show wnated dialog 
	}
	
	public void setView(JApplet view) {
		this.view = view;
	}
	
}
