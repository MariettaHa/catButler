package catbutler.model;

import catbutler.gui.mainControllers.EditorViewController;
import catbutler.gui.ui.QuickInfoPopup;
import catbutler.gui.ui.dataTableView.*;
import catbutler.io.documents.*;
import catbutler.logic.ReactionTreeBuilder;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;

public class DataModel {

    FoodSet<Species> foodSet = new FoodSet<>();
    ReactionSet<Reaction> reactionSet = new ReactionSet<>();
    SpeciesSet<Species> speciesSet = new SpeciesSet<>();
    ArrayList<String> comments = new ArrayList<>();
    CRSDoc crsDoc = null;
    DBDoc dbDoc = null;
    SBMLDoc sbmlDoc = null;
    WimDoc wimDoc = null;

    DocumentCreationSettingsPane documentCreationSettingsPane = null;

    SimpleBooleanProperty changed = new SimpleBooleanProperty(false);

    public DataModel() {
    }

    public DataModel(DataModel dataModel) {
        this.foodSet = new FoodSet<>(dataModel.getFoodSet());
        this.speciesSet = new SpeciesSet<>(dataModel.getSpeciesSet());
        this.reactionSet = new ReactionSet<>(dataModel.getReactionSet());
        this.comments = new ArrayList<String>(dataModel.getComments());
    }

    public DataModel(Doc doc) {
        foodSet = doc.getSpeciesFoodSet();
        reactionSet = doc.getReactionSet();
        speciesSet = doc.getSpeciesSet();
        comments = doc.getComments();
        setDocVar(doc);
    }

    public DataModel(FoodSet<Species> foodSet, ReactionSet<Reaction> reactionSet) {
        this.foodSet = foodSet;
        this.reactionSet = reactionSet;
    }

    private void setDocVar(Doc doc) {
        switch (doc.getClass().getSimpleName()) {
            case "CRSDoc":
                crsDoc = (CRSDoc) doc;
                break;
            case "DBDoc":
                dbDoc = (DBDoc) doc;
                break;
            case "SBMLDoc":
                sbmlDoc = (SBMLDoc) doc;
                break;
            case "WimDoc":
                wimDoc = (WimDoc) doc;
                break;
            default:
                break;
        }
    }

    public int getFoodCount() {
        return foodSet.size();
    }

    public void addSpecies(Species species) {
        speciesSet.add(species);
    }

    public void addReaction(Reaction reaction) {
        reactionSet.add(reaction);
    }

    public void addFood(Species species) {
        foodSet.addFood(species);
    }

    public boolean satisfiesCRScriteria() {
        boolean hasMod = true;
        boolean hasReactant = true;
        boolean hasProduct = true;
        boolean hasReactionAndFood = !(reactionSet.isEmpty()) && !(foodSet.isEmpty());
        for (Reaction r : reactionSet) {
            hasMod = !(r.getCatalystsList().isEmpty() && r.getInhibitorsList().isEmpty());
            hasReactant = !(r.getReactantsList().isEmpty());
            hasProduct = !(r.getProductsList().isEmpty());
        }
        return hasMod && hasReactant && hasProduct && hasReactionAndFood;
    }

    public FoodSet<Species> getFoodSet() {
        return foodSet;
    }

    public ReactionSet<Reaction> getReactionSet() {
        return reactionSet;
    }

