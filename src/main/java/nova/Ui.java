package nova;

/**
 * Handles the user-facing output of the text-based chatbot interface.
 */
public class Ui {
    public static final String MESSAGE_TODO_EMPTY = "The description of a todo cannot be empty.";
    public static final String MESSAGE_INVALID_DEADLINE =
            "Please give a deadline like: deadline <description> /by <date>";
    public static final String MESSAGE_INVALID_EVENT =
            "Please give an event like: event <description> /from <start> /to <end>";
    public static final String MESSAGE_INVALID_DATE =
            "Invalid date: use yyyy-MM-dd, e.g. deadline return book /by 2026-08-28";
    public static final String MESSAGE_INVALID_NUMBER =
            "That doesn't look like a valid task number, e.g. mark 2";
    public static final String MESSAGE_NUMBER_REQUIRED = "Please give a task number, e.g. mark 2";
    public static final String MESSAGE_FIND_EMPTY =
            "Please give a keyword to find, e.g. find book";
    public static final String MESSAGE_INVALID_COMMAND =
            "I don't know that command. Try: todo, deadline, event, find, list, mark, unmark, delete, bye";

    private static final String BANNER = " _   _\n"
            + "| \\ | | _____   ____ _\n"
            + "|  \\| |/ _ \\ \\ / / _` |\n"
            + "| |\\  | (_) \\ V / (_| |\n"
            + "|_| \\_|\\___/ \\_/ \\__,_|\n";

    /**
     * Prints the chatbot banner and the greeting message.
     */
    public void greet() {
        showMessage(BANNER, "Hello! I'm Nova. What can I do for you?");
    }

    /**
     * Prints each given message on its own line.
     *
     * @param messages the messages to print
     */
    public void showMessage(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }
}
