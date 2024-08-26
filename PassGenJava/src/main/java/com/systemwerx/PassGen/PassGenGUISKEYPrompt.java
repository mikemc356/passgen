package com.systemwerx.PassGen;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PassGenGUISKEYPrompt {

    static Insets insets = new Insets(20, 15, 20, 15);
    static Insets insets2 = new Insets(0, 15, 20, 15);

    private static int selection;
    private static ImageView icon = new ImageView();
    TextField input;

    static class Dialog extends Stage {
        public Dialog(String title, Stage owner, Scene scene, String iconFile) {
            setTitle(title);
            initStyle(StageStyle.UTILITY);
            initModality(Modality.WINDOW_MODAL);
            initOwner(owner);
            setResizable(false);
            setScene(scene);
            icon.setImage(new Image(getClass().getResourceAsStream(iconFile)));
        }

        public void showDialog() {
            sizeToScene();
            centerOnScreen();
            showAndWait();
        }
    }

    static class Message extends Text {
        public Message(String msg) {
            super(msg);
        }
    }

    public static int showSKeyDialog(Stage owner) {
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog("S/Key information", owner, scene, "/images/confirm.png");
        vb.setPadding(insets);
        vb.setSpacing(10);
        Button okButton = new Button("OK");
        okButton.setAlignment(Pos.CENTER);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                TextField input = (TextField) scene.lookup("#input");
                String stringValue = input.getText();
                try {
                    selection = Integer.parseInt(stringValue);
                } catch (NumberFormatException ex) {
                    input.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
                    return;
                }
                dial.close();
            }
        });
        
        Node messageNode = new Message("Please enter your sequence number");
        BorderPane bp = new BorderPane();
        bp.setCenter(okButton);
        BorderPane bpInput = new BorderPane();
        TextField input = new TextField();
        input.setId("input");
        input.setMaxWidth(100);
        bpInput.setCenter(input);
        bpInput.setPadding(insets2);
        HBox msg = new HBox();
        msg.setSpacing(5);
        msg.getChildren().addAll(icon, messageNode);
        vb.getChildren().addAll(msg, bpInput, bp);
        dial.setOnShown(event -> {
            event.consume();
            dial.toFront();
        });
        dial.showDialog();
        return selection;
    }
}