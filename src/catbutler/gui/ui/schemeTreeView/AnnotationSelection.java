package catbutler.gui.ui.schemeTreeView;

import catbutler.gui.ui.QuickInfoPopup;
import catbutler.utils.Misc;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AnnotationSelection extends StackPane {

    TextField searchBar = new TextField();
    Button b = new Button("OK");
    Button b2 = new Button("Cancel");
    ListView<String> resultList = new ListView<>();

    public AnnotationSelection() {
        init();
    }

    private void init() {
        resultList.getItems().addAll(Misc.getValidSchemeAnnotations());
        Label label = new Label("Choose Annotation Type:");
        searchBar.setPromptText("Search...");
        resultList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        initListener();
        VBox vbox = new VBox();
        ScrollPane pane = new ScrollPane(resultList);
        Pane pane2 = new Pane();
        pane2.setPrefHeight(30);
        searchBar.prefWidthProperty().bind(this.prefWidthProperty());
        resultList.prefWidthProperty().bind(this.prefWidthProperty());
        this.getChildren().add(new VBox(label, new Pane(), searchBar, pane, new HBox(b, b2)));
    }

    public ListView<String> getResultList() {
        return resultList;
    }

    public Button getB() {
        return b;
    }

    public Button getB2() {
        return b2;
    }

    private void initListener() {
        resultList.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<Object>() {
                    public void changed(ObservableValue<? extends Object> observable,
                                        Object oldValue, Object newValue) {
                        if (!resultList.getSelectionModel().isEmpty()) {
                            if (!resultList.getSelectionModel().getSelectedItem().equals(searchBar.getText())) {
                                searchBar.setText(resultList.getSelectionModel().getSelectedItem());
                            }
                        }
                    }
                });

        searchBar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                boolean bb = true;
                if (!resultList.getSelectionModel().isEmpty()) {
                    if (resultList.getSelectionModel().getSelectedItem().equals(newValue)) {
                        bb = false;
                    }
                }
                if (bb) {
                    resultList.getItems().clear();
                    try {
                        Thread.sleep(90);
                    } catch (InterruptedException e) {
                        new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                    }
                    String s = newValue;
                    for (String p : Misc.getValidSchemeAnnotations()) {
                        if (p.startsWith(s)) {
                            if (!resultList.getItems().contains(p)) {
                                resultList.getItems().add(p);
                            }
                        }
                    }
                }
            }
        });

    }


}
