package com.actuator.ui.controller;

import com.actuator.ui.Main;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class InfomationController implements Initializable {
    public ImageView logo;
    public Hyperlink linker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logo.setImage(new Image(getClass().getResourceAsStream("/images/icons/giphy.gif")));
        linker.setOnAction(event -> Main.getInstance().openBrowser(linker.getText()));
    }
}
