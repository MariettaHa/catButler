package catbutler.gui.visualization.reactionView;

import catbutler.gui.ui.dataTableView.ReactionTableEntry;
import catbutler.gui.visualization.ViewStyler;
import catbutler.utils.Misc;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


public class ReactionViewPane extends Pane {

    ReactionTableEntry reactionEntry;
    HashMap<String, Color> speciesColorMap = new HashMap<>();
    ArrayList<String> idxSpecies = new ArrayList<>();

    double[] reactantsAnchor = new double[2];
    double[] catalystsAnchor = new double[2];
    double[] inhibitorsAnchor = new double[2];
    double[] productsAnchor = new double[2];

    public ReactionViewPane(ReactionTableEntry reactionEntry) {
        this.reactionEntry = reactionEntry;
        addElements();
        initListeners();
    }

    private void initListeners() {

    }

    private void addElements() {
        addReactionArrow();
        initSpeciesLists();
        getChildren().addAll(new LogicTreeViewTop(reactionEntry, catalystsAnchor, speciesColorMap, idxSpecies).getChildren());
        getChildren().addAll(new LogicTreeViewBottom(reactionEntry, inhibitorsAnchor, speciesColorMap, idxSpecies).getChildren());
        getChildren().addAll(new LogicTreeViewRight(reactionEntry, productsAnchor, speciesColorMap, idxSpecies).getChildren());
        getChildren().addAll(new LogicTreeViewLeft(reactionEntry, reactantsAnchor, speciesColorMap, idxSpecies).getChildren());
        HashSet<Node> cs = new HashSet<>();
        HashSet<Node> cs2 = new HashSet<>();
        for (Node n : this.getChildren()) {
            if (n instanceof Circle || n instanceof Label || n instanceof CollapsibleCircle) {
                cs.add(n);
            } else {
                cs2.add(n);
            }
        }
        for (Node n : cs2) {
            n.toBack();
        }

    }

    private void initSpeciesLists() {
        StringBuilder str = new StringBuilder();
        if (!reactionEntry.getCatalystsDNF().isBlank()) {
            str.append(reactionEntry.getCatalystsDNF());
        }
        if (!reactionEntry.getInhibitorsDNF().isBlank()) {
            if (str.length() > 0) {
                str.append("&");
            }
            str.append(reactionEntry.getInhibitorsDNF());
        }
        if (!reactionEntry.getReactantsDNF().isBlank()) {
            if (str.length() > 0) {
                str.append("&");
            }
            str.append(reactionEntry.getReactantsDNF());
        }
        if (!reactionEntry.getProductsDNF().isBlank()) {
            if (str.length() > 0) {
                str.append("&");
            }
            str.append(reactionEntry.getProductsDNF());
        }

        HashSet<String> sSet = new HashSet<>(new ArrayList(Arrays.asList(str.toString().split("&|,"))));
        for (String s : sSet) {
            if (!speciesColorMap.containsKey(s)) {
                speciesColorMap.put(s, Misc.getRandomColor());
            }
            if (!idxSpecies.contains(s)) {
                idxSpecies.add(s);
            }
        }
    }


    private void addReactionArrow() {
        double x = 410.5;
        double y = 290.0;
        double widthR = 50.0;
        double heightR = 20.0;
        double widthTri = 30.0;
        double heightTri = 30.0;
        this.getChildren().addAll(Arrays.asList(ViewStyler.getDefaultReactionArrow(x, y, widthR, heightR, widthTri, heightTri, reactionEntry.isIsReversible())));
        int revAdd = reactionEntry.isIsReversible() ? 1 : 0;
        reactantsAnchor = new double[]{x - (revAdd * widthTri), y + (heightR / 2.0), 3.0}; // x, y, spacing
        productsAnchor = new double[]{x + widthR + widthTri, y + (heightR / 2.0), 3.0};
        catalystsAnchor = new double[]{x - (revAdd * widthTri) + revAdd * widthTri + 0.5 * widthR, y, 3.0};//x+(widthR)/2.0, 75.0};
        inhibitorsAnchor = new double[]{x - (revAdd * widthTri) + revAdd * widthTri + 0.5 * widthR, y + heightR, 3.0};
    }


}
