package catbutler.model;

import catbutler.gui.ui.dataTableView.ReactionTableEntry;
import catbutler.logic.BooleanTreeNode;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashSet;

public class Reaction extends ModelElement {

    String reactantAND = "&";
    String reactantOR = ",";
    String productAND = "&";
    String productOR = ",";
    String catalystAND = "&";
    String catalystOR = ",";
    String inhibitorAND = "&";
    String inhibitorOR = ",";
    SimpleStringProperty rawReactantsStr = new SimpleStringProperty("");
    SimpleStringProperty rawProductsStr = new SimpleStringProperty("");
    SimpleStringProperty rawCatalysatorsStr = new SimpleStringProperty("");
    SimpleStringProperty rawInhibitorsStr = new SimpleStringProperty("");
    private SimpleStringProperty reactionId = new SimpleStringProperty("");
    private SimpleStringProperty reactionType = new SimpleStringProperty("defaultType");
    private SimpleStringProperty reactionName = new SimpleStringProperty("");
    private SimpleStringProperty sboTerm = new SimpleStringProperty("SBO:0000231");
    private SimpleStringProperty description = new SimpleStringProperty("");
    private SimpleStringProperty metaData = new SimpleStringProperty("");
    private SimpleStringProperty note = new SimpleStringProperty("");
    private SimpleStringProperty reactionAllParticipants = new SimpleStringProperty("");
    private SimpleStringProperty networkId = new SimpleStringProperty("defaultNetwork");
    private SimpleStringProperty formula = new SimpleStringProperty("");
    private SimpleDoubleProperty startPosX = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty startPosY = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty startPosZ = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty endPosX = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty endPosY = new SimpleDoubleProperty(0.0);
    private SimpleDoubleProperty endPosZ = new SimpleDoubleProperty(0.0);
    private SimpleBooleanProperty isReversible = new SimpleBooleanProperty(false);
    private SimpleDoubleProperty weight = new SimpleDoubleProperty(1.0);
    private SimpleStringProperty metaId = new SimpleStringProperty("");
    private SimpleStringProperty compartment = new SimpleStringProperty("default");
    private SimpleDoubleProperty centerPosX = new SimpleDoubleProperty();
    private SimpleDoubleProperty centerPosY = new SimpleDoubleProperty();
    private BooleanTreeNode reactantsTree = new BooleanTreeNode();
    private BooleanTreeNode productsTree = new BooleanTreeNode();
    private BooleanTreeNode catalystsTree = new BooleanTreeNode();
    private BooleanTreeNode inhibitorsTree = new BooleanTreeNode();
    private HashSet<Species> reactantsList = new HashSet<>();
    private HashSet<Species> productsList = new HashSet<>();
    private HashSet<Species> catalystsList = new HashSet<>();
    private HashSet<Species> inhibitorsList = new HashSet<>();
    private org.sbml.jsbml.Reaction sbmlReactionInstance = null;

    public Reaction(String reactionId) {
        setReactionId(reactionId);
        initBindings();
    }

    public Reaction(ReactionTableEntry entry) {
        this.setReactionId(entry.getReactionId());
        this.setReactionName(entry.getName());
        this.setSboTerm(entry.getSbo());
        this.setDescription(entry.getDescription());
        this.setMetaData(entry.getMetaData());
        this.setCompartment(entry.getCompartment());
        this.setReactionType(entry.getType());
        this.setNote(entry.getNote());
        this.setNetworkId(entry.getNetworkId());
        this.setFormula(entry.getReactantsDNF() + " <".repeat(entry.isIsReversible() ? 1 : 0)
                + " -> " + entry.getProductsDNF() + " [ " + entry.getCatalystsDNF() + " ]"
                + " { " + entry.getInhibitorsDNF() + " }".replace("< ->", "<->"));
        this.setStartPosX(entry.getStartPosX());
        this.setStartPosY(entry.getStartPosY());
        this.setStartPosZ(entry.getStartPosZ());
        this.setEndPosX(entry.getEndPosX());
        this.setEndPosY(entry.getEndPosY());
        this.setEndPosZ(entry.getEndPosZ());
        this.setIsReversible(entry.isIsReversible());
        this.setMetaId(entry.getMetaId());
        this.setWeight(entry.getWeight());
        this.setRawReactantsStr(entry.getReactantsDNF());
        this.setRawProductsStr(entry.getProductsDNF());
        this.setRawCatalysatorsStr(entry.getCatalystsDNF());
        this.setRawInhibitorsStr(entry.getInhibitorsDNF());

        initBindings();
        initReactionView();

    }

