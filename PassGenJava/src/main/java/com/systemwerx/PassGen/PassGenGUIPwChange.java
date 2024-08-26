package com.systemwerx.PassGen;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;
import com.systemwerx.common.util.FXOptionPane;
import com.systemwerx.utils.trace;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;

/**
 * 
 */
public class PassGenGUIPwChange extends Application {
	PassGenGUIMain parent;
	PassGenGUIPwDisplayController controller;
	PassGenGUILogonNotification notify;
	String password;
	boolean inputState = false;
	PassGenGUI caller;
	boolean traceActive = false;
	boolean first;
	trace tc;
	Stage stage;
	Scene scene;
	KeyStoreBean kb;
	String newPassword;

	/**
	*/
	public PassGenGUIPwChange(PassGenGUIMain parent) {
		this.parent = parent;
	}

	public String launchInCurrentApplication() {
		try {
			InputStream fxmlStream;
			// Get caller and first logon flag
			caller = PassGenGUI.getPassGenGUI();
			first = caller.isFirstLogon();
			this.stage = stage;
			// Create the FXMLLoader
			FXMLLoader loader = new FXMLLoader();
			// Path to the FXML File
			Log.info("Password change");

			if (first) {
				fxmlStream = this.getClass().getResourceAsStream("/fxml/PassGenGUIPwChange.fxml");

			} else {
				fxmlStream = this.getClass().getResourceAsStream("/fxml/PassGenGUIPwChange.fxml");
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
			stage.setTitle("Password change");
			URL iconURL = this.getClass().getResource("/images/logo.gif");
			Image iconImage = new Image(iconURL.toExternalForm());
			stage.getIcons().add(iconImage);
			// Display the Stage
			stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parent.getStage());
            stage.setResizable(false);
            stage.setScene(scene);
			Button okButton = (Button) scene.lookup("#okButton");
			okButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					PasswordField pw = (PasswordField) scene.lookup("#pw");
					if ( pw.getText() == null || !pw.getText().equals( parent.getPassGenGUILogon().getValue())) {
						FXOptionPane.showErrorDialog(parent.getStage(),
						"Password invalid", "Error");	
						pw.requestFocus();
						return;
					}
					 
					PasswordField pwNew = (PasswordField) scene.lookup("#pwNew");
					PasswordField pwNewConfirm = (PasswordField) scene.lookup("#pwNewConfirm");

					if ( pwNew == null || pwNewConfirm == null || pwNew.getText() == null || pwNewConfirm.getText() == null) {
						FXOptionPane.showErrorDialog(parent.getStage(),
						"New/Confirm Password invalid", "Error");	
						pwNew.requestFocus();
						return;
					}

					if ( !pwNew.getText().equals(pwNewConfirm.getText())) {
						FXOptionPane.showErrorDialog(parent.getStage(),
						"New passwords do not match", "Error");	
						pwNew.requestFocus();
						return;
					}

					newPassword = pwNew.getText();
					stage.close();
				}
			});

			Button cancelButton = (Button) scene.lookup("#cancelButton");
			cancelButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					stage.close();
				}
			});
			stage.setOnShown(event -> {
				event.consume();
				stage.toFront();
			});
	
			stage.showAndWait();
			return newPassword;
		} catch (

		Exception ex) {
			Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
			ExceptionUtil.printStackTrace(ex);
			return null;
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
