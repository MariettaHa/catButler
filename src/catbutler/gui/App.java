package catbutler.gui;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    public static boolean appRunning = false;
    private static Scene scene;

    public static void setCursor(Cursor cursor) {
        scene.setCursor(cursor);
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                // long running task here...
                return null;
            }
        };
        task.setOnSucceeded(e -> scene.setCursor(Cursor.DEFAULT));
        new Thread(task).start();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Scene getScene() {
        return scene;
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        appRunning = true;
        scene = new Scene(loadFXML("editorView"));
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                appRunning = false;
            }
        });

    }

}