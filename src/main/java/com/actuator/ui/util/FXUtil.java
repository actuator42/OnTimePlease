package com.actuator.ui.util;

import com.actuator.ui.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.util.logging.Logger;

public class FXUtil {
    public static final Logger logger = Logger.getLogger("FXUtil");
    public static Stage getStage(String path) {
        Stage stage = new Stage();
        try {
            URL location = MainController.class.getResource(path);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Parent main = fxmlLoader.load();
            stage.setScene(new Scene(main));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stage;
    }

    public static String getDayOfWeekForKor(DayOfWeek dayOfWeek) {
            switch (dayOfWeek) {
            case SUNDAY:
                return "(일)";
            case MONDAY:
                return "(월)";
            case TUESDAY:
                return "(화)";
            case WEDNESDAY:
                return "(수)";
            case THURSDAY:
                return "(목)";
            case FRIDAY:
                return "(금)";
            case SATURDAY:
                return "(토)";
        }
        return "";
    }
}
