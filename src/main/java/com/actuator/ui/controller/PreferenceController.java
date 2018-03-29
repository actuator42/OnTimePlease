package com.actuator.ui.controller;

import com.actuator.ui.view.Dialog;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PreferenceController implements Initializable {
    public Button okButton;
    public Button closeButton;
    public SplitMenuButton skinMenu;
    public MenuItem selected;
    public TextField durationField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (MenuItem item : skinMenu.getItems()) {
            item.addEventHandler(EventType.ROOT, event -> {
                selected = (MenuItem) event.getSource();
                skinMenu.setText(selected.getText());
            });
        }
    }

    public void okButtonClick(MouseEvent mouseEvent) throws IOException {
        if (!selected.getText().equalsIgnoreCase("None")) {
            MainController.getInstance().showSkin(
                    "/images/skin/" + (selected.getText().equals("Random") ? "" : selected.getText() + "/"),
                    Double.valueOf(durationField.getText()));
        }
        Dialog.Dialog_kind.preference.close();
    }

    public void closeButtonClick(MouseEvent mouseEvent) {
        Dialog.Dialog_kind.preference.close();
    }
}
