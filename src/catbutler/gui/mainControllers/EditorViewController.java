package catbutler.gui.mainControllers;

import catbutler.gui.App;
import catbutler.gui.converter.Converter;
import catbutler.gui.converter.FormatValidator;
import catbutler.gui.ui.QuickInfoPopup;
import catbutler.gui.ui.dataTableView.*;
import catbutler.gui.ui.schemeTreeView.AnnotationSelection;
import catbutler.gui.ui.schemeTreeView.SchemeTreeEntry;
import catbutler.gui.ui.schemeTreeView.SchemeTreeView;
import catbutler.io.documents.CRSDoc;
import catbutler.io.documents.DBDoc;
import catbutler.io.parser.CustomParser;
import catbutler.io.writer.DBWriter;
import catbutler.model.DataModel;
import catbutler.utils.Misc;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;

import static catbutler.utils.Misc.writeTmp;

public class EditorViewController implements Initializable {

    public static boolean correctInvalidStrings = false;
    @FXML
    public VBox schemeViewVbox;
    @FXML
    public VirtualizedScrollPane codeAreaPane;
    @FXML
    public Tab xmlCodeTab;
    @FXML
    public Tab treeViewTab;
    @FXML
    public TabPane formatTabPane;
    @FXML
    public Button loadFileLeftButton;
    @FXML
    public Button loadSchemeFileEditorButton;
    @FXML
    public Button saveSchemeFileEditorButton;
    @FXML
    public VirtualizedScrollPane leftCodeAreaPane;
    @FXML
    public Button loadLeftSchemeButton;
    @FXML
    public CodeArea leftConvertArea;
    @FXML
    public CodeArea rightConvertArea;
    @FXML
    public Button loadLeftConvertFile;
    @FXML
    public Button saveOutputFileButton;
    @FXML
    public TextField leftSchemePath;
    @FXML
    public ChoiceBox leftDocTypeCBox;
    @FXML
    public ChoiceBox rightDocTypeCBox;
    @FXML
    public Button convertButton;
    @FXML
    public CheckBox correctIdsCheckBox;
    @FXML
    public Button converterLeftValidationButton;
    @FXML
    public Label editorValidLabel;
    @FXML
    public Button leftEditorValidationButton;
    @FXML
    public Tab converterTab;
    @FXML
    public Tab schemeEditorTab;
    @FXML
    public ScrollPane documentTableScrollPane;
    @FXML
    public ScrollPane speciesTableScrollPane;
    @FXML
    public ScrollPane reactionTableScrollPane;
    @FXML
    public TextArea createDocCodeArea;
    @FXML
    public StackPane dataViewStackPane;
    @FXML
    public TabPane dataViewTabPane;
    @FXML
    public Button maximizeDataViewButton;
    @FXML
    public VBox dataViewBox;
    @FXML
    public TabPane mainTabPane;
    @FXML
    public Tab wizardTab;
    @FXML
    public AnchorPane leftCodeAnchorPane;
    @FXML
    public AnchorPane xmlCodeAreaAnchorPane;
    @FXML
    public TextField leftInputPath;
    @FXML
    public Label converterValidLabel;
    @FXML
    public TextField queryTextField;
    @FXML
    public Tab visualizationTab;
    @FXML
    public Button runQueryButton;
    @FXML
    public ImageView moveRowUp;
    @FXML
    public ImageView moveRowDown;
    @FXML
    public MenuBar menuBar;
    @FXML
    public Label selectedCount;
    @FXML
    public Tab documentTab;
    @FXML
    public CodeArea xmlCodeArea;
    @FXML
    public CodeArea leftCodeArea;
    SchemeTreeView schemeTreeView;

    File editorSchemeFile;
    File leftSchemeFile;
    File currentDocFile;
    File leftConvertFile;
    File editorInputFile;
    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;

