package catbutler.io.parser;

import catbutler.gui.converter.Converter;
import catbutler.gui.ui.QuickInfoPopup;
import catbutler.logic.DisjunctiveNormalForm;
import catbutler.model.DataModel;
import catbutler.model.Reaction;
import catbutler.model.Species;
import catbutler.utils.Misc;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CustomParser {

    public static SimpleBooleanProperty isInvalid = new SimpleBooleanProperty();
    String fileInPath = "";
    File schemeFile = null;
    boolean schemeValid = false;
    DataModel dataModel = new DataModel();
    String listSepAND = "+";
    String listSepOR = ",";
    String reversibleRegex = "<->";
    ArrayList<Reaction> reactionArrayList = new ArrayList<>();
    ArrayList<Species> speciesArrayList = new ArrayList<>();

    public CustomParser(File inputFile, File parsingSchemeFile) {
        isInvalid.set(false);
        try {
            parseFile(inputFile, parsingSchemeFile);
            int x = 0;
            dataModel.initDescendants();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }
    }

    public CustomParser(String inputStr, File parsingSchemeFile) {
        isInvalid.set(false);
        try {
            parseFile(inputStr, parsingSchemeFile);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }
        dataModel.initDescendants();
    }

    public static boolean validateFile(File file, File parsingScheme) {
        CustomParser customParser = new CustomParser(file, parsingScheme);
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(parsingScheme);

            Node rootNode = document.getChildNodes().item(0);
            String nodeMatchStr = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            customParser.recursion2(rootNode, nodeMatchStr);
            customParser.getDataModel().buildCustomTrees("custom");
            int x = 0;
            customParser.getDataModel().initDescendants();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return false;
        }
        DataModel dataModel = customParser.getDataModel();
        boolean v = (dataModel != null && !isInvalid.getValue());
        if (v) {
            Converter.lastDataModel = dataModel;
        }
        return v;
    }


    public void parseFile(File inputFile, File parsingSchemePath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(parsingSchemePath);
        Node rootNode = document.getChildNodes().item(0);
        String nodeMatchStr = FileUtils.readFileToString(inputFile, StandardCharsets.UTF_8);
        recursion2(rootNode, nodeMatchStr);
        dataModel.buildCustomTrees("custom");
    }

    public void parseFile(String inputStr, File parsingSchemePath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(parsingSchemePath);
        Node rootNode = document.getChildNodes().item(0);
        String nodeMatchStr = inputStr;
        recursion2(rootNode, nodeMatchStr);
        dataModel.buildCustomTrees("custom");
    }

    public void parse() {
        if (!schemeValid) {
            new QuickInfoPopup("Error!", "File is not valid (validated against chosen XML schema)", -1, null);
        } else {
            dataModel = new DataModel();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = null;
            try {
                docBuilder = docBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
            Document document = null;
            try {
                document = docBuilder.parse(schemeFile);
            } catch (SAXException | IOException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
            Node rootNode = document.getChildNodes().item(0);
            String nodeMatchStr = null;
            try {
                nodeMatchStr = FileUtils.readFileToString(new File(this.fileInPath), StandardCharsets.UTF_8);
            } catch (IOException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
            detectSpecificAttributes(nodeMatchStr);
            recursion2(rootNode, nodeMatchStr);
            dataModel.buildCustomTrees("custom");

        }
    }

    private void detectSpecificAttributes(String nodeMatchStr) {
        if (nodeMatchStr != null) {
            if (nodeMatchStr.contains("listSepAnd=")) {
                Pattern p = Pattern.compile("listSepAnd=\"(\\S+)\"");
                Matcher m = p.matcher(nodeMatchStr);
                if (m.find()) {
                    listSepAND = m.group(1);
                }
            }
            if (nodeMatchStr.contains("listSepOR=")) {
                Pattern p = Pattern.compile("listSepOR=\"(\\S+)\"");
                Matcher m = p.matcher(nodeMatchStr);
                if (m.find()) {
                    listSepOR = m.group(1);
                }
            }
            if (nodeMatchStr.contains("reversibleRegex=")) {
                Pattern p = Pattern.compile("reversibleRegex=\"(\\S+)\"");
                Matcher m = p.matcher(nodeMatchStr);
                if (m.find()) {
                    reversibleRegex = m.group(1);
                }
            }
        }
    }

    public void recursion2(Node node, String str) {
        if (node.getAttributes() != null) {
            if (node.getAttributes().getNamedItem("regex") != null) {
                Node childNode = null;
                for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                    if (node.getChildNodes().item(j).getNodeName().equals("match")) {
                        boolean hasGrandChildNodes = false;
                        childNode = node.getChildNodes().item(j);
                        if (childNode.getChildNodes().getLength() > 0) {
                            hasGrandChildNodes = true;
                        }
                        if (hasGrandChildNodes) {
                            HashMap<Integer, String> groupAnnotationMap = new HashMap<>();
                            HashMap<Integer, Node> groupNodeMap = new HashMap<>();
                            NodeList grandChildNodes = childNode.getChildNodes();
                            for (int i = 0; i < grandChildNodes.getLength(); i++) {
                                Node grandChildNode = grandChildNodes.item(i);
                                if (grandChildNode.getAttributes() != null) {
                                    NamedNodeMap atts = grandChildNode.getAttributes();
                                    Node idN = atts.getNamedItem("id");
                                    Node annotationN = atts.getNamedItem("annotation");
                                    Node grIdN = atts.getNamedItem("grId");
                                    if (grIdN != null && annotationN != null) {
                                        groupAnnotationMap.put(Integer.parseInt(grIdN.getNodeValue()), annotationN.getNodeValue());
                                    }
                                    if (grIdN != null) {
                                        groupNodeMap.put(Integer.parseInt(grIdN.getNodeValue()), grandChildNode);
                                    }
                                }
                            }
                            Pattern p = Pattern.compile(node.getAttributes().getNamedItem("regex").getNodeValue());
                            if (str.length() > 0) {
                                Matcher m = p.matcher(str);
                                while (m.find()) {

                                    for (int i = 0; i < m.groupCount() + 1; i++) {

                                        int kk = 0;
                                        if (m.group(i) != null) {
                                            if (groupAnnotationMap.containsKey(i)) {
                                                Node tmp = null;
                                                if (groupNodeMap.containsKey(i)) {
                                                    tmp = groupNodeMap.get(i);
                                                }
                                                String annotationStr = groupAnnotationMap.get(i);
                                                switch (annotationStr) {
                                                    case "doc":
                                                        break;
                                                    case "metadata":
                                                        dataModel.getComments().add(m.group(i));
                                                        break;
                                                    case "reaction":
                                                        reactionArrayList.add(new Reaction("default"));
                                                        dataModel.addReaction(reactionArrayList.get(reactionArrayList.size() - 1));
                                                        break;
                                                    case "species":
                                                        speciesArrayList.add(new Species("default"));
                                                        dataModel.addSpecies(speciesArrayList.get(speciesArrayList.size() - 1));
                                                        break;
                                                    default:
                                                        if (m.group(i) != null) {
                                                            if (annotationStr.startsWith("species")) {
                                                                addAttribute(speciesArrayList.get(speciesArrayList.size() - 1), tmp, annotationStr, m.group(i));
                                                            } else if (annotationStr.startsWith("reaction")) {
                                                                addAttribute(reactionArrayList.get(reactionArrayList.size() - 1), tmp, annotationStr, m.group(i));
                                                            }
                                                        }
                                                        break;
                                                }
                                            }
                                            if (groupNodeMap.containsKey(i)) {
                                                if (groupNodeMap.get(i).getChildNodes().getLength() > 0) {
                                                    recursion2(groupNodeMap.get(i), m.group(i));
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

        }

    }


    private void addAttribute(Species species, Node node, String annotationStr, String value) {
        NamedNodeMap nodes = node.getAttributes();
        if (value != null) {
            if (!value.isBlank()) {
                switch (annotationStr) {
                    case "speciesId":
                        species.setSpeciesId(value);
                        break;
                    case "speciesNames":
                        if (nodes.getNamedItem("listSeparator") != null) {
                            species.getSpeciesNames().addAll(Arrays.asList(value.split(nodes.getNamedItem("listSeparator").getNodeValue())));
                        }
                        break;
                    case "speciesName":
                        species.getSpeciesNames().add(value);
                        break;
                    case "speciesInitAmount":
                        if (nodes.getNamedItem("value") != null) {
                            if (nodes.getNamedItem("value").getNodeValue().matches("\\d+(\\.\\d+)?")) {
                                species.setInitAmount(Double.parseDouble(nodes.getNamedItem("value").getNodeValue()));
                            }
                        }
                        if (species.getInitAmount() > 0.0) {
                            dataModel.addFood(species);
                        }
                        break;
                    case "speciesMetaData":
                        species.setMetaData(species.getMetaData() + value + "\n");
                        break;
                    case "speciesCompartment":
                        species.setCompartment(value);
                        break;
                    case "speciesSboTerm":
                        species.setSboTerm(Misc.correctSBOTerm(value, "species"));
                        break;
                    case "speciesType":
                        species.setSpeciesType(value);
                        break;
                    case "speciesDescription":
                        species.setDescription(value);
                        break;
                    case "speciesNote":
                        species.setNote(value);
                        break;
                    case "speciesNetworkId":
                        species.setNetworkId(value);
                        break;
                    case "speciesPosX":
                        if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
                            species.setPosX(Double.parseDouble(value));
                        } else {
                            isInvalid.setValue(true);
                        }
                        break;
                    case "speciesPosY":
                        if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
                            species.setPosY(Double.parseDouble(value));
                        } else {
                            isInvalid.setValue(true);
                        }
                        break;
                    case "speciesPosZ":
                        if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
                            species.setPosZ(Double.parseDouble(value));
                        } else {
                            isInvalid.setValue(true);
                        }
                        break;
                    default:
                        break;

                }
            }
        } else {
            isInvalid.setValue(true);
        }
    }

    public void addAttribute(Reaction reaction, Node node, String annotationStr, String value) {
        NamedNodeMap attributes = node.getAttributes();
        if (value != null) {
            if (!value.isBlank()) {
                switch (annotationStr) {
                    case "reactionId":
                        reaction.setReactionId(value);
                        break;
                    case "reactionType":
                        reaction.setReactionType(value);
                        break;
                    case "reactionName":
                        reaction.setReactionName(value);
                        break;
                    case "reactionSboTerm":
                        reaction.setSboTerm(Misc.correctSBOTerm(value, "reaction"));
                        break;
                    case "reactionDescription":
                        reaction.setDescription(value);
                        break;
                    case "reactionNote":
                        reaction.setNote(value);
                        break;
                    case "reactionNetworkId":
                        reaction.setNetworkId(value);
                        break;
                    case "reactionFormula":
                        reaction.setFormula(value);
                    case "reactionReactantsList":
                        String[] logicSep = getLogicSeps(attributes);
                        reaction.setReactantAND(logicSep[0]);
                        reaction.setReactantOR(logicSep[1]);
                        reaction.setRawReactantsStr(value);
                        break;
                    case "reactionIsReversible":
                        if (attributes.getNamedItem("reversibleRegex") != null) {
                            String revRegex = attributes.getNamedItem("reversibleRegex").getNodeValue();
                            if (value.matches(revRegex)) {
                                reaction.setIsReversible(true);
                            }
                        }
                        break;
                    case "reactionProductsList":
                        logicSep = getLogicSeps(attributes);
                        reaction.setProductAND(logicSep[0]);
                        reaction.setProductOR(logicSep[1]);
                        reaction.setRawProductsStr(value);
                        break;
                    case "reactionCatalystsList":
                        logicSep = getLogicSeps(attributes);
                        reaction.setCatalystAND(logicSep[0]);
                        reaction.setCatalystOR(logicSep[1]);
                        reaction.setRawCatalysatorsStr(value);
                        break;
                    case "reactionInhibitorsList":
                        logicSep = getLogicSeps(attributes);
                        reaction.setInhibitorAND(logicSep[0]);
                        reaction.setInhibitorOR(logicSep[1]);
                        reaction.setRawInhibitorsStr(value);
                        break;
                    case "reactionWeight":
                        if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
                            reaction.setWeight(Double.parseDouble(value));
                        } else {
                            isInvalid.setValue(true);
                        }
                        break;
                    case "reactionEndPosX":
                        if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
                            reaction.setEndPosX(Double.parseDouble(value));
                        } else {
                            isInvalid.setValue(true);
                        }
                        break;
                    case "reactionStartPosX":
                        if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
                            reaction.setStartPosX(Double.parseDouble(value));
                        } else {
                            isInvalid.setValue(true);
                        }
                        break;
                    case "reactionEndPosY":
                        if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
                            reaction.setEndPosY(Double.parseDouble(value));
                        } else {
                            isInvalid.setValue(true);
                        }
                        break;
                    case "reactionStartPosY":
                        if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
                            reaction.setStartPosY(Double.parseDouble(value));
                        } else {
                            isInvalid.setValue(true);
                        }
                        break;
                    case "reactionEndPosZ":
                        if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
                            reaction.setEndPosZ(Double.parseDouble(value));
                        } else {
                            isInvalid.setValue(true);
                        }
                        break;
                    case "reactionStartPosZ":
                        if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
                            reaction.setStartPosZ(Double.parseDouble(value));
                        } else {
                            isInvalid.setValue(true);
                        }
                        break;
                    default:
                        break;
                }
            }
        } else {
            isInvalid.setValue(true);
        }
    }

    private String[] getLogicSeps(NamedNodeMap attributes) {
        String defAnd = "&";
        String defOr = ",";
        String[] arr = new String[2];
        if (attributes.getNamedItem("listSepAND") != null) {
            arr[0] = attributes.getNamedItem("listSepAND").getNodeValue();
        } else {
            arr[0] = defAnd;
        }
        if (attributes.getNamedItem("listSepOR") != null) {
            arr[1] = attributes.getNamedItem("listSepOR").getNodeValue();
        } else {
            arr[1] = defOr;
        }
        return arr;
    }

    public String parseStringToDNF(String s, String and, String or) {
        s = s.replaceAll(and, "&").replaceAll(or, " ");
        return DisjunctiveNormalForm.compute(s);
    }

    public DataModel getDataModel() {
        return dataModel;
    }
}
