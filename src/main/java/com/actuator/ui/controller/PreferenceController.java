package com.actuator.ui.controller;

import com.actuator.ui.view.Dialog;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferenceController implements Initializable {
    public Button okButton;
    public Button closeButton;
    public SplitMenuButton skinMenu;
    public MenuItem selected;
    public TextField durationField;
    public CheckBox rotationCheckbox;
    public CheckBox useCurrentTime;
    private static PreferenceController instance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (instance == null)
            instance = this;
        for (MenuItem item : skinMenu.getItems()) {
            item.addEventHandler(EventType.ROOT, event -> {
                selected = (MenuItem) event.getSource();
                skinMenu.setText(selected.getText());
            });
        }
        rotationCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            durationField.setDisable(oldValue);
        });
    }

    public void okButtonClick(MouseEvent mouseEvent) {
        if (selected != null && !selected.getText().equalsIgnoreCase("None")) {
            MainController.getInstance().showSkin(selected.getText(),
                    Double.valueOf(durationField.isDisable() ? "0" : durationField.getText()));
        } else {
            MainController.getInstance().clearSkin();
        }
        Dialog.Dialog_kind.preference.close();
    }

    public void closeButtonClick(MouseEvent mouseEvent) {
        Dialog.Dialog_kind.preference.close();
    }

    public static PreferenceController getInstance() {
        return instance;
    }

    public boolean getUseCurrentTime() {
        return useCurrentTime.isSelected();
    }
}
