package com.actuator.ui.model;

import javafx.event.Event;
import javafx.scene.control.*;
import javafx.util.StringConverter;

public class EditCell<S, T> extends TableCell<S, T> {

    // Text field for editing
    // TODO: allow this to be a plugable control.
    private final TextField textField = new TextField();

    // Converter for converting the text in the text field to the user type, and vice-versa:
    private final StringConverter<T> converter ;

    private EditCell(StringConverter<T> converter) {
        this.converter = converter ;

        itemProperty().addListener((obx, oldItem, newItem) -> {
            if (newItem == null) {
                setText(null);
            } else {
                setText(converter.toString(newItem));
            }
        });
        setGraphic(textField);
        setContentDisplay(ContentDisplay.TEXT_ONLY);

        textField.setOnAction(evt -> commitEdit(this.converter.fromString(textField.getText())));
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (! isNowFocused) {
                commitEdit(this.converter.fromString(textField.getText()));
            }
        });
    }

    /**
     * Convenience converter that does nothing (converts Strings to themselves and vice-versa...).
     */
    private static final StringConverter<String> IDENTITY_CONVERTER = new StringConverter<String>() {

        @Override
        public String toString(String object) {
            return object;
        }

        @Override
        public String fromString(String string) {
            return string;
        }

    };

    public static <S> EditCell<S, String> createStringEditCell() {
        return new EditCell<>(IDENTITY_CONVERTER);
    }

    // set the text of the text field and display the graphic
    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText(converter.toString(getItem()));
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.requestFocus();
    }

    // revert to text display
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    // commits the edit. Update property if possible and revert to text display
    @Override
    public void commitEdit(T item) {

        // This block is necessary to support commit on losing focus, because the baked-in mechanism
        // sets our editing state to false before we can intercept the loss of focus.
        // The default commitEdit(...) method simply bails if we are not editing...
        if (! isEditing() && ! item.equals(getItem())) {
            TableView<S> table = getTableView();
            if (table != null) {
                TableColumn<S, T> column = getTableColumn();
                TableColumn.CellEditEvent<S, T> event = new TableColumn.CellEditEvent<>(table,
                        new TablePosition<>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item);
                Event.fireEvent(column, event);
            }
        }
        super.commitEdit(item);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (!isEmpty() && item.toString().startsWith("12.00(")) {
            setStyle("-fx-text-fill: red; -fx-alignment: CENTER");
        } else {
            setStyle("-fx-text-fill: black; -fx-alignment: CENTER");
        }
    }
}