package catbutler.gui.ui.schemeTreeView;

import catbutler.gui.ui.QuickInfoPopup;
import catbutler.utils.Misc;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import java.util.ArrayList;

public class SchemeTreeView extends TreeTableView {
    File file = null;
    ObservableList<TreeItem<SchemeTreeEntry>> items = FXCollections.observableArrayList();
    Document document = null;
    SimpleBooleanProperty changeListener = new SimpleBooleanProperty();

    public SchemeTreeView(File xmlFile) {
        init();
        try {
            addData(xmlFile);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            new QuickInfoPopup("Error!", e.getMessage(), -1, e);
        }
    }

    private void initListeners() {
        changeListenerProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (t1) {
                    changeListenerProperty().setValue(false);
                }
            }
        });

        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.DELETE) {
                    TreeItem<SchemeTreeEntry> item = (TreeItem<SchemeTreeEntry>) SchemeTreeView.this.getSelectionModel().getSelectedItem();
                    if (item != null) {
                        if (!SchemeTreeView.this.getRoot().equals(item)) {
                            if (item.getChildren().size() > 0) {
                                ArrayList<TreeItem<SchemeTreeEntry>> children = new ArrayList<>(item.getChildren());

                                item.getChildren().clear();
                                item.getParent().getChildren().addAll(children);
                            }
                            items.remove(item);
                            TreeItem<SchemeTreeEntry> parent = item.getParent();
                            parent.getChildren().remove(item);
                            changeListener.setValue(true);
                        }
                    }

                } else if (keyEvent.getCode().equals(KeyCode.INSERT)) {
                    Object selectedItem = SchemeTreeView.this.getSelectionModel().getSelectedItem();
                    TreeItem<SchemeTreeEntry> parentItem = (TreeItem<SchemeTreeEntry>) selectedItem;
                    TreeItem<SchemeTreeEntry> newItem = new TreeItem<SchemeTreeEntry>(new SchemeTreeEntry(parentItem.getValue().getId() + "_" + parentItem.getChildren().size()));
                    if (parentItem == null) {
                        parentItem = SchemeTreeView.this.getRoot();
                    }
                    addItem(parentItem, newItem);
                    changeListener.setValue(true);

                }
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Insert Row");
        MenuItem menuItem2 = new MenuItem("Remove Selected");
        MenuItem menuItem3 = new MenuItem("Add Row");

        MenuItem menuItem4 = new MenuItem("Select Annotation");
        contextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3, new SeparatorMenuItem(), menuItem4);

        menuItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Object selectedItem = SchemeTreeView.this.getSelectionModel().getSelectedItem();
                TreeItem<SchemeTreeEntry> parentItem = (TreeItem<SchemeTreeEntry>) selectedItem;
                TreeItem<SchemeTreeEntry> newItem = new TreeItem<SchemeTreeEntry>(new SchemeTreeEntry(parentItem.getValue().getId() + "_" + parentItem.getChildren().size()));
                if (parentItem == null) {
                    parentItem = SchemeTreeView.this.getRoot();
                }
                addItem(parentItem, newItem);
                changeListener.setValue(true);

            }
        });
        menuItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TreeItem<SchemeTreeEntry> item = (TreeItem<SchemeTreeEntry>) SchemeTreeView.this.getSelectionModel().getSelectedItem();
                if (item != null) {
                    if (!SchemeTreeView.this.getRoot().equals(item)) {
                        if (item.getChildren().size() > 0) {
                            ArrayList<TreeItem<SchemeTreeEntry>> children = new ArrayList<>(item.getChildren());

                            item.getChildren().clear();
                            item.getParent().getChildren().addAll(children);
                        }
                        items.remove(item);
                        TreeItem<SchemeTreeEntry> parent = item.getParent();
                        parent.getChildren().remove(item);
                        changeListener.setValue(true);
                    }
                }
            }
        });

        menuItem3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TreeItem<SchemeTreeEntry> root = SchemeTreeView.this.getRoot();
                TreeItem<SchemeTreeEntry> newItem = new TreeItem<SchemeTreeEntry>(new SchemeTreeEntry(root.getValue().getId() + "_" + root.getChildren().size()));
                root.getChildren().add(root.getChildren().size(), newItem);
                changeListener.setValue(true);
            }
        });


        this.setContextMenu(contextMenu);
    }

    private void init() {
        setEditable(true);

        TreeTableColumn<SchemeTreeEntry, String> grIdCol //
                = new TreeTableColumn<SchemeTreeEntry, String>("grId");
        grIdCol.setPrefWidth(40.0);
        TreeTableColumn<SchemeTreeEntry, String> idCol//
                = new TreeTableColumn<SchemeTreeEntry, String>("id");
        idCol.setPrefWidth(155.0);
        TreeTableColumn<SchemeTreeEntry, String> regexCol //
                = new TreeTableColumn<SchemeTreeEntry, String>("regex");
        regexCol.setPrefWidth(200.0);
        TreeTableColumn<SchemeTreeEntry, String> annotationCol //
                = new TreeTableColumn<SchemeTreeEntry, String>("annotation");
        annotationCol.setPrefWidth(80.0);
        TreeTableColumn<SchemeTreeEntry, String> valueCol //
                = new TreeTableColumn<SchemeTreeEntry, String>("value");
        valueCol.setPrefWidth(40.0);
        TreeTableColumn<SchemeTreeEntry, String> fileEndingCol //
                = new TreeTableColumn<SchemeTreeEntry, String>("fileEnding");
        fileEndingCol.setPrefWidth(75.0);
        TreeTableColumn<SchemeTreeEntry, String> schemeNameCol //
                = new TreeTableColumn<SchemeTreeEntry, String>("schemeName");
        schemeNameCol.setPrefWidth(80.0);

        idCol.setCellValueFactory(new TreeItemPropertyValueFactory<SchemeTreeEntry, String>("id"));
        grIdCol.setCellValueFactory(new TreeItemPropertyValueFactory<SchemeTreeEntry, String>("grId"));
        regexCol.setCellValueFactory(new TreeItemPropertyValueFactory<SchemeTreeEntry, String>("regex"));
        annotationCol.setCellValueFactory(new TreeItemPropertyValueFactory<SchemeTreeEntry, String>("annotation"));
        valueCol.setCellValueFactory(new TreeItemPropertyValueFactory<SchemeTreeEntry, String>("value"));
        fileEndingCol.setCellValueFactory(new TreeItemPropertyValueFactory<SchemeTreeEntry, String>("fileEnding"));
        schemeNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<SchemeTreeEntry, String>("schemeName"));

        this.getColumns().addAll(idCol, grIdCol, regexCol, annotationCol, valueCol, fileEndingCol, schemeNameCol);
    }

    private void addItem(TreeItem<SchemeTreeEntry> parent, TreeItem<SchemeTreeEntry> newItem) {
        parent.getChildren().add(newItem);
        items.add(newItem);
    }

    private void addData(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        if (!Misc.isValidParsingScheme(xmlFile, false)) {
            xmlFile = new File("src/catbutler/resources/exampleData/wizRex/emptyScheme.xml");
        }

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        this.document = docBuilder.parse(xmlFile);
        parseToItem(this.document.getDocumentElement(), null);
        file = xmlFile;
    }


    public void parseToItem(Node node, TreeItem<SchemeTreeEntry> parent) {
        NamedNodeMap nodeMap = node.getAttributes();
        SchemeTreeEntry newEntry = new SchemeTreeEntry(nodeMap);

        for (int i = 0; i < nodeMap.getLength(); i++) {
            String attName = nodeMap.item(i).getNodeName();
            String value = nodeMap.item(i).getNodeValue().replaceAll("LEFTTAG", "<").replaceAll("RIGHTTAG", ">");
            switch (attName) {
                case "id":
                    newEntry.setId(value);
                    break;
                case "grId":
                    newEntry.setGrId(value);
                    break;
                case "annotation":
                    newEntry.setAnnotation(value);
                    break;
                case "value":
                    newEntry.setValue(value);
                    break;
                case "regex":
                    newEntry.setRegex(value);
                    break;
                case "listSeparator":
                    newEntry.setListSeparator(value);
                    break;
                case "fileEnding":
                    newEntry.setFileEnding(value);
                    break;
                case "schemeName":
                    newEntry.setSchemeName(value);
                    break;
                default:
                    break;

            }
        }
        NodeList nodeList = node.getChildNodes();
        TreeItem<SchemeTreeEntry> newItem = new TreeItem<>(newEntry);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            NodeList nodeList1 = currentNode.getChildNodes();
            if (currentNode.getNodeName().matches("group|doc")) {
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    parseToItem(currentNode, newItem);
                }
            } else {
                for (int j = 0; j < nodeList1.getLength(); j++) {
                    Node currentGrandNode = nodeList1.item(j);
                    if (currentGrandNode.getNodeType() == Node.ELEMENT_NODE) {
                        parseToItem(currentGrandNode, newItem);
                    }
                }
            }

        }
        if (parent != null) {
            parent.getChildren().add(newItem);
            ArrayList<TreeItem> tmpL = new ArrayList<>();
            tmpL.add(newItem);
        } else {
            this.setRoot(newItem);
        }
    }

    public File getFile() {
        return file;
    }

    public ObservableList<TreeItem<SchemeTreeEntry>> getItems() {
        return items;
    }

    public Document getDocument() {
        return document;
    }

    public SimpleBooleanProperty changeListenerProperty() {
        return changeListener;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        if (this.getRoot() != null) {
            stringBuilder = parseItemToString(this.getRoot(), stringBuilder, 0);
        }
        return stringBuilder.toString();
    }

    private StringBuilder parseItemToString(TreeItem<SchemeTreeEntry> item, StringBuilder stringBuilder, int level) {
        SchemeTreeEntry entry = item.getValue();
        if (entry.getId().equals("doc")) {
            stringBuilder.append("\t".repeat(level) + "<doc");
            stringBuilder.append((" fileEnding=\"" + entry.getFileEnding() + "\"").repeat(entry.getFileEnding().isEmpty() ? 1 : 0));
            stringBuilder.append(" id=\"").append(entry.getId()).append("\"");
            stringBuilder.append((" schemeName=\"" + entry.getSchemeName() + "\"").repeat(entry.getSchemeName().isEmpty() ? 1 : 0));
            stringBuilder.append(" regex=\"").append(entry.getRegex()).append("\"").append(">\n");
        } else {
            stringBuilder.append("\t".repeat(level)).append("<group");
            stringBuilder.append(" grId=\"").append(entry.getGrId()).append("\"");
            stringBuilder.append((" id=\"" + entry.getId() + "\"").repeat(entry.getId().isEmpty() ? 1 : 0));
            stringBuilder.append((" annotation=\"" + entry.getAnnotation() + "\"").repeat(entry.getAnnotation().isEmpty() ? 1 : 0));
            stringBuilder.append((" regex=\"" + entry.getRegex() + "\"").repeat(entry.getRegex().isEmpty() ? 1 : 0));
            stringBuilder.append((" listSep=\"" + entry.getListSeparator() + "\"").repeat(entry.getListSeparator().isEmpty() ? 1 : 0));
            stringBuilder.append((" value=\"" + entry.getValue() + "\"").repeat(entry.getValue().isEmpty() ? 1 : 0));
            stringBuilder.append(">\n");
        }
        if (item.getChildren().size() > 0) {
            stringBuilder.append("\t".repeat(level + 1)).append("<match>\n");
        }
        for (Object i : item.getChildren()) {
            TreeItem<SchemeTreeEntry> ii = (TreeItem<SchemeTreeEntry>) i;
            stringBuilder = parseItemToString(ii, stringBuilder, level + 2);
        }
        stringBuilder.append("\n".repeat(item.getChildren().size() > 0 ? 1 : 0)).append("</group>\n");
        if (item.getChildren().size() > 0) {
            stringBuilder.append("\t".repeat(level + 1)).append("</match>\n");
        }
        return stringBuilder;
    }
}
