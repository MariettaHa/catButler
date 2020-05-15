package catbutler.io.parser;

import catbutler.gui.converter.Converter;
import catbutler.io.Patterns;
import catbutler.io.documents.CRSDoc;
import catbutler.io.documents.Doc;
import catbutler.logic.ReactionTreeBuilder;
import catbutler.model.DataModel;
import catbutler.model.Reaction;
import catbutler.model.Species;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class CRSParser extends Parser {

    private CRSDoc crsDocument;

    public CRSParser(Doc doc) {
        super(doc);
        this.crsDocument = (CRSDoc) doc;
    }

    public static boolean validateFile(File file) {
        CRSDoc crsDoc = new CRSDoc(file.toPath());
        crsDoc.readIn();
        DataModel dataModel = crsDoc.getDataModel();
        boolean v = dataModel.satisfiesCRScriteria();
        if (v) {
            Converter.lastDataModel = dataModel;
        }
        return dataModel.satisfiesCRScriteria();
    }

    private static String formatStr(String str) {
        str = str.replaceAll("([+*])", "&");
        str = str.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\{", "").replaceAll("\\}", "");
        return str;
    }

    public void parseLine(String line) {
        String lineStrip = line.replaceAll("\\s+", "");
        if (line.startsWith("#")) {
            parseComment(line);
        } else if (line.matches(Patterns.crsFoodSetPattern) && !(line.matches(Patterns.crsReactionPattern))) {
            parseFoodSet(line);
        } else if (lineStrip.matches(Patterns.crsReactionPattern)) {
            parseReactionLine(lineStrip);
        }
    }

    private void parseReactionLine(String lineStrip) {
        String reactionId = lineStrip.split(":")[0];//m.group(1);
        String leftSplit = lineStrip.split(":")[1].split("<?[-=]>")[0];
        String rightSplit = lineStrip.split(":")[1].split("<?[-=]>")[1];
        Reaction reaction = new Reaction(reactionId);
        String reactantsStr = "";
        if (leftSplit.contains("[")) {
            reactantsStr = formatStr(leftSplit.split("\\[")[0]);
        } else {
            reactantsStr = formatStr(leftSplit.split("\\{")[0]);
        }
        String catStr = "noCata";
        String inhStr = "noInhib";
        if (leftSplit.contains("[")) {
            catStr = formatStr(leftSplit.split("\\[")[1].split("\\]")[0]);
        }
        if (leftSplit.contains("{")) {
            inhStr = formatStr(leftSplit.split("\\{")[1].split("\\}")[0]);
        }
        if ((lineStrip.contains("<->")) || lineStrip.contains("<=>")) {
            reaction.setIsReversible(true);
        }
        String productsStr = formatStr(rightSplit);
        ReactionTreeBuilder reactionTreeBuilder =
                new ReactionTreeBuilder("crs", reaction, reactantsStr, catStr + "\t" + inhStr, productsStr,
                        new ArrayList<>(Arrays.asList("reactants::" + reactantsStr, "products::" + productsStr,
                                "catalysts::" + catStr, "inhibitors::" + inhStr)));
        reactionTreeBuilder.buildTrees();

        crsDocument.getReactionSet().addReaction(reaction);
    }

    private void parseFoodSet(String lineStrip) {
        lineStrip = lineStrip.split(":")[1].strip().replaceAll("\\s+|\\t+", ",");//.replaceAll("\\s+","");
        for (String s : lineStrip.split(",")) {
            if (!s.isBlank()) {
                crsDocument.getSpeciesSet().addSpecies(s);
                Species sp = crsDocument.getSpeciesSet().getIdToSpeciesMap().get(s);
                if (sp.getInitConcentration() + sp.getInitAmount() <= 0) {
                    sp.setInitAmount(1.0);
                }
                crsDocument.getSpeciesFoodSet().addFood(crsDocument.getSpeciesSet().getIdToSpeciesMap().get(s));
            }
        }
    }

    private void parseComment(String line) {
        crsDocument.getComments().add(line.replaceAll("#", ""));
    }

}
