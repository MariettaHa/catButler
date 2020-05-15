package catbutler.gui.ui;

import catbutler.gui.App;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class QuickInfoPopup extends Stage {

    public static String lastErrorStr = "";
    public static double lastErrorTimeStamp = 0.0;
    Scene dialogScene = null;
    Button optionButton = null;


    public QuickInfoPopup(String title, String text, double duration, Exception e) {
        if (App.appRunning) {
            boolean show = false;
            if (!text.equals(lastErrorStr)) {
                show = true;
            } else {
                if (System.currentTimeMillis() - lastErrorTimeStamp > 1200.0) {
                    show = true;
                }
            }
            if (show) {
                lastErrorStr = text;
                lastErrorTimeStamp = System.currentTimeMillis();
                init(title, text);
                this.show();


                if (duration > 0) {
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(duration),
                            ae -> {
                                this.close();
                            }));
                    timeline.playFromStart();
                }
            }
        } else {
            if (e != null) {
                e.printStackTrace();
            }
        }
    }

    public QuickInfoPopup(String title, String text, double duration, Exception e, String optionButtonText) {
        if (App.appRunning) {
            boolean show = false;
            if (!text.equals(lastErrorStr)) {
                show = true;
            } else {
                if (System.currentTimeMillis() - lastErrorTimeStamp > 1200.0) {
                    show = true;
                }
            }
            if (show) {
                lastErrorStr = text;
                lastErrorTimeStamp = System.currentTimeMillis();
                init(title, text, optionButtonText);
                this.show();

                if (duration > 0) {
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(duration),
                            ae -> {
                                this.close();
                            }));
                    timeline.playFromStart();
                }
            }
        } else {
            if (e != null) {
                e.printStackTrace();
            }
        }
    }

    private void init(String title, String text) {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle(title);

        this.initOwner(new Stage());
        Label label = new Label(text);
        label.setWrapText(true);
        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        if (text.length() > 100) {
            dialogScene = new Scene(new ScrollPane(textArea), 500, 250);
        } else {
            dialogScene = new Scene(new StackPane(label), 300, 200);
        }
        dialogScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                QuickInfoPopup.this.close();
            }
        });

        this.setScene(dialogScene);
    }

    private void init(String title, String text, String optionButtonText) {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle(title);

        this.initOwner(new Stage());
        Label label = new Label(text);
        label.setWrapText(true);
        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        optionButton = new Button(optionButtonText);
        if (text.length() > 100) {
            dialogScene = new Scene(new VBox(new ScrollPane(textArea), optionButton), 500, 250);
        } else {
            dialogScene = new Scene(new VBox(label, optionButton), 300, 200);
        }
        dialogScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                QuickInfoPopup.this.close();
            }
        });

        this.setScene(dialogScene);
    }

    public Button getOptionButton() {
        return optionButton;
    }
}
