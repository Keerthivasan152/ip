package nova;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Nova built with JavaFX and FXML.
 */
public class Main extends Application {
    private static final double MIN_WINDOW_WIDTH = 360;
    private static final double MIN_WINDOW_HEIGHT = 420;

    private final Nova nova = new Nova();

    /**
     * Shows the main chat window.
     *
     * @param stage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            Scene scene = new Scene(mainWindow);
            scene.getStylesheets().add(Main.class.getResource("/view/nova.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Nova");
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            fxmlLoader.<MainWindow>getController().setNova(nova);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
