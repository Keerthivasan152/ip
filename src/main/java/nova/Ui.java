package nova;

/**
 * Handles the user-facing output of the text-based chatbot interface, and holds
 * the messages that both interfaces show to the user.
 */
public class Ui {
    public static final String MESSAGE_TODO_EMPTY = "The description of a todo cannot be empty.";
    public static final String MESSAGE_INVALID_DEADLINE =
            "Please give a deadline like: deadline <description> /by <date>";
    public static final String MESSAGE_DUPLICATE_DEADLINE_DATE =
            "Please give the /by date once, like: deadline return book /by 2026-08-28";
    public static final String MESSAGE_INVALID_EVENT =
            "Please give an event like: event <description> /from <start> /to <end>";
    public static final String MESSAGE_DUPLICATE_EVENT_DATE =
            "Please give /from and /to once each, like: event meeting /from 2026-08-28 /to 2026-08-29";
    public static final String MESSAGE_INVALID_EVENT_RANGE =
            "The event ends before it starts. The /from date must be earlier than the /to date.";
    public static final String MESSAGE_INVALID_DATE =
            "Invalid date: use yyyy-MM-dd, e.g. deadline return book /by 2026-08-28";
    public static final String MESSAGE_INVALID_NUMBER =
            "That doesn't look like a valid task number, e.g. mark 2";
    public static final String MESSAGE_NUMBER_REQUIRED = "Please give a task number, e.g. mark 2";
    public static final String MESSAGE_ONE_TASK_NUMBER =
            "Please give one task number only, e.g. mark 2";
    public static final String MESSAGE_FIND_EMPTY =
            "Please give a keyword to find, e.g. find book";
    public static final String MESSAGE_EMPTY_INPUT =
            "Please type a command, e.g. list";
    public static final String MESSAGE_EXTRA_ARGUMENTS =
            "The %s command does not take any arguments.";
    public static final String MESSAGE_INVALID_COMMAND =
            "I don't know that command. Try: todo, deadline, event, find, list, mark, unmark, delete, archive,"
                    + " help, bye";
    public static final String MESSAGE_LIST_EMPTY =
            "Your list is empty. Add one with todo, deadline or event.";
    public static final String MESSAGE_COMMAND_HINT =
            "Try: todo read book, deadline return book /by 2026-09-20, list, find book, bye";
    public static final String MESSAGE_HELP =
            "Here is everything I can do:\n"
                    + "  todo <description>                        add a task\n"
                    + "  deadline <description> /by <date>         add a task with a deadline\n"
                    + "  event <description> /from <date> /to <date>\n"
                    + "                                            add an event\n"
                    + "  list                                      show every task\n"
                    + "  find <keyword>                            show the matching tasks\n"
                    + "  mark <number> / unmark <number>           change a task's status\n"
                    + "  delete <number>                           remove a task\n"
                    + "  archive                                   move done tasks to the archive\n"
                    + "  bye                                       save and close\n"
                    + "Dates look like 2026-09-20. The up and down arrow keys recall what you typed,"
                    + " Tab completes a command, and Ctrl+D switches between the dark and light themes.";
    public static final String MESSAGE_ARCHIVE_EMPTY = "There are no completed tasks to archive.";
    public static final String MESSAGE_ARCHIVE_HEADER = "I've archived these completed tasks:";

    private static final String BANNER = " _   _\n"
            + "| \\ | | _____   ____ _\n"
            + "|  \\| |/ _ \\ \\ / / _` |\n"
            + "| |\\  | (_) \\ V / (_| |\n"
            + "|_| \\_|\\___/ \\_/ \\__,_|\n";

    /**
     * Prints the chatbot banner, the greeting and any startup warning.
     *
     * @param startupWarning the warning to show, or an empty string when there is none
     */
    public void greet(String startupWarning) {
        showMessage(BANNER, Nova.MESSAGE_GREETING);
        if (!startupWarning.isEmpty()) {
            showMessage(startupWarning);
        }
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
