package com.actuator.ui.view;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static com.actuator.ui.util.FXUtil.getStage;

public class InfoDialog {
    private static Stage dialog;

    public InfoDialog() {
        if (dialog == null) {
            dialog = getStage("/info.fxml");
            dialog.initStyle(StageStyle.DECORATED);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(getStage("/main.fxml"));
        }
    }

    public void show() {
        dialog.showAndWait();
    }

    public static Stage getDialog() {
        return dialog;
    }
}