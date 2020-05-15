package catbutler.io.parser;

import catbutler.gui.converter.Converter;
import catbutler.io.documents.Doc;
import catbutler.io.documents.WimDoc;
import catbutler.logic.ReactionTreeBuilder;
import catbutler.model.DataModel;
import catbutler.model.Reaction;
import catbutler.model.Species;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class WimParser extends Parser {

    private WimDoc wimDocument;
    private int paragraphType = 0; //0: meta-data; 1: molecules; 2: food set; 3: reactions
    private Map<String, Integer> paragraphMap = Map.of(
            "<meta-data>", 1, "<molecules>", 2, "<food set>", 3, "<reactions>", 4);

    public WimParser(Doc doc) {
        super(doc);
        this.wimDocument = (WimDoc) doc;
        this.wimDocument.setParser(this);
    }

    public static boolean validateFile(File file) {
        WimDoc wimDoc = new WimDoc(file.toPath());
        wimDoc.readIn();
        DataModel dataModel = wimDoc.getDataModel();
        boolean v = dataModel.satisfiesWIMcriteria();
        if (v) {
            Converter.lastDataModel = dataModel;
        }
        return v;
    }

    @Override
    public void parseLine(String line) {
        if (paragraphMap.containsKey(line)) {
            paragraphType = paragraphMap.get(line);
        } else {
            if (!line.isBlank()) {
                switch (paragraphType) {
                    case 1:
                        parseMetaData(line);
                        break;
                    case 2:
                        parseMolLine(line);
                        break;
                    case 3:
                        parseFoodSet(line);
                        break;
                    case 4:
                        parseReactionLine(line);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void parseReactionLine(String line) {
        String[] info = line.split("\\t");
        if (info.length == 4) {
            ArrayList<String> stringsWithCoefficients = new ArrayList<>();
            Reaction reaction = new Reaction(info[0]);
            reaction.setWeight(Double.parseDouble(info[3]));
            String reactantsStr = formatStr(info[1].split(" <?=> ")[0]);
            reaction.setIsReversible(info[1].contains("<=>"));
            String productsStr = formatStr(info[1].split("=> ")[1]);
            String modifiersStr = formatStr(info[2]);
            stringsWithCoefficients.add(formatStrWithCoeff("reactants::" + info[1].split(" <?=> ")[0])); //add reactants with coefficients (idx0)
            stringsWithCoefficients.add(formatStrWithCoeff("products::" + info[1].split("=> ")[1])); //add products with coefficients (idx1)

            String catStr = "noCata";
            String[] splitMod = modifiersStr.split("\\{");

            String inhibStr = "noInhib";

            if (splitMod.length > 1) {
                catStr = splitMod[0].strip();
                inhibStr = splitMod[1].replaceAll("\\}|\\{", "");
                stringsWithCoefficients.add(formatStrWithCoeff("catalysts::" + info[2].split("\\{")[0].strip()));
                stringsWithCoefficients.add(formatStrWithCoeff("inhibitors::" + info[2].split("\\{")[1].replaceAll("\\}|\\{", "")));
            } else {
                if (modifiersStr.contains("{")) {
                    inhibStr = modifiersStr.strip().replaceAll("\\}|\\{", "");
                    stringsWithCoefficients.add(formatStrWithCoeff("inhibitors::" + info[2].split("\\{")[1].replaceAll("\\}|\\{", "")));
                } else if (modifiersStr.length() > 0) {
                    stringsWithCoefficients.add(formatStrWithCoeff("catalysts::" + info[2].split("\\{")[0].strip()));
                    catStr = modifiersStr.strip();
                }
            }

            ReactionTreeBuilder reactionTreeBuilder
                    = new ReactionTreeBuilder("wim", reaction, reactantsStr, catStr + "\t" + inhibStr, productsStr, stringsWithCoefficients);
            reactionTreeBuilder.buildTrees();

            wimDocument.addReaction(reaction);
        }

    }

    private void parseFoodSet(String line) {
        String[] info = line.split("\\t");
        if (!info[1].isBlank()) {
            if (wimDocument.getSpeciesSet().getIdToSpeciesMap().containsKey(info[1])) {
                wimDocument.getSpeciesSet().getIdToSpeciesMap().get(info[1]).setInitAmount(1.0);
                wimDocument.addFood(wimDocument.getSpeciesSet().getIdToSpeciesMap().get(info[1]));
            } else {
                wimDocument.addSpecies(info[1]);
                Species s = wimDocument.getSpeciesSet().getIdToSpeciesMap().get(info[1]);
                s.setInitAmount(1.0);
                s.addSpeciesNames(info[0].split("; "));
                wimDocument.addFood(s);
            }
        }
    }

    private void parseMolLine(String line) {
        String[] info = line.split("\\t");
        if (!info[1].isBlank()) {
            if (info.length == 2 && !wimDocument.getSpeciesSet().getIdToSpeciesMap().containsKey(info[1])) {
                Species species = new Species(info[1]);
                species.addSpeciesNames(info[0].split("; "));
                wimDocument.addSpecies(species);
            }
        }
    }

    private void parseMetaData(String line) {
        wimDocument.getComments().add(line);
        if (line.matches("nrMolecules\\s+=\\s+(\\d+)")) {
            wimDocument.setMolCount(Integer.parseInt(line.split("= ")[1]));
        } else if (line.matches("nrFoodSet\\s+=\\s+(\\d+)")) {
            wimDocument.setFoodCount(Integer.parseInt(line.split("= ")[1]));
        } else if (line.matches("nrReactions\\s+=\\s+(\\d+)")) {
            wimDocument.setReactionCount(Integer.parseInt(line.split("= ")[1]));
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

}
