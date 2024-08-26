package com.systemwerx.PassGen;

import java.io.File;
import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.FXOptionPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 */
public class PassGenGUILogonController {

	@FXML
	TextField pw;

	@FXML
	TextField pwConfirm;

	PassGenGUILogon passGenGUILogon;
	boolean firstLogon;
	KeyStoreBean kb;

	public PassGenGUILogonController() {
	}

	@FXML
	public void init(PassGenGUILogon passGenGUILogon, KeyStoreBean kb, boolean firstLogon) {
		this.passGenGUILogon = passGenGUILogon;
		this.firstLogon = firstLogon;
		this.kb = kb;
	}

	@FXML
	protected void handleOkButton(ActionEvent event) {
		File keystore = PassGenGUI.getPassGenGUI().getKeystore();
		if (pw.getText().length() == 0 || pw.getText().length() > 20) {
			pw.requestFocus();
			FXOptionPane.showErrorDialog(null, "Please enter a valid password - length is invalid", "Error");
			return;
		}

		if (firstLogon) {
			if (pwConfirm.getText().length() == 0 || pwConfirm.getText().length() > 20) {
				FXOptionPane.showErrorDialog(null, "Please confirm password - length is invalid", "Error");
				pwConfirm.requestFocus();
				return;
			}

			// Check values are the same
			if (!pwConfirm.getText().equals(pw.getText())) {
				FXOptionPane.showErrorDialog(null, "Please confirm password - values not equal", "Error");
				pwConfirm.requestFocus();
				return;
			}

			try {
				kb.create(kb.Key_Supplied, keystore.getAbsolutePath(), pw.getText());
				kb.open(kb.Key_Supplied, keystore.getAbsolutePath(),  pw.getText());
				KeyStoreEntry ke = new KeyStoreEntry();
				ke.setApplicationName("?????????");
				ke.setUserID("");
				ke.setSessionKey( pw.getText());
				ke.setGmtOffset(0);
				ke.setPasswordType(1);
				kb.addKey(ke);
			} catch (Exception ex) {
				FXOptionPane.showErrorDialog(null, "Exception setting password - " + ex.getMessage(), "Error");
				pwConfirm.requestFocus();
				return;
			}

		} else {
			try {
				kb.open(kb.Key_Supplied, keystore.getAbsolutePath(), pw.getText());
				KeyStoreEntry ke;
				ke = kb.getKey("?????????");
				if (ke == null) {
					FXOptionPane.showErrorDialog(null, "Password invalid", "Error");
					return;
				}
				String keyval = ke.getSessionKey();

				if (!keyval.equals(pw.getText())) {
					// custom title, error icon
					FXOptionPane.showErrorDialog(null, "Password invalid", "Error");
					passGenGUILogon.stage.close();
					return;
				}
			} catch (Exception ex) {
				FXOptionPane.showErrorDialog(null, "Exception setting password - " + ex.getMessage(), "Error");
				pwConfirm.requestFocus();
				return;
			}
		}

		/*
		 * Push value back to main as JavaFX creates these classes unconnected to our
		 * classes
		 */
		passGenGUILogon.setValue(pw.getText());
		PassGenGUI.getPassGenGUI().setLoginPasswordOk(true);
		passGenGUILogon.getStage().close();
		passGenGUILogon.getNotify().notifyPasswordOk();
	}

	@FXML
	protected void handleCancelButton(ActionEvent event) {
		passGenGUILogon.inputState = true;
		passGenGUILogon.stage.close();
		passGenGUILogon.setInputState(false);
	}

	void displayInfoMessage(String message) {
		Log.info(message);
	}

}
