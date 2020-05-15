package catbutler.gui.visualization.networkView;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.HashMap;

public class Drawer {

    SimpleNetworkRepresentation net;
    HashMap<String, Node> elements = new HashMap<>();
    int edgeCount = 0;
    int nodeCount = 0;

    public Drawer(SimpleNetworkRepresentation simpleNetworkRepresentation) {
        this.net = simpleNetworkRepresentation;
        createElements();
    }

    void createElements() {
        for (String s : net.getCoords().keySet()) {
            if (!elements.containsKey(s)) {
                Circle circle = new Circle();
                circle.setRadius(5.0);
                circle.setFill(Color.BLACK);
                circle.setStroke(Color.BLACK);
                if (net.getType().contains("Id")) {
                    Tooltip t = new Tooltip(s);
                    Tooltip.install(circle, t);
                }
                circle.setCenterX(net.coords.get(s)[0]);
                circle.setCenterY(net.coords.get(s)[1]);
                nodeCount++;
                elements.put(s, circle);
            }
        }
        if (net.getType().contains("sp")) {
            for (int i = 0; i < net.adjacencyMatrix.length; i++) {
                for (int j = 0; j < net.adjacencyMatrix[i].length; j++) {
                    if (i != j && net.adjacencyMatrix[i][j] != 0) {
                        String iStr = net.getiIdx()[i];
                        String jStr = net.getjIdx()[j];
                        if (elements.containsKey(iStr) && elements.containsKey(jStr)) {
                            boolean b1 = false;
                            boolean b2 = false;
                            if (net.getSpecies().size() > i) {
                                if (net.getCoords().containsKey(iStr)) {
                                    b1 = true;
                                }
                            }
                            if (net.getReactions().size() > j) {
                                if (net.getCoords().containsKey(jStr)) {
                                    b2 = true;
                                }
                            }
                            if (b1 && b2) {
                                Circle c1 = (Circle) elements.get(iStr);
                                c1.setFill(Color.RED);
                                Circle c2 = (Circle) elements.get(jStr);
                                Path path = new Path();
                                MoveTo moveTo = new MoveTo();
                                moveTo.setX(c1.getCenterX());
                                moveTo.setY(c1.getCenterY());
                                LineTo lineTo = new LineTo();
                                lineTo.setX(c2.getCenterX());
                                lineTo.setY(c2.getCenterY());
                                path.getElements().addAll(moveTo, lineTo);
                                path.setStrokeWidth(1.0);
                                path.setStroke(Color.BLUE);
                                path.setFill(Color.BLUE);
                                edgeCount++;
                                elements.put("path_" + iStr + "_to_" + jStr, path);
                                if (net.getType().contains("Id")) {
                                    Tooltip t = new Tooltip("path_" + iStr + "_to_" + jStr);
                                    Tooltip.install(path, t);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < net.adjacencyMatrix.length; i++) {
                for (int j = 0; j < net.adjacencyMatrix[i].length; j++) {
                    if (i != j && net.adjacencyMatrix[i][j] != 0) {
                        if (elements.containsKey(net.getReactions().get(i)) && elements.containsKey(net.getReactions().get(j))) {
                            String iStr = net.getiIdx()[i];
                            String jStr = net.getjIdx()[j];

                            Circle c1 = (Circle) elements.get(net.getReactions().get(i));
                            Circle c2 = (Circle) elements.get(net.getReactions().get(j));
                            Path path = new Path();
                            MoveTo moveTo = new MoveTo();
                            moveTo.setX(c1.getCenterX());
                            moveTo.setY(c1.getCenterY());
                            LineTo lineTo = new LineTo();
                            lineTo.setX(c2.getCenterX());
                            lineTo.setY(c2.getCenterY());
                            path.getElements().addAll(moveTo, lineTo);
                            path.setStrokeWidth(1.0);
                            path.setStroke(Color.BLUE);
                            path.setFill(Color.BLUE);
                            edgeCount++;
                            elements.put("path_" + net.getReactions().get(i) + "_to_" + net.getReactions().get(j), path);
                            if (net.getType().contains("Id")) {
                                Tooltip t = new Tooltip("path_" + net.getReactions().get(i) + "_to_" + net.getReactions().get(j));
                                Tooltip.install(path, t);
                            }
                        }
                    }
                }
            }
        }
    }

    public HashMap<String, Node> getElements() {
        return elements;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public int getNodeCount() {
        return nodeCount;
    }
}
