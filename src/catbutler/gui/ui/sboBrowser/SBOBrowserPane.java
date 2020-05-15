package catbutler.gui.ui.sboBrowser;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SBOBrowserPane extends Pane {

    VBox vBox = new VBox();

    String selectedSBO = "SBO:0000001";
    Button accept = new Button("OK");
    Button cancel = new Button("Cancel");
    TreeView<String> glossaryTree;

    public SBOBrowserPane() {
        init();
    }

    private void init() {
        HBox hBox = new HBox();
        Label label = new Label("Filter SBO Glossary: ");
        TextField textField = new TextField();
        textField.setPromptText("Filter...");
        hBox.getChildren().addAll(label, textField);

        TreeItem<String> root = null;
        try {
            root = SBOParser.readData("src/catbutler/resources/ontologies/SBO_OBO.obo");
        } catch (IOException e) {
            e.printStackTrace();
        }
        glossaryTree = new TreeView<>(root);

        glossaryTree.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<String>>() {
            @Override
            public void onChanged(Change<? extends TreeItem<String>> change) {
                if (change.getList().size() > 0) {
                    TreeItem<String> firstMatch = change.getList().get(0);
                    if (firstMatch.getChildren().size() == 0) {
                        TreeItem<String> parent = firstMatch.getParent();
                        TreeItem<String> idStr = parent.getChildren().get(0);
                        if (idStr.getValue().startsWith("id: SBO:")) {
                            selectedSBO = idStr.getValue().replaceAll("id: ", "").strip();
                        }
                    } else {
                        if (!firstMatch.getValue().equals("SBO Glossary")) {
                            TreeItem<String> idStr = firstMatch.getChildren().get(0);
                            if (idStr.getValue().startsWith("id: SBO:")) {
                                selectedSBO = idStr.getValue().replaceAll("id: ", "").strip();
                            }
                        }
                    }
                }
            }
        });

        vBox.getChildren().addAll(hBox, glossaryTree, accept, cancel);
        this.getChildren().add(vBox);

        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                for (TreeItem<String> t : glossaryTree.getRoot().getChildren()) {
                    t.setExpanded(false);
                }
                glossaryTree.getRoot().setExpanded(false);
                glossaryTree.getSelectionModel().clearSelection();
                if (t1.startsWith("SBO:")) {
                    for (TreeItem<String> item : glossaryTree.getRoot().getChildren()) {
                        for (TreeItem<String> item2 : item.getChildren()) {
                            if (item.getValue().startsWith("id: " + t1)) {
                                glossaryTree.getSelectionModel().select(item);
                                item.setExpanded(true);
                            }
                        }
                    }
                } else {
                    if (t1.length() > 1) {
                        for (TreeItem<String> item : glossaryTree.getRoot().getChildren()) {
                            if (item.getChildren().size() > 0) {
                                String id = "";
                                TreeItem<String> selection = null;
                                boolean matches = false;
                                for (TreeItem<String> item2 : item.getChildren()) {
                                    if (item2.getValue().startsWith("id: " + t1)
                                            || item2.getValue().startsWith("name: " + t1)
                                            || item2.getValue().startsWith("def: " + t1)) {
                                        matches = true;
                                    }
                                    if (item2.getValue().startsWith("id: ")) {
                                        selection = item2;
                                    }
                                }
                                if (matches && selection != null) {
                                    glossaryTree.getSelectionModel().select(selection);
                                    item.setExpanded(true);
                                }
                            }
                        }
                    }

                }
            }
        });


    }

    public String getSelectedSBO() {
        return selectedSBO;
    }

    public void setSelectedSBO(String selectedSBO) {
        this.selectedSBO = selectedSBO;
    }

    public Button getAccept() {
        return accept;
    }

    public Button getCancel() {
        return cancel;
    }

    public TreeView<String> getGlossaryTree() {
        return glossaryTree;
    }
}
