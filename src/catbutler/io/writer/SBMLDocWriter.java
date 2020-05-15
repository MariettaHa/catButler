package catbutler.io.writer;

import catbutler.gui.mainControllers.EditorViewController;
import catbutler.gui.ui.QuickInfoPopup;
import catbutler.gui.ui.dataTableView.DocumentCreationSettingsPane;
import catbutler.io.SBOTerms;
import catbutler.io.documents.SBMLDoc;
import catbutler.model.Reaction;
import catbutler.model.Species;
import org.sbml.jsbml.*;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.util.TreeNodeRemovedEvent;

import javax.swing.tree.TreeNode;
import javax.xml.stream.XMLStreamException;
import java.beans.PropertyChangeEvent;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.stream.Collectors;

public class SBMLDocWriter implements TreeNodeChangeListener {

    SBMLDoc sbmlDoc;
    SBMLDocument sbmlDocument;
    HashSet<String> speciesIds = new HashSet<>();
    HashSet<String> speciesMeta = new HashSet<>();
    HashSet<String> reactionIds = new HashSet<>();
    HashSet<String> reactionMeta = new HashSet<>();

    public SBMLDocWriter(SBMLDoc sbmlDoc) {
        this.sbmlDoc = sbmlDoc;
        buildSBMLDocument();
    }

    public void write(String outputPath) {
        org.sbml.jsbml.SBMLWriter writer = new org.sbml.jsbml.SBMLWriter();
        try {
            writer.writeSBMLToFile(sbmlDocument, outputPath);
        } catch (FileNotFoundException | XMLStreamException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }
    }

