package nova;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.property.ReadOnlyDoubleProperty;
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
 * Represents one turn of the conversation: the speaker's text and, on Nova's
 * side, a small round avatar. Errors carry their own style class so that a
 * reply reporting a problem looks different from an ordinary reply.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 34;
    private static final double BUBBLE_WIDTH_RATIO = 0.78;

    private static final String STYLE_DIALOG_BOX = "dialog-box";
    private static final String STYLE_BUBBLE = "bubble";
    private static final String STYLE_NOVA = "nova";
    private static final String STYLE_USER = "user";
    private static final String STYLE_ERROR = "error";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getStyleClass().add(STYLE_DIALOG_BOX);
        dialog.getStyleClass().add(STYLE_BUBBLE);
        dialog.setText(text);
        displayPicture.setImage(img);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
    }

    /**
     * Creates a dialog box for the user's message. The user's turns carry no
     * avatar, because the input field already shows who typed the text.
     *
     * @param text the message text
     * @param img the user's avatar
     * @return the dialog box
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.getStyleClass().add(STYLE_USER);
        dialogBox.displayPicture.setVisible(false);
        dialogBox.displayPicture.setManaged(false);
        return dialogBox;
    }

    /**
     * Creates a dialog box for the chatbot's reply, styled by whether the reply
     * reports a problem.
     *
     * @param result the outcome of the user's command
     * @param img the chatbot's avatar
     * @return the dialog box
     */
    public static DialogBox getNovaDialog(CommandResult result, Image img) {
        DialogBox dialogBox = new DialogBox(result.text(), img);
        dialogBox.getStyleClass().add(STYLE_NOVA);
        dialogBox.flip();
        if (result.isError()) {
            dialogBox.getStyleClass().add(STYLE_ERROR);
            dialogBox.dialog.setGraphic(createBadge());
        }
        return dialogBox;
    }

    /**
     * Keeps the text bubble to a readable share of the chat width by tracking
     * the width of its container.
     *
     * @param containerWidth the width of the container holding the dialog boxes
     */
    public void limitBubbleWidth(ReadOnlyDoubleProperty containerWidth) {
        dialog.maxWidthProperty().bind(containerWidth.multiply(BUBBLE_WIDTH_RATIO));
    }

    /** Creates the marker that draws the eye to an error reply. */
    private static Label createBadge() {
        Label badge = new Label("!");
        badge.getStyleClass().add("badge");
        return badge;
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and the
     * text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }
}
