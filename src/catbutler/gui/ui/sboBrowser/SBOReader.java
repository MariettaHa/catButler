package catbutler.gui.ui.sboBrowser;

import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class SBOReader {
    static TreeItem<String> treeView = new TreeItem<>("SBO Glossary");
    static ArrayList<String> info = new ArrayList<>();

    static void read(String path) throws IOException {

        try (Stream<String> stream = Files.lines(Paths.get(path))) {

            stream.forEach(x -> {
                if (x.equals("[Term]")) {
                    info = new ArrayList<>();
                } else if (x.isEmpty() || x.isBlank()) {
                    addNewItem();
                } else {
                    info.add(x);
                }
            });
        }
    }

    private static void addNewItem() {
        TreeItem<String> subTreeView = new TreeItem<>(info.get(1).split(":")[1]);
        for (int i = 0; i < info.size(); i++) {
            TreeItem t = new TreeItem<String>(info.get(i));
            subTreeView.getChildren().add(t);
        }
        treeView.getChildren().add(subTreeView);


    }

}
