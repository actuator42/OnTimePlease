package com.actuator.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private static Main instance;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(
                getClass().getResource("/main.fxml"));
        primaryStage.setTitle("OnTimePlease");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icons/wonderboy.png")));
        primaryStage.setScene(new Scene(root, 907, 564));
        primaryStage.setResizable(false);
        primaryStage.show();
        instance = this;
    }

    public void openBrowser(String url) {
        getHostServices().showDocument(url);
    }

    public static Main getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