    private void initBindings() {
        centerPosX.bind(Bindings.divide(Bindings.add(startPosX, endPosX), 2.0));
        centerPosY.bind(Bindings.divide(Bindings.add(startPosY, endPosY), 2.0));
    }

    private void initReactionView() {
    }

    public HashSet<Species> getModifiersList() {
        HashSet<Species> tmpLst = new HashSet<>();
        tmpLst.addAll(catalystsList);
        tmpLst.addAll(inhibitorsList);
        return tmpLst;
    }

    public HashSet<Species> getReactantsList() {
        return reactantsList;
    }

    public HashSet<Species> getProductsList() {
        return productsList;
    }

    public HashSet<Species> getCatalystsList() {
        return catalystsList;
    }

    public HashSet<Species> getInhibitorsList() {
        return inhibitorsList;
    }

    public BooleanTreeNode getReactantsTree() {
        return reactantsTree;
    }

    public void setReactantsTree(BooleanTreeNode reactantsTree) {
        this.reactantsTree = reactantsTree;
    }

    public BooleanTreeNode getProductsTree() {
        return productsTree;
    }

    public void setProductsTree(BooleanTreeNode productsTree) {
        this.productsTree = productsTree;
    }

    public BooleanTreeNode getCatalystsTree() {
        return catalystsTree;
    }

    public void setCatalystsTree(BooleanTreeNode catalystsTree) {
        this.catalystsTree = catalystsTree;
    }

    public BooleanTreeNode getInhibitorsTree() {
        return inhibitorsTree;
    }

    public void setInhibitorsTree(BooleanTreeNode inhibitorsTree) {
        this.inhibitorsTree = inhibitorsTree;
    }

    public String createFormula() {
        if (getProductsList().size() > 0 && getReactantsList().size() > 0) {
            String s = getReactantsTree().getDnfWithCoeff().replaceAll("%coeffOf%|%+", " ");
            if (catalystsTree.getDnfWithCoeff().length() > 0) {
                s += "[" + catalystsTree.getDnfWithCoeff().replaceAll("%coeffOf%|%+", " ").strip() + "]";
            }
            if (inhibitorsTree.getDnfWithCoeff().length() > 0) {
                s += "{" + inhibitorsTree.getDnfWithCoeff().replaceAll("%coeffOf%|%+", " ").strip() + "}";
            }
            s += "<".repeat(isIsReversible() ? 1 : 0) + "->" + getProductsTree().getDnfWithCoeff()
                    .replaceAll("%coeffOf%|%+", " ");

            setFormula(s);
        }
        return getFormula().replaceAll("(\\[|\\{)( )?,", "$1").replaceAll("(\\[|\\{) ", "$1");
    }

    public void setSbmlReactionInstance(org.sbml.jsbml.Reaction sbmlReactionInstance) {
        this.sbmlReactionInstance = sbmlReactionInstance;
    }

    public void setReactantAND(String reactantAND) {
        this.reactantAND = reactantAND;
    }

    public void setReactantOR(String reactantOR) {
        this.reactantOR = reactantOR;
    }

    public void setProductAND(String productAND) {
        this.productAND = productAND;
    }

    public void setProductOR(String productOR) {
        this.productOR = productOR;
    }

    public void setCatalystAND(String catalystAND) {
        this.catalystAND = catalystAND;
    }

    public void setCatalystOR(String catalystOR) {
        this.catalystOR = catalystOR;
    }

    public void setInhibitorAND(String inhibitorAND) {
        this.inhibitorAND = inhibitorAND;
    }

    public void setInhibitorOR(String inhibitorOR) {
        this.inhibitorOR = inhibitorOR;
    }

    public String getReactionId() {
        return reactionId.get();
    }

    public void setReactionId(String reactionId) {
        this.reactionId.set(reactionId);
    }

    public String getReactionType() {
        return reactionType.get();
    }

    public void setReactionType(String reactionType) {
        this.reactionType.set(reactionType);
    }

    public String getReactionName() {
        return reactionName.get();
    }

    public void setReactionName(String reactionName) {
        this.reactionName.set(reactionName);
    }

    public String getSboTerm() {
        return sboTerm.get();
    }

