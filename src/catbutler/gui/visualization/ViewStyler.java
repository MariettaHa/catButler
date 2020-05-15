package catbutler.gui.visualization;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class ViewStyler {

    public static Color defaultCatalystsColor = Color.LAWNGREEN;
    public static Color defaultInhibitorsColor = Color.RED;
    public static Color defaultReactantsColor = Color.BLUE;
    public static Color defaultProductsColor = Color.ORANGE;
    public static Color reactionViewArrowColor = Color.DARKGREY;

    public static Circle getDefaultCircle(double radius, Color fill) {
        Circle circle = new Circle();
        circle.setRadius(radius);
        circle.setFill(fill);
        circle.setStroke(fill);
        circle.setStrokeWidth(1.0);
        return circle;
    }

    public static Shape[] getDefaultReactionArrow(double x, double y, double widthR, double heightR, double widthTri, double heightTri, boolean reversible) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(widthR);
        rectangle.setHeight(heightR);
        rectangle.setFill(reactionViewArrowColor);
        rectangle.setTranslateX(x);
        rectangle.setTranslateY(y);
        rectangle.setStrokeWidth(0.0);
        rectangle.setStroke(reactionViewArrowColor);

        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(0.0, 0.0, 0.0, heightTri, widthTri, (heightTri / 2.0));
        polygon.setTranslateX(x + widthR);
        polygon.setTranslateY(y - ((heightTri - heightR) / 2.0));
        polygon.setFill(reactionViewArrowColor);
        polygon.setStroke(reactionViewArrowColor);
        polygon.setStrokeWidth(1.0);

        if (reversible) {
            Polygon polygon2 = new Polygon();
            polygon2.getPoints().addAll(0.0, 0.0, 0.0, heightTri, -widthTri, (heightTri / 2.0));
            polygon2.setTranslateX(x);
            polygon2.setTranslateY(y - ((heightTri - heightR) / 2.0));
            polygon2.setStrokeWidth(1.0);
            polygon2.setFill(reactionViewArrowColor);
            polygon2.setStroke(reactionViewArrowColor);
            return new Shape[]{rectangle, polygon, polygon2};
        }
        return new Shape[]{rectangle, polygon};
    }

    public static Node[] getDefaultCatalystsPath(double x, double y, double length, double radius) {
        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        moveTo.setX(x);
        moveTo.setY(y - radius * 2.0);
        LineTo lineTo = new LineTo();
        lineTo.setX(x);
        lineTo.setY(moveTo.getY() - length);
        path.getElements().addAll(moveTo, lineTo);
        path.setStrokeWidth(2.0);
        path.setStroke(defaultCatalystsColor);
        path.setFill(defaultCatalystsColor);

        Circle circle = new Circle();
        circle.setTranslateX(x);
        circle.setTranslateY(y - radius * 2);
        circle.setRadius(radius);
        circle.setFill(defaultCatalystsColor);
        circle.setStroke(defaultCatalystsColor);
        circle.setStrokeWidth(1.0);
        return new Node[]{circle, path};
    }

    public static Node[] getDefaultInhibitorsPath(double x, double y, double spacing, double length, double hPathLength) {
        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        moveTo.setX(x);
        moveTo.setY(y + spacing);
        LineTo lineTo = new LineTo();
        lineTo.setX(x);
        lineTo.setY(moveTo.getY() + length + spacing);
        path.getElements().addAll(moveTo, lineTo);
        path.setStrokeWidth(2.0);
        path.setStroke(defaultInhibitorsColor);
        path.setFill(defaultInhibitorsColor);

        Path hPath = new Path();
        MoveTo hMoveTo = new MoveTo();
        hMoveTo.setX(x - hPathLength / 2.0);
        hMoveTo.setY(y + spacing);
        LineTo hLineTo = new LineTo();
        hLineTo.setX(x + hPathLength / 2.0);
        hLineTo.setY(y + spacing);
        hPath.getElements().addAll(hMoveTo, hLineTo);
        hPath.setStrokeWidth(2.0);
        hPath.setStroke(defaultInhibitorsColor);
        hPath.setFill(defaultInhibitorsColor);

        return new Node[]{path, hPath};
    }

    public static Path getDefaultReactionViewPath(double startX, double startY, double endX, double endY, Color fill) {
        Path path = new Path();
        MoveTo moveTo = new MoveTo();
        moveTo.setX(startX);
        moveTo.setY(startY);
        LineTo lineTo = new LineTo();
        lineTo.setX(endX);
        lineTo.setY(endY);
        path.getElements().addAll(moveTo, lineTo);
        path.setStrokeWidth(2.0);
        path.setStroke(fill);
        path.setFill(fill);
        return path;
    }
}
