package com.systemwerx.PassGen;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;
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
public class PassGenGUIPwDisplay extends Application {
	PassGenGUIMain parent;
	PassGenGUIPwDisplayController controller;
	PassGenGUILogonNotification notify;
	String password;
	boolean inputState = false;
	PassGenGUI caller;
	boolean traceActive = false;
	boolean first;
	Stage stage;
	Scene scene;
	KeyStoreBean kb;

	/**
	*/
	public PassGenGUIPwDisplay(PassGenGUIMain parent) {
		this.parent = parent;
	}

	public void launchInCurrentApplication(String pwValue, String title) {
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
				fxmlStream = this.getClass().getResourceAsStream("/fxml/PassGenGUIPwDisplay.fxml");

			} else {
				fxmlStream = this.getClass().getResourceAsStream("/fxml/PassGenGUIPwDisplay.fxml");
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
			controller.init(this);
			// Set the title of this pane
			TextField text = (TextField) scene.lookup("#pwValue");
			text.setText(pwValue);
			Label label = (Label) scene.lookup("#title");

			label.setText(title);

			// Display the Stage
			stage.show();
		} catch (

		Exception ex) {
			Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
			ExceptionUtil.printStackTrace(ex);
		}
	}

	@Override
	public void start(Stage stage) throws IOException, URISyntaxException {
		try {
		} catch (

		Exception ex) {
			Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
			ExceptionUtil.printStackTrace(ex);
		}
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
}
