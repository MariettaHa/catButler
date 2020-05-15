package catbutler.logic;

import catbutler.io.parser.CustomParser;
import catbutler.model.Reaction;

import java.util.ArrayList;

public class ReactionTreeBuilder {

    private Reaction reaction;
    private String parserType;
    private String reactantsStr;
    private String modifiersStr;
    private String catalystsStr;
    private String inhibitorsStr;
    private String productsStr;
    private String reactantsStrCoeff;
    private String catalystsStrCoeff;
    private String inhibitorsStrCoeff;
    private String productsStrCoeff;
    private ArrayList<String> stringsWithCoefficients;

    public ReactionTreeBuilder(String parserType, Reaction reaction, String reactantsStr,
                               String modifiersStr, String productsStr, ArrayList<String> stringsWithCoefficients) {
        this.parserType = parserType;
        this.reaction = reaction;
        this.reactantsStr = reactantsStr;
        this.modifiersStr = modifiersStr;
        this.productsStr = productsStr;
        this.stringsWithCoefficients = stringsWithCoefficients;
        initCoefficientStrings();
        defineSubModifiersStr();
    }

    private void initCoefficientStrings() {
        for (String s : stringsWithCoefficients) {
            String[] strSplit = s.split("::");
            if (strSplit.length > 1) {
                switch (strSplit[0]) {
                    case "reactants":
                        reactantsStrCoeff = strSplit[1];
                        break;
                    case "products":
                        productsStrCoeff = strSplit[1];
                        break;
                    case "catalysts":
                        catalystsStrCoeff = strSplit[1];
                        break;
                    case "inhibitors":
                        inhibitorsStrCoeff = strSplit[1];
                        break;
                }
            }
        }
    }

    private void defineSubModifiersStr() {
        if (parserType.matches("sbml|wim|db|crs|table")) {
            productsStr = productsStr.replaceAll("\\s+", "").replaceAll("\\s+\\s*", "\\s");
            reactantsStr = reactantsStr.replaceAll("\\s+", "").replaceAll("\\s+\\s*", "\\s");
            catalystsStr = "";
            inhibitorsStr = "";
            String[] modStrSplit = modifiersStr.split("\\t");
            if (!modStrSplit[0].equals("noCata")) {
                catalystsStr = modStrSplit[0].strip().replaceAll("\\s?&\\s?", "&").replaceAll("\\s", ",").replaceAll("\\s+\\s*", "\\s");
            }
            if (!modStrSplit[1].equals("noInhib")) {
                inhibitorsStr = modStrSplit[1].strip().replaceAll("\\s?&\\s?", "&").replaceAll("\\s", ",").replaceAll("\\s+\\s*", "\\s");
            }
        }
    }

    public void buildTrees() {
        reaction.setReactantsTree(buildTree(reactantsStr, reactantsStrCoeff));
        reaction.setProductsTree(buildTree(productsStr, productsStrCoeff));
        if (catalystsStr != null) {
            if (!catalystsStr.isBlank()) {
                reaction.setCatalystsTree(buildTree(catalystsStr, catalystsStrCoeff));
            }
        }
        if (inhibitorsStr != null) {
            if (!inhibitorsStr.isBlank()) {
                reaction.setInhibitorsTree(buildTree(inhibitorsStr, inhibitorsStrCoeff));
            }
        }

    }


    BooleanTreeNode buildTree(String expression, String expressionWithCoefficients) {
        BooleanTreeNode root = new BooleanTreeNode(true);
        String dnfStr = "";
        String dnfStrWithCoefficients = "";
        if (expression != null) {
            if (!expression.isBlank()) {
                dnfStr = DisjunctiveNormalForm.compute(expression);
                if (expressionWithCoefficients != null) {
                    if (!expressionWithCoefficients.isBlank()) {
                        dnfStrWithCoefficients = DisjunctiveNormalForm.compute(expressionWithCoefficients);
                    } else {
                        dnfStrWithCoefficients = DisjunctiveNormalForm.compute(expression);
                    }
                } else {
                    dnfStrWithCoefficients = DisjunctiveNormalForm.compute(expression);
                }
            }
        }

        if (dnfStr.matches("[,&A-Za-z0-9-_/().'% ]+")) {
            root.setDnf(dnfStr);
            root.setDnfWithCoeff(dnfStrWithCoefficients);
            if (dnfStr.split(",").length == 1) {
                if (dnfStr.split("&").length == 1) {
                    root.addChild(new BooleanTreeNode(1, dnfStr, 0, root));
                } else {
                    BooleanTreeNode andTreeNode = new BooleanTreeNode(1, dnfStr, 1, root);
                    root.addChild(andTreeNode);
                    for (String conj : dnfStr.split("&")) {
                        andTreeNode.addChild(new BooleanTreeNode(2, conj, 0, andTreeNode));
                    }
                }
            } else {
                BooleanTreeNode orTreeNode = new BooleanTreeNode(1, dnfStr, 2, root);
                root.addChild(orTreeNode);
                for (String disj : dnfStr.split(",")) {
                    if (disj.split("&").length == 1) {
                        orTreeNode.addChild(new BooleanTreeNode(2, disj, 0, orTreeNode));

                    } else {
                        BooleanTreeNode andTreeNode = new BooleanTreeNode(2, disj, 1, orTreeNode);
                        orTreeNode.addChild(andTreeNode);
                        for (String conj : disj.split("&")) {
                            andTreeNode.addChild(new BooleanTreeNode(3, conj, 0, andTreeNode));
                        }
                    }
                }
            }
        } else {
            CustomParser.isInvalid.setValue(true);
            System.err.println(expression + " could not be parsed to DNF");
        }


        return root;
    }

}
