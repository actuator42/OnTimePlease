package com.actuator.ui;

import com.actuator.ui.util.FXUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Main extends Application {
    private static Main instance;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Thread.setDefaultUncaughtExceptionHandler(Main::setError);
        Parent root = FXMLLoader.load(
                getClass().getResource("/main.fxml"));
        primaryStage.setTitle("OnTimePlease");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icons/wonderboy.png")));
        primaryStage.setScene(new Scene(root, 907, 564));
        primaryStage.setResizable(false);
        primaryStage.show();
        instance = this;
    }

    private static void setError(Thread thread, Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        FXUtil.log(sw.toString());
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