    public void setSboTerm(String sboTerm) {
        this.sboTerm.set(sboTerm);
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

    public String getReactionAllParticipants() {
        return reactionAllParticipants.get();
    }

    public void setReactionAllParticipants(String reactionAllParticipants) {
        this.reactionAllParticipants.set(reactionAllParticipants);
    }

    public String getNetworkId() {
        return networkId.get();
    }


    public void setNetworkId(String networkId) {
        this.networkId.set(networkId);
    }


    public double getStartPosX() {
        return startPosX.get();
    }


    public void setStartPosX(double startPosX) {
        this.startPosX.set(startPosX);
    }

    public double getStartPosY() {
        return startPosY.get();
    }

    public void setStartPosY(double startPosY) {
        this.startPosY.set(startPosY);
    }

    public String getFormula() {
        return formula.get();
    }

    public void setFormula(String formula) {
        this.formula.set(formula);
    }

    public double getStartPosZ() {
        return startPosZ.get();
    }


    public void setStartPosZ(double startPosZ) {
        this.startPosZ.set(startPosZ);
    }

    public double getEndPosX() {
        return endPosX.get();
    }


    public void setEndPosX(double endPosX) {
        this.endPosX.set(endPosX);
    }

    public double getEndPosY() {
        return endPosY.get();
    }


    public void setEndPosY(double endPosY) {
        this.endPosY.set(endPosY);
    }

    public double getEndPosZ() {
        return endPosZ.get();
    }


    public void setEndPosZ(double endPosZ) {
        this.endPosZ.set(endPosZ);
    }

    public boolean isIsReversible() {
        return isReversible.get();
    }


    public void setIsReversible(boolean isReversible) {
        this.isReversible.set(isReversible);
    }

    public double getWeight() {
        return weight.get();
    }


    public void setWeight(double weight) {
        this.weight.set(weight);
    }

    public String getMetaId() {
        return metaId.get();
    }


    public void setMetaId(String metaId) {
        this.metaId.set(metaId);
    }

    public String getCompartment() {
        return compartment.get();
    }


    public void setCompartment(String compartment) {
        this.compartment.set(compartment);
    }

    public String getRawReactantsStr() {
        return rawReactantsStr.get();
    }


    public void setRawReactantsStr(String rawReactantsStr) {
        this.rawReactantsStr.set(rawReactantsStr);
    }

    public String getRawProductsStr() {
        return rawProductsStr.get();
    }


    public void setRawProductsStr(String rawProductsStr) {
        this.rawProductsStr.set(rawProductsStr);
    }

    public String getRawCatalysatorsStr() {
        return rawCatalysatorsStr.get();
    }


    public void setRawCatalysatorsStr(String rawCatalysatorsStr) {
        this.rawCatalysatorsStr.set(rawCatalysatorsStr);
    }

    public String getRawInhibitorsStr() {
        return rawInhibitorsStr.get();
    }


    public void setRawInhibitorsStr(String rawInhibitorsStr) {
        this.rawInhibitorsStr.set(rawInhibitorsStr);
    }

    public String createFormulaRepaired() {
        String formula = createFormula().replaceAll("&", "+").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;").replaceAll("[^a-zA-Z0-9-+&;\\s_\\[\\]\\{\\}]"
                        , "_");
        return formula;
    }

    public double getCenterPosX() {
        return centerPosX.get();
    }

    public double getCenterPosY() {
        return centerPosY.get();
    }


    public HashSet<String> getInputSpeciesStrIds() {
        HashSet<Species> input = new HashSet<>();
        input.addAll(reactantsList);
        input.addAll(catalystsList);
        input.addAll(inhibitorsList);
        HashSet<String> ids = new HashSet<>();
        for (Species s : input) {
            ids.add(s.getSpeciesId());
        }
        return ids;
    }

    public HashSet<String> getOutputSpeciesStrIds() {
        HashSet<Species> output = new HashSet<>();
        output.addAll(productsList);
        HashSet<String> ids = new HashSet<>();
        for (Species s : output) {
            ids.add(s.getSpeciesId());
        }
        return ids;
    }

    public HashSet<Species> getInputSet() {
        HashSet<Species> in = new HashSet<>(reactantsList);
        in.addAll(catalystsList);
        in.addAll(inhibitorsList);
        return in;
    }

    public HashSet<Species> getOutputSet() {
        return new HashSet<Species>(productsList);
    }

}
