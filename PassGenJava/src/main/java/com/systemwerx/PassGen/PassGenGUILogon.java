package com.systemwerx.PassGen;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;
import com.systemwerx.utils.trace;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.Image;

/**
 * 
 */
public class PassGenGUILogon extends Application {
	PassGenGUILogon thelogon;
	PassGenGUILogonController controller;
	PassGenGUILogonNotification notify;
	String value;
	boolean inputState = false;
	PassGenGUI caller;
	boolean traceActive = false;
	boolean first;
	trace tc;
	Stage stage;
	Scene scene;
	KeyStoreBean kb;

	/**
	*/
	public PassGenGUILogon(PassGenGUILogonNotification notify) {
		this.notify = notify;
	}

	public void initApp(KeyStoreBean kb) {
		this.kb = kb;
		launchInCurrentApplication();
	}

	public void launchInCurrentApplication() {
		try {
			InputStream fxmlStream;
			// Get caller and first logon flag
			caller = PassGenGUI.getPassGenGUI();
			first = caller.isFirstLogon();
			this.stage = stage;
			// Create the FXMLLoader
			FXMLLoader loader = new FXMLLoader();
			// Path to the FXML File
			Log.info("Starting PassGen Login UI");

			if (first) {
				fxmlStream = this.getClass().getResourceAsStream("/fxml/PassGenGUILogonPwChange.fxml");

			} else {
				fxmlStream = this.getClass().getResourceAsStream("/fxml/PassGenGUILogon.fxml");
			}

			// Create the Pane and all Details
			Pane root = (Pane) loader.load(fxmlStream);
			// New stage
			stage = new Stage();
			// Create the Scene
			scene = new Scene(root);
			this.scene = scene;
			// Set the Scene to the Stage
			stage.setScene(scene);
			// Set the Title to the Stage
			stage.setTitle("Passgen for Java");
			URL iconURL = this.getClass().getResource("/images/logo.gif");
			Image iconImage = new Image(iconURL.toExternalForm());
			stage.getIcons().add(iconImage);
			controller = loader.getController();
			controller.init(this, kb, this.first);
			// Set the title of this pane
			Label label = (Label) scene.lookup("#title");

			if (this.first) {
				label.setText("First access - please set password");
			} else {
				label.setText("Please enter password");
			}

			// Display the Stage
			stage.show();
			TextField textField = (TextField) scene.lookup("#pw");
			textField.requestFocus();
		} catch (

		Exception ex) {
			Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
			ExceptionUtil.printStackTrace(ex);
		}
	}

	@Override
	public void start(Stage stage) throws IOException, URISyntaxException {
		try {
			InputStream fxmlStream;
			// Get caller and first logon flag
			caller = PassGenGUI.getPassGenGUI();
			first = caller.isFirstLogon();
			this.stage = stage;
			// Create the FXMLLoader
			FXMLLoader loader = new FXMLLoader();
			// Path to the FXML File
			Log.info("Starting PassGen Login UI");

			if (first) {
				fxmlStream = this.getClass().getResourceAsStream("/fxml/PassGenGUILogonPwChange.fxml");

			} else {
				fxmlStream = this.getClass().getResourceAsStream("/fxml/PassGenGUILogon.fxml");
			}

			// Create the Pane and all Details
			Pane root = (Pane) loader.load(fxmlStream);
			// Create the Scene
			scene = new Scene(root);
			this.scene = scene;
			// Set the Scene to the Stage
			stage.setScene(scene);
			// Set the Title to the Stage
			stage.setTitle("Passgen for Java");
			URL iconURL = this.getClass().getResource("/images/logo.gif");
			Image iconImage = new Image(iconURL.toExternalForm());
			stage.getIcons().add(iconImage);
			controller = loader.getController();
			controller.init(this, kb, this.first);
			// Set the title of this pane
			Label label = (Label) scene.lookup("#title");

			if (this.first) {
				label.setText("First access - please set password");
			} else {
				label.setText("Please enter password");
			}

			// Display the Stage
			stage.show();
			TextField textField = (TextField) scene.lookup("#pw");
			textField.requestFocus();
		} catch (

		Exception ex) {
			Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
			ExceptionUtil.printStackTrace(ex);
		}
	}

	boolean isInputOK() {
		return inputState;
	}

	public void setInputState(boolean inputState) {
		this.inputState = inputState;
	}

	public boolean isInputState() {
		return inputState;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public PassGenGUILogonNotification getNotify() {
		return notify;
	}

	public void setNotify(PassGenGUILogonNotification notify) {
		this.notify = notify;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
