package sel.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import sel.Sel;

/**
 * Controller for the main Sel chat window.
 */
public class MainWindow {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Sel sel;

    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/user.png"));

    private final Image selImage = new Image(
            this.getClass().getResourceAsStream("/images/sel.png"));

    /**
     * Configures the dialog area after the FXML components are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot logic to this controller.
     *
     * @param sel the chatbot instance used to process commands
     */
    public void setSel(Sel sel) {
        this.sel = sel;
        dialogContainer.getChildren().add(
                DialogBox.getSelDialog("Sup, I'm Sel.", selImage));
    }

    /**
     * Sends the current text field contents to Sel and displays both
     * the user's message and Sel's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String trimmedInput = input.trim();
        String response = sel.getResponse(trimmedInput);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(trimmedInput, userImage),
                DialogBox.getSelDialog(response, selImage));

        userInput.clear();

        if (trimmedInput.equals("bye")) {
            startClosingCountdown();
        }
    }

    /**
     * Displays a three-second countdown before closing the application.
     */
    private void startClosingCountdown() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        dialogContainer.getChildren().add(
                DialogBox.getSelDialog("App closing in", selImage));

        Timeline countdown = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        event -> showCountdownNumber("3")),
                new KeyFrame(
                        Duration.seconds(1),
                        event -> showCountdownNumber("2")),
                new KeyFrame(
                        Duration.seconds(2),
                        event -> showCountdownNumber("1")),
                new KeyFrame(
                        Duration.seconds(3),
                        event -> closeApplication())
        );

        countdown.play();
    }

    /**
     * Displays one countdown number as a new Sel dialog.
     *
     * @param number countdown number to display
     */
    private void showCountdownNumber(String number) {
        dialogContainer.getChildren().add(
                DialogBox.getSelDialog(number, selImage));
    }

    /**
     * Closes the application window.
     */
    private void closeApplication() {
        userInput.getScene().getWindow().hide();
    }
}
