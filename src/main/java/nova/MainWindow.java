package nova;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Nova nova;
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image novaImage = new Image(this.getClass().getResourceAsStream("/images/DaNova.png"));

    /**
     * Sets up the chat window: auto-scrolls to the newest dialog and focuses the
     * input field.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userInput.requestFocus();
    }

    /**
     * Injects the chatbot instance used to generate responses, and shows the
     * greeting once the chatbot has loaded its tasks.
     *
     * @param nova the chatbot
     */
    public void setNova(Nova nova) {
        this.nova = nova;
        String greeting = Nova.MESSAGE_GREETING + "\n" + Ui.MESSAGE_COMMAND_HINT;
        if (!nova.getStartupWarning().isEmpty()) {
            greeting = greeting + "\n" + nova.getStartupWarning();
        }
        addDialog(DialogBox.getNovaDialog(CommandResult.ok(greeting), novaImage));
    }

    /**
     * Creates two dialog boxes, one echoing the user input and the other
     * containing the chatbot's reply, and appends them to the dialog container.
     * Clears the user input after processing, and exits the app on "bye".
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        CommandResult result = nova.executeCommand(input);
        addDialog(DialogBox.getUserDialog(input, userImage));
        addDialog(DialogBox.getNovaDialog(result, novaImage));
        userInput.clear();
        if (nova.isExitCommand(input)) {
            Platform.exit();
        }
    }

    /** Adds a dialog box that keeps its text within the width of the chat area. */
    private void addDialog(DialogBox dialogBox) {
        dialogBox.limitBubbleWidth(dialogContainer.widthProperty());
        dialogContainer.getChildren().add(dialogBox);
    }
}
