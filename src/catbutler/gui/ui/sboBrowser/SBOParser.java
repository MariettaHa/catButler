package catbutler.gui.ui.sboBrowser;

import javafx.scene.control.TreeItem;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

public class SBOParser extends DefaultHandler {
    private TreeItem<String> item = new TreeItem<>();

    public static TreeItem<String> readData(String path) throws IOException {
        SBOReader.read(path);
        return SBOReader.treeView;


    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.item = this.item.getParent();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        TreeItem<String> item = new TreeItem<>(qName);
        this.item.getChildren().add(item);
        this.item = item;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String s = String.valueOf(ch, start, length).trim();
        if (!s.isEmpty()) {
            this.item.getChildren().add(new TreeItem<>(s));
        }
    }
}