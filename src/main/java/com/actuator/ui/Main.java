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
    public final static String TRANSPARENT_CSS = Main.class.getResource("/transparentButton.css").toExternalForm();
    private static Main instance;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Thread.setDefaultUncaughtExceptionHandler(Main::setError);
        Parent root = FXMLLoader.load(
                getClass().getResource("/main.fxml"));
        primaryStage.setTitle("OnTimePlease");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icons/wonderboy.png")));
        primaryStage.setScene(new Scene(root, 907, 564));
        primaryStage.setResizable(false);
//        primaryStage.getScene().getStylesheets().add(TRANSPARENT_CSS);
        primaryStage.show();
        instance = this;
        stage = primaryStage;
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

    public void close() {
        stage.close();
    }

    public static Main getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
