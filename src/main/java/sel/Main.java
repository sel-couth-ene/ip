package sel;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sel.gui.MainWindow;

/** JavaFX application for Sel. */
public class Main extends Application {

    private final Sel sel = new Sel("data/sel.txt");

    /**
     * Loads and displays the main JavaFX window.
     *
     * @param stage the primary application stage.
     * @throws IOException if the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();

        MainWindow controller = fxmlLoader.getController();
        controller.setSel(sel);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sel");
        stage.setMinWidth(400);
        stage.setMinHeight(450);
        stage.show();
    }
}
