package catbutler.gui.mainControllers;

import catbutler.gui.converter.Converter;
import catbutler.gui.visualization.layout.DavidsonHarel;
import catbutler.gui.visualization.layout.ForceAtlas;
import catbutler.gui.visualization.layout.FruchtermanReingold;
import catbutler.gui.visualization.layout.LayoutAlgorithm;
import catbutler.gui.visualization.networkView.Drawer;
import catbutler.gui.visualization.networkView.SimpleNetworkRepresentation;
import catbutler.model.DataModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class VisualizationController implements Initializable {
    @FXML
    public StackPane canvasStackPane;
    @FXML
    public AnchorPane visualizationSubAnchorPane;
    @FXML
    public Group mainGroup;
    @FXML
    public ChoiceBox graphTypeCBox;
    @FXML
    public ChoiceBox layoutCBox;
    @FXML
    public Button applyLayoutButton;
    @FXML
    public VBox toolBoxVBox;
    @FXML
    public TitledPane toolboxTitledPane;
    @FXML
    public CheckBox edgeTooltipsCheckBox;
    @FXML
    public Button clearAndApplyLayoutButton;
    @FXML
    public TextField iterationsTextField;
    @FXML
    public ImageView layoutSettingsIcon;
    @FXML
    public Label nodeCountLabel;
    @FXML
    public Label edgeCountLabel;
    @FXML
    public Label timeLabel;
    EditorViewController editorViewController;
    double[] toolboxpos = new double[]{0.0, 0.0};
    double[] canvasPos = new double[]{0.0, 0.0};
    SimpleNetworkRepresentation net;
    @FXML
    private StackPane mainCanvas;
    @FXML
    private CheckBox animateCheckBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void init() {
        initBindings();
        initListeners();
        initOther();
    }


    private void initOther() {
        graphTypeCBox.getItems().addAll("Species+Reactions", "Reactions");
        graphTypeCBox.setValue("Species+Reactions");

        layoutCBox.getItems().addAll("Fruchterman-Reingold", "ForceAtlas", "Davidson Harel");
        layoutCBox.setValue("Fruchterman-Reingold");
    }

    private void initBindings() {
        canvasStackPane.prefWidthProperty().bind(visualizationSubAnchorPane.widthProperty());
        canvasStackPane.prefHeightProperty().bind(visualizationSubAnchorPane.heightProperty());
        mainGroup.translateXProperty().bind(mainCanvas.translateXProperty());
        mainGroup.translateYProperty().bind(mainCanvas.translateYProperty());
        visualizationSubAnchorPane.setOnScroll(scrollEvent -> {
            double delta = scrollEvent.getDeltaY();
            if (scrollEvent.getDeltaY() > 0) {
                if (mainCanvas.getScaleX() < 0.5) {
                    delta *= 0.5;
                }
                double scale = mainCanvas.getScaleX() + delta / 180.0;
                if (scale < 0.1) {
                    scale = 0.1;
                } else if (scale > 10.0) {
                    scale = 10.0;
                }
                mainCanvas.setScaleX(scale);
                mainCanvas.setScaleY(scale);
            } else {
                double scale = mainCanvas.getScaleX() + delta / 180.0;
                if (scale < 0.1) {
                    scale = 0.1;
                } else if (scale > 10.0) {
                    scale = 10.0;
                }
                mainCanvas.setScaleX(scale);
                mainCanvas.setScaleY(scale);
            }
        });

    }

    private void initListeners() {
        clearAndApplyLayoutButton.setOnAction(actionEvent -> draw(Converter.lastDataModel, true));

        applyLayoutButton.setOnAction(actionEvent -> draw(Converter.lastDataModel, false));

        iterationsTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.matches("\\d+")) {
                iterationsTextField.setText(s);
            }
        });

        canvasStackPane.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isMiddleButtonDown()) {
                mainCanvas.setTranslateX(0.0);
                mainCanvas.setTranslateY(0.0);
                mainCanvas.setScaleX(1.0);
                mainCanvas.setScaleY(1.0);
            }
        });
        mainCanvas.setOnMousePressed(t -> {
            if (t.isControlDown()) {
                mainCanvas.setCursor(Cursor.MOVE);
                canvasPos[0] = t.getSceneX();
                canvasPos[1] = t.getSceneY();
            } else if (t.isMiddleButtonDown()) {
                mainCanvas.setTranslateX(0.0);
                mainCanvas.setTranslateY(0.0);
                mainCanvas.setScaleX(1.0);
                mainCanvas.setScaleY(1.0);
            }
        });

        mainCanvas.setOnMouseDragged((t) -> {
            if (t.isControlDown()) {
                mainCanvas.setCursor(Cursor.MOVE);
                double offsetX = t.getSceneX() - canvasPos[0];
                double offsetY = t.getSceneY() - canvasPos[1];
                mainCanvas.setTranslateX(mainCanvas.getTranslateX() + offsetX / mainCanvas.getScaleX());
                mainCanvas.setTranslateY(mainCanvas.getTranslateY() + offsetY / mainCanvas.getScaleY());
                canvasPos[0] = t.getSceneX();
                canvasPos[1] = t.getSceneY();
            }
        });

        mainCanvas.setOnMouseReleased(mouseEvent -> mainCanvas.setCursor(Cursor.DEFAULT));

        toolboxTitledPane.setOnMousePressed((t) -> {
            toolboxpos[0] = t.getSceneX();
            toolboxpos[1] = t.getSceneY();

            toolBoxVBox.toFront();
        });

        toolboxTitledPane.setOnMouseDragged((t) -> {
            double offsetX = t.getSceneX() - toolboxpos[0];
            double offsetY = t.getSceneY() - toolboxpos[1];
            toolBoxVBox.setTranslateX(toolBoxVBox.getTranslateX() + offsetX);
            toolBoxVBox.setTranslateY(toolBoxVBox.getTranslateY() + offsetY);

            toolboxpos[0] = t.getSceneX();
            toolboxpos[1] = t.getSceneY();
        });
    }

    public void draw(DataModel dataModel, boolean clear) {
        long start = System.currentTimeMillis();
        String type = "reactionsCount";
        if (edgeTooltipsCheckBox.isSelected()) {
            if (graphTypeCBox.getValue().equals("Species+Reactions")) {
                type = "speciesReactionsIdLst";
            } else {
                type = "reactionsIdLst";
            }
        } else {
            if (graphTypeCBox.getValue().equals("Species+Reactions")) {
                type = "speciesReactionsCount";
            }
        }

        if (clear || net == null) {
            net = new SimpleNetworkRepresentation(dataModel, type);
        }

        LayoutAlgorithm layoutAlgorithm = null;
        switch (layoutCBox.getSelectionModel().getSelectedItem().toString()) {
            case "ForceAtlas":
                layoutAlgorithm = new ForceAtlas(net);
                break;
            case "Fruchterman-Reingold":
                layoutAlgorithm = new FruchtermanReingold(net);
                break;
            case "Davidson Harel":
                layoutAlgorithm = new DavidsonHarel(net);
                break;
        }

        animateCheckBox.setDisable(true);
        if (animateCheckBox.isSelected()) {

        } else {
            if (layoutAlgorithm != null) {
                for (int i = 0; i < Integer.parseInt(iterationsTextField.getText()); i++) {
                    layoutAlgorithm.apply();
                }
            }
        }

        net = layoutAlgorithm.getNet();
        Drawer drawer = new Drawer(net);
        mainGroup.getChildren().clear();
        for (String s : drawer.getElements().keySet()) {
            mainGroup.getChildren().add(drawer.getElements().get(s));
        }

        nodeCountLabel.setText(String.valueOf(drawer.getNodeCount()));
        edgeCountLabel.setText(String.valueOf(drawer.getEdgeCount()));
        timeLabel.setText(String.valueOf((System.currentTimeMillis() - start) / 1000.0));
    }

    public void setEditorViewController(EditorViewController editorViewController) {
        this.editorViewController = editorViewController;
    }


}