    private Stage stage;
    private VisualizationController visualizationController;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSchemeView();
        initDataTableViews();
        createDocCodeArea.setText(Converter.convertDataModelTo(Converter.lastDataModel, "crs"));
        initXMLCodeAreas();
        initListeners();
        initButtons();
        initOther();
        initVisualizationView();
        initMenuBar();
    }

    private void initMenuBar() {
    }

    private void initVisualizationView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/view/visualizationView.fxml"));
            visualizationTab.setContent(fxmlLoader.load());
            visualizationController = fxmlLoader.getController();
            visualizationController.setEditorViewController(this);
            visualizationController.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initOther() {
        leftDocTypeCBox.setItems(FXCollections.observableList(Arrays.asList("SBML", "CRS", "WIM", "DB", "custom")));
        leftDocTypeCBox.getSelectionModel().select("CRS");
        rightDocTypeCBox.setItems(FXCollections.observableList(Arrays.asList("SBML", "CRS", "WIM", "DB")));
        rightDocTypeCBox.getSelectionModel().select("CRS");

        correctIdsCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                correctInvalidStrings = t1;
            }
        });

        leftDocTypeCBox.valueProperty().addListener((observableValue, o, t1) -> {
            converterValidLabel.setText("");
            if (t1.toString().toLowerCase().equals("db")) {
                converterLeftValidationButton.setVisible(false);
            } else {
                converterLeftValidationButton.setVisible(true);
            }
        });
    }

    private void initButtons() {
        moveRowUp.setOnMouseClicked(mouseEvent -> {
            Tab currentTab = dataViewTabPane.getSelectionModel().getSelectedItem();
            if (currentTab.getText().equals("Species") && ((TableView) speciesTableScrollPane.getContent()).getSelectionModel().getSelectedItems().size() > 0) {
                SpeciesTableView speciesTableView = (SpeciesTableView) speciesTableScrollPane.getContent();
                speciesTableView.moveSelectionUp();
            } else if (currentTab.getText().equals("Reactions")) {
                ReactionTableView reactionTableView = (ReactionTableView) reactionTableScrollPane.getContent();
                reactionTableView.moveSelectionUp();
            }
        });

        moveRowDown.setOnMouseClicked(mouseEvent -> {
            Tab currentTab = dataViewTabPane.getSelectionModel().getSelectedItem();
            if (currentTab.getText().equals("Species") && ((TableView) speciesTableScrollPane.getContent()).getSelectionModel().getSelectedItems().size() > 0) {
                SpeciesTableView speciesTableView = (SpeciesTableView) speciesTableScrollPane.getContent();
                speciesTableView.moveSelectionDown();
            } else if (currentTab.getText().equals("Reactions")) {
                ReactionTableView reactionTableView = (ReactionTableView) reactionTableScrollPane.getContent();
                reactionTableView.moveSelectionDown();
            }
        });

        runQueryButton.setOnAction(actionEvent -> {
            try {
                query(queryTextField.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        saveOutputFileButton.setOnAction(actionEvent -> saveContent(rightConvertArea));

        convertButton.setOnAction(actionEvent -> convert());

        loadFileLeftButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    editorInputFile = file;
                    leftCodeArea.replaceText(0, leftCodeArea.getText().length(), new String(Files.readAllBytes(file.toPath())));
                } catch (IOException e) {
                    new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                }
            }
        });

        loadLeftConvertFile.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    leftConvertFile = file;
                    leftInputPath.setText(file.getPath());
                    leftConvertArea.replaceText(0, leftConvertArea.getText().length(), new String(Files.readAllBytes(file.toPath())));
                } catch (IOException e) {
                    new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                }
            }
        });


        loadLeftSchemeButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                leftSchemeFile = file;
                leftSchemePath.setText(file.getPath());
            } else {
                new QuickInfoPopup("Error!", "File not found or does not exist.", -1, null);
            }
        });


        maximizeDataViewButton.setOnAction(actionEvent -> {
            if ((!dataViewBox.isVisible())) {
                dataViewBox.setVisible(true);
                dataViewBox.setTranslateY(dataViewBox.getTranslateY() - dataViewBox.getHeight());
                maximizeDataViewButton.setStyle("-fx-background-color: #696969;  -fx-background-radius: 0");
            } else {
                dataViewBox.setVisible(false);
                dataViewBox.setTranslateY(dataViewBox.getTranslateY() + dataViewBox.getHeight());
                maximizeDataViewButton.setStyle("-fx-background-color: #dcdcdc;  -fx-background-radius: 0");
            }
        });

        leftEditorValidationButton.setOnAction(actionEvent -> {
            boolean valid = false;
            String tmpFilePath = "src/catbutler/resources/utilFiles/tmpInputFile.txt";
            boolean couldCreateFile = Misc.writeTmp(leftCodeArea.getText(), tmpFilePath);
            String tmpSchemeFilePath = "src/catbutler/resources/utilFiles/tmpInputFile_Scheme.xml";
            boolean couldCreateSchemeFile = Misc.writeTmp(xmlCodeArea.getText(), tmpSchemeFilePath);

            if (leftCodeArea.getText().length() == 0) {
                editorValidLabel.setText("Empty Custom File");
                editorValidLabel.setTextFill(Color.web("#b1b1b1"));

            } else if (couldCreateFile && couldCreateSchemeFile && leftCodeArea.getText().length() > 0 && xmlCodeArea.getText().length() > 0) {
                File input = new File(tmpFilePath);
                File scheme = new File(tmpSchemeFilePath);
                CustomParser customParser = new CustomParser(input, scheme);
                Converter.lastDataModel = customParser.getDataModel();
                if (customParser.getDataModel().notEmpty() && !customParser.isInvalid.getValue()) {
                    valid = true;
                } else {
                    valid = false;
                }

                if (valid) {
                    editorValidLabel.setText("Valid Custom File");
                    editorValidLabel.setTextFill(Color.web("#abc893"));
                    update(Converter.lastDataModel);

                } else {
                    editorValidLabel.setText("Invalid Custom File");
                    editorValidLabel.setTextFill(Color.web("#c893ab"));
                }
            }
        });


        converterLeftValidationButton.setOnAction(actionEvent -> {
            String fromType = leftDocTypeCBox.getValue().toString().toLowerCase();

            boolean valid = false;
            String tmpFilePath = "src/catbutler/resources/utilFiles/tmpInputFile." + Misc.getFileEnding(fromType);
            boolean couldCreateFile = Misc.writeTmp(leftConvertArea.getText(), tmpFilePath);
            String tmpSchemeFilePath = "src/catbutler/resources/utilFiles/tmpInputFile_Scheme.xml";
            leftSchemeFile = new File(leftSchemePath.getText());
            boolean schemeFileExists = leftSchemeFile.exists();//Misc.writeTmp(xmlCodeArea.getText(), tmpSchemeFilePath);
            String schemeStr = "";
            if (schemeFileExists) {
                try {
                    schemeStr = FileUtils.readFileToString(leftSchemeFile, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (leftConvertArea.getText().length() == 0) {
                converterValidLabel.setText("Empty Custom File");
                converterValidLabel.setTextFill(Color.web("#b1b1b1"));

            } else if (couldCreateFile && leftConvertArea.getText().length() > 0) {
                File input = new File(tmpFilePath);

                if (fromType.equals("custom")) {
                    if (schemeFileExists && schemeStr.length() > 0) {
                        CustomParser customParser = new CustomParser(input, leftSchemeFile);
                        if (customParser.getDataModel().notEmpty() && !customParser.isInvalid.getValue()) {
                            valid = true;
                            Converter.lastDataModel = customParser.getDataModel();
                        } else {
                            valid = false;
                        }
                    }
                } else {
                    if (FormatValidator.validateFile(input, leftSchemeFile, fromType)) {
                        valid = true;
                    } else {
                        valid = false;
                    }
                }

                if (valid) {
                    converterValidLabel.setText("Valid Custom File");
                    converterValidLabel.setTextFill(Color.web("#abc893"));
                    convertButton.setDisable(false);
                    update(Converter.lastDataModel);
                } else {
                    converterValidLabel.setText("Invalid Custom File");
                    converterValidLabel.setTextFill(Color.web("#c893ab"));
                }
            }
        });

        loadSchemeFileEditorButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            formatTabPane.getSelectionModel().select(xmlCodeTab);

            if (file != null) {
                editorSchemeFile = file;
                if (file.exists()) {
                    try {
                        xmlCodeArea.replaceText(0, xmlCodeArea.getLength(), FileUtils.readFileToString(file, "UTF-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        saveSchemeFileEditorButton.setOnAction(actionEvent -> saveContent(xmlCodeArea));
    }

    private void saveContent(CodeArea area) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            PrintWriter writer;
            try {
                writer = new PrintWriter(file);
                writer.println(area.getText());
                writer.close();
            } catch (FileNotFoundException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
        }
    }

    private void setupCurrentDataModelListener() {
        Converter.lastDataModel.changedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                boolean correctInvalidStringsOld = correctInvalidStrings;
                correctInvalidStrings = true;
                String convertResult = "";
                DocumentCreationSettingsPane settingsPane = (DocumentCreationSettingsPane) documentTableScrollPane.getContent();
                switch (settingsPane.getFormatType()) {
                    case "CRS":
                    case "WIM":
                    case "SBML":
                    case "DB":
                        convertResult = Converter.convertDataModelTo(Converter.lastDataModel, settingsPane.getFormatType().toLowerCase());
                    default:
                        break;
                }
                createDocCodeArea.replaceText(0, createDocCodeArea.getLength(), convertResult);
                Converter.lastDataModel.changedProperty().set(false);
                correctInvalidStrings = correctInvalidStringsOld;
            }
        });
    }

    private void initListeners() {
        xmlCodeArea.textProperty().addListener((observableValue, s, t1) -> {
            if (writeTmp(t1, "src/catbutler/resources/utilFiles/tmpFile.xml")) {
                SchemeTreeView schemeTreeView2 = new SchemeTreeView(new File("src/catbutler/resources/utilFiles/tmpFile.xml"));
                if (schemeTreeView2.getRoot() != null) {
                    schemeTreeView2.getRoot().setExpanded(true);
                    schemeTreeView = schemeTreeView2;
                    schemeViewVbox.getChildren().clear();
                    schemeViewVbox.getChildren().add(schemeTreeView);
                    for (Object o : schemeTreeView.getRoot().getChildren()) {
                        TreeItem<SchemeTreeEntry> t = (TreeItem<SchemeTreeEntry>) o;
                        t.setExpanded(true);
                    }
                    schemeTreeView.setPrefHeight(schemeViewVbox.getPrefHeight());
                    schemeTreeView.prefWidthProperty().bind(schemeViewVbox.widthProperty());
                }
            }
        });
    }

    private void query(String query) {
        Tab currentTab = (dataViewTabPane.getSelectionModel().getSelectedItem());
        DBDoc dbDoc = new DBDoc(Converter.lastDataModel);
        DBWriter writer = new DBWriter(dbDoc);
        String tmpDBPath = "src/catbutler/resources/utilFiles/tmpDB.db";
        writer.write(tmpDBPath);
        HashSet<String> selectIds = dbDoc.queryDB(query, tmpDBPath);
        if (currentTab.getText().equals("Species")) {
            SpeciesTableView speciesTableView = (SpeciesTableView) speciesTableScrollPane.getContent();
            speciesTableView.selectById(selectIds);
        } else if (currentTab.getText().equals("Reactions")) {
            ReactionTableView reactionTableView = (ReactionTableView) reactionTableScrollPane.getContent();
            reactionTableView.selectById(selectIds);
        }
    }

    private void initSchemeView() {

        schemeTreeView = new SchemeTreeView(new File("src/catbutler/resources/schemes/wimParsingScheme.xml"));
        if (schemeTreeView.getRoot() != null) {
            schemeTreeView.getRoot().setExpanded(true);
            schemeViewVbox.getChildren().add(schemeTreeView);
            for (Object s : schemeTreeView.getRoot().getChildren()) {
                TreeItem<SchemeTreeEntry> t = (TreeItem<SchemeTreeEntry>) s;
                t.setExpanded(true);
            }
            schemeTreeView.setPrefHeight(schemeViewVbox.getPrefHeight());
            schemeTreeView.prefWidthProperty().bind(schemeViewVbox.widthProperty());
        }

        schemeTreeView.getItems().addListener((ListChangeListener<TreeItem<SchemeTreeEntry>>) change -> {
            if (schemeTreeView.getRoot() != null) {
                try {
                    xmlCodeArea.replaceText(0, xmlCodeArea.getLength(), FileUtils.readFileToString(schemeTreeView.getFile(), "UTF-8"));
                } catch (IOException e) {
                    new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                }
            }
        });
    }

    public void initDataTableViews() {
        CRSDoc crsDoc = new CRSDoc(new File("src/catbutler/resources/examples/crs/example-1.crs").toPath());
        crsDoc.readIn();
        Converter.lastDataModel = crsDoc.getDataModel();
        DocumentCreationSettingsPane docPane = new DocumentCreationSettingsPane(Converter.lastDataModel, this);
        SpeciesTableView speciesTable = new SpeciesTableView(Converter.lastDataModel, this);
        ReactionTableView reactionTable = new ReactionTableView(Converter.lastDataModel, this);
        speciesTableScrollPane.setContent(speciesTable);
        documentTableScrollPane.setContent(docPane);
        reactionTableScrollPane.setContent(reactionTable);
        speciesTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<SpeciesTableEntry>() {
            @Override
            public void onChanged(Change<? extends SpeciesTableEntry> change) {
                selectedCount.setText(speciesTable.getSelectionModel().getSelectedItems().size() + " species "
                        + "(all) ".repeat((speciesTable.getSelectionModel().getSelectedItems().size() == speciesTable.getItems().size()) ? 1 : 0) + "rows selected.");
            }
        });
        reactionTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<ReactionTableEntry>() {
            @Override
            public void onChanged(Change<? extends ReactionTableEntry> change) {
                selectedCount.setText(reactionTable.getSelectionModel().getSelectedItems().size() + " reactions "
                        + "(all) ".repeat((reactionTable.getSelectionModel().getSelectedItems().size() == reactionTable.getItems().size()) ? 1 : 0) + "rows selected.");
            }
        });

        Converter.lastDataModel.changedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (t1) {
                    boolean correctInvalidStringsOld = correctInvalidStrings;
                    correctInvalidStrings = true;
                    String convertResult = "";
                    switch (docPane.getFormatType()) {
                        case "CRS":
                        case "WIM":
                        case "SBML":
                        case "DB":
                            convertResult = Converter.convertDataModelTo(Converter.lastDataModel, docPane.getFormatType().toLowerCase());
                        default:
                            break;
                    }
                    createDocCodeArea.replaceText(0, createDocCodeArea.getLength(), convertResult);
                    Converter.lastDataModel.changedProperty().set(false);
                    correctInvalidStrings = correctInvalidStringsOld;
                }
            }
        });
    }

    private void initXMLCodeAreas() {
        if (schemeTreeView.getRoot() != null) {
            try {
                xmlCodeArea.replaceText(0, xmlCodeArea.getLength(), FileUtils.readFileToString(schemeTreeView.getFile(), "UTF-8"));
            } catch (IOException e) {
                new QuickInfoPopup("Error!", e.getMessage(), -1, e);
            }
            schemeTreeView.changeListenerProperty().addListener((observableValue, aBoolean, t1) -> {
                if (t1) {
                    try {
                        xmlCodeArea.replaceText(0, xmlCodeArea.getLength(), FileUtils.readFileToString(schemeTreeView.getFile(), "UTF-8"));
                    } catch (IOException e) {
                        new QuickInfoPopup("Error!", e.getMessage(), -1, e);
                    }
                    schemeTreeView.changeListenerProperty().setValue(false);
                }
            });
            ContextMenu contextMenu = xmlCodeArea.getContextMenu();
            if (contextMenu == null) {
                contextMenu = new ContextMenu();
                xmlCodeArea.setContextMenu(contextMenu);
            }
            MenuItem menuItem = new MenuItem("Insert annotation from browser...");
            contextMenu.getItems().add(menuItem);
            menuItem.setOnAction(actionEvent -> {
                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(new Stage());
                VBox dialogVbox = new VBox(20);
                AnnotationSelection aSel = new AnnotationSelection();
                dialogVbox.getChildren().add(aSel);
                Scene dialogScene = new Scene(dialogVbox, 300, 525);
                dialog.setScene(dialogScene);
                dialog.show();

                aSel.getB().setOnAction(actionEvent1 -> {
                    if (!aSel.getResultList().getSelectionModel().isEmpty()) {
                        String s = aSel.getResultList().getSelectionModel().getSelectedItem();
                        xmlCodeArea.replaceText(xmlCodeArea.getCaretPosition(), xmlCodeArea.getCaretPosition(), s);
                    }
                    dialog.close();
                });
                aSel.getB2().setOnAction(actionEvent12 -> dialog.close());
            });
        }
    }

    public void convert() {
        if (!convertButton.isDisable()) {

            String fromType = leftDocTypeCBox.getValue().toString().toLowerCase();
            String toType = rightDocTypeCBox.getValue().toString().toLowerCase();
            if (fromType.length() == 0) {
                fromType = "crs";
            }
            if (toType.length() == 0) {
                toType = "crs";
            }

            String outputStr = "";
            if (leftConvertArea.getText().length() == 0 && leftConvertFile != null) {
                if (fromType.equals("custom") && new File(leftSchemePath.getText()).exists()) {
                    CustomParser customParser = new CustomParser(leftConvertFile, new File(leftSchemePath.getText()));
                    outputStr = Converter.convertDataModelTo(customParser.getDataModel(), toType);
                } else {
                    outputStr = Converter.convertToString(leftConvertFile.toPath(), fromType, toType);

                }
            } else {
                if (fromType.equals("custom") && new File(leftSchemePath.getText()).exists()) {
                    CustomParser customParser = new CustomParser(leftConvertArea.getText(), new File(leftSchemePath.getText()));
                    outputStr = Converter.convertDataModelTo(customParser.getDataModel(), toType);
                } else {
                    outputStr = Converter.convertStringToString(leftConvertArea.getText(), fromType, toType);
                }
            }

            if (outputStr.length() > 0) {
                if (!FormatValidator.validateString(outputStr, null, toType)) {
                    QuickInfoPopup pop = new QuickInfoPopup("Error!", "Conversion failed. Input data does not" +
                            " comply with output format criteria.", -1, null, "Force Conversion");
                    if (pop.getOptionButton() != null) {
                        String finalOutputStr = outputStr;
                        pop.getOptionButton().setOnAction(actionEvent -> {
                            rightConvertArea.replaceText(0, rightConvertArea.getLength(), finalOutputStr);
                            update(Converter.lastDataModel);
                            pop.close();
                        });
                    }
                } else {
                    rightConvertArea.replaceText(0, rightConvertArea.getLength(), outputStr);
                    update(Converter.lastDataModel);
                }
            }
        } else {
            new QuickInfoPopup("Error!", "Please validate your input file.", -1, null);
        }
        convertButton.setDisable(true);
    }

    public void update(Object source) {
        if ((source instanceof ReactionTableView) || (source instanceof SpeciesTableView)
                || (source instanceof DocumentCreationSettingsPane)) {
            if (Converter.lastDataModel != null) {
                Converter.lastDataModel.updateModel(this);
                setupCurrentDataModelListener();
            }
        } else if (source instanceof DataModel) {
            reactionTableScrollPane.setContent(new ReactionTableView(Converter.lastDataModel, this));
            speciesTableScrollPane.setContent(new SpeciesTableView(Converter.lastDataModel, this));
            documentTableScrollPane.setContent(new DocumentCreationSettingsPane(Converter.lastDataModel,
                    this));
            setupCurrentDataModelListener();
        } else {
            if (Converter.lastDataModel != null) {
                Converter.lastDataModel.updateModel(this);
                setupCurrentDataModelListener();
            }
        }
    }

    public ScrollPane getDocumentTableScrollPane() {
        return documentTableScrollPane;
    }

    public ScrollPane getSpeciesTableScrollPane() {
        return speciesTableScrollPane;
    }

    public ScrollPane getReactionTableScrollPane() {
        return reactionTableScrollPane;
    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CRS Files", "*.crs"),
                new FileChooser.ExtensionFilter("WIM Files", "*.wim", "*.txt"),
                new FileChooser.ExtensionFilter("SBML Files", "*.sbml", "*.xml"),
                new FileChooser.ExtensionFilter("DB Files", "*.db*"));

        if (file != null) {
            boolean validFile = FormatValidator.validateFile(file, null,
                    file.getPath().split("\\.")[file.getPath().split("\\.").length - 1]);
            currentDocFile = file;
            if (validFile) {
                try {
                    createDocCodeArea.setText(new String(Files.readAllBytes(file.toPath())));
                    update(Converter.lastDataModel);
                } catch (IOException e) {
                    createDocCodeArea.setText("File not valid.");
                }
            } else {
                createDocCodeArea.setText("File not valid.");
            }

        } else {
            new QuickInfoPopup("Error!", "File not found or does not exist.", -1, null);
            createDocCodeArea.setText("File " + file.getPath() + " not found or does not exist.");
        }

        mainTabPane.getSelectionModel().select(1);
    }

    public void saveFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(stage);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CRS Files", "*.crs"),
                new FileChooser.ExtensionFilter("WIM Files", "*.wim", "*.txt"),
                new FileChooser.ExtensionFilter("SBML Files", "*.sbml", "*.xml"),
                new FileChooser.ExtensionFilter("DB Files", "*.db*"));
        update((ReactionTableView) reactionTableScrollPane.getContent());
        update((SpeciesTableView) speciesTableScrollPane.getContent());
        if (file != null) {
            String toType = file.getPath().split("\\.")[file.getPath().split("\\.").length - 1];
            if (toType.matches("wim|txt|sbml|xml|crs|db")) {
                Misc.writeTmp(Converter.convertDataModelTo(Converter.lastDataModel, toType), file.getPath());
            } else {
                new QuickInfoPopup("Error!", "File could not be saved.", -1, null);
            }
            mainTabPane.getSelectionModel().select(1);
        }
    }

    public void quitGui(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}
