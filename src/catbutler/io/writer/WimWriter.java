package catbutler.io.writer;

import catbutler.gui.ui.QuickInfoPopup;
import catbutler.io.documents.WimDoc;
import catbutler.model.Reaction;
import catbutler.model.Species;
import catbutler.model.SpeciesSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class WimWriter {

    WimDoc wimDoc;
    String wimString;

    public WimWriter(WimDoc wimDoc) {
        this.wimDoc = wimDoc;
    }

    public void write(String outputPath) {
        wimString = buildString();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputPath));
            wimDoc.setPath(new File(outputPath).toPath());
        } catch (IOException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }
        try {
            writer.write(wimString);
        } catch (IOException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }
        //}
    }

    public String buildString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<meta-data>\n");
        int m = wimDoc.getSpeciesSet().size() - wimDoc.getSpeciesFoodSet().size();

        wimDoc.getComments().add("nrMolecules = " + m);
        wimDoc.getComments().add("nrFoodSet   = " + wimDoc.getSpeciesFoodSet().size());
        wimDoc.getComments().add("nrReactions = " + wimDoc.getReactionSet().size());

        SpeciesSet<Species> notFoodSpecies = new SpeciesSet<Species>(wimDoc.getSpeciesSet().getSortedSet());
        notFoodSpecies.removeAllSpecies(wimDoc.getSpeciesFoodSet());
        HashSet<String> printedComments = new HashSet<>();
        for (String comment : wimDoc.getComments()) {
            if (!printedComments.contains(comment.strip())) {
                printedComments.add(comment.strip());
                stringBuilder.append(comment.strip() + "\n");
            }
        }

        stringBuilder.append("\n<molecules>\n");
        for (Species s : notFoodSpecies.getSortedSet()) {
            ArrayList<String> speciesNames = new ArrayList<>(s.getSpeciesNames());
            Collections.sort(speciesNames);
            stringBuilder.append(speciesNames.stream().collect(Collectors.joining("; ")) + "\t");
            stringBuilder.append(s.getSpeciesId() + "\n");
        }

        stringBuilder.append("\n<food set>\n");
        for (Species s : wimDoc.getSpeciesFoodSet().getSortedSet()) {
            ArrayList<String> speciesNames = new ArrayList<>(s.getSpeciesNames());
            Collections.sort(speciesNames);
            stringBuilder.append(speciesNames.stream().collect(Collectors.joining("; ")) + "\t");
            stringBuilder.append(s.getSpeciesId() + "\n");
        }

        stringBuilder.append("\n<reactions>\n");
        for (Reaction r : wimDoc.getReactionSet().getSortedList()) {
            stringBuilder.append(r.getReactionId() + "\t");
            stringBuilder.append(dnfToWimWithCoeff(r.getReactantsTree().getDnfWithCoeff(), " ", " + ", " ") + " ");
            stringBuilder.append("<".repeat((r.isIsReversible()) ? 1 : 0) + "=> ");
            stringBuilder.append(dnfToWimWithCoeff(r.getProductsTree().getDnfWithCoeff(), " ", " + ", " ") + "\t");
            String c = dnfToWimWithCoeff(r.getCatalystsTree().getDnfWithCoeff(), " ", "&", " ");
            String i = dnfToWimWithCoeff(r.getInhibitorsTree().getDnfWithCoeff(), " ", "&", " ");

            if (c.length() > 0) {
                stringBuilder.append(c + " ");
            }
            if (i.length() > 0) {
                stringBuilder.append("{" + i + "}");
            }
            stringBuilder.append("\t" + r.getWeight() + "\n");
        }

        return stringBuilder.toString().replaceAll("[ ][ ]*", " ");
    }

    String dnfToWimWithCoeff(String dnf, String or, String and, String coeffSep) {
        dnf = dnf.replaceAll("%%", "");
        return dnf.replaceAll(",", or).replaceAll("&", and).replaceAll("%coeffOf%", coeffSep);
    }

}
