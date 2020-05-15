package catbutler.io.documents;


import catbutler.gui.ui.QuickInfoPopup;
import catbutler.io.SBOTerms;
import catbutler.logic.ReactionTreeBuilder;
import catbutler.model.DataModel;
import org.apache.commons.io.IOUtils;
import org.sbml.jsbml.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class SBMLDoc extends Doc {

    SBMLDocument sbmlDocument = new SBMLDocument();
    private int sbmlLevel = 3;
    private int sbmlVersion = 2;

    public SBMLDoc(Path path, int sbmlLevel, int sbmlVersion) {
        super(path);
        this.sbmlLevel = sbmlLevel;
        this.sbmlVersion = sbmlVersion;
    }

    public SBMLDoc(String inputStr, int sbmlLevel, int sbmlVersion) {
        super(inputStr);
        this.sbmlLevel = sbmlLevel;
        this.sbmlVersion = sbmlVersion;
    }

    public SBMLDoc(DataModel dataModel) {
        super(dataModel);
    }

    public void readIn() {
        SBMLReader reader = new SBMLReader();
        if (this.getInputStr().length() > 0) {
            try {
                sbmlDocument = reader.readSBMLFromStream(IOUtils.toInputStream(this.getInputStr(), Charset.defaultCharset()));
            } catch (XMLStreamException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
        } else {
            try {
                sbmlDocument = reader.readSBML(getPath().toFile());
            } catch (XMLStreamException | IOException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            } catch (ClassCastException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);

            }
        }
        setDataModel(new DataModel(this));
        getDataModel().initDescendants();
        int p = 0;
        getData();
    }

    public void readIn(String in) {
        SBMLReader reader = new SBMLReader();
        if (in.length() > 0) {
            this.setInputStr(in);
            try {
                sbmlDocument = reader.readSBMLFromStream(IOUtils.toInputStream(in, Charset.defaultCharset()));
            } catch (XMLStreamException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
            setDataModel(new DataModel(this));
            getDataModel().initDescendants();
            int p = 0;
            getData();
        }
    }


    void getData() {
        if (sbmlDocument != null) {
            if (sbmlDocument.getModel() != null) {
                for (Species species : sbmlDocument.getModel().getListOfSpecies()) {
                    catbutler.model.Species s = new catbutler.model.Species(species.getId());
                    s.init(species);
                    s.addSpeciesName(species.getName());
                    addSpecies(s);
                    if (species.getInitialAmount() + species.getInitialConcentration() > 0.0) {
                        addFood(s);
                    }
                    s.setInitAmount(species.getInitialAmount());
                    s.setInitConcentration(species.getInitialConcentration());
                }
                for (Reaction sbmlReaction : sbmlDocument.getModel().getListOfReactions()) {
                    catbutler.model.Reaction r = new catbutler.model.Reaction(sbmlReaction.getId());
                    r.setSbmlReactionInstance(sbmlReaction);
                    r.setIsReversible(sbmlReaction.isReversible());
                    String reactantsListWithCoeff = "reactants::";
                    String productsListWithCoeff = "products::";
                    String catalystsListWithCoeff = "catalysts::";
                    String inhibitorsListWithCoeff = "inhibitors::";

                    for (SpeciesReference speciesReference : sbmlReaction.getListOfReactants()) {
                        if (getSpeciesSet().getIdToSpeciesMap().containsKey(speciesReference.getSpeciesInstance().getId())) {
                            r.getReactantsList().add(getSpeciesSet().getIdToSpeciesMap().get(speciesReference.getSpeciesInstance().getId()));
                            if (!reactantsListWithCoeff.equals("reactants::")) {
                                reactantsListWithCoeff += "&";
                            }
                            reactantsListWithCoeff += "%%" + speciesReference.getStoichiometry() + "%coeffOf%" + speciesReference.getSpeciesInstance().getId();
                        }
                    }
                    for (SpeciesReference speciesReference : sbmlReaction.getListOfProducts()) {
                        if (getSpeciesSet().getIdToSpeciesMap().containsKey(speciesReference.getSpeciesInstance().getId())) {
                            r.getProductsList().add(getSpeciesSet().getIdToSpeciesMap().get(speciesReference.getSpeciesInstance().getId()));
                            if (!productsListWithCoeff.equals("products::")) {
                                productsListWithCoeff += "&";
                            }
                            productsListWithCoeff += "%%" + speciesReference.getStoichiometry() + "%coeffOf%" + speciesReference.getSpeciesInstance().getId();
                        }
                    }
                    for (ModifierSpeciesReference speciesReference : sbmlReaction.getListOfModifiers()) {
                        if (getSpeciesSet().getIdToSpeciesMap().containsKey(speciesReference.getSpeciesInstance().getId())) {
                            catbutler.model.Species species = getSpeciesSet().getIdToSpeciesMap().get(speciesReference.getSpeciesInstance().getId());
                            double coefficient = 1.0;
                            String coeffStr = "";
                            try {

                                if (speciesReference.getNotesString().length() > 0 && speciesReference.getNotesString().contains("MODIFIER_STOICHIOMETRY: ")) {
                                    coefficient = Double.parseDouble(speciesReference.getNotesString().split("MODIFIER_STOICHIOMETRY: ")[1].split("</p>|&lt")[0]);
                                }
                            } catch (XMLStreamException e) {
                                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                            }

                            coeffStr += "%%" + coefficient + "%coeffOf%" + speciesReference.getSpeciesInstance().getId();

                            if (speciesReference.getSBOTermID().equals(SBOTerms.getDefaultSBOInhibitor())) {
                                if (!coeffStr.equals("inhibitors::")) {
                                    coeffStr += ",";
                                }
                                r.getInhibitorsList().add(species);
                                inhibitorsListWithCoeff += coeffStr;
                            } else {
                                if (!coeffStr.equals("catalysts::")) {
                                    coeffStr += ",";
                                }
                                r.getCatalystsList().add(species);
                                catalystsListWithCoeff += coeffStr;
                            }
                        }
                    }
                    createTree(r, new ArrayList<String>(Arrays.asList(reactantsListWithCoeff, productsListWithCoeff, catalystsListWithCoeff, inhibitorsListWithCoeff)));
                    r.setSboTerm(sbmlReaction.getSBOTermID());
                    r.setReactionName(sbmlReaction.getName());
                    r.setMetaId(sbmlReaction.getMetaId());
                    try {
                        r.setNote(sbmlReaction.getNotesString());
                    } catch (XMLStreamException e) {
                        new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                    }
                    getReactionSet().addReaction(r);

                }
            }
        }

    }

    private void createTree(catbutler.model.Reaction reaction, ArrayList<String> expressionWithCoefficients) {
        String catStr = "noCata";
        String inhStr = "noInhib";
        if (toDNF(reaction.getCatalystsList()).length() > 0) {
            catStr = toDNF(reaction.getCatalystsList());
        }
        if (toDNF(reaction.getInhibitorsList()).length() > 0) {
            inhStr = toDNF(reaction.getInhibitorsList());
        }
        String modifierStr = catStr + "\t" + inhStr;

        ReactionTreeBuilder reactionTreeBuilder
                = new ReactionTreeBuilder("sbml", reaction,
                toDNF(reaction.getReactantsList()), modifierStr, toDNF(reaction.getProductsList()), expressionWithCoefficients);
        reactionTreeBuilder.buildTrees();
    }

    private String toDNF(HashSet<catbutler.model.Species> list) {
        HashSet<String> l = new HashSet();
        for (catbutler.model.Species s : list) {
            l.add(s.getSpeciesId());
        }
        return l.stream().collect(Collectors.joining(","));
    }


    public int getSbmlLevel() {
        return sbmlLevel;
    }

    public int getSbmlVersion() {
        return sbmlVersion;
    }


}
