package com.slivkam.graphdemonstrator.utils;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/**
 *
 * @author Miroslav
 * Specifies button grup in which nothing might be selected.
 */
public class MyButtonGroup extends ButtonGroup {

	@Override
	public void setSelected(ButtonModel model, boolean selected) {
		if (selected){
			super.setSelected(model, selected);
		}
		else {
			clearSelection();
		}
	}
}
