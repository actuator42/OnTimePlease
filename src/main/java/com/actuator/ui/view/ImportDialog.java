package com.actuator.ui.view;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static com.actuator.ui.util.FXUtil.getStage;

public class ImportDialog {
    private static Stage dialog;

    public ImportDialog() {
        if (dialog == null) {
            dialog = getStage("/import.fxml");
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
