package com.actuator.ui.controller;

import com.actuator.ui.model.EditCell;
import com.actuator.ui.model.DayInfo;
import com.actuator.ui.view.ImportDialog;
import com.actuator.ui.view.InfoDialog;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public TableColumn<DayInfo, String> indexCol;
    public TableColumn<DayInfo, String> dateCol;
    public TableColumn<DayInfo, String> dayCol;
    public TableColumn<DayInfo, String> inCol;
    public TableColumn<DayInfo, String> outCol;
    public TableColumn<DayInfo, String> workCol;
    public TableColumn<DayInfo, String> statusCol;

    private static MainController instance;
    public Pane mainPane;
    public TableView tableView;
    public ProgressBar progressBar;
    public Label total;
    public Button plus;
    public Button minus;
    public Button refresh;

    private Thread thread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (instance == null)
            instance = this;
        tableView.setOnKeyPressed(event -> {
            TablePosition<DayInfo, String> pos = tableView.getFocusModel().getFocusedCell() ;
            if (pos != null && event.getCode().isLetterKey()) {
                tableView.edit(pos.getRow(), pos.getTableColumn());
            }
        });
        setColumnFactoryAndAction(indexCol, "index");
        setColumnFactoryAndAction(dateCol, "day");
        setColumnFactoryAndAction(dayCol, "dayType");
        setColumnFactoryAndAction(inCol, "getTheWork");
        setColumnFactoryAndAction(outCol, "leaveTheWork");
        setColumnFactoryAndAction(workCol, "workTime");
        setColumnFactoryAndAction(statusCol, "status");
        refresh.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/icons/menu_refresh.png"))));
        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            double progress = newValue == null ? 0 : newValue.doubleValue();
            if (progress >= 1) {
                progressBar.setStyle("-fx-accent: red;");
            } else {
                progressBar.setStyle("-fx-accent: #6487ff;");
            }
        });

    }

    private void setColumnFactoryAndAction(TableColumn<DayInfo, String> column, String columnName) {
        column.setCellValueFactory(new PropertyValueFactory<>(columnName));
        column.setCellFactory(col -> EditCell.createStringEditCell());
        column.setOnEditCommit(event -> {
            if (event != null && event.getTablePosition() != null && event.getTableView() != null) {
                DayInfo dayInfo = event.getTableView().getItems().get(event.getTablePosition().getRow());
                String newValue = event.getNewValue();
                dayInfo.setValuesAtColumns(event.getTablePosition().getTableColumn().getText(), newValue);
            }
        });
    }

    public void summeryTime(ObservableList<DayInfo> dataList) {
        Duration cumulative = Duration.ofHours(0);
        for (DayInfo day : dataList) {
            cumulative = cumulative.plus(day.getWorkTimeDuration());
        }
        ((TableColumn)tableView.getColumns().get(0)).setVisible(false);
        ((TableColumn)tableView.getColumns().get(0)).setVisible(true);
        final Duration finalCumulative = cumulative;
        Task<Void> task = new Task<Void>() {
            @Override public Void call() {
                LocalDateTime startTime = LocalDateTime.now();
                while (true) {
                    Duration now = Duration.between(startTime, LocalDateTime.now());
                    updateProgress(finalCumulative.toMinutes() + now.toMinutes(), 2400);
                    String v = String.format(
                            "%d:%02d", (finalCumulative.getSeconds() + now.getSeconds()) / 3600,
                            ((finalCumulative.getSeconds() + now.getSeconds()) % 3600) / 60);
                    v+= " hours work a week\nweek remain " + getWeekRemain(now);
                    updateMessage(v);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                        break;
                    }
                }
                return null;
            }

            private String getWeekRemain(Duration now) {
                String v = String.format(
                        "%d:%02d", ((2400 * 60) - finalCumulative.getSeconds() + now.getSeconds()) / 3600,
                        (((2400 * 60) - finalCumulative.getSeconds() - now.getSeconds()) % 3600) / 60);
                if (v.contains(":-"))
                    return v.replace(":-", ":") + "\nAre you crazy?";
                return v;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        total.textProperty().bind(task.messageProperty());
        if (thread != null)
            thread.interrupt();
        thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static MainController getInstance() {
        return instance;
    }

    public void importMenuClick(ActionEvent actionEvent) {
        new ImportDialog().show();
    }

    public void refreshButtionClick(MouseEvent mouseEvent) {
        summeryTime(tableView.getItems());
    }

    public void plusClick(MouseEvent mouseEvent) {
        LocalDate date = null;
        if (tableView.getItems().size() > 0) {
            for (Object dayInfo : tableView.getItems()) {
                if (date == null)
                    date = LocalDate.parse(((DayInfo)dayInfo).getDay(), PATTERN);
                else if (!date.isAfter(LocalDate.parse(((DayInfo)dayInfo).getDay(), PATTERN))) {
                    date = LocalDate.parse(((DayInfo)dayInfo).getDay(), PATTERN);
                }
            }
        } else {
            date = LocalDate.now();
        }
        DayInfo info = new DayInfo();
        info.setDay(date.plusDays(1).format(PATTERN));
        info.setDefault();
        info.setIndex(String.valueOf(tableView.getItems().size() + 1));
        tableView.getItems().add(info);
    }

    public void minusClick(MouseEvent mouseEvent) {
        if (tableView.getItems().size() > 0)
            tableView.getItems().remove(tableView.getSelectionModel().getSelectedIndex());
    }

    public void aboutMenuClick(ActionEvent actionEvent) {
        new InfoDialog().show();
    }
}