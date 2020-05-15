package catbutler.gui.ui.dataTableView;

import catbutler.gui.ui.sboBrowser.SBOBrowserPane;
import catbutler.gui.visualization.reactionView.ReactionViewPane;
import catbutler.io.Patterns;
import catbutler.model.Reaction;
import catbutler.utils.Misc;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ReactionTableEntry extends DataTableEntry {

    Reaction reaction;
    SimpleStringProperty reactionId = new SimpleStringProperty();
    SimpleStringProperty type = new SimpleStringProperty();
    SimpleStringProperty name = new SimpleStringProperty();
    SimpleStringProperty sbo = new SimpleStringProperty();
    SimpleStringProperty description = new SimpleStringProperty();
    SimpleStringProperty metaData = new SimpleStringProperty();
    SimpleStringProperty note = new SimpleStringProperty();
    SimpleStringProperty formula = new SimpleStringProperty();
    SimpleDoubleProperty startPosX = new SimpleDoubleProperty();
    SimpleDoubleProperty startPosY = new SimpleDoubleProperty();
    SimpleDoubleProperty startPosZ = new SimpleDoubleProperty();
    SimpleDoubleProperty endPosX = new SimpleDoubleProperty();
    SimpleDoubleProperty endPosY = new SimpleDoubleProperty();
    SimpleDoubleProperty endPosZ = new SimpleDoubleProperty();
    SimpleBooleanProperty isReversible = new SimpleBooleanProperty();
    SimpleDoubleProperty weight = new SimpleDoubleProperty();
    SimpleStringProperty metaId = new SimpleStringProperty();
    SimpleStringProperty compartment = new SimpleStringProperty();
    SimpleStringProperty reactantsDNF = new SimpleStringProperty();
    SimpleStringProperty productsDNF = new SimpleStringProperty();
    SimpleStringProperty catalystsDNF = new SimpleStringProperty();
    SimpleStringProperty inhibitorsDNF = new SimpleStringProperty();
    SimpleStringProperty networkId = new SimpleStringProperty();

    HashMap<String, SimpleStringProperty> stringPropertyHashMap = new HashMap<>();
    HashMap<String, SimpleDoubleProperty> doublePropertyHashMap = new HashMap<>();
    HashMap<String, SimpleBooleanProperty> booleanPropertyHashMap = new HashMap<>();

    SimpleBooleanProperty isSelected = new SimpleBooleanProperty(false);

    public ReactionTableEntry(Reaction r) {
        reaction = r;
        reactionId.setValue(r.getReactionId());
        type.setValue(r.getReactionType());
        name.setValue(r.getReactionName());
        sbo.setValue(r.getSboTerm());
        description.setValue(r.getDescription());
        metaData.setValue(r.getMetaData());
        note.setValue(r.getNote());
        formula.setValue(r.createFormula());
        startPosX.setValue(r.getStartPosX());
        startPosY.setValue(r.getStartPosY());
        startPosZ.setValue(r.getStartPosZ());
        endPosX.setValue(r.getEndPosX());
        endPosY.setValue(r.getEndPosY());
        endPosZ.setValue(r.getEndPosZ());
        isReversible.setValue(r.isIsReversible());
        weight.setValue(r.getWeight());
        metaId.setValue(r.getMetaId());
        compartment.setValue(r.getCompartment());
        reactantsDNF.setValue(Misc.toTableDNF(r.getReactantsTree().getDnfWithCoeff()));
        productsDNF.setValue(Misc.toTableDNF(r.getProductsTree().getDnfWithCoeff()));
        catalystsDNF.setValue(Misc.toTableDNF(r.getCatalystsTree().getDnfWithCoeff()));
        inhibitorsDNF.setValue(Misc.toTableDNF(r.getInhibitorsTree().getDnfWithCoeff()));
        networkId.setValue(r.getNetworkId());
        stringPropertyHashMap.putAll(Map.of("reactionId", reactionId, "type", type, "name", name, "sbo", sbo,
                "description", description, "metaData", metaData, "note", note, "formula", formula,
                "metaId", metaId, "compartment", compartment));
        stringPropertyHashMap.putAll(Map.of("reactantsDNF", reactantsDNF, "productsDNF", productsDNF,
                "catalystsDNF", catalystsDNF, "inhibitorsDNF", inhibitorsDNF, "networkId", networkId));
        doublePropertyHashMap.putAll(Map.of("startPosX", startPosX, "startPosY", startPosY, "startPosZ", startPosZ,
                "endPosX", endPosX, "endPosY", endPosY, "endPosZ", endPosZ, "weight", weight));
        booleanPropertyHashMap.putAll(Map.of("isReversible", isReversible));
    }

    public boolean isIsSelected() {
        return isSelected.get();
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected.set(isSelected);
    }

    public SimpleBooleanProperty isSelectedProperty() {
        return isSelected;
    }

    public HashMap<String, SimpleStringProperty> getStringPropertyHashMap() {
        return stringPropertyHashMap;
    }

    public HashMap<String, SimpleDoubleProperty> getDoublePropertyHashMap() {
        return doublePropertyHashMap;
    }

    public HashMap<String, SimpleBooleanProperty> getBooleanPropertyHashMap() {
        return booleanPropertyHashMap;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public String getReactionId() {
        return reactionId.get();
    }

    public SimpleStringProperty reactionIdProperty() {
        return reactionId;
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getSbo() {
        return sbo.get();
    }

    public SimpleStringProperty sboProperty() {
        return sbo;
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public String getMetaData() {
        return metaData.get();
    }

    public SimpleStringProperty metaDataProperty() {
        return metaData;
    }

    public String getNote() {
        return note.get();
    }

    public SimpleStringProperty noteProperty() {
        return note;
    }

    public String getFormula() {
        return formula.get();
    }

    public SimpleStringProperty formulaProperty() {
        return formula;
    }

    public double getStartPosX() {
        return startPosX.get();
    }

    public SimpleDoubleProperty startPosXProperty() {
        return startPosX;
    }

    public double getStartPosY() {
        return startPosY.get();
    }

    public SimpleDoubleProperty startPosYProperty() {
        return startPosY;
    }

    public double getStartPosZ() {
        return startPosZ.get();
    }

    public SimpleDoubleProperty startPosZProperty() {
        return startPosZ;
    }

    public double getEndPosX() {
        return endPosX.get();
    }

    public SimpleDoubleProperty endPosXProperty() {
        return endPosX;
    }

    public double getEndPosY() {
        return endPosY.get();
    }

    public SimpleDoubleProperty endPosYProperty() {
        return endPosY;
    }

    public double getEndPosZ() {
        return endPosZ.get();
    }

    public SimpleDoubleProperty endPosZProperty() {
        return endPosZ;
    }

    public boolean isIsReversible() {
        return isReversible.get();
    }

    public SimpleBooleanProperty isReversibleProperty() {
        return isReversible;
    }

    public double getWeight() {
        return weight.get();
    }

    public SimpleDoubleProperty weightProperty() {
        return weight;
    }

    public String getMetaId() {
        return metaId.get();
    }

    public SimpleStringProperty metaIdProperty() {
        return metaId;
    }

    public String getCompartment() {
        return compartment.get();
    }

    public SimpleStringProperty compartmentProperty() {
        return compartment;
    }

    public String getReactantsDNF() {
        return reactantsDNF.get();
    }

    public SimpleStringProperty reactantsDNFProperty() {
        return reactantsDNF;
    }

    public String getProductsDNF() {
        return productsDNF.get();
    }

    public SimpleStringProperty productsDNFProperty() {
        return productsDNF;
    }

    public String getCatalystsDNF() {
        return catalystsDNF.get();
    }

    public SimpleStringProperty catalystsDNFProperty() {
        return catalystsDNF;
    }

    public String getInhibitorsDNF() {
        return inhibitorsDNF.get();
    }

    public SimpleStringProperty inhibitorsDNFProperty() {
        return inhibitorsDNF;
    }

    public String getNetworkId() {
        return networkId.get();
    }

    public boolean containsValidDNFStr() {
        boolean b1 = reactantsDNF.getValue().matches(Patterns.dnfPattern) || reactantsDNF.getValue().length() == 0;
        boolean b2 = productsDNF.getValue().matches(Patterns.dnfPattern) || productsDNF.getValue().length() == 0;
        boolean b3 = catalystsDNF.getValue().matches(Patterns.dnfPattern) || catalystsDNF.getValue().length() == 0;
        boolean b4 = inhibitorsDNF.getValue().matches(Patterns.dnfPattern) || inhibitorsDNF.getValue().length() == 0;
        return (b1 && b2 && b3 && b4);
    }

    public void createReactionView() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(new Stage());
        VBox dialogVbox = new VBox(20);
        dialog.setTitle("Reaction View: " + getReactionId());
        dialog.centerOnScreen();
        ReactionViewPane view = new ReactionViewPane(this);
        dialogVbox.getChildren().add(view);
        view.setPrefWidth(dialogVbox.getWidth());
        view.setPrefHeight(dialogVbox.getHeight());
        Scene dialogScene = new Scene(dialogVbox, 900, 600);
        dialog.setResizable(false);
        dialog.setScene(dialogScene);
        final ContextMenu[] contextMenuOld = {null};
        dialogVbox.setOnContextMenuRequested(contextMenuEvent -> {
            if (contextMenuOld[0] != null) {
                contextMenuOld[0].hide();
            }
            ContextMenu contextMenu = new ContextMenu();

            MenuItem menuItem1 = new MenuItem("Save as png");
            MenuItem menuItem2 = new MenuItem("Save as jpg");
            menuItem1.setOnAction((event) -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Reaction View");
                fileChooser.setInitialDirectory(
                        new File(System.getProperty("user.home"))
                );
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PNG", "*.png")
                );
                File file = fileChooser.showSaveDialog(new Stage());
                if (file != null) {
                    Misc.savePaneSnapshotToFile(dialogVbox, file);
                }
            });
            contextMenu.getItems().addAll(menuItem1, menuItem2);
            contextMenu.show(view, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            contextMenu.setHideOnEscape(true);

            contextMenu.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
                if (!t1) {
                    contextMenu.hide();
                }
            });
            contextMenuOld[0] = contextMenu;
        });
        dialog.show();
    }

    public void createSBOBrowser() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(new Stage());
        VBox dialogVbox = new VBox(20);
        dialog.setTitle("SBO Ontology Browser");
        dialog.centerOnScreen();
        SBOBrowserPane pane = new SBOBrowserPane();
        dialogVbox.getChildren().add(pane);
        pane.setPrefWidth(dialogVbox.getWidth());
        pane.setPrefHeight(dialogVbox.getHeight());
        Scene dialogScene = new Scene(dialogVbox, 400, 500);
        dialog.setResizable(false);
        dialog.setScene(dialogScene);
        dialog.show();

        pane.getAccept().setOnAction(actionEvent -> {
            ReactionTableEntry.this.sboProperty().setValue(pane.getSelectedSBO());
            dialog.close();
        });

        pane.getCancel().setOnAction(actionEvent -> dialog.close());

    }
}
