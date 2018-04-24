package com.actuator.ui.controller;

import com.actuator.ui.model.DayInfo;
import com.actuator.ui.util.FXUtil;
import com.actuator.ui.view.Dialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ResourceBundle;

public class ImportDialogController implements Initializable {
    private ObservableList<DayInfo> dataList = FXCollections.observableArrayList();
    public TextArea importArea;
    public Button importBtn;
    public Button helpBtn;
    private PopOver popOver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXUtil.createImageButton("", helpBtn);
        popOver = new PopOver();
        popOver.setContentNode(new ImageView(new Image(FXUtil.class.getResourceAsStream("/images/help/import_help.gif"))));
        popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        popOver.setCornerRadius(4);
    }

    public void importButtonClick(ActionEvent actionEvent) {
        dataList.clear();
        for (String element : importArea.getText().split("\\n")) {
            dataList.add(new DayInfo(element));
        }
        MainController.getInstance().summeryTime(dataList);
        MainController.getInstance().tableView.setItems(dataList);
        Dialog.Dialog_kind.imports.close();
    }

    public void helpButtionClick(ActionEvent actionEvent) {
        if (popOver.isShowing()) {
            popOver.hide();
        } else if (!popOver.isShowing()) {
            showPopover(helpBtn, popOver);
        } else {
            new RuntimeException("isShowing() state not recognised.");
        }
    }

    public void showPopover(Button button, PopOver popOver) {
        double clickX = button.localToScreen(button.getBoundsInLocal()).getMinX();
        double clickY = (button.localToScreen(button.getBoundsInLocal()).getMinY() +
                button.localToScreen(button.getBoundsInLocal()).getMaxY()) / 2;
        popOver.show(button.getScene().getWindow());
        // Show on left
//        popOver.setX(clickX - popOver.getWidth());
//        popOver.setY(clickY - popOver.getHeight() / 2);
        // Show on right
        popOver.setX(clickX + 40.0);
        popOver.setY(clickY - popOver.getHeight() / 2);
    }
}