    public void buildSBMLDocument() {

        sbmlDocument = new SBMLDocument(sbmlDoc.getSbmlLevel(), sbmlDoc.getSbmlVersion());
        sbmlDocument.addTreeNodeChangeListener(this);
        Model model = sbmlDocument.createModel();

        if (sbmlDoc.getDataModel().getDocumentCreationSettingsPane() != null) {
            DocumentCreationSettingsPane docSettings = sbmlDoc.getDataModel().getDocumentCreationSettingsPane();
            model.setId(docSettings.getDocId());
            model.setName(docSettings.getName());
            try {

                model.setNotes("<body xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                        "    <p>Model Description: " + docSettings.getDescription() + "</p>\n" +
                        "    <p>Model Type: " + docSettings.getDocType() + "</p>\n" +
                        "    <p>File Name: " + docSettings.getFileName() + "</p>\n" +
                        "    <p>Meta Data: " + docSettings.getMetaData() + "</p>\n" +
                        "    <p>Model Version: " + docSettings.getVersion() + "</p>\n" +
                        "  </body>");
            } catch (XMLStreamException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
            History hist = model.getHistory();
            Creator creator = new Creator(docSettings.getAuthorGivenName(), docSettings.getAuthorFamilyName(), docSettings.getAuthorOrganization(), docSettings.getAuthorEmail());
            hist.addCreator(creator);

        } else {
            History hist = model.getHistory();
            Creator creator = new Creator("Default Given Name", "Default Family Name", "Default Organisation", "Default@EMail.com");
            hist.addCreator(creator);
        }

        for (Species s : sbmlDoc.getSpeciesSet().getSortedSet()) {
            s.setSpeciesId(correctName(s.getSpeciesId()));
            s.setMetaId(correctName(s.getMetaId()));
            speciesIds.add(s.getSpeciesId());
            org.sbml.jsbml.Species species = new org.sbml.jsbml.Species(s.getSpeciesId());
            species.setLevel(sbmlDocument.getLevel());
            species.setVersion(sbmlDocument.getVersion());
            species.setName(correctName(s.getSpeciesNames().stream().collect(Collectors.joining("_"))));

            if (s.getInitAmount() == 0.0 && s.getInitConcentration() > 0.0) {
                species.setInitialConcentration(s.getInitConcentration());
            } else {
                species.setInitialAmount(s.getInitAmount());
            }

            if (!model.containsCompartment(correctName(s.getCompartment()))) {
                Compartment compartment = model.createCompartment(correctName(s.getCompartment()));
                compartment.setConstant(false);
            }
            species.setCompartment(correctName(s.getCompartment()));
            species.setHasOnlySubstanceUnits(s.isHasOnlySubstanceUnits());
            species.setBoundaryCondition(s.isBoundaryCondition());
            species.setConstant(s.isConstant());
            speciesMeta.add(s.getMetaId());
            species.setSBOTerm(correctSBO(String.valueOf(s.getSboTerm())));
            species.setName(String.join("_", s.getSpeciesNames()).replaceAll(" ", "_"));
            species.setSubstanceUnits(s.getSubstanceUnits());
            if (model.getLevel() == 3) {
                species.setConversionFactor(s.getConversionFactor());
            }
            if (model.containsSpecies(s.getSpeciesId())) {
                System.err.println("Duplicate species with id = " + s.getSpeciesId() + " ignored.");
            } else {
                if (model.containsSpecies(s.getMetaId())) {
                    System.err.println("Duplicate species with metaId = " + s.getMetaId() + " ignored.");
                } else {
                    model.addSpecies(species);
                }
            }

        }

        for (Reaction r : sbmlDoc.getReactionSet().getSortedList()) {
            r.setReactionId(correctName(r.getReactionId()));
            r.setMetaId(correctName(r.getMetaId()));

            if (model.containsReaction(r.getReactionId())) {
                System.err.println("Duplicate reaction with id + " + r.getReactionId() + " ignored.");
            } else {
                reactionIds.add(r.getReactionId());
                org.sbml.jsbml.Reaction reaction = model.createReaction(r.getReactionId());
                try {
                    reaction.setNotes("<body xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                            "    <p>FORMULA: " + r.createFormulaRepaired() + "</p>\n" +
                            "    <p>FORMULA MIGHT CONTAIN REPAIRED IDs - DUPLICATES POSSIBLE - SEE LOG.</p>\n" +
                            "  </body>");
                } catch (XMLStreamException e) {
                    new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                }
                reaction.setLevel(sbmlDocument.getLevel());
                reaction.setVersion(sbmlDocument.getVersion());
                reaction.setName(correctName(r.getReactionName()));
                reaction.setSBOTerm(correctSBO(r.getSboTerm()));
                reactionMeta.add(r.getMetaId());
                if (!model.containsCompartment(correctName(r.getCompartment()))) {
                    Compartment compartment = model.createCompartment(correctName(r.getCompartment()));
                    compartment.setConstant(false);
                }
                if (model.getLevel() == 3) {
                    reaction.setCompartment(correctName(r.getCompartment()));
                }
                for (String string : r.getReactantsTree().getDnfWithCoeff().split("[&,]")) {
                    if (string.length() > 0) {
                        String[] split = string.split("%coeffOf%");
                        double coeff = 1.0;
                        String id = correctName(split[0]);//+"_"+"reactant";
                        if (split.length > 1) {
                            coeff = correctDouble(split[0].replaceAll("%%", ""));
                            id = correctName(split[1]);
                        }
                        if (!model.containsSpecies(id)) {
                            System.err.println("Reaction specifies unlisted reactant species - adding this species. (" + id + ")");
                            org.sbml.jsbml.Species species = model.createSpecies(id);
                            species.setCompartment(r.getCompartment());
                            species.setMetaId(species.getId() + model.getSpeciesCount());

                        }
                        String refId = id + "_reactantOf_" + reaction.getId();
                        if (model.findSpeciesReference(refId) == null) {
                            SpeciesReference ref = model.createReactant();
                            ref.setSpecies(id);
                            if (model.getLevel() == 3) {
                                ref.setConstant(false);
                            }
                            ref.setId(refId);
                            try {
                                ref.setNotes("<body xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                                        "    <p>REACTANT_STOICHIOMETRY: " + coeff + "</p>\n" +
                                        "  </body>");
                            } catch (XMLStreamException e) {
                                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                            }
                            ref.setStoichiometry(coeff);
                            ref.setSBOTerm(SBOTerms.getDefaultSBOReactant());
                        } else {
                            reaction.addReactant(model.findSpeciesReference(refId));
                        }
                    }
                }

                for (String string : r.getProductsTree().getDnfWithCoeff().split("[&,]")) {
                    if (string.length() > 0) {
                        String[] split = string.split("%coeffOf%");
                        double coeff = 1.0;
                        String id = correctName(split[0]);
                        if (split.length > 1) {
                            coeff = correctDouble(split[0].replaceAll("%%", ""));
                            id = correctName(split[1]);
                        }
                        if (!model.containsSpecies(id)) {
                            System.err.println("Reaction specifies unlisted product species - adding this species. (" + id + ")");
                            org.sbml.jsbml.Species species = model.createSpecies(id);
                            species.setCompartment(r.getCompartment());
                            species.setMetaId(species.getId() + model.getSpeciesCount());
                        }
                        String refId = id + "_productOf_" + reaction.getId();
                        if (model.findSpeciesReference(refId) == null) {
                            SpeciesReference ref = model.createProduct();
                            ref.setSpecies(id);
                            ref.setConstant(false);
                            ref.setId(refId);
                            try {
                                ref.setNotes("<body xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                                        "    <p>PRODUCT_STOICHIOMETRY: " + coeff + "</p>\n" +
                                        "  </body>");
                            } catch (XMLStreamException e) {
                                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                            }
                            ref.setStoichiometry(coeff);
                            ref.setSBOTerm(SBOTerms.getDefaultSBOProduct());
                        } else {
                            reaction.addProduct(model.findSpeciesReference(refId));
                        }


                    }
                }


                for (String string : r.getCatalystsTree().getDnfWithCoeff().split("[&,]")) {
                    if (string.length() > 0) {
                        String[] split = string.split("%coeffOf%");
                        String id = correctName(split[0]);
                        double coefficient = 1.0;
                        if (split.length > 1) {
                            id = correctName(split[1]);
                            coefficient = Double.parseDouble(split[0].replaceAll("%%", ""));
                        }
                        if (!model.containsSpecies(id)) {
                            System.err.println("Reaction specifies unlisted catalyst/modifier species - adding this species. (" + id + ")");
                            org.sbml.jsbml.Species species = model.createSpecies(id);
                            species.setCompartment(r.getCompartment());
                            species.setMetaId(species.getId() + model.getSpeciesCount());
                        }
                        String modId = id + "_catalystOf_" + reaction.getId();
                        if (model.findModifierSpeciesReference(modId) == null) {
                            ModifierSpeciesReference mod = model.createModifier();
                            mod.setSBOTerm(SBOTerms.getDefaultSBOCatalyst());
                            mod.setSpecies(id);

                            mod.setId(modId);
                            model.getModifierSpeciesReferences().add(mod);
                            try {
                                mod.setNotes("<body xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                                        "    <p>CATALYST/MODIFIER_STOICHIOMETRY: " + coefficient + "</p>\n" +
                                        "  </body>");
                            } catch (XMLStreamException e) {
                                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                            }
                        } else {
                            reaction.addModifier(model.findModifierSpeciesReference(modId));
                        }
                    }
                }

                for (String string : r.getInhibitorsTree().getDnfWithCoeff().split("[&,]")) {
                    if (string.length() > 0) {
                        String[] split = string.split("%coeffOf%");
                        String id = correctName(split[0]);
                        double coefficient = 1.0;
                        if (split.length > 1) {
                            id = correctName(split[1]);
                            coefficient = Double.parseDouble(split[0].replaceAll("%%", ""));
                        }
                        ModifierSpeciesReference m = null;
                        if (!model.containsSpecies(id)) {
                            System.err.println("Reaction specifies unlisted inhibitor species - adding this species. (" + id + ")");
                            org.sbml.jsbml.Species species = model.createSpecies(id);
                            species.setCompartment(r.getCompartment());
                            species.setMetaId(species.getId() + model.getSpeciesCount());
                        }
                        String modId = id + "_inhibitorOf_" + reaction.getId();
                        if (model.findModifierSpeciesReference(modId) == null) {
                            ModifierSpeciesReference mod = model.createModifier();
                            mod.setSBOTerm(SBOTerms.getDefaultSBOInhibitor());
                            mod.setSpecies(id);
                            mod.setId(modId);
                            model.getModifierSpeciesReferences().add(mod);
                            try {
                                mod.setNotes("<body xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                                        "    <p>INHIBITOR_STOICHIOMETRY: " + coefficient + "</p>\n" +
                                        "  </body>");
                            } catch (XMLStreamException e) {
                                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                            }
                        } else {
                            reaction.addModifier(model.findModifierSpeciesReference(modId));
                        }
                    }
                }
                reaction.setReversible(r.isIsReversible());
            }
        }
    }

    private double correctDouble(String str) {
        if (str.matches("\\d+(\\.\\d+)")) {
            return Double.parseDouble(str);
        } else {
            return 0.0;
        }

    }

    private int correctSBO(String sboTerm) {
        if (sboTerm.matches("(SBO:)?\\d{7}")) {
            return Integer.parseInt(sboTerm.replace("SBO:", ""));
        } else {
            return 1;
        }

    }

    public SBMLDocument getSbmlDocument() {
        return sbmlDocument;
    }

    @Override
    public void nodeAdded(TreeNode treeNode) {

    }

    @Override
    public void nodeRemoved(TreeNodeRemovedEvent treeNodeRemovedEvent) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    String correctName(String name) {
        if (EditorViewController.correctInvalidStrings) {
            if (!name.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
                name = name.replaceAll("[&+]", "_and_");
                name = name.replaceAll("[,/]", "_or_");
                name = name.replaceAll("[.'() ]", "_");
                name = name.replaceAll("(_and_|_or_|_)+", "_");
                name = name.replaceAll("[^_a-zA-Z0-9]", "");
                if (name.length() == 0) {
                    name = "null";
                }
            } else if (name.length() == 0) {
                return "null";
            }
        }
        return name;
    }

    public String getDocAsString() {
        SBMLWriter writer = new SBMLWriter();
        try {
            return writer.writeSBMLToString(this.sbmlDocument);
        } catch (SBMLException | XMLStreamException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }
        return "";
    }
}