    public SpeciesSet<Species> getSpeciesSet() {
        return speciesSet;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void initDescendants() {
        for (Reaction r : reactionSet) {
            String reactants[] = r.getReactantsTree().getDnf().split("[,&]");
            for (String s : reactants) {
                if (!speciesSet.getIdToSpeciesMap().containsKey(s)) {
                    speciesSet.addSpecies(s);
                }
                r.getReactantsList().add(speciesSet.getIdToSpeciesMap().get(s));
            }

            String products[] = r.getProductsTree().getDnf().split("[,&]");
            for (String s : products) {
                if (!speciesSet.getIdToSpeciesMap().containsKey(s)) {
                    speciesSet.addSpecies(s);
                }
                r.getProductsList().add(speciesSet.getIdToSpeciesMap().get(s));
            }

            if (r.getCatalystsTree().getChildren().size() > 0) {
                String catalysts[] = r.getCatalystsTree().getDnf().split("[,&]");
                for (String s : catalysts) {
                    if (!speciesSet.getIdToSpeciesMap().containsKey(s)) {
                        speciesSet.addSpecies(s);
                    }
                    r.getCatalystsList().add(speciesSet.getIdToSpeciesMap().get(s));
                }
            }

            if (r.getInhibitorsTree().getChildren().size() > 0) {
                String inhibitors[] = r.getInhibitorsTree().getDnf().split("[,&]");
                for (String s : inhibitors) {
                    if (!speciesSet.getIdToSpeciesMap().containsKey(s)) {
                        speciesSet.addSpecies(s);
                    }
                    r.getInhibitorsList().add(speciesSet.getIdToSpeciesMap().get(s));
                }
            }
        }
    }

    public void buildCustomTrees(String parserType) {

        if (parserType.equals("custom")) {
            for (Reaction reaction : reactionSet) {
                ArrayList<String> stringsWithCoefficients = new ArrayList<>();
                String reactantsStr = formatStr(reaction.getRawReactantsStr());
                String productsStr = formatStr(reaction.getRawProductsStr());
                String catStr = formatStr(reaction.getRawCatalysatorsStr());
                String inhibStr = formatStr(reaction.getRawInhibitorsStr());
                if (catStr.length() == 0) {
                    catStr = "noCata";
                }
                if (inhibStr.length() == 0) {
                    inhibStr = "noInhib";
                }
                stringsWithCoefficients.add(formatStrWithCoeff("reactants::" + reaction.getRawReactantsStr().strip()));
                stringsWithCoefficients.add(formatStrWithCoeff("products::" + reaction.getRawProductsStr().strip()));

                if (reaction.getRawCatalysatorsStr().length() > 0) {
                    stringsWithCoefficients.add(formatStrWithCoeff("catalysts::" + reaction.getRawCatalysatorsStr().strip()));
                }
                if (reaction.getRawCatalysatorsStr().length() > 0) {
                    stringsWithCoefficients.add(formatStrWithCoeff("inhibitors::" + reaction.getRawInhibitorsStr().strip()));
                }


                ReactionTreeBuilder reactionTreeBuilder
                        = new ReactionTreeBuilder(parserType, reaction, reactantsStr, catStr + "\t" + inhibStr, productsStr, stringsWithCoefficients);
                reactionTreeBuilder.buildTrees();
            }
        } else if (parserType.equals("table")) {

            String reactantsStr = "";
            String productsStr = "";
            String catStr = "";
            String inhibStr = "";
            for (Reaction reaction : reactionSet) {
                ArrayList<String> stringsWithCoefficients = new ArrayList<>();
                reactantsStr = formatStr(reaction.getRawReactantsStr());
                productsStr = formatStr(reaction.getRawProductsStr());
                catStr = formatStr(reaction.getRawCatalysatorsStr());
                inhibStr = formatStr(reaction.getRawInhibitorsStr());
                if (catStr.length() == 0) {
                    catStr = "noCata";
                }
                if (inhibStr.length() == 0) {
                    inhibStr = "noInhib";
                }
                stringsWithCoefficients.add(formatStrWithCoeff("reactants::" + reaction.getRawReactantsStr().strip()));
                stringsWithCoefficients.add(formatStrWithCoeff("products::" + reaction.getRawProductsStr().strip()));

                if (reaction.getRawCatalysatorsStr().length() > 0) {
                    stringsWithCoefficients.add(formatStrWithCoeff("catalysts::" + reaction.getRawCatalysatorsStr().strip()));
                }
                if (reaction.getRawCatalysatorsStr().length() > 0) {
                    stringsWithCoefficients.add(formatStrWithCoeff("inhibitors::" + reaction.getRawInhibitorsStr().strip()));
                }
                ReactionTreeBuilder reactionTreeBuilder
                        = new ReactionTreeBuilder(parserType, reaction, reactantsStr, catStr + "\t" + inhibStr, productsStr, stringsWithCoefficients);
                reactionTreeBuilder.buildTrees();
            }
        }
    }

    private String formatStr(String str) {
        str = str.replaceAll("\\s?\\+\\s?", "&");
        str = str.replaceAll("(^|\\s|&|,|\\(|\\[|\\{)(\\d+[.]\\d+|\\d+)($|\\s)", "$1");
        str = str.replaceAll("\\s&\\s", "&").replaceAll("\\s,\\s", ",");
        return str.strip();
    }

    private String formatStrWithCoeff(String str) {
        str = str.replaceAll("\\s\\s+", " ");
        str = str.replaceAll("([^A-Za-z0-9-_/().'%]|^)(\\d+[.]\\d+|\\d+)(\\s+)([A-Za-z-_/().'%][A-Za-z0-9-_/().'%]*)([^A-Za-z0-9-_/().'%]|$)", "$1%%$2%coeffOf%$4$5");
        str = str.replaceAll("\\s?\\+\\s?", "&");
        str = str.replaceAll("\\s\\s+", " ");
        str = str.replaceAll("\\s*&\\s*", "&").replaceAll("\\s,\\s", ",");
        return str;
    }

    public boolean satisfiesWIMcriteria() {
        boolean hasReactant = true;
        boolean hasProduct = true;
        boolean hasMoleculesOrFood = speciesSet.size() + foodSet.size() > 0;
        for (Reaction r : reactionSet) {
            hasReactant = !(r.getReactantsList().isEmpty());
            hasProduct = !(r.getProductsList().isEmpty());
        }
        return hasReactant && hasProduct & hasMoleculesOrFood;
    }

    public boolean notEmpty() {
        boolean hasReactant = true;
        boolean hasProduct = true;
        boolean hasReaction = !reactionSet.isEmpty();
        for (Reaction r : reactionSet) {
            hasReactant = !(r.getReactantsList().isEmpty());
            hasProduct = !(r.getProductsList().isEmpty());
        }
        boolean valid = false;
        valid = (hasReactant && hasProduct && hasReaction);
        return valid;
    }

    public boolean isChanged() {
        return changed.get();
    }

    public SimpleBooleanProperty changedProperty() {
        return changed;
    }

    public DocumentCreationSettingsPane getDocumentCreationSettingsPane() {
        return documentCreationSettingsPane;
    }

    public void updateModel(ReactionTableView reactionTableView, SpeciesTableView speciesTableView, DocumentCreationSettingsPane documentCreationSettingsPane) {
        speciesSet.clear();
        foodSet.clear();
        for (SpeciesTableEntry entry : speciesTableView.getItems()) {
            Species species = new Species(entry);
            if (species.getInitAmount() + species.getInitConcentration() >= 0.0) {
                this.addFood(species);
            } else {
                if (foodSet.contains(species)) {
                    foodSet.removeFood(species);
                }
            }
            this.addSpecies(species);
        }

        reactionSet.clear();
        for (ReactionTableEntry entry : reactionTableView.getItems()) {
            if (entry.containsValidDNFStr()) {
                entry.formulaProperty().setValue(entry.getReactantsDNF() + " <"
                        .repeat(entry.isIsReversible() ? 1 : 0) + " ".repeat(entry.isIsReversible() ? 0 : 1)
                        + "-> " + entry.getProductsDNF() + " [ " + entry.getCatalystsDNF() + " ]" + " { "
                        + entry.getInhibitorsDNF() + " }".replaceAll("< ->", "<->"));
            } else {
                new QuickInfoPopup("Error!", "Could not parse DNF strings to formula.", -1, null);
            }
            Reaction reaction = new Reaction(entry);
            this.addReaction(reaction);
        }
        this.buildCustomTrees("table");

        this.documentCreationSettingsPane = documentCreationSettingsPane;
    }

    public void updateModel(EditorViewController editorViewController) {
        ReactionTableView reactionTableView = (ReactionTableView) editorViewController.getReactionTableScrollPane().getContent();
        SpeciesTableView speciesTableView = (SpeciesTableView) editorViewController.getSpeciesTableScrollPane().getContent();
        DocumentCreationSettingsPane documentCreationSettingsPane = (DocumentCreationSettingsPane) editorViewController.getDocumentTableScrollPane().getContent();
        updateModel(reactionTableView, speciesTableView, documentCreationSettingsPane);
    }

}
