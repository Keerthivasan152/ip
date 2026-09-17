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
     * Sets up the chat window: auto-scrolls to the newest dialog, shows the
     * greeting and focuses the input field.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userInput.requestFocus();
    }

    /**
     * Injects the chatbot instance used to generate responses.
     *
     * @param nova the chatbot
     */
    public void setNova(Nova nova) {
        this.nova = nova;
        String greeting = Nova.MESSAGE_GREETING;
        if (!nova.getStartupWarning().isEmpty()) {
            greeting = greeting + "\n" + nova.getStartupWarning();
        }
        dialogContainer.getChildren().add(DialogBox.getNovaDialog(greeting, novaImage));
    }

    /**
     * Creates two dialog boxes, one echoing the user input and the other
     * containing the chatbot's reply, and appends them to the dialog container.
     * Clears the user input after processing, and exits the app on "bye".
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = nova.executeCommand(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getNovaDialog(response, novaImage)
        );
        userInput.clear();
        if (nova.isExitCommand(input)) {
            Platform.exit();
        }
    }
}
