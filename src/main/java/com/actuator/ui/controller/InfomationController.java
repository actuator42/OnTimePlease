package com.actuator.ui.controller;

import com.actuator.ui.Main;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.jar.Manifest;

public class InfomationController implements Initializable {
    public ImageView logo;
    public Hyperlink linker;
    public Label manifestLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logo.setImage(new Image(getClass().getResourceAsStream("/images/icons/giphy.gif")));
        linker.setOnAction(event -> Main.getInstance().openBrowser(linker.getText()));
        URLClassLoader cl = (URLClassLoader) getClass().getClassLoader();
        try {
            URL url = cl.findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());
            String v = "";
            for (Map.Entry<Object, Object> entry : manifest.getMainAttributes().entrySet()) {
                v += entry.getKey() + " : " + entry.getValue() + "\n";
            }
            manifestLabel.setText(v);
        } catch (IOException E) {
            // handle
        }
    }
}
