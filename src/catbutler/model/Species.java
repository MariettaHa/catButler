package catbutler.model;

import catbutler.gui.ui.QuickInfoPopup;
import catbutler.gui.ui.dataTableView.SpeciesTableEntry;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import javax.xml.stream.XMLStreamException;
import java.util.Arrays;

public class Species extends ModelElement {

    SimpleStringProperty speciesId = new SimpleStringProperty("");
    SimpleStringProperty sboTerm = new SimpleStringProperty("SBO:0000285");
    SimpleStringProperty speciesType = new SimpleStringProperty("defaultType");
    SimpleDoubleProperty initAmount = new SimpleDoubleProperty(0.0);
    SimpleStringProperty compartment = new SimpleStringProperty("default");
    SimpleStringProperty description = new SimpleStringProperty("");
    SimpleStringProperty metaData = new SimpleStringProperty("");
    SimpleStringProperty note = new SimpleStringProperty("");
    SimpleStringProperty networkId = new SimpleStringProperty("defaultNetwork");
    SimpleDoubleProperty posX = new SimpleDoubleProperty(100.0);
    SimpleDoubleProperty posY = new SimpleDoubleProperty(100.0);
    SimpleDoubleProperty posZ = new SimpleDoubleProperty(100.0);

    SimpleBooleanProperty hasOnlySubstanceUnits = new SimpleBooleanProperty(false);
    SimpleBooleanProperty boundaryCondition = new SimpleBooleanProperty(false);
    SimpleBooleanProperty constant = new SimpleBooleanProperty(false);
    SimpleStringProperty metaId = new SimpleStringProperty("");
    SimpleDoubleProperty initConcentration = new SimpleDoubleProperty(0.0);
    SimpleStringProperty substanceUnits = new SimpleStringProperty("");
    SimpleStringProperty conversionFactor = new SimpleStringProperty("");

    SimpleSetProperty<String> speciesNames = new SimpleSetProperty<>();

    public Species() {

    }

    public Species(String speciesId) {
        setSpeciesId(speciesId);
    }

    public Species(SpeciesTableEntry entry) {
        this.setSpeciesId(entry.getSpeciesId());
        this.setSpeciesType(entry.getType());

        this.setPosX(entry.getPosX());
        this.setPosY(entry.getPosY());
        this.setPosZ(entry.getPosZ());
        this.setDescription(entry.getDescription());
        this.setMetaData(entry.getMetaData());
        this.setNetworkId(entry.getNetworkId());
        this.setCompartment(entry.getCompartment());
        this.setHasOnlySubstanceUnits(entry.isHasOnlySubstanceUnits());
        this.setBoundaryCondition(entry.isBoundaryCondition());
        this.setConstant(entry.isConstant());
        this.setMetaId(entry.getMetaId());
        this.setSboTerm(String.valueOf(entry.getSbo()));
        this.getSpeciesNames().add(entry.getNames());
        this.setInitAmount(entry.getInitAmount());
        this.setInitConcentration(entry.getInitConcentration());
        this.setSubstanceUnits(entry.getSubstanceUnits());
        this.setConversionFactor(entry.getConversionFactor());
        this.setNote(entry.getNote());

    }

    public void addSpeciesName(String name) {
        getSpeciesNames().add(name);
    }

    public void addSpeciesNames(String[] names) {
        if (names != null) {
            if (names.length > 0) {
                getSpeciesNames().addAll(Arrays.asList(names));
            }
        }
    }

    public ObservableSet<String> getSpeciesNames() {
        if (speciesNames.getSize() == 0) {
            return FXCollections.observableSet();
        }
        return speciesNames.get();
    }

    public void setSpeciesNames(ObservableSet<String> speciesNames) {
        this.speciesNames.set(speciesNames);
    }

    public void init(org.sbml.jsbml.Species sbmlSpecies) {
        this.setSpeciesId(sbmlSpecies.getId());
        this.setCompartment(sbmlSpecies.getCompartment());
        this.setHasOnlySubstanceUnits(sbmlSpecies.getHasOnlySubstanceUnits());
        this.setBoundaryCondition(sbmlSpecies.getBoundaryCondition());
        this.setConstant(sbmlSpecies.getConstant());
        this.setMetaId(sbmlSpecies.getMetaId());
        this.setSboTerm(String.valueOf(sbmlSpecies.getSBOTerm()));
        this.getSpeciesNames().add(sbmlSpecies.getName());
        this.setInitAmount(sbmlSpecies.getInitialAmount());
        this.setInitConcentration(sbmlSpecies.getInitialConcentration());
        this.setSubstanceUnits(sbmlSpecies.getSubstanceUnits());
        this.setConversionFactor(sbmlSpecies.getConversionFactor());
        try {
            this.setNote(sbmlSpecies.getNotesString());
        } catch (XMLStreamException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }

    }

    public String getSpeciesId() {
        return speciesId.get();
    }

    public void setSpeciesId(String speciesId) {
        this.speciesId.set(speciesId);
    }

    public String getSboTerm() {
        return sboTerm.get();
    }

    public void setSboTerm(String sboTerm) {
        this.sboTerm.set(sboTerm);
    }

    public String getSpeciesType() {
        return speciesType.get();
    }

    public void setSpeciesType(String speciesType) {
        this.speciesType.set(speciesType);
    }

    public double getInitAmount() {
        return initAmount.get();
    }

    public void setInitAmount(double initAmount) {
        this.initAmount.set(initAmount);
    }

    public String getCompartment() {
        return compartment.get();
    }

    public void setCompartment(String compartment) {
        this.compartment.set(compartment);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getMetaData() {
        return metaData.get();
    }

    public void setMetaData(String metaData) {
        this.metaData.set(metaData);
    }

    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public String getNetworkId() {
        return networkId.get();
    }

    public void setNetworkId(String networkId) {
        this.networkId.set(networkId);
    }

    public double getPosX() {
        return posX.get();
    }

    public void setPosX(double posX) {
        this.posX.set(posX);
    }

    public double getPosY() {
        return posY.get();
    }

    public void setPosY(double posY) {
        this.posY.set(posY);
    }

    public double getPosZ() {
        return posZ.get();
    }

    public void setPosZ(double posZ) {
        this.posZ.set(posZ);
    }

    public boolean isHasOnlySubstanceUnits() {
        return hasOnlySubstanceUnits.get();
    }

    public void setHasOnlySubstanceUnits(boolean hasOnlySubstanceUnits) {
        this.hasOnlySubstanceUnits.set(hasOnlySubstanceUnits);
    }

    public boolean isBoundaryCondition() {
        return boundaryCondition.get();
    }

    public void setBoundaryCondition(boolean boundaryCondition) {
        this.boundaryCondition.set(boundaryCondition);
    }

    public boolean isConstant() {
        return constant.get();
    }

    public void setConstant(boolean constant) {
        this.constant.set(constant);
    }

    public String getMetaId() {
        return metaId.get();
    }

    public void setMetaId(String metaId) {
        this.metaId.set(metaId);
    }

    public double getInitConcentration() {
        return initConcentration.get();
    }

    public void setInitConcentration(double initConcentration) {
        this.initConcentration.set(initConcentration);
    }

    public String getSubstanceUnits() {
        return substanceUnits.get();
    }

    public void setSubstanceUnits(String substanceUnits) {
        this.substanceUnits.set(substanceUnits);
    }

    public String getConversionFactor() {
        return conversionFactor.get();
    }

    public void setConversionFactor(String conversionFactor) {
        this.conversionFactor.set(conversionFactor);
    }


}
