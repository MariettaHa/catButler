package catbutler.gui.ui.schemeTreeView;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class SchemeTreeCell extends TreeCell<String> {
    private TextField textField;

    @Override
    public void cancelEdit() {
        super.cancelEdit();
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (textField == null)
            createTextField();
        setText(null);
        setGraphic(textField);
        textField.selectAll();
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.TAB) {
                    commitEdit(textField.getText());
                } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            }
        });

    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }

    @Override
    protected void updateItem(String string, boolean empty) {
        super.updateItem(string, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
            }
        }
    }

}
