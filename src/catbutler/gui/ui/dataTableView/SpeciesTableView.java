package catbutler.gui.ui.dataTableView;

import catbutler.gui.mainControllers.EditorViewController;
import catbutler.model.DataModel;
import catbutler.model.Species;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.input.KeyEvent;
import javafx.util.converter.DoubleStringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class SpeciesTableView extends TableView<SpeciesTableEntry> {

    DataModel dataModel;
    ObservableList data = FXCollections.observableList(new ArrayList<>());
    HashSet<String> ids = new HashSet<>();

    EditorViewController editorViewController;

    public SpeciesTableView(DataModel dataModel, EditorViewController editorViewController) {
        this.dataModel = dataModel;
        this.editorViewController = editorViewController;

        for (Species s : dataModel.getSpeciesSet()) {
            data.add(new SpeciesTableEntry(s));
        }
        initColumns();
        this.setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        initListeners();
        this.getItems().addAll(data);
    }

    public void addNewRow() {
        String s = "newS_1";
        while (ids.contains(s)) {
            s = s.split("_")[0] + "_" + (int) (Integer.parseInt(s.split("_")[1]) + 1);
        }
        ids.add(s);
        Species sp = new Species(s);
        getItems().add(new SpeciesTableEntry(sp));
        editorViewController.update(SpeciesTableView.this);

    }

    public void removeSelected() {
        for (SpeciesTableEntry e : this.getSelectionModel().getSelectedItems()) {
            ids.remove(e.getSpeciesId());
        }
        this.getItems().removeAll(this.getSelectionModel().getSelectedItems());
        editorViewController.update(SpeciesTableView.this);
    }

    public void selectById(String id) {
        for (SpeciesTableEntry e : this.getSelectionModel().getSelectedItems()) {
            if (e.getSpeciesId().equals(id)) {
                this.getSelectionModel().select(e);
            }
        }
    }

    public void selectById(HashSet<String> ids) {
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.getSelectionModel().selectAll();
        this.getSelectionModel().clearSelection();
        for (SpeciesTableEntry e : this.getItems()) {
            if (ids.contains(e.getSpeciesId())) {
                this.getSelectionModel().select(e);
            }
        }
    }

    private void initColumns() {
        TableColumn<SpeciesTableEntry, String> idCol
                = new TableColumn<>("speciesId");
        TableColumn<SpeciesTableEntry, String> sboCol
                = new TableColumn<>("sbo");
        TableColumn<SpeciesTableEntry, Double> initAmountCol
                = new TableColumn<>("initAmount");
        TableColumn<SpeciesTableEntry, String> namesCol
                = new TableColumn<>("names");
        TableColumn<SpeciesTableEntry, String> typeCol
                = new TableColumn<>("type");
        TableColumn<SpeciesTableEntry, String> compartmentCol
                = new TableColumn<>("compartment");
        TableColumn<SpeciesTableEntry, String> descriptionCol
                = new TableColumn<>("description");
        TableColumn<SpeciesTableEntry, String> metadataCol
                = new TableColumn<>("metaData");
        TableColumn<SpeciesTableEntry, String> noteCol
                = new TableColumn<>("note");
        TableColumn<SpeciesTableEntry, Double> posXCol
                = new TableColumn<>("posX");
        TableColumn<SpeciesTableEntry, Double> posYCol
                = new TableColumn<>("posY");
        TableColumn<SpeciesTableEntry, Double> posZCol
                = new TableColumn<>("posZ");
        TableColumn<SpeciesTableEntry, Boolean> hasOnlySubstanceUnitsCol
                = new TableColumn<>("hasOnlySubstanceUnits");
        TableColumn<SpeciesTableEntry, Boolean> boundaryConditionCol
                = new TableColumn<>("boundaryCondition");
        TableColumn<SpeciesTableEntry, Boolean> constantCol
                = new TableColumn<>("constant");
        TableColumn<SpeciesTableEntry, String> metaIdCol
                = new TableColumn<>("metaId");
        TableColumn<SpeciesTableEntry, String> networkIdCol
                = new TableColumn<>("networkId");
        TableColumn<SpeciesTableEntry, Double> initConcentrationCol
                = new TableColumn<>("initConcentration");
        TableColumn<SpeciesTableEntry, String> substanceUnitsCol
                = new TableColumn<>("substanceUnits");
        TableColumn<SpeciesTableEntry, String> conversionFactorCol
                = new TableColumn<>("conversionFactor");
        this.getColumns().addAll(idCol, sboCol, initAmountCol, namesCol, typeCol, compartmentCol, descriptionCol, metadataCol,
                noteCol, posXCol, posYCol, posZCol, hasOnlySubstanceUnitsCol, boundaryConditionCol,
                constantCol, metaIdCol, networkIdCol, initConcentrationCol, substanceUnitsCol, conversionFactorCol);

        for (TableColumn<SpeciesTableEntry, String> col : Arrays.asList(idCol, sboCol, namesCol, typeCol, compartmentCol, descriptionCol, metadataCol,
                noteCol, metaIdCol, substanceUnitsCol, conversionFactorCol, networkIdCol)) {
            setEditableString(col);
        }
        for (TableColumn<SpeciesTableEntry, Double> col : Arrays.asList(initAmountCol, posXCol, posYCol, posZCol, initConcentrationCol)) {
            setEditableDouble(col);
        }
        for (TableColumn<SpeciesTableEntry, Boolean> col : Arrays.asList(hasOnlySubstanceUnitsCol, boundaryConditionCol,
                constantCol)) {
            setEditableBoolean(col);
        }
    }

    private void setEditableString(TableColumn<SpeciesTableEntry, String> col) {
        col.setCellValueFactory(new PropertyValueFactory<>(col.getText()));
        col.setCellFactory(TextFieldTableCell.forTableColumn());
        col.setOnEditCommit((TableColumn.CellEditEvent<SpeciesTableEntry, String> event) -> {
            TablePosition<SpeciesTableEntry, String> pos = event.getTablePosition();
            String newValue = event.getNewValue();
            int row = pos.getRow();
            SpeciesTableEntry speciesTableEntry = event.getTableView().getItems().get(row);
            speciesTableEntry.getStringPropertyHashMap().get(col.getText()).setValue(newValue);


        });


    }

    private void setEditableDouble(TableColumn<SpeciesTableEntry, Double> col) {
        col.setCellValueFactory(new PropertyValueFactory<>(col.getText()));
        col.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        col.setOnEditCommit((TableColumn.CellEditEvent<SpeciesTableEntry, Double> event) -> {
            TablePosition<SpeciesTableEntry, Double> pos = event.getTablePosition();
            Double newValue = event.getNewValue();
            int row = pos.getRow();
            SpeciesTableEntry reactionEntry = event.getTableView().getItems().get(row);
            reactionEntry.getDoublePropertyHashMap().get(col.getText()).setValue(newValue);
        });
    }

    private void setEditableBoolean(TableColumn<SpeciesTableEntry, Boolean> col) {
        col.setCellValueFactory(
                param -> {
                    return param.getValue().getBooleanPropertyHashMap().get(col.getText());//;.getValue().registeredProperty();
                });
        col.setCellFactory(CheckBoxTableCell.forTableColumn(col));
        final CheckBoxTableCell<SpeciesTableEntry, Boolean> ctCell = new CheckBoxTableCell<>();
        ctCell.setSelectedStateCallback(index -> {
            SpeciesTableEntry entry = SpeciesTableView.this.getItems().get(index);
            SimpleBooleanProperty prop = entry.getBooleanPropertyHashMap().get(col.getText());
            return prop;
        });

        col.setCellFactory(column -> new CheckBoxTableCell());
        col.setEditable(true);
    }

    public void setEditorViewController(EditorViewController editorViewController) {
        this.editorViewController = editorViewController;
    }

    public void moveSelectionUp() {
        if (getSelectionModel().getSelectedItems().size() > 0) {
            SpeciesTableEntry entry = this.getSelectionModel().getSelectedItems().get(0);
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
            SpeciesTableEntry entry = this.getSelectionModel().getSelectedItems().get(0);
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

    private void initListeners() {
        this.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                    SpeciesTableView.this.removeSelected();
                } else if (keyEvent.getCode().equals(KeyCode.INSERT)) {
                    SpeciesTableView.this.addNewRow();
                }
            }
        });
        this.getItems().addListener(new ListChangeListener<SpeciesTableEntry>() {
            @Override
            public void onChanged(Change<? extends SpeciesTableEntry> change) {
                for (SpeciesTableEntry s : SpeciesTableView.this.getItems()) {
                    for (String prop : s.getStringPropertyHashMap().keySet()) {
                        SimpleStringProperty simpleStringProperty = s.getStringPropertyHashMap().get(prop);
                        simpleStringProperty.addListener((observableValue, s1, t1)
                                -> editorViewController.update(SpeciesTableView.this));
                    }
                    for (String prop : s.getDoublePropertyHashMap().keySet()) {
                        SimpleDoubleProperty simpleDoubleProperty = s.getDoublePropertyHashMap().get(prop);
                        simpleDoubleProperty.addListener((observableValue, number, t1)
                                -> editorViewController.update(SpeciesTableView.this));
                    }
                    for (String prop : s.getBooleanPropertyHashMap().keySet()) {
                        SimpleBooleanProperty simpleBooleanProperty = s.getBooleanPropertyHashMap().get(prop);
                        simpleBooleanProperty.addListener((observableValue, s12, t1)
                                -> editorViewController.update(SpeciesTableView.this));
                    }
                }

            }
        });

        this.setRowFactory(tv -> {
            TableRow<SpeciesTableEntry> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteRow = new MenuItem("Delete Row");
            deleteRow.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    SpeciesTableView.this.removeRow(row.getItem());
                }
            });

            MenuItem deleteSelection = new MenuItem("Delete Selection");
            deleteSelection.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    SpeciesTableView.this.getItems().removeAll(SpeciesTableView.this.getSelectionModel().getSelectedItems());
                }
            });

            MenuItem selectAll = new MenuItem("Select All");
            selectAll.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    SpeciesTableView.this.getSelectionModel().selectAll();
                }
            });

            MenuItem addRow = new MenuItem("Add Row to Table");
            addRow.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    SpeciesTableView.this.addNewRow();
                }
            });

            MenuItem browseSBO = new MenuItem("Choose SBO");
            browseSBO.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    row.getItem().createSBOBrowser();
                }
            });

            contextMenu.getItems().addAll(new SeparatorMenuItem(), deleteRow, deleteSelection, addRow, selectAll, new SeparatorMenuItem(), browseSBO);
            row.setContextMenu(contextMenu);
            return row;
        });

    }

    public void removeRow(SpeciesTableEntry row) {
        this.getItems().remove(row);
        editorViewController.update(SpeciesTableView.this);
    }
}