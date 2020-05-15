package catbutler.gui.ui.schemeTreeView;

import javafx.beans.property.SimpleStringProperty;
import org.w3c.dom.NamedNodeMap;

public class SchemeTreeEntry {
    private String grIdStr = "0";
    private String idStr = "";
    private String regexStr = "(.*)";
    private String annotationStr = "";
    private String valueStr = "";
    private String listSeparatorStr = "";
    private String fileEndingStr = "";
    private String schemeNameStr = "";


    private SimpleStringProperty grId = new SimpleStringProperty(grIdStr);
    private SimpleStringProperty id = new SimpleStringProperty(idStr);
    private SimpleStringProperty regex = new SimpleStringProperty(regexStr);
    private SimpleStringProperty annotation = new SimpleStringProperty(annotationStr);
    private SimpleStringProperty value = new SimpleStringProperty(valueStr);
    private SimpleStringProperty listSeparator = new SimpleStringProperty(listSeparatorStr);
    private SimpleStringProperty fileEnding = new SimpleStringProperty(fileEndingStr);
    private SimpleStringProperty schemeName = new SimpleStringProperty(schemeNameStr);

    private SimpleStringProperty listSepAND = new SimpleStringProperty("&");
    private SimpleStringProperty listSepOR = new SimpleStringProperty(",");
    private SimpleStringProperty reversibleRegex = new SimpleStringProperty("<=>");

    public SchemeTreeEntry(String idStr) {
        this.id = new SimpleStringProperty(idStr);
    }

    public SchemeTreeEntry(NamedNodeMap nodeMap) {
    }

    public String getGrId() {
        return grId.get();
    }

    public void setGrId(String grId) {
        this.grId.set(grId);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getRegex() {
        return regex.get();
    }

    public void setRegex(String regex) {
        this.regex.set(regex);
    }

    public String getAnnotation() {
        return annotation.get();
    }

    public void setAnnotation(String annotation) {
        this.annotation.set(annotation);
    }

    public String getListSeparator() {
        return listSeparator.get();
    }

    public void setListSeparator(String listSeparator) {
        this.listSeparator.set(listSeparator);
    }

    public String getFileEnding() {
        return fileEnding.get();
    }

    public void setFileEnding(String fileEnding) {
        this.fileEnding.set(fileEnding);
    }

    public String getSchemeName() {
        return schemeName.get();
    }

    public void setSchemeName(String schemeName) {
        this.schemeName.set(schemeName);
    }

    public String getValueStr() {
        return valueStr;
    }

    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }


}
