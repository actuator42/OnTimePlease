package com.actuator.ui.controller;

import com.actuator.ui.model.DayInfo;
import com.actuator.ui.model.EditCell;
import com.actuator.ui.view.Dialog;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public TableColumn<DayInfo, String> indexCol;
    public TableColumn<DayInfo, String> dateCol;
    public TableColumn<DayInfo, String> dayCol;
    public TableColumn<DayInfo, String> dayTimeCol;
    public TableColumn<DayInfo, String> inCol;
    public TableColumn<DayInfo, String> outCol;
    public TableColumn<DayInfo, String> workCol;
    public TableColumn<DayInfo, String> statusCol;

    private static MainController instance;
    public ImageView skinView;
    public Pane mainPane;
    public TableView tableView;
    public ProgressBar progressBar;
    public Label total;
    public Button plus;
    public Button minus;
    public Button refresh;

    private ArrayList<Image> imagesList = new ArrayList<>();
    private Duration workOnWeekTime = Duration.ofHours(40);
    private Timeline animation = null;
    private EventHandler<ActionEvent> eventHandler = e -> changeImage();
    private Thread thread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (instance == null)
            instance = this;
        tableView.setOnKeyPressed(event -> {
            TablePosition<DayInfo, String> pos = tableView.getFocusModel().getFocusedCell();
            if (pos != null && event.getCode().isLetterKey()) {
                tableView.edit(pos.getRow(), pos.getTableColumn());
            }
        });
        setColumnFactoryAndAction(indexCol, "index");
        setColumnFactoryAndAction(dateCol, "day");
        setColumnFactoryAndAction(dayCol, "dayType");
        setColumnFactoryAndAction(dayTimeCol, "dayTypeTime");
        setColumnFactoryAndAction(inCol, "getTheWork");
        setColumnFactoryAndAction(outCol, "leaveTheWork");
        setColumnFactoryAndAction(workCol, "workTime");
        setColumnFactoryAndAction(statusCol, "status");
        refresh.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/icons/menu_refresh.png"))));
        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            double progress = newValue == null ? 0 : newValue.doubleValue();
            if (progress >= 1) {
                progressBar.setStyle("-fx-accent: red;");
            } else if (progress > 0.8) {
                progressBar.setStyle("-fx-accent: #6487ff;");
            } else if (progress > 0.5) {
                progressBar.setStyle("-fx-accent: #43ff88;");
            } else {
                progressBar.setStyle("-fx-accent: #fdff60;");
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
                dayInfo.updateValuesAtColumns(event.getTablePosition().getTableColumn().getText(), newValue);
            }
        });
    }

    public void summeryTime(ObservableList<DayInfo> dataList) {
        Duration cumulative = Duration.ofHours(0);
        for (DayInfo day : dataList) {
            cumulative = cumulative.plus(day.getWorkTimeDuration());
        }
        ((TableColumn) tableView.getColumns().get(0)).setVisible(false);
        ((TableColumn) tableView.getColumns().get(0)).setVisible(true);
        final Duration finalCumulative = cumulative;
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                LocalDateTime startTime = LocalDateTime.now();
                while (true) {
                    Duration runningDuration = Duration.between(startTime, LocalDateTime.now());
                    updateProgress(finalCumulative.toMinutes() + runningDuration.toMinutes(), 2400);
                    String v = String.format(
                            "%d:%02d", (runningDuration.plusSeconds(finalCumulative.getSeconds()).toHours()),
                            (runningDuration.plusSeconds(finalCumulative.getSeconds()).toMinutes() % 60));
                    v += " hours work a week\nweek remain " + getWeekRemain(runningDuration);
                    updateMessage(v);
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                }
            }

            private String getWeekRemain(Duration runningDuration) {
                String v = String.format(
                        "%d:%02d", workOnWeekTime.minus(runningDuration.plusSeconds(finalCumulative.getSeconds())).toHours(),
                        workOnWeekTime.minus(runningDuration.plusSeconds(finalCumulative.getSeconds())).toMinutes() % 60);
                if (v.contains(":-")) {
                    if (!v.startsWith("-")) {
                        v = "-" + v;
                    }
                    return v.replace(":-", ":") + "\nAre you crazy?";
                }
                return v;
            }
        };
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());
        total.textProperty().bind(task.messageProperty());
        if (thread != null)
            thread.interrupt();
        thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void showSkin(String skinPath, Double time) throws IOException {
        imagesList.clear();
        for (String filePath : getResourceFiles(skinPath)) {
            imagesList.add(new Image(getClass().getResourceAsStream(skinPath + filePath)));
        }
        skinView.setImage(imagesList.get(0));
        skinView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> changeImage());
        if (animation != null)
            animation.stop();
        animation = new Timeline(new KeyFrame(javafx.util.Duration.millis(time), eventHandler));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void changeImage() {
        int index;
        if (imagesList.indexOf(skinView.getImage()) == imagesList.size() - 1) {
            index = 0;
        } else {
            index = imagesList.indexOf(skinView.getImage()) + 1;
        }
        skinView.setImage(imagesList.get(index));
    }

    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();
        try (InputStream in = getResourceAsStream(path);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;
            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }
        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static MainController getInstance() {
        return instance;
    }

    public void importMenuClick(ActionEvent actionEvent) {
        Dialog.Dialog_kind.imports.show();
    }

    public void refreshButtionClick(MouseEvent mouseEvent) {
        summeryTime(tableView.getItems());
    }

    public void plusClick(MouseEvent mouseEvent) {
        LocalDate date = null;
        if (tableView.getItems().size() > 0) {
            for (Object dayInfo : tableView.getItems()) {
                if (date == null)
                    date = LocalDate.parse(((DayInfo) dayInfo).getDay(), PATTERN);
                else if (!date.isAfter(LocalDate.parse(((DayInfo) dayInfo).getDay(), PATTERN))) {
                    date = LocalDate.parse(((DayInfo) dayInfo).getDay(), PATTERN);
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
        Dialog.Dialog_kind.info.show();
    }


    public void preferenceMenuClick(ActionEvent actionEvent) {
        Dialog.Dialog_kind.preference.show();
    }
}
