package catbutler.gui.visualization.reactionView;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.util.HashSet;

public class CollapsibleCircle extends Circle {

    HashSet<Node> children = new HashSet<>();
    Label label;

    public CollapsibleCircle(double radius, String text, double x, double y, Color fill, javafx.scene.paint.Color stroke) {
        this.setCenterX(x);
        this.setCenterY(y);
        this.setRadius(radius);
        this.setFill(javafx.scene.paint.Color.LIGHTSKYBLUE);
        this.setStroke(javafx.scene.paint.Color.BLACK);
        if (fill != null) {
            this.setFill(fill);
        }
        if (stroke != null) {
            this.setStroke(stroke);
        }
        this.setStrokeWidth(1.0);

        label = new Label(text);
        label.setTextFill(javafx.scene.paint.Color.BLACK);
        label.setTranslateX(x - radius + 4 * (3 - text.length()));
        label.setTranslateY(y - radius);
        label.setMinHeight(radius * 2);
        label.setFont(new Font("Arial", label.getFont().getSize() - 1));
        label.setAlignment(Pos.CENTER);
    }

    public void collapse() {
        boolean collapsed = false;
        for (Node circle : children) {
            circle.setVisible(!circle.isVisible());
            collapsed = !circle.isVisible();

            if (circle instanceof CollapsibleCircle) {
                CollapsibleCircle c = (CollapsibleCircle) circle;
                c.getLabel().setVisible(!c.getLabel().isVisible());
            }
        }
        if (collapsed & children.size() > 0) {
            this.setStroke(Color.LIGHTGRAY);
            label.setTextFill(Color.LIGHTGRAY);
        } else {
            this.setStroke(Color.BLACK);
            label.setTextFill(Color.BLACK);
        }
    }


    public Label getLabel() {
        return label;
    }

    public HashSet<Node> getChildren() {
        return children;
    }

    public void addChild(Node node) {
        setCursor(Cursor.HAND);
        label.setCursor(Cursor.HAND);
        children.add(node);
    }

}
