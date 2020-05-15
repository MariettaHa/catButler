package catbutler.gui.ui.dataTableView;

import catbutler.gui.mainControllers.EditorViewController;
import catbutler.model.DataModel;
import catbutler.model.Reaction;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.converter.DoubleStringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ReactionTableView extends TableView<ReactionTableEntry> {

    DataModel dataModel;
    ObservableList data = FXCollections.observableList(new ArrayList<>());
    HashSet<String> ids = new HashSet<>();
    EditorViewController editorViewController;

    public ReactionTableView(DataModel dataModel, EditorViewController editorViewController) {
        this.dataModel = dataModel;
        this.editorViewController = editorViewController;
        for (Reaction r : dataModel.getReactionSet()) {
            ids.add(r.getReactionId());
            data.add(new ReactionTableEntry(r));
        }

        initColumns();
        this.setEditable(true);
        initListeners();
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.getItems().addAll(data);

    }

    private void initListeners() {
        this.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                ReactionTableView.this.removeSelected();
            } else if (keyEvent.getCode().equals(KeyCode.INSERT)) {
                ReactionTableView.this.addNewRow();
            }
        });
        this.setRowFactory(tv -> {
            TableRow<ReactionTableEntry> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem showReactionView = new MenuItem("Show Reaction View");
            showReactionView.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    row.getItem().createReactionView();
                }
            });

            MenuItem deleteRow = new MenuItem("Delete Row");
            deleteRow.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    ReactionTableView.this.removeRow(row.getItem());
                }
            });

            MenuItem deleteSelection = new MenuItem("Delete Selection");
            deleteSelection.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    ReactionTableView.this.getItems().removeAll(ReactionTableView.this.getSelectionModel().getSelectedItems());
                }
            });

            MenuItem selectAll = new MenuItem("Select All");
            selectAll.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    ReactionTableView.this.getSelectionModel().selectAll();
                }
            });

            MenuItem addRow = new MenuItem("Add Row to Table");
            addRow.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    ReactionTableView.this.addNewRow();
                }
            });

            MenuItem browseSBO = new MenuItem("Choose SBO");
            browseSBO.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    row.getItem().createSBOBrowser();
                }
            });

            contextMenu.getItems().addAll(showReactionView, new SeparatorMenuItem(), deleteRow, deleteSelection, selectAll, addRow, new SeparatorMenuItem(), browseSBO);
            row.setContextMenu(contextMenu);
            return row;
        });

        this.getItems().addListener(new ListChangeListener<ReactionTableEntry>() {
            @Override
            public void onChanged(Change<? extends ReactionTableEntry> change) {
                for (ReactionTableEntry s : ReactionTableView.this.getItems()) {
                    for (String prop : s.getStringPropertyHashMap().keySet()) {
                        SimpleStringProperty simpleStringProperty = s.getStringPropertyHashMap().get(prop);
                        simpleStringProperty.addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                                editorViewController.update(ReactionTableView.this);
                            }
                        });
                    }
                    for (String prop : s.getDoublePropertyHashMap().keySet()) {
                        SimpleDoubleProperty simpleDoubleProperty = s.getDoublePropertyHashMap().get(prop);
                        simpleDoubleProperty.addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                                editorViewController.update(ReactionTableView.this);
                            }
                        });
                    }
                    for (String prop : s.getBooleanPropertyHashMap().keySet()) {
                        SimpleBooleanProperty simpleBooleanProperty = s.getBooleanPropertyHashMap().get(prop);
                        simpleBooleanProperty.addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean s, Boolean t1) {
                                editorViewController.update(ReactionTableView.this);
                            }
                        });
                    }
                }

            }
        });
    }

    public void addNewRow() {
        String s = "newR_1";
        while (ids.contains(s)) {
            s = s.split("_")[0] + "_" + (int) (Integer.parseInt(s.split("_")[1]) + 1);
        }
        ids.add(s);
        Reaction r = new Reaction(s);
        this.getItems().add(new ReactionTableEntry(r));

        editorViewController.update(ReactionTableView.this);
    }

    public void removeSelected() {
        for (ReactionTableEntry e : this.getSelectionModel().getSelectedItems()) {
            ids.remove(e.getReactionId());
        }
        this.getItems().removeAll(this.getSelectionModel().getSelectedItems());
        editorViewController.update(ReactionTableView.this);
    }

    public void removeRow(ReactionTableEntry row) {
        this.getItems().remove(row);
        editorViewController.update(ReactionTableView.this);
    }

    private void initColumns() {
        TableColumn<ReactionTableEntry, String> idCol //
                = new TableColumn<>("reactionId");
        TableColumn<ReactionTableEntry, String> typeCol
                = new TableColumn<>("type");
        TableColumn<ReactionTableEntry, String> nameCol //
                = new TableColumn<>("name");
        TableColumn<ReactionTableEntry, String> sboCol //
                = new TableColumn<>("sbo");
        TableColumn<ReactionTableEntry, String> descriptionCol //
                = new TableColumn<>("description");
        TableColumn<ReactionTableEntry, String> metaDataCol //
                = new TableColumn<>("metaData");
        TableColumn<ReactionTableEntry, String> noteCol //
                = new TableColumn<>("note");
        TableColumn<ReactionTableEntry, String> formulaCol //
                = new TableColumn<>("formula");
        TableColumn<ReactionTableEntry, Double> startPosXCol //
                = new TableColumn<>("startPosX");
        TableColumn<ReactionTableEntry, Double> startPosYCol //
                = new TableColumn<>("startPosY");
        TableColumn<ReactionTableEntry, Double> startPosZCol //
                = new TableColumn<>("startPosZ");
        TableColumn<ReactionTableEntry, Double> endPosXCol ////
                = new TableColumn<>("endPosX");
        TableColumn<ReactionTableEntry, Double> endPosYCol //
                = new TableColumn<>("endPosY");
        TableColumn<ReactionTableEntry, Double> endPosZCol //
                = new TableColumn<>("endPosZ");
        TableColumn<ReactionTableEntry, Boolean> isReversibleCol //
                = new TableColumn<>("isReversible");
        TableColumn<ReactionTableEntry, Double> weightCol //
                = new TableColumn<>("weight");
        TableColumn<ReactionTableEntry, String> metaIdCol //
                = new TableColumn<>("metaId");
        TableColumn<ReactionTableEntry, String> compartmentCol //
                = new TableColumn<>("compartment");
        TableColumn<ReactionTableEntry, String> networkIdCol //
                = new TableColumn<>("networkId");
        TableColumn<ReactionTableEntry, String> reactantsDnfCol //
                = new TableColumn<>("reactantsDNF");
        TableColumn<ReactionTableEntry, String> productsDnfCol //
                = new TableColumn<>("productsDNF");
        TableColumn<ReactionTableEntry, String> catalystsDnfCol //
                = new TableColumn<>("catalystsDNF");
        TableColumn<ReactionTableEntry, String> inhibitorsDnfCol //
                = new TableColumn<>("inhibitorsDNF");
        this.getColumns().addAll(idCol, typeCol, nameCol, sboCol, descriptionCol, metaDataCol, noteCol,
                formulaCol, startPosXCol, startPosYCol, startPosZCol, endPosXCol, endPosYCol,
                endPosZCol, isReversibleCol, weightCol, metaIdCol, compartmentCol, networkIdCol, reactantsDnfCol,
                productsDnfCol, catalystsDnfCol, inhibitorsDnfCol);

        for (TableColumn<ReactionTableEntry, String> col : Arrays.asList(idCol, typeCol, nameCol, sboCol, descriptionCol, metaDataCol, noteCol,
                formulaCol, metaIdCol, compartmentCol, networkIdCol, reactantsDnfCol, productsDnfCol, catalystsDnfCol, inhibitorsDnfCol)) {
            if (!col.getText().equals("formula")) {
                setEditableString(col);
            } else {
                col.setCellValueFactory(new PropertyValueFactory<>(col.getText()));
                col.setEditable(false);
            }
        }
        for (TableColumn<ReactionTableEntry, Double> col : Arrays.asList(startPosXCol, startPosYCol, startPosZCol, endPosXCol, endPosYCol,
                endPosZCol, weightCol)) {
            setEditableDouble(col);
        }
        for (TableColumn<ReactionTableEntry, Boolean> col : Arrays.asList(isReversibleCol)) {
            setEditableBoolean(col);
        }
    }

    private void setEditableString(TableColumn<ReactionTableEntry, String> col) {
        col.setCellValueFactory(new PropertyValueFactory<>(col.getText()));
        col.setCellFactory(TextFieldTableCell.forTableColumn());
        col.setOnEditCommit((TableColumn.CellEditEvent<ReactionTableEntry, String> event) -> {
            TablePosition<ReactionTableEntry, String> pos = event.getTablePosition();
            String newValue = event.getNewValue();
            int row = pos.getRow();
            ReactionTableEntry reactionEntry = event.getTableView().getItems().get(row);
            reactionEntry.getStringPropertyHashMap().get(col.getText()).setValue(newValue);
        });
    }

    private void setEditableDouble(TableColumn<ReactionTableEntry, Double> col) {
        col.setCellValueFactory(new PropertyValueFactory<>(col.getText()));
        col.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        col.setOnEditCommit((TableColumn.CellEditEvent<ReactionTableEntry, Double> event) -> {
            TablePosition<ReactionTableEntry, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();
            int row = pos.getRow();
            ReactionTableEntry reactionEntry = event.getTableView().getItems().get(row);
            reactionEntry.getDoublePropertyHashMap().get(col.getText()).setValue(newValue);
        });
    }

    private void setEditableBoolean(TableColumn<ReactionTableEntry, Boolean> col) {
        col.setCellValueFactory(
                param -> {
                    return param.getValue().getBooleanPropertyHashMap().get(col.getText());//;.getValue().registeredProperty();
                });
        col.setCellFactory(CheckBoxTableCell.forTableColumn(col));
        final CheckBoxTableCell<ReactionTableEntry, Boolean> ctCell = new CheckBoxTableCell<>();
        ctCell.setSelectedStateCallback(index -> {
            ReactionTableEntry entry = (ReactionTableEntry) ReactionTableView.this.getItems().get(index);
            SimpleBooleanProperty prop = entry.getBooleanPropertyHashMap().get(col.getText());
            return prop;
        });

        col.setCellFactory(column -> new CheckBoxTableCell());
        col.setEditable(true);
    }

    public void selectById(String id) {

        for (ReactionTableEntry e : this.getSelectionModel().getSelectedItems()) {
            if (e.getReactionId().equals(id)) {
                this.getSelectionModel().select(e);
            }
        }
    }

    public void selectById(HashSet<String> ids) {
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.getSelectionModel().selectAll();
        this.getSelectionModel().clearSelection();
        for (ReactionTableEntry e : this.getItems()) {
            if (ids.contains(e.getReactionId())) {
                this.getSelectionModel().select(e);
            }
        }
    }

    public void moveSelectionUp() {
        if (getSelectionModel().getSelectedItems().size() > 0) {
            ReactionTableEntry entry = this.getSelectionModel().getSelectedItems().get(0);
            int pos = this.getItems().indexOf(entry);
            this.getItems().remove(entry);
            if (pos == 0) {
                pos = 1;
            }
            this.getItems().add(pos - 1, entry);
            getSelectionModel().clearSelection();
            getSelectionModel().select(entry);
        }
    }

    public void moveSelectionDown() {
        if (getSelectionModel().getSelectedItems().size() > 0) {
            ReactionTableEntry entry = this.getSelectionModel().getSelectedItems().get(0);
            int pos = this.getItems().indexOf(entry);
            this.getItems().remove(entry);
            pos++;
            if (pos >= this.getItems().size()) {
                pos = this.getItems().size();
            }
            this.getItems().add(pos, entry);
            getSelectionModel().clearSelection();
            getSelectionModel().select(entry);
        }
    }
}
