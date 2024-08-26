package com.systemwerx.PassGen;

import com.systemwerx.common.event.Log;
import com.systemwerx.common.license.LicenseManager;
import com.systemwerx.common.license.LicenseManagerNotification;
import com.systemwerx.common.license.license;
import com.systemwerx.common.util.ExceptionUtil;
import com.systemwerx.common.util.FXOptionPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.List;
import java.util.Vector;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.image.Image;

/**
 */
public class PassGenGUIMain extends Application implements LicenseManagerNotification, PassGenGUILogonNotification {
   PassGenGUIMain theapps;
   PassGenGUI parent;
   PassGenGUIEditApp edit;
   KeyStoreEntry ke = null;
   Vector appList;
   KeyStoreBean kb;
   String selectedValue = null;
   PassGenGUILogon theLogon;
   license lic;
   String jarFile = null;
   Stage stage;
   Scene scene;
   PassGenGUIMainController controller;
   PassGenGUILogon passGenGUILogon;
   PassGenGUIPwDisplay passGenGUIPwDisplay;
   PassGenGUITimerThread timerThread;

   /**
   */
   public void initApp(PassGenGUI parent, KeyStoreBean ikb, PassGenGUILogon ilogon) {
      this.parent = parent;
      lic = new license("05");
      kb = ikb;
      Application.launch();
   }

   @Override
   public void start(Stage stage) throws IOException, URISyntaxException {
      try {
         this.stage = stage;
         kb = PassGenGUI.getPassGenGUI().getKeyStoreBean();
         lic = new license("05");
         // Create the FXMLLoader
         FXMLLoader loader = new FXMLLoader();
         // Path to the FXML File
         Log.info("Starting PassGen UI");
         InputStream fxmlStream = this.getClass().getResourceAsStream("/fxml/PassGenGUIAppFrame.fxml");
         // Create the Pane and all Details
         Pane root = (Pane) loader.load(fxmlStream);
         // Create the Scene
         scene = new Scene(root);
         this.scene = scene;
         // Set the Scene to the Stage
         stage.setScene(scene);
         // Set the Title to the Stage
         stage.setTitle("PassGen for Java");
         URL iconURL = this.getClass().getResource("/images/logo.gif");
         Image iconImage = new Image(iconURL.toExternalForm());
         stage.getIcons().add(iconImage);
         List<String> parms = getParameters().getRaw();
         controller = loader.getController();
         controller.init(this);

         if (!lic.verifyLicenseFile(PassGenGUI.getPassGenGUI().getLicenseFile().getAbsolutePath())) {
            stage.hide();
            Log.error("License expired");
            LicenseManager lm = new LicenseManager();

            // lm.setLaunchJar(jarPath);
            lm.setProductID("05");
            lm.setLicensePath(PassGenGUI.getPassGenGUI().getLicenseFile().getAbsolutePath());
            lm.setProductName("PassGen for Java");
            lm.setNotify(this);
            lm.runInCurrentApplication();
            FXOptionPane.showErrorDialog(stage, "License expired - please apply key", "Error");
            return;
         }

         doLogon();
      } catch (Exception ex) {
         Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
         ExceptionUtil.printStackTrace(ex);
      }
   }

   @Override
   public void notifyLicenseOk() {
      doLogon();
   }

