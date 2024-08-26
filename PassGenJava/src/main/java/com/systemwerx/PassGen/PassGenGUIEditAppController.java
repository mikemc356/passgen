package com.systemwerx.PassGen;

import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;
import com.systemwerx.common.util.FXOptionPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 */
public class PassGenGUIEditAppController {
	final static String hexChars = "01234567890ABCDEF";

	@FXML
	TextField name;

	@FXML
	PasswordField key;

	@FXML
	Label keyLabel;

	@FXML
	TextField user;

	@FXML
	TextField seed;

	@FXML
	Label plusminusLabel;

	@FXML
	ComboBox plusminus;

	@FXML
	ComboBox offset;

	@FXML
	RadioButton passticketButton;

	@FXML
	RadioButton passwordButton;

	@FXML
	RadioButton skeyButton;

	PassGenGUIEditApp passGenGUIEditApp;
	boolean firstLogon;
	KeyStoreBean kb;
	boolean newApplication;

	public PassGenGUIEditAppController() {

	}

	@FXML
	public void init(PassGenGUIEditApp passGenGUIEditApp, KeyStoreBean kb, boolean newApplication) {
		this.passGenGUIEditApp = passGenGUIEditApp;
		this.kb = kb;
		this.newApplication = newApplication; 
	}

	@FXML
	protected void handlePassticketButton(ActionEvent event) {
		try {
		    Log.info("Passticket selected");
			setPassticket();
		} catch (Exception ex) {
			Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
			ExceptionUtil.printStackTrace(ex);
		}
	}

	@FXML
	protected void handleSkeyButton(ActionEvent event) {
		try {
		    Log.info("Skey selected");
			setSkey();
		} catch (Exception ex) {
			Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
			ExceptionUtil.printStackTrace(ex);
		}
	}

	@FXML
	protected void handlePasswordButton(ActionEvent event) {
		try {
		    Log.info("Password selected");
			setPassword();
		} catch (Exception ex) {
			Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
			ExceptionUtil.printStackTrace(ex);
		}
	}

	@FXML
	protected void handleOkButton(ActionEvent event) {
		try {
			KeyStoreEntry ke = new KeyStoreEntry();
			if (name.getText() == null || name.getText().length() == 0 || name.getText().length() > 8) {
				name.requestFocus();
				FXOptionPane.showErrorDialog(passGenGUIEditApp.getStage(),
						"Invalid length application name - must be 1-8 characters", "Error");
				return;
			}

			// Check if already exists

			if (newApplication && kb.getKey(name.getText()) != null) {
				FXOptionPane.showErrorDialog(passGenGUIEditApp.getStage(), "Application already exists", "Error");
				return;
			}

			if (passticketButton.isSelected() && (key.getText() == null || key.getText().length() == 0 || key.getText().length() < 16)) {
				key.requestFocus();
				FXOptionPane.showErrorDialog(passGenGUIEditApp.getStage(),
						"Invalid length signon key - must be 16 hex characters", "Error");
				return;
			}

			if ((passwordButton.isSelected() || skeyButton.isSelected()) && ( key.getText() == null || key.getText().length() == 0)) {
				key.requestFocus();
				FXOptionPane.showErrorDialog(passGenGUIEditApp.getStage(),
						"Invalid length key", "Error");
				return;
			}

			if (passticketButton.isSelected() && !isHexChars(key.getText().toUpperCase())) {
				name.requestFocus();
				FXOptionPane.showErrorDialog(passGenGUIEditApp.getStage(),
						"Invalid signon key - must be hex characters", "Error");
				return;
			}

			if (user.getText() == null || user.getText().length() == 0 || user.getText().length() > 8) {
				user.requestFocus();
				FXOptionPane.showErrorDialog(passGenGUIEditApp.getStage(),
						"Invalid length user name - must be 1-8 characters", "Error");
				return;
			}

			if ( skeyButton.isSelected() && (seed.getText() == null || seed.getText().length() == 0)) {
				user.requestFocus();
				FXOptionPane.showErrorDialog(passGenGUIEditApp.getStage(),
						"Invalid length seed", "Error");
				return;
			}

			/* save value to keystore */

			ke.setApplicationName(name.getText());
			ke.setUserID(user.getText());
			ke.setSessionKey(key.getText());
			ke.setSeed(seed.getText());

			if ( skeyButton.isSelected() ) {
				ke.setPasswordType(PassGenConstants.PasswordType_SKEY);				
			} else if ( passwordButton.isSelected() ) {
				ke.setPasswordType(PassGenConstants.PasswordType_Password);				
			}  else if ( passticketButton.isSelected() ) {
				ke.setPasswordType(PassGenConstants.PasswordType_PassTicket);				
			}

			Log.debug("Adding entry");

			String plusminusString = (String) plusminus.getSelectionModel().getSelectedItem();
			String offsetString = (String) offset.getSelectionModel().getSelectedItem();
			if (offsetString == null || offsetString.equals("")) {
				offsetString = "0";
			}

			int plusMinusInt = Integer.parseInt(offsetString);
			if (plusminusString.equals("-")) {
				plusMinusInt = 0 - plusMinusInt;
			}

			ke.setGmtOffset(plusMinusInt);

			if (passticketButton.isSelected()) {
				ke.setPasswordType(KeyStoreEntry.passwordType_PassTicket);
				ke.setSessionKey(key.getText().toUpperCase());
			} else if (passwordButton.isSelected()) {
				ke.setPasswordType(KeyStoreEntry.passwordType_Password);
			} else if (skeyButton.isSelected()) {
				ke.setPasswordType(KeyStoreEntry.passwordType_SKEY);
			} else {
				FXOptionPane.showErrorDialog(passGenGUIEditApp.getStage(), "Invalid password type", "Error");
				return;
			}

			kb.addKey(ke);

			passGenGUIEditApp.getMainApp().refreshApps();
			passGenGUIEditApp.getStage().close();
		} catch (Exception ex) {
			Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
			ExceptionUtil.printStackTrace(ex);
		}

	}

	@FXML
	protected void handleCancelButton(ActionEvent event) {
		passGenGUIEditApp.getStage().close();
	}

	void setPassword() {
		plusminus.setVisible(false);
		offset.setVisible(false);
		plusminusLabel.setVisible(false);
		keyLabel.setText("Password");
		seed.setVisible(false);
		passwordButton.setSelected(true);
	}

	void setPassticket() {
		plusminus.setVisible(true);
		offset.setVisible(true);
		keyLabel.setText("Secure signon key");
		plusminusLabel.setVisible(true);
		seed.setVisible(false);
		plusminusLabel.setText("Host/GMT Offset");
		passticketButton.setSelected(true);
	}

	void setSkey() {
		plusminus.setVisible(false);
		offset.setVisible(false);
		keyLabel.setText("Secure signon key");
		plusminusLabel.setVisible(true);
		keyLabel.setText("Key");
		plusminusLabel.setText("Seed");
		seed.setVisible(true);
		skeyButton.setSelected(true);
	}

	void displayInfoMessage(String message) {
		Log.info(message);
	}

	boolean isHexChars(String inputValue) {
		for (int i = 0; i < inputValue.length(); i++) {
			char character = inputValue.charAt(i);
			if (hexChars.indexOf(character) == -1) {
				return false;
			}
		}
		return true;
	}
}
