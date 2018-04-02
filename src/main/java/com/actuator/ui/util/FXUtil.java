package com.actuator.ui.util;

import com.actuator.ui.controller.InfomationController;
import com.actuator.ui.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class FXUtil {
    public static final Logger logger = Logger.getLogger("FXUtil");
    private static final Properties skin_properties;

    static {
        skin_properties = new Properties();
        skin_properties.setProperty("IU", "1.jpg,2.jpg,3.jpg,4.jpg");
        skin_properties.setProperty("OnePeace", "1.gif,2.jpg,3.png,4.png");
        skin_properties.setProperty("Seol-Hyun", "1.jpeg,2.jpeg");
        skin_properties.setProperty("other", "1.jpg,2.jpg,3.png,4.jpg");
    }
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

    public static List<String> getDefaultSkin(String kind) {
        List<String> list = new ArrayList<>();
        //"/images/skin/" + skinKind + "/" + fileName)
        if (!kind.equals("Random")) {
            addList(kind, list);
        } else {
            for (Map.Entry<Object, Object> v : skin_properties.entrySet()) {
                addList(v.getKey().toString(), list);
            }
        }
        return list;
    }

    private static void addList(String kind, List<String> list) {
        for (String v : skin_properties.getProperty(kind).split(",")) {
            list.add("/images/skin/" + kind + "/" + v);
        }
    }

    public static void log(String value) {
        InfomationController.getInstance().debugArea.appendText(value);
    }
}
