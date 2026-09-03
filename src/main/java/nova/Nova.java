package nova;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The chatbot application. Processes user commands, updates the task list and
 * persists it to disk via {@link Storage}. Both the text-based interface and
 * the JavaFX GUI feed user input into {@link #getResponse(String)}.
 */
public class Nova {
    private static final String MESSAGE_BYE = "Bye. Hope to see you again soon!";
    private static final String MESSAGE_ADDED = "Got it. I've added this task:";
    private static final String MESSAGE_REMOVED = "Noted. I've removed this task:";
    private static final String MESSAGE_MARKED = "Nice! I've marked this task as done:";
    private static final String MESSAGE_UNMARKED = "Ok, I've marked this task as not done yet:";
    private static final String MESSAGE_FIND_HEADER = "Here are the matching tasks in your list:";
    private static final String MESSAGE_TASK_COUNT_PREFIX = "Now you have ";

    private final Storage storage;
    private final TaskList taskList;

    /**
     * Creates a chatbot that loads its tasks from the default save file.
     */
    public Nova() {
        storage = new Storage();
        taskList = new TaskList(storage.load());
    }

    /**
     * Processes one user command and returns the text of the chatbot's response.
     *
     * @param input the raw command entered by the user
     * @return the response text, or an empty string when there is nothing to show
     */
    public String getResponse(String input) {
        String[] parts = input.split(" ", 2);
        String command = parts[0];

        if (command.equals("bye")) {
            storage.save(taskList.getAll());
            return MESSAGE_BYE;
        }
        if (command.equals("list")) {
            return formatTaskList();
        }
        if (command.equals("todo")) {
            if (parts.length == 2 && !parts[1].trim().isEmpty()) {
                Task task = new Todo(parts[1].trim());
                taskList.add(task);
                storage.save(taskList.getAll());
                return formatAddedMessage(task);
            }
            return Ui.MESSAGE_TODO_EMPTY;
        }
        if (command.equals("deadline")) {
            String rest = parts.length == 2 ? parts[1] : "";
            String[] byParts = rest.split("/by", 2);
            boolean isValidFormat = parts.length == 2
                    && byParts.length == 2
                    && !byParts[0].trim().isEmpty()
                    && !byParts[1].trim().isEmpty();
            if (!isValidFormat) {
                return Ui.MESSAGE_INVALID_DEADLINE;
            }
            LocalDate by = parseDate(byParts[1]);
            if (by == null) {
                return Ui.MESSAGE_INVALID_DATE;
            }
            Task task = new Deadline(byParts[0].trim(), by);
            taskList.add(task);
            storage.save(taskList.getAll());
            return formatAddedMessage(task);
        }
        if (command.equals("event")) {
            String rest = parts.length == 2 ? parts[1] : "";
            String[] fromParts = rest.split("/from", 2);
            String[] toParts = fromParts.length == 2 ? fromParts[1].split("/to", 2) : new String[0];
            boolean isValidFormat = parts.length == 2
                    && fromParts.length == 2
                    && toParts.length == 2
                    && !fromParts[0].trim().isEmpty()
                    && !toParts[0].trim().isEmpty()
                    && !toParts[1].trim().isEmpty();
            if (!isValidFormat) {
                return Ui.MESSAGE_INVALID_EVENT;
            }
            LocalDate from = parseDate(toParts[0]);
            LocalDate to = parseDate(toParts[1]);
            if (from == null || to == null) {
                return Ui.MESSAGE_INVALID_DATE;
            }
            Task task = new Event(fromParts[0].trim(), from, to);
            taskList.add(task);
            storage.save(taskList.getAll());
            return formatAddedMessage(task);
        }
        if (command.equals("mark") || command.equals("unmark")) {
            if (parts.length != 2) {
                return Ui.MESSAGE_NUMBER_REQUIRED;
            }
            int index = parseTaskIndex(parts[1], taskList.size());
            if (index < 0) {
                return taskNumberError(parts[1], taskList.size());
            }
            Task task = taskList.get(index);
            if (command.equals("mark")) {
                task.markDone();
            } else {
                task.markUndone();
            }
            storage.save(taskList.getAll());
            return (command.equals("mark") ? MESSAGE_MARKED : MESSAGE_UNMARKED) + "\n" + task;
        }
        if (command.equals("delete")) {
            if (parts.length != 2) {
                return Ui.MESSAGE_NUMBER_REQUIRED;
            }
            int index = parseTaskIndex(parts[1], taskList.size());
            if (index < 0) {
                return taskNumberError(parts[1], taskList.size());
            }
            Task removed = taskList.remove(index);
            storage.save(taskList.getAll());
            return MESSAGE_REMOVED + "\n" + removed + "\n" + taskCountMessage(taskList.size());
        }
        if (command.equals("find")) {
            if (parts.length == 2 && !parts[1].trim().isEmpty()) {
                String keyword = parts[1].trim();
                ArrayList<Task> matches = new ArrayList<>();
                for (int i = 0; i < taskList.size(); i++) {
                    Task task = taskList.get(i);
                    if (task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                        matches.add(task);
                    }
                }
                return formatFindResults(matches);
            }
            return Ui.MESSAGE_FIND_EMPTY;
        }
        return Ui.MESSAGE_INVALID_COMMAND;
    }

    /**
     * Returns whether the given input is the bye command.
     *
     * @param input the command entered by the user
     * @return true if the command exits the chatbot
     */
    public boolean isExitCommand(String input) {
        return input.split(" ", 2)[0].equals("bye");
    }

    /**
     * Runs the text-based chatbot loop until the user types "bye".
     *
     * @param args unused
     */
    public static void main(String[] args) {
        Nova nova = new Nova();
        Ui ui = new Ui();
        Scanner scanner = new Scanner(System.in);
        ui.greet();
        while (true) {
            String input = scanner.nextLine();
            String response = nova.getResponse(input);
            if (!response.isEmpty()) {
                ui.showMessage(response);
            }
            if (nova.isExitCommand(input)) {
                break;
            }
        }
    }

    private String formatAddedMessage(Task task) {
        return MESSAGE_ADDED + "\n" + task + "\n" + taskCountMessage(taskList.size());
    }

    private String taskCountMessage(int count) {
        return MESSAGE_TASK_COUNT_PREFIX + count + " tasks in the list.";
    }

    /** Formats all tasks using their one-based task numbers. */
    private String formatTaskList() {
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < taskList.size(); i++) {
            if (i > 0) {
                response.append('\n');
            }
            response.append(i + 1).append(".").append(taskList.get(i));
        }
        return response.toString();
    }

    /** Formats the matching tasks of a find command, numbered from 1. */
    private String formatFindResults(ArrayList<Task> matches) {
        StringBuilder response = new StringBuilder(MESSAGE_FIND_HEADER);
        for (int i = 0; i < matches.size(); i++) {
            response.append('\n').append(i + 1).append(".").append(matches.get(i));
        }
        return response.toString();
    }

    /**
     * Converts a one-based task number from the user to a zero-based index.
     *
     * @param argument the task number given by the user
     * @param taskCount the number of tasks in the list
     * @return the zero-based index, or -1 if the argument is not a valid number
     *         or is out of range
     */
    private static int parseTaskIndex(String argument, int taskCount) {
        try {
            int number = Integer.parseInt(argument);
            if (number < 1 || number > taskCount) {
                return -1;
            }
            return number - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Returns the error message for a task number argument that could not be used.
     *
     * @param argument the task number given by the user
     * @param taskCount the number of tasks in the list
     * @return the message for an invalid or out-of-range task number
     */
    private static String taskNumberError(String argument, int taskCount) {
        try {
            int number = Integer.parseInt(argument);
            return "There's no task at number " + number + ". You have " + taskCount + " tasks.";
        } catch (NumberFormatException e) {
            return Ui.MESSAGE_INVALID_NUMBER;
        }
    }

    /**
     * Parses a date string in the ISO format yyyy-MM-dd.
     *
     * @param text the date string to parse
     * @return the parsed date, or null if the text is not a valid date
     */
    private static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
