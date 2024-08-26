package com.systemwerx.PassGen;

import com.systemwerx.common.util.FXOptionPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 */
public class PassGenGUIPwDisplayController {

	PassGenGUIPwDisplay parent;

	public PassGenGUIPwDisplayController() {
	}

	@FXML
	public void init(PassGenGUIPwDisplay parent) {
		this.parent = parent;
	}

	@FXML
	protected void handleOkButton(ActionEvent event) {
		try {
			parent.getStage().close();
		} catch (

		Exception ex) {
			FXOptionPane.showErrorDialog(null, "Exception displaying password - " + ex.getMessage(), "Error");
			return;
		}
	}
}
