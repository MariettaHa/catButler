package catbutler.gui.ui.dataTableView;

import catbutler.gui.mainControllers.EditorViewController;
import catbutler.model.DataModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DocumentCreationSettingsPane extends GridPane {

    DataModel dataModel;
    SimpleStringProperty fileName = new SimpleStringProperty("");
    SimpleStringProperty docType = new SimpleStringProperty("null");
    SimpleStringProperty docId = new SimpleStringProperty("null");
    SimpleStringProperty description = new SimpleStringProperty("null");
    SimpleStringProperty metaData = new SimpleStringProperty("null");
    SimpleStringProperty authorGivenName = new SimpleStringProperty("null");
    SimpleStringProperty authorFamilyName = new SimpleStringProperty("null");
    SimpleStringProperty authorOrganization = new SimpleStringProperty("null");
    SimpleStringProperty authorEmail = new SimpleStringProperty("null");
    SimpleStringProperty version = new SimpleStringProperty("null");
    SimpleStringProperty name = new SimpleStringProperty("null");
    SimpleStringProperty formatType = new SimpleStringProperty("CRS");
    HashMap<String, SimpleStringProperty> propertyHashMap = new HashMap<>();
    ArrayList<String> propNames = new ArrayList<>(Arrays.asList("File Name:", "Document Type:", "Document Id:",
            "Description:", "Meta Data/Comments:", "Given Name (Author):",
            "Family Name (Author):", "Organization (Author):", "Email (Author):", "Version:", "Document Name:"));

    EditorViewController editorViewController;

    public DocumentCreationSettingsPane(DataModel dataModel, EditorViewController editorViewController) {
        this.dataModel = dataModel;
        this.editorViewController = editorViewController;
        initContent();
    }


    private void initContent() {
        this.setHgap(5.0);
        this.setVgap(5.0);
        this.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
        propertyHashMap = new HashMap<>(Map.of("File Name:", fileName, "Document Type:", docType, "Document Id:", docId,
                "Description:", description, "Meta Data/Comments:", metaData, "Given Name (Author):", authorGivenName,
                "Family Name (Author):", authorFamilyName,
                "Organization (Author):", authorOrganization,
                "Email (Author):", authorEmail));
        propertyHashMap.putAll(Map.of("Version:", version, "Document Name:", name));
        int i = 0;
        for (String labelStr : propNames) {
            Label label = new Label(labelStr);
            TextField textField = new TextField();
            propertyHashMap.get(labelStr).bind(textField.textProperty());
            if (i > 5) {
                this.add(label, 3, i - 6);
                this.add(textField, 4, i - 6);
            } else if (i > 10) {
                this.add(label, 6, i - 11);
                this.add(textField, 7, i - 11);
            } else {
                this.add(label, 0, i);
                this.add(textField, 1, i);
            }
            i++;
            propertyHashMap.get(labelStr).addListener((observableValue, s, t1)
                    -> editorViewController.update(DocumentCreationSettingsPane.this));
        }

        Label label = new Label("File Format:");
        ChoiceBox<String> formatTypeChoiceBox = new ChoiceBox<>();
        formatTypeChoiceBox.getItems().setAll(new ArrayList<>(Arrays.asList("CRS", "SBML", "DB", "WIM")));
        formatTypeChoiceBox.setValue("CRS");
        formatTypeChoiceBox.valueProperty().addListener((observableValue, s, t1)
                -> editorViewController.update(DocumentCreationSettingsPane.this));
        this.add(label, 9, 0);
        this.add(formatTypeChoiceBox, 10, 0);
        formatType.bind(formatTypeChoiceBox.valueProperty());

    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public String getFileName() {
        return fileName.get();
    }

    public String getDocType() {
        return docType.get();
    }

    public String getDocId() {
        return docId.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getMetaData() {
        return metaData.get();
    }

    public String getAuthorGivenName() {
        return authorGivenName.get();
    }

    public String getAuthorFamilyName() {
        return authorFamilyName.get();
    }

    public String getAuthorOrganization() {
        return authorOrganization.get();
    }

    public String getAuthorEmail() {
        return authorEmail.get();
    }

    public String getVersion() {
        return version.get();
    }

    public String getName() {
        return name.get();
    }

    public String getFormatType() {
        return formatType.get();
    }
}
