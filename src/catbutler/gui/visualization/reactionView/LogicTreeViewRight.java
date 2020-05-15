package catbutler.gui.visualization.reactionView;

import catbutler.gui.ui.dataTableView.ReactionTableEntry;
import catbutler.gui.visualization.ViewStyler;
import catbutler.utils.Misc;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class LogicTreeViewRight extends Group {

    ReactionTableEntry reactionEntry;
    double[] anchor;
    HashMap<String, Color> speciesColorMap;
    ArrayList<String> idxSpecies;

    double pathsLength = 100.0;

    LogicTreeViewRight(ReactionTableEntry reactionTableEntry, double[] anchor, HashMap<String, Color> speciesColorMap, ArrayList<String> idxSpecies) {
        this.reactionEntry = reactionTableEntry;
        this.anchor = anchor;
        this.speciesColorMap = speciesColorMap;
        this.idxSpecies = idxSpecies;
        init();
        initTooltips();
    }

    private void initTooltips() {
        ArrayList<Circle> newCircles = new ArrayList<>();
        for (Node node : this.getChildren()) {
            if (node instanceof CollapsibleCircle) {
                CollapsibleCircle c = (CollapsibleCircle) node;
                Circle accessibleCircle = new Circle();
                accessibleCircle.setRadius(c.getRadius());
                accessibleCircle.setCenterX(c.getCenterX());
                accessibleCircle.setCenterY(c.getCenterY());
                accessibleCircle.setFill(Color.TRANSPARENT);
                accessibleCircle.setStroke(Color.TRANSPARENT);
                accessibleCircle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        c.collapse();
                    }
                });
                accessibleCircle.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        accessibleCircle.setStroke(Color.BLACK);
                        accessibleCircle.setFill(Color.color(0.0, 0.3, 0.9, 0.3));
                    }
                });
                accessibleCircle.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        accessibleCircle.setFill(Color.TRANSPARENT);
                        accessibleCircle.setStroke(Color.TRANSPARENT);
                    }
                });
                String txt = c.getLabel().getText();
                if (!txt.equals("||") && !txt.equals("&")) {
                    txt = idxSpecies.get(Integer.parseInt(c.getLabel().getText()));
                } else {
                    accessibleCircle.setFill(Color.WHITE);
                    accessibleCircle.toFront();
                }
                Tooltip tooltip = new Tooltip(txt);
                tooltip.setShowDelay(Duration.seconds(0.3));
                Tooltip.install(accessibleCircle, tooltip);
                newCircles.add(accessibleCircle);
            }
        }
        for (Circle c : newCircles) {
            this.getChildren().add(c);
            c.toFront();
        }
    }


    private void init() {
        String dnf = this.reactionEntry.getProductsDNF();
        if (dnf.length() > 0 && !dnf.isBlank()) {
            if (dnf.contains(",")) {
                CollapsibleCircle collapsibleCircle = new CollapsibleCircle(11.0, "||", anchor[0] + 15.0, anchor[1], Color.WHITE, Color.BLACK);
                this.getChildren().addAll(collapsibleCircle, collapsibleCircle.getLabel());

                ArrayList<String> splitByOr = new ArrayList<>(new HashSet<String>(Arrays.asList(dnf.split(","))));
                int size = splitByOr.size();
                double[] firstPosAngle = Misc.getAnglesRight(Misc.getAngleSpan(size), Misc.getMiddle(size), size, pathsLength, anchor[0] + 15.0, anchor[1]);
                for (String s : splitByOr) {
                    double[] posAngle = Misc.getAnglesRight(Misc.getAngleSpan(size), splitByOr.indexOf(s), size, pathsLength, anchor[0] + 15.0, anchor[1]);
                    if (s.contains("&")) {
                        CollapsibleCircle collapsibleCircle2 = new CollapsibleCircle(11.0, "&", firstPosAngle[0], posAngle[1], Color.WHITE, Color.BLACK);

                        Path path = ViewStyler.getDefaultReactionViewPath(collapsibleCircle.getCenterX(), collapsibleCircle.getCenterY(),
                                collapsibleCircle2.getCenterX(), collapsibleCircle2.getCenterY(), ViewStyler.defaultProductsColor);
                        this.getChildren().add(path);
                        collapsibleCircle.getChildren().add(path);
                        collapsibleCircle.addChild(collapsibleCircle2);
                        this.getChildren().addAll(collapsibleCircle2, collapsibleCircle2.getLabel());
                        ArrayList<String> OrsSplitByAnd = new ArrayList<>(new HashSet<String>(Arrays.asList(s.split("&"))));
                        int size2 = OrsSplitByAnd.size();
                        double[] firstPosAngle2 = Misc.getAnglesRight(Misc.getAngleSpan(size2), Misc.getMiddle(size2), size2, pathsLength, firstPosAngle[0], posAngle[1]);//,
                        for (String t : OrsSplitByAnd) {
                            double[] posAngle2 = Misc.getAnglesRight(Misc.getAngleSpan(size2), OrsSplitByAnd.indexOf(t), size2, pathsLength, posAngle[0], firstPosAngle2[1]);//,
                            String str = "nan";
                            if (idxSpecies.contains(t.strip())) {
                                str = String.valueOf(idxSpecies.indexOf(t.strip()));
                            }
                            Color fill = ViewStyler.defaultProductsColor;
                            if (speciesColorMap.containsKey(t.strip())) {
                                fill = speciesColorMap.get(t.strip());
                            }
                            CollapsibleCircle collapsibleCircle3 = new CollapsibleCircle(11.0, str, firstPosAngle2[0], posAngle2[1], fill, ViewStyler.defaultProductsColor);


                            Path path2 = ViewStyler.getDefaultReactionViewPath(collapsibleCircle2.getCenterX(), collapsibleCircle2.getCenterY(),
                                    collapsibleCircle3.getCenterX(), collapsibleCircle3.getCenterY(), ViewStyler.defaultProductsColor);
                            this.getChildren().add(path2);
                            collapsibleCircle2.getChildren().add(path2);
                            collapsibleCircle2.addChild(collapsibleCircle3);
                            this.getChildren().addAll(collapsibleCircle3, collapsibleCircle3.getLabel());
                        }
                        collapsibleCircle2.collapse();
                    } else {
                        String str = "nan";
                        if (idxSpecies.contains(s.strip())) {
                            str = String.valueOf(idxSpecies.indexOf(s.strip()));
                        }
                        Color fill = ViewStyler.defaultProductsColor;
                        if (speciesColorMap.containsKey(s.strip())) {
                            fill = speciesColorMap.get(s.strip());
                        }
                        CollapsibleCircle collapsibleCircle2 = new CollapsibleCircle(11.0, str, firstPosAngle[0], posAngle[1], fill, ViewStyler.defaultProductsColor);
                        Path path = ViewStyler.getDefaultReactionViewPath(collapsibleCircle.getCenterX(), collapsibleCircle.getCenterY(),
                                collapsibleCircle2.getCenterX(), collapsibleCircle2.getCenterY(), ViewStyler.defaultProductsColor);
                        this.getChildren().add(path);
                        collapsibleCircle.getChildren().add(path);
                        collapsibleCircle.addChild(collapsibleCircle2);
                        this.getChildren().addAll(collapsibleCircle2, collapsibleCircle2.getLabel());

                    }
                }
                collapsibleCircle.collapse();
            } else if (dnf.contains("&")) {
                CollapsibleCircle collapsibleCircle = new CollapsibleCircle(11.0, "&", anchor[0] + 15.0, anchor[1], Color.WHITE, Color.BLACK);

                ArrayList<String> splitByAnd = new ArrayList<>(new HashSet<String>(Arrays.asList(dnf.split("&"))));
                int size = splitByAnd.size();
                double[] firstPosAngle = Misc.getAnglesRight(Misc.getAngleSpan(size), Misc.getMiddle(size), size, pathsLength, anchor[0] + 15.0, anchor[1]);
                for (String t : splitByAnd) {
                    double[] posAngle = Misc.getAnglesRight(Misc.getAngleSpan(size), splitByAnd.indexOf(t), size, pathsLength, anchor[0] + 15.0, anchor[1]);
                    String str = "nan";
                    if (idxSpecies.contains(t.strip())) {
                        str = String.valueOf(idxSpecies.indexOf(t.strip()));
                    }
                    Color fill = ViewStyler.defaultProductsColor;
                    if (speciesColorMap.containsKey(t.strip())) {
                        fill = speciesColorMap.get(t.strip());
                    }
                    CollapsibleCircle collapsibleCircle2 = new CollapsibleCircle(11.0, str, firstPosAngle[0], posAngle[1], fill, ViewStyler.defaultProductsColor);
                    Path path = ViewStyler.getDefaultReactionViewPath(collapsibleCircle.getCenterX(), collapsibleCircle.getCenterY(),
                            collapsibleCircle2.getCenterX(), collapsibleCircle2.getCenterY(), ViewStyler.defaultProductsColor);
                    this.getChildren().add(path);
                    collapsibleCircle.getChildren().add(path);
                    collapsibleCircle.addChild(collapsibleCircle2);
                    this.getChildren().addAll(collapsibleCircle2, collapsibleCircle2.getLabel());

                }
                this.getChildren().addAll(collapsibleCircle, collapsibleCircle.getLabel());
                collapsibleCircle.collapse();
            } else {
                String str = "nan";
                if (idxSpecies.contains(dnf.strip())) {
                    str = String.valueOf(idxSpecies.indexOf(dnf.strip()));
                }
                Color fill = ViewStyler.defaultProductsColor;
                if (speciesColorMap.containsKey(dnf.strip())) {
                    fill = speciesColorMap.get(dnf.strip());
                }
                CollapsibleCircle collapsibleCircle = new CollapsibleCircle(11.0, str, anchor[0] + 15.0, anchor[1], fill, ViewStyler.defaultProductsColor);
                this.getChildren().addAll(collapsibleCircle, collapsibleCircle.getLabel());
            }
        } else {
            Circle circle = ViewStyler.getDefaultCircle(2.0, ViewStyler.reactionViewArrowColor);
            circle.setTranslateX(anchor[0] + 4.0);
            circle.setTranslateY(anchor[1]);
            this.getChildren().add(circle);
        }
    }
}