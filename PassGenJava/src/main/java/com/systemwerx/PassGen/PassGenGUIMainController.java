package com.systemwerx.PassGen;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.systemwerx.common.event.Log;
import com.systemwerx.common.license.LicenseManager;
import com.systemwerx.common.util.ExceptionUtil;
import com.systemwerx.common.util.FXOptionPane;
import com.systemwerx.common.util.FXOptionPane.Response;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.stage.FileChooser;

public class PassGenGUIMainController {
    PassGenGUIMain parent;
    KeyStoreBean keystoreBean = PassGenGUI.passGenGUIstatic.getKeyStoreBean();

    public PassGenGUIMainController() {
    }

    @FXML
    TableView appList;

    @FXML
    public void init(PassGenGUIMain passGenGUIMain) {
        parent = passGenGUIMain;
    }

    @FXML
    protected void handleNewButton(ActionEvent event) {
        PassGenGUIEditApp passGenGUIEditApp = new PassGenGUIEditApp(parent);
        passGenGUIEditApp.launchInCurrentApplication(true, null);
        Log.debug("New button selected");
    }

    @FXML
    protected void handleEditButton(ActionEvent event) {

        TableViewSelectionModel selectionModel = appList.getSelectionModel();
        KeyStoreEntry selected = (KeyStoreEntry) selectionModel.getSelectedItem();
        if (selected == null) {
            FXOptionPane.showErrorDialog(parent.getStage(),
                    "No Application set selected - please make a selection", "Error");
            return;
        }

        PassGenGUIEditApp passGenGUIEditApp = new PassGenGUIEditApp(parent);
        passGenGUIEditApp.launchInCurrentApplication(false, selected);
        Log.debug("Edit button selected");
    }

    @FXML
    protected void handleDeleteButton(ActionEvent event) {
        try {
            Log.debug("Delete button selected");

            TableViewSelectionModel selectionModel = appList.getSelectionModel();
            KeyStoreEntry selected = (KeyStoreEntry) selectionModel.getSelectedItem();
            if (selected == null) {
                FXOptionPane.showErrorDialog(parent.getStage(),
                        "No Application set selected - please make a selection", "Error");
                return;
            }

            if (FXOptionPane.showConfirmDialog(parent.getStage(), "Please confirm delete", "Info") == Response.NO) {
                return;
            }

            parent.getKeyStoreBean().deleteKey(selected);
            parent.refreshApps();
        } catch (Exception ex) {
            Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
            ExceptionUtil.printStackTrace(ex);
            FXOptionPane.showErrorDialog(parent.getStage(),
                    "Exception - " + ex.getClass().getName() + " " + ex.getMessage(), "Error");
        }
    }

    @FXML
    protected void handleHelpButton(ActionEvent event) {
        try {
            Log.debug("Help button selected");
            HostServices hostServices = parent.getHostServices();
            Path jarPath = new File(PassGenGUIMainController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).toPath();
            Path docPath = jarPath.resolveSibling("documentation");
            String pdfFile = Paths.get(docPath.toString(),"PassGen_Java.pdf").toString();
            hostServices.showDocument(pdfFile);

        } catch (Exception ex) {
            Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
            ExceptionUtil.printStackTrace(ex);
            FXOptionPane.showErrorDialog(parent.getStage(),
                    "Exception - " + ex.getClass().getName() + " " + ex.getMessage(), "Error");
        }
    }

    @FXML
    protected void handleDiagMenu(ActionEvent event) {
        try {
            int time = (int) (System.currentTimeMillis() / 1000);
            FXOptionPane.showMessageDialog(parent.getStage(),
                    "Time in seconds - " + time, "Info");

        } catch (Exception ex) {
            Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
            ExceptionUtil.printStackTrace(ex);
            FXOptionPane.showErrorDialog(parent.getStage(),
                    "Exception - " + ex.getClass().getName() + " " + ex.getMessage(), "Error");
        }
    }

    @FXML
    protected void handleAboutMenu(ActionEvent event) {
        try {
            FXOptionPane.showMessageDialog(parent.getStage(),
                    "PassGen for Java Release 7", "About PassGen");

        } catch (Exception ex) {
            Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
            ExceptionUtil.printStackTrace(ex);
            FXOptionPane.showErrorDialog(parent.getStage(),
                    "Exception - " + ex.getClass().getName() + " " + ex.getMessage(), "Error");
        }
    }

    @FXML
    protected void handleStoreMenu(ActionEvent event) {
        try {
            FXOptionPane.showMessageDialog(parent.getStage(),
                    "This option saves your password to a file. You must ensure this file stays secure. Select OK to Contiue or Cancel to abort.",
                    "Info");

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select file to save key to");
            File fc = fileChooser.showOpenDialog(parent.getStage());

            if ( fc == null)
                return;

            PassGenGUI.passGenGUIstatic.getKeyStoreBean();
            keystoreBean.saveEncryptionKey(fc.getAbsolutePath(),parent.getPassGenGUILogon().getValue());

        } catch (Exception ex) {
            Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
            ExceptionUtil.printStackTrace(ex);
            FXOptionPane.showErrorDialog(parent.getStage(),
                    "Exception - " + ex.getClass().getName() + " " + ex.getMessage(), "Error");
        }
    }

    @FXML
    protected void handleChangeMenu(ActionEvent event) {
        try {
            String pw = null;

            PassGenGUIPwChange passGenGUIPwChange = new PassGenGUIPwChange(parent);
            pw = passGenGUIPwChange.launchInCurrentApplication();
            if ( pw == null ) return;

            if ( keystoreBean.changeEncryptionKey(pw))
            {
               KeyStoreEntry ke = new KeyStoreEntry();
               ke.setApplicationName("?????????");
               ke.setUserID("");
               ke.setSessionKey(pw);
               ke.setGmtOffset(0);
               ke.setPasswordType(1);
               keystoreBean.addKey(ke);
               FXOptionPane.showMessageDialog(parent.getStage(),
               "Password changed", "Info");
       }
            else
            {
                FXOptionPane.showErrorDialog(parent.getStage(),
                "Password change failed", "Error");
        }
        } catch (Exception ex) {
            Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
            ExceptionUtil.printStackTrace(ex);
            FXOptionPane.showErrorDialog(parent.getStage(),
                    "Exception - " + ex.getClass().getName() + " " + ex.getMessage(), "Error");
        }
    }

    @FXML
    protected void handleLicenseMenu(ActionEvent event) {
        try {
            LicenseManager lm = new LicenseManager();
            lm.setProductID("05");
            lm.setLicensePath(PassGenGUI.getPassGenGUI().getLicenseFile().getAbsolutePath());
            lm.setProductName("PassGen for Java");
            lm.run();        
        } catch (Exception ex) {
            Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
            ExceptionUtil.printStackTrace(ex);
            FXOptionPane.showErrorDialog(parent.getStage(),
                    "Exception - " + ex.getClass().getName() + " " + ex.getMessage(), "Error");
        }
    }

    @FXML
    void displayInfoMessage(String message) {
        Log.info(message);
    }

}