   @Override
   public void notifyLicenseFailed() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'notifyLicenseFailed'");
   }

   boolean doLogon() {
      try {
         passGenGUILogon = new PassGenGUILogon(this);
         passGenGUILogon.initApp(kb);
         return true;

      } catch (Exception ex) {
         Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
         ExceptionUtil.printStackTrace(ex);
      }
      return true;
   }

   boolean loadApps() throws Exception {
      KeyStoreEntry ke;
      ke = kb.getFirstKey();
      if (!ke.getApplicationName().equals("?????????") && !ke.getApplicationName().equals("               "))
         appList.addElement(ke);

      while ((ke = kb.getNextKey()) != null) {
         if (!ke.getApplicationName().equals("?????????") && !ke.getApplicationName().equals("               "))
            appList.addElement(ke);
      }
      return true;
   }

   boolean refreshApps() throws Exception {
      appList = new Vector();
      loadApps();
      stage.show();

      TableView tableView = (TableView) scene.lookup("#appList");
      TableColumn column = (TableColumn) tableView.getColumns().get(0);
      column.setCellValueFactory(new PropertyValueFactory<>("applicationName"));
      tableView.getItems().clear();
      tableView.getItems().addAll(appList);
      return true;
   }

   boolean getOTP(String AppName) {
      String result;

      try {
         ke = kb.getKey(AppName);
      } catch (Exception ex) {
         Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
         ExceptionUtil.printStackTrace(ex);
         FXOptionPane.showErrorDialog(stage, "Unable to load application details - " + ex.getMessage(), "Error");
      }

      switch (ke.getPasswordType()) {
         case KeyStoreEntry.passwordType_Password:
            try {
               passGenGUIPwDisplay = new PassGenGUIPwDisplay(this);
               passGenGUIPwDisplay.launchInCurrentApplication(ke.getSessionKey(), "Password");
            } catch (Exception ex) {
               Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
               ExceptionUtil.printStackTrace(ex);
               FXOptionPane.showErrorDialog(stage, "Unable to generate SKEY value - " + ex.getMessage(), "Error");
            }
            break;

         case KeyStoreEntry.passwordType_SKEY:
            try {
               SKeyBean s = new SKeyBean();
               s.setSeed(ke.getSeed());
               s.setPassword(ke.getSessionKey());
               s.setHashAlgorithm(s.MD4);
               s.setLicense(getLicense());
               int intValue = PassGenGUISKEYPrompt.showSKeyDialog(stage);
               result = s.generatePassword(intValue);

               passGenGUIPwDisplay = new PassGenGUIPwDisplay(this);
               passGenGUIPwDisplay.launchInCurrentApplication(result, "S/KEY password value");
            } catch (Exception ex) {
               Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
               ExceptionUtil.printStackTrace(ex);
               FXOptionPane.showErrorDialog(stage, "Unable to generate SKEY value - " + ex.getMessage(), "Error");
            }
            break;

         case KeyStoreEntry.passwordType_PassTicket:
            PassTicketPureBean pw = new PassTicketPureBean();
            result = null;
            try {

               // If we have user MIKEM system TSOSYS1 and key E001193519561977 the ticket is
               // AEPRRZ3S

               // pw.SetDebugTime(1111111);
               pw.setUserID(ke.getUserID());
               pw.setGmtOffset(ke.getGmtOffset());
               Path jarPath = new File(PassGenGUIMainController.class.getProtectionDomain().getCodeSource()
                     .getLocation().toURI().getPath()).toPath();

               if (jarPath.toString().contains(".jar")) {
                  Path transPath = jarPath.resolveSibling("trans.txt");
                  String transFilePath = transPath.toString();
                  pw.setTranslateTable(transFilePath);
               } else {
                  pw.setTranslateTable("trans.txt");
               }

               pw.setSessionKey(ke.getSessionKey());
               pw.setApplication(ke.getApplicationName());
               PassGenGUI.getPassGenGUI().getLicenseFile().getAbsolutePath();
               pw.setLicense(getLicense());
               result = pw.getPassTicket();
               passGenGUIPwDisplay = new PassGenGUIPwDisplay(this);
               passGenGUIPwDisplay.launchInCurrentApplication(result, "Passticket value");
            } catch (Exception ex) {
               Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
               ExceptionUtil.printStackTrace(ex);
               FXOptionPane.showErrorDialog(stage, "Unable to generate Passticket value - " + ex.getMessage(), "Error");
            }

            break;
      }
      return true;
   }

   @Override
   public void notifyPasswordOk() {
      try {
         refreshApps();
      } catch (Exception ex) {
         FXOptionPane.showErrorDialog(stage, "Unable to load application details - " + ex.getMessage(), "Error");
      }
      stage.show();
      TableView<KeyStoreEntry> table = (TableView<KeyStoreEntry>) scene.lookup("#appList");

      table.setRowFactory(tv -> {
         TableRow<KeyStoreEntry> row = new TableRow<KeyStoreEntry>();
         row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!row.isEmpty())) {
               KeyStoreEntry rowData = row.getItem();
               Platform.runLater(new Runnable() {
                  @Override
                  public void run() {
                     getOTP(rowData.getApplicationName());
                  }
               });
            }
         });
         return row;
      });

   }

   @Override
   public void notifyPasswordFailed() {
      throw new UnsupportedOperationException("Unimplemented method 'notifyPasswordFailed'");
   }

   public KeyStoreBean getKeyStoreBean() {
      return kb;
   }

   public void setKeyStoreBean(KeyStoreBean kb) {
      this.kb = kb;
   }

   public Stage getStage() {
      return stage;
   }

   public void setStage(Stage stage) {
      this.stage = stage;
   }

   String getLicense() {
      try {
         PassGenGUI.getPassGenGUI().getLicenseFile().getAbsolutePath();
         // Get license
         FileInputStream fis = new FileInputStream(PassGenGUI.getPassGenGUI().getLicenseFile().getAbsolutePath());
         ObjectInputStream ois = new ObjectInputStream(fis);
         String licenseKey;
         licenseKey = ois.readUTF();
         fis.close();
         return licenseKey;
      } catch (Exception ex) {
         Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
         ExceptionUtil.printStackTrace(ex);
         FXOptionPane.showErrorDialog(stage, "Unable to generate Passticket value - " + ex.getMessage(), "Error");
      }
      return null;
   }

   public PassGenGUILogon getPassGenGUILogon() {
      return passGenGUILogon;
   }

   public void setPassGenGUILogon(PassGenGUILogon passGenGUILogon) {
      this.passGenGUILogon = passGenGUILogon;
   }

}