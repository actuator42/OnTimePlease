package com.actuator.ui.controller;

import com.actuator.ui.model.DayInfo;
import com.actuator.ui.view.ImportDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class ImportDialogController {
    private ObservableList<DayInfo> dataList = FXCollections.observableArrayList();
    public TextArea importArea;
    public Button importBtn;

    public void importButtonClick(ActionEvent actionEvent) {
        dataList.clear();
        for (String element : importArea.getText().split(System.lineSeparator())) {
            dataList.add(new DayInfo(element));
        }
        MainController.getInstance().summeryTime(dataList);
        MainController.getInstance().tableView.setItems(dataList);
        ImportDialog.getDialog().close();
    }
}