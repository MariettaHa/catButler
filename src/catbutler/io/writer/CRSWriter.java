package catbutler.io.writer;

import catbutler.gui.ui.QuickInfoPopup;
import catbutler.io.documents.CRSDoc;
import catbutler.model.FoodSet;
import catbutler.model.Reaction;
import catbutler.model.Species;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CRSWriter {

    CRSDoc crsDoc;
    String crsString;

    public CRSWriter(CRSDoc crsDoc) {
        this.crsDoc = crsDoc;
    }

    public void write(String outputPath) {
        if (crsDoc.getDataModel().satisfiesCRScriteria()) {
            crsString = buildString();
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(outputPath));
                crsDoc.setPath(new File(outputPath).toPath());
            } catch (IOException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
            try {
                writer.write(crsString);
            } catch (IOException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
            try {
                writer.close();
            } catch (IOException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
        } else {
            new QuickInfoPopup("Error!", "Input does not satisfy criteria for CRS format (min. 1 food item, " +
                    "min. 1 reaction item with min. 1 reactant and min. 1 product", -1, null);
        }
    }

    public String buildString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : crsDoc.getComments()) {
            stringBuilder.append("# " + s + "\n");
        }


        if (crsDoc.getComments().size() > 0) {
            stringBuilder.append("\n");
        }

        for (Reaction r : crsDoc.getReactionSet().getSortedList()) {
            stringBuilder.append(r.getReactionId() + ": ");
            stringBuilder.append(dnfToCRS(r.getReactantsTree().getDnf(), "+") + " ");
            if (r.getCatalystsTree().getDnf().length() > 0) {
                stringBuilder.append("[" + dnfToCRS(r.getCatalystsTree().getDnf(), "*") + "] ");
            }

            if (r.getInhibitorsTree().getDnf().length() > 0) {
                stringBuilder.append("{" + dnfToCRS(r.getInhibitorsTree().getDnf(), "*") + "} ");
            }
            stringBuilder.append("<".repeat((r.isIsReversible()) ? 1 : 0) + "-> ");
            stringBuilder.append(dnfToCRS(r.getProductsTree().getDnf(), "+") + "\n");
        }

        if (crsDoc.getSpeciesFoodSet().size() > 0) {
            stringBuilder.append("\n");
        }

        stringBuilder.append("Food: ");
        int c = 1;
        FoodSet<Species> foods = crsDoc.getSpeciesFoodSet();
        for (Species s : foods.getSortedSet()) {
            stringBuilder.append(s.getSpeciesId());
            if (c < foods.size()) {
                stringBuilder.append(",");
            }
            c++;
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    String dnfToCRS(String dnf, String and) {
        return dnf.strip().replaceAll("\\s?&\\s?", and);
    }

}
