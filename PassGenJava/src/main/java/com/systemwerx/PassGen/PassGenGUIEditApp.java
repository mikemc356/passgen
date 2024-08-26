package com.systemwerx.PassGen;

import java.io.InputStream;
import java.net.URL;
import javafx.scene.image.Image;
import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;
import com.systemwerx.utils.TranslateBean;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 */
public class PassGenGUIEditApp extends Application {
   static PassGenGUIEditApp theapps1;

   PassGenGUITimerThread tThread;
   int passwordType = 0;
   final int passwordType_Password = 1;
   final int passwordType_PassTicket = 2;
   final int passwordType_SKEY = 3;
   boolean traceActive = false;
   PassGenGUIMain caller;

   TranslateBean tb = new TranslateBean();
   Object list[] = { " - ", " + " };
   Object list1[] = { " 0", " 1", " 2", " 3", " 4", " 5", " 6", " 7", " 8", " 9", "10", "11", "12" };
   String AppName;
   KeyStoreBean kb;
   Stage stage;
   Scene scene;
   PassGenGUIEditAppController controller;
   PassGenGUIMain mainApp;

   /**
    */

   public PassGenGUIEditApp(PassGenGUIMain main, KeyStoreBean ikb, String iAppName) {
      AppName = iAppName;
      this.mainApp = main;
   }

   public PassGenGUIEditApp(PassGenGUIMain main) {
      this.mainApp = main;
   }

   public void launchInCurrentApplication(boolean newApp, KeyStoreEntry ke) {
      try {
         this.kb = kb;
         InputStream fxmlStream;
         // Get caller and first logon flag
         this.stage = stage;
         // Create the FXMLLoader
         FXMLLoader loader = new FXMLLoader();
         // Path to the FXML File
         Log.debug("New Application");
         fxmlStream = this.getClass().getResourceAsStream("/fxml/PassGenGUIEditApp.fxml");

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
         stage.setTitle("Add/Edit Application");
         URL iconURL = this.getClass().getResource("/images/logo.gif");
         Image iconImage = new Image(iconURL.toExternalForm());
         stage.getIcons().add(iconImage);
         controller = loader.getController();
         controller.init(this, mainApp.getKeyStoreBean(), newApp);
         // Set the title of this pane
         ComboBox comboBox = (ComboBox) scene.lookup("#plusminus");
         comboBox.getItems().add("+");
         comboBox.getItems().add("-");
         comboBox.getSelectionModel().select("+");

         comboBox = (ComboBox) scene.lookup("#offset");
         comboBox.getItems().add("0");
         comboBox.getItems().add("1");
         comboBox.getItems().add("2");
         comboBox.getItems().add("3");
         comboBox.getItems().add("4");
         comboBox.getItems().add("5");
         comboBox.getItems().add("6");
         comboBox.getItems().add("7");
         comboBox.getItems().add("8");
         comboBox.getItems().add("9");
         comboBox.getItems().add("10");
         comboBox.getItems().add("11");

         if (newApp) {
            RadioButton radioButton = (RadioButton) scene.lookup("#passticketButton");
            radioButton.setSelected(true);
         }

         TextField textField = (TextField) scene.lookup("#name");
         textField.requestFocus();
         // Display the Stage
         stage.show();
         controller.setPassticket();
         if (ke != null) {
            // Fill in details as we have a keyStoreEntry
            TextField name = (TextField) scene.lookup("#name");
            name.setText(ke.getApplicationName());
            TextField user = (TextField) scene.lookup("#user");
            user.setText(ke.getUserID());
            TextField key = (TextField) scene.lookup("#key");
            key.setText(ke.getSessionKey());

            switch (ke.passwordType) {
               case KeyStoreEntry.passwordType_PassTicket:
                  controller.setPassticket();
                  ComboBox plusminus = (ComboBox) scene.lookup("#plusminus");
                  if (ke.getGmtOffset() >= 0) {
                     plusminus.getSelectionModel().select(0);
                  } else {
                     plusminus.getSelectionModel().select(1);
                  }

                  ComboBox offset = (ComboBox) scene.lookup("#offset");
                  offset.getSelectionModel().select(Math.abs(ke.getGmtOffset()));

                  break;

               case KeyStoreEntry.passwordType_Password:
                  controller.setPassword();
                  break;

               case KeyStoreEntry.passwordType_SKEY:
                  controller.setSkey();
                  TextField seed = (TextField) scene.lookup("#seed");
                  seed.setText(ke.getSeed());
                  break;
            }
         }
      } catch (

      Exception ex) {
         Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
         ExceptionUtil.printStackTrace(ex);
      }
   }

   @Override
   public void start(Stage stage) {
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

   public PassGenGUIMain getMainApp() {
      return mainApp;
   }

   public void setMainApp(PassGenGUIMain mainApp) {
      this.mainApp = mainApp;
   }
}
