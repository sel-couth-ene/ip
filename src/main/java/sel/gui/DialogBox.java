package sel.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box containing a message and the speaker's profile picture.
 */
public class DialogBox extends HBox {

    private static final double IMAGE_SIZE = 50.0;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the given text and profile picture.
     *
     * @param text text to display in the dialog box
     * @param image profile picture of the speaker
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);

        makeImageCircular();
    }

    /**
     * Clips the profile picture into a circle.
     */
    private void makeImageCircular() {
        double radius = IMAGE_SIZE / 2;

        Circle clip = new Circle(
                radius,
                radius,
                radius);

        displayPicture.setClip(clip);
    }

    /**
     * Flips the dialog so that the profile picture appears on the left.
     */
    private void flip() {
        ObservableList<Node> nodes = FXCollections.observableArrayList(
                this.getChildren());
        Collections.reverse(nodes);
        this.getChildren().setAll(nodes);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    /**
     * Creates a dialog box for the user.
     *
     * @param text user's message
     * @param image user's profile picture
     * @return dialog box for the user
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a dialog box for Sel.
     *
     * @param text Sel's response
     * @param image Sel's profile picture
     * @return dialog box for Sel
     */
    public static DialogBox getSelDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }
}
