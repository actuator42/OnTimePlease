package com.actuator.ui.view;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static com.actuator.ui.util.FXUtil.getStage;

public class Dialog {
    public enum Dialog_kind {
        info, imports, preference;
        private Stage stage;

        public String getPath() {
            return "/" + this.name() + ".fxml";
        }

        public void show() {
            if (stage == null)
                setDialog();
            stage.show();
        }

        public void close() {
            stage.close();
        }

        public void setStage(Stage stage) {
            this.stage = stage;
        }
    }

    private static void setDialog() {
        for (Dialog_kind kind : Dialog_kind.values()) {
            Stage stage = getStage(kind.getPath());
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(getStage("/main.fxml"));
            kind.setStage(stage);
        }
    }
}
