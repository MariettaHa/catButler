package catbutler.gui.ui.dataTableView;

import catbutler.gui.ui.sboBrowser.SBOBrowserPane;
import catbutler.model.Species;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SpeciesTableEntry extends DataTableEntry {
    Species species;
    SimpleStringProperty speciesId = new SimpleStringProperty();
    SimpleStringProperty sbo = new SimpleStringProperty();
    SimpleStringProperty type = new SimpleStringProperty();
    SimpleStringProperty names = new SimpleStringProperty();
    SimpleDoubleProperty initAmount = new SimpleDoubleProperty();
    SimpleStringProperty compartment = new SimpleStringProperty();
    SimpleStringProperty description = new SimpleStringProperty();
    SimpleStringProperty metaData = new SimpleStringProperty();
    SimpleStringProperty note = new SimpleStringProperty();
    SimpleStringProperty networkId = new SimpleStringProperty();
    SimpleDoubleProperty posX = new SimpleDoubleProperty();
    SimpleDoubleProperty posY = new SimpleDoubleProperty();
    SimpleDoubleProperty posZ = new SimpleDoubleProperty();
    SimpleBooleanProperty hasOnlySubstanceUnits = new SimpleBooleanProperty();
    SimpleBooleanProperty boundaryCondition = new SimpleBooleanProperty();
    SimpleBooleanProperty constant = new SimpleBooleanProperty();
    SimpleStringProperty metaId = new SimpleStringProperty();
    SimpleDoubleProperty initConcentration = new SimpleDoubleProperty();
    SimpleStringProperty substanceUnits = new SimpleStringProperty();
    SimpleStringProperty conversionFactor = new SimpleStringProperty();

    SimpleBooleanProperty isSelected = new SimpleBooleanProperty(false);

    HashMap<String, SimpleStringProperty> stringPropertyHashMap = new HashMap<>();
    HashMap<String, SimpleDoubleProperty> doublePropertyHashMap = new HashMap<>();
    HashMap<String, SimpleBooleanProperty> booleanPropertyHashMap = new HashMap<>();


    public SpeciesTableEntry(Species s) {
        species = s;
        speciesId.setValue(s.getSpeciesId());//
        sbo.setValue(s.getSboTerm());//
        type.setValue(s.getSpeciesType());//
        names.setValue(new HashSet<>(s.getSpeciesNames()).toString().replaceAll("\\[\\]", "").replaceAll(", ", "; "));//
        initAmount.setValue(s.getInitAmount());
        compartment.setValue(s.getCompartment());//
        description.setValue(s.getDescription());//
        metaData.setValue(s.getMetaData());//
        note.setValue(s.getNote());//
        networkId.setValue(s.getNetworkId());
        posX.setValue(s.getPosX());
        posY.setValue(s.getPosY());
        posZ.setValue(s.getPosZ());
        hasOnlySubstanceUnits.setValue(s.isHasOnlySubstanceUnits());
        boundaryCondition.setValue(s.isBoundaryCondition());
        constant.setValue(s.isConstant());
        metaId.setValue(s.getMetaId());//
        initConcentration.setValue(s.getInitConcentration());
        substanceUnits.setValue(s.getSubstanceUnits());
        conversionFactor.setValue(s.getConversionFactor());
        stringPropertyHashMap.putAll(Map.of("speciesId", speciesId, "type", type, "names", names, "sbo", sbo, "description", description, "metaData", metaData, "note", note, "networkId", networkId, "metaId", metaId, "compartment", compartment));
        stringPropertyHashMap.putAll(Map.of("substanceUnits", substanceUnits, "conversionFactor", conversionFactor));
        doublePropertyHashMap.putAll(Map.of("posX", posX, "posY", posY, "posZ", posZ, "initConcentration", initConcentration, "initAmount", initAmount));
        booleanPropertyHashMap.putAll(Map.of("hasOnlySubstanceUnits", hasOnlySubstanceUnits, "boundaryCondition", boundaryCondition, "constant", constant));
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

    public Species getSpecies() {
        return species;
    }

    public String getSpeciesId() {
        return speciesId.get();
    }

    public SimpleStringProperty speciesIdProperty() {
        return speciesId;
    }

    public String getSbo() {
        return sbo.get();
    }

    public SimpleStringProperty sboProperty() {
        return sbo;
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public double getInitAmount() {
        return initAmount.get();
    }

    public SimpleDoubleProperty initAmountProperty() {
        return initAmount;
    }

    public String getCompartment() {
        return compartment.get();
    }

    public SimpleStringProperty compartmentProperty() {
        return compartment;
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

    public String getNetworkId() {
        return networkId.get();
    }

    public SimpleStringProperty networkIdProperty() {
        return networkId;
    }

    public double getPosX() {
        return posX.get();
    }

    public SimpleDoubleProperty posXProperty() {
        return posX;
    }

    public double getPosY() {
        return posY.get();
    }

    public SimpleDoubleProperty posYProperty() {
        return posY;
    }

    public double getPosZ() {
        return posZ.get();
    }

    public SimpleDoubleProperty posZProperty() {
        return posZ;
    }

    public boolean isHasOnlySubstanceUnits() {
        return hasOnlySubstanceUnits.get();
    }

    public SimpleBooleanProperty hasOnlySubstanceUnitsProperty() {
        return hasOnlySubstanceUnits;
    }

    public boolean isBoundaryCondition() {
        return boundaryCondition.get();
    }

    public SimpleBooleanProperty boundaryConditionProperty() {
        return boundaryCondition;
    }

    public boolean isConstant() {
        return constant.get();
    }

    public SimpleBooleanProperty constantProperty() {
        return constant;
    }

    public String getMetaId() {
        return metaId.get();
    }

    public SimpleStringProperty metaIdProperty() {
        return metaId;
    }

    public double getInitConcentration() {
        return initConcentration.get();
    }

    public SimpleDoubleProperty initConcentrationProperty() {
        return initConcentration;
    }

    public String getSubstanceUnits() {
        return substanceUnits.get();
    }

    public SimpleStringProperty substanceUnitsProperty() {
        return substanceUnits;
    }

    public String getConversionFactor() {
        return conversionFactor.get();
    }

    public SimpleStringProperty conversionFactorProperty() {
        return conversionFactor;
    }

    public String getNames() {
        return names.get();
    }

    public SimpleStringProperty namesProperty() {
        return names;
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

        pane.getAccept().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                SpeciesTableEntry.this.sboProperty().setValue(pane.getSelectedSBO());
                dialog.close();
            }
        });

        pane.getCancel().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                dialog.close();
            }
        });

    }
}
