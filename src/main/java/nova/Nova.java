package nova;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The chatbot application. Processes user commands, updates the task list and
 * persists it to disk via {@link Storage}. Both the text-based interface and
 * the JavaFX GUI feed user input into {@link #executeCommand(String)}.
 */
public class Nova {
    /** The greeting shown when the application starts. */
    public static final String MESSAGE_GREETING = "Hello! I'm Nova. What can I do for you?";

    private static final String MESSAGE_BYE = "Bye. Hope to see you again soon!";
    private static final String MESSAGE_ADDED = "Got it. I've added this task:";
    private static final String MESSAGE_REMOVED = "Noted. I've removed this task:";
    private static final String MESSAGE_MARKED = "Nice! I've marked this task as done:";
    private static final String MESSAGE_UNMARKED = "Ok, I've marked this task as not done yet:";
    private static final String MESSAGE_FIND_HEADER = "Here are the matching tasks in your list:";
    private static final String MESSAGE_TASK_COUNT_PREFIX = "Now you have ";

    private static final String PARAMETER_BY = "/by";
    private static final String PARAMETER_FROM = "/from";
    private static final String PARAMETER_TO = "/to";

    private final Storage storage;
    private final TaskList taskList;
    private final String startupWarning;

    /**
     * Creates a chatbot that loads its tasks from the default save file.
     */
    public Nova() {
        this(new Storage());
    }

    /**
     * Creates a chatbot that loads its tasks from the given storage.
     *
     * @param storage the storage to load tasks from and save tasks to
     */
    public Nova(Storage storage) {
        assert storage != null : "Storage must not be null";
        this.storage = storage;
        this.taskList = new TaskList(storage.load());
        this.startupWarning = storage.consumeErrorMessage();
    }

    /**
     * Returns any warning about the save file that was found while starting up.
     *
     * @return the warning text, or an empty string when the save file read cleanly
     */
    public String getStartupWarning() {
        return this.startupWarning;
    }

    /**
     * Processes one user command and returns the text of the chatbot's response.
     *
     * @param input the raw command entered by the user
     * @return the response text, or an empty string when there is nothing to show
     */
    public String executeCommand(String input) {
        Parser parser = new Parser(input);
        String command = parser.getCommandWord();
        if (command.isEmpty()) {
            return Ui.MESSAGE_EMPTY_INPUT;
        }
        switch (command) {
            case "bye":
                return handleBye(parser);
            case "list":
                return handleList(parser);
            case "todo":
                return handleTodo(parser);
            case "deadline":
                return handleDeadline(parser);
            case "event":
                return handleEvent(parser);
            case "mark":
            case "unmark":
            case "delete":
                return handleIndexedCommand(command, parser);
            case "find":
                return handleFind(parser);
            case "archive":
                return handleArchive(parser);
            default:
                return Ui.MESSAGE_INVALID_COMMAND;
        }
    }

    /**
     * Returns whether the given input is the bye command.
     *
     * @param input the command entered by the user
     * @return true if the command exits the chatbot
     */
    public boolean isExitCommand(String input) {
        return new Parser(input).getCommandWord().equals("bye");
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
        ui.greet(nova.getStartupWarning());
        while (true) {
            String input = scanner.nextLine();
            String response = nova.executeCommand(input);
            if (!response.isEmpty()) {
                ui.showMessage(response);
            }
            if (nova.isExitCommand(input)) {
                break;
            }
        }
    }

    /** Handles the bye command, saving the tasks before the app exits. */
    private String handleBye(Parser parser) {
        if (parser.hasArgument()) {
            return extraArgumentsMessage("bye");
        }
        return MESSAGE_BYE + saveTasks();
    }

    /** Handles the list command. */
    private String handleList(Parser parser) {
        if (parser.hasArgument()) {
            return extraArgumentsMessage("list");
        }
        return formatTaskList();
    }

    /** Handles the todo command, which needs a non-empty description. */
    private String handleTodo(Parser parser) {
        if (!parser.hasArgument()) {
            return Ui.MESSAGE_TODO_EMPTY;
        }
        Task task = new Todo(parser.getArguments());
        taskList.add(task);
        return formatAddedMessage(task) + saveTasks();
    }

    /** Handles the find command, which needs a non-empty keyword. */
    private String handleFind(Parser parser) {
        if (!parser.hasArgument()) {
            return Ui.MESSAGE_FIND_EMPTY;
        }
        String keyword = parser.getArguments().toLowerCase();
        ArrayList<Task> matches = taskList.getAll().stream()
                .filter(task -> task.getDescription().toLowerCase().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return formatFindResults(matches);
    }

    /**
     * Handles the archive command, moving completed tasks to the archive file.
     *
     * @param parser the parsed user input
     * @return the response text for the command
     */
    private String handleArchive(Parser parser) {
        if (parser.hasArgument()) {
            return extraArgumentsMessage("archive");
        }
        ArrayList<Task> archived = taskList.removeCompletedTasks();
        if (archived.isEmpty()) {
            return Ui.MESSAGE_ARCHIVE_EMPTY;
        }
        storage.appendToArchive(archived);
        String warning = saveTasks();
        String body = IntStream.range(0, archived.size())
                .mapToObj(i -> (i + 1) + "." + archived.get(i))
                .collect(Collectors.joining("\n"));
        return Ui.MESSAGE_ARCHIVE_HEADER + "\n" + body + "\n"
                + taskCountMessage(taskList.size()) + warning;
    }

    /**
     * Handles the deadline command. The description and the /by date are both
     * required, and /by may appear only once.
     *
     * @param parser the parsed user input
     * @return the response text for the command
     */
    private String handleDeadline(Parser parser) {
        String arguments = parser.getArguments();
        if (Parser.countOf(arguments, PARAMETER_BY) > 1) {
            return Ui.MESSAGE_DUPLICATE_DEADLINE_DATE;
        }
        String[] halves = Parser.splitAround(arguments, PARAMETER_BY);
        boolean isWellFormed = halves.length == 2 && !halves[0].isEmpty() && !halves[1].isEmpty();
        if (!isWellFormed) {
            return Ui.MESSAGE_INVALID_DEADLINE;
        }
        LocalDate by = parseDate(halves[1]);
        if (by == null) {
            return Ui.MESSAGE_INVALID_DATE;
        }
        Task task = new Deadline(halves[0], by);
        taskList.add(task);
        return formatAddedMessage(task) + saveTasks();
    }

    /**
     * Handles the event command. The description and both dates are required,
     * /from and /to may appear only once each, and the event must end after it
     * starts.
     *
     * @param parser the parsed user input
     * @return the response text for the command
     */
    private String handleEvent(Parser parser) {
        String arguments = parser.getArguments();
        if (Parser.countOf(arguments, PARAMETER_FROM) > 1 || Parser.countOf(arguments, PARAMETER_TO) > 1) {
            return Ui.MESSAGE_DUPLICATE_EVENT_DATE;
        }
        String[] fromHalves = Parser.splitAround(arguments, PARAMETER_FROM);
        String[] toHalves = fromHalves.length == 2
                ? Parser.splitAround(fromHalves[1], PARAMETER_TO) : new String[0];
        boolean isWellFormed = toHalves.length == 2
                && !fromHalves[0].isEmpty() && !toHalves[0].isEmpty() && !toHalves[1].isEmpty();
        if (!isWellFormed) {
            return Ui.MESSAGE_INVALID_EVENT;
        }
        LocalDate from = parseDate(toHalves[0]);
        LocalDate to = parseDate(toHalves[1]);
        if (from == null || to == null) {
            return Ui.MESSAGE_INVALID_DATE;
        }
        if (!from.isBefore(to)) {
            return Ui.MESSAGE_INVALID_EVENT_RANGE;
        }
        Task task = new Event(fromHalves[0], from, to);
        taskList.add(task);
        return formatAddedMessage(task) + saveTasks();
    }

    /**
     * Handles the mark, unmark and delete commands, which all act on a task number.
     *
     * @param command one of mark, unmark or delete
     * @param parser the parsed user input
     * @return the response text for the command
     */
    private String handleIndexedCommand(String command, Parser parser) {
        if (!parser.hasArgument()) {
            return Ui.MESSAGE_NUMBER_REQUIRED;
        }
        if (!parser.hasSingleWordArgument()) {
            return Ui.MESSAGE_ONE_TASK_NUMBER;
        }
        int index = parseTaskIndex(parser.getArguments(), taskList.size());
        if (index < 0) {
            return taskNumberError(parser.getArguments(), taskList.size());
        }
        if (command.equals("delete")) {
            Task removed = taskList.remove(index);
            return MESSAGE_REMOVED + "\n" + removed + "\n"
                    + taskCountMessage(taskList.size()) + saveTasks();
        }
        Task task = taskList.get(index);
        if (command.equals("mark")) {
            task.markDone();
        } else {
            task.markUndone();
        }
        return (command.equals("mark") ? MESSAGE_MARKED : MESSAGE_UNMARKED) + "\n" + task + saveTasks();
    }

    /** Returns the message for a command that was given arguments it does not take. */
    private static String extraArgumentsMessage(String command) {
        return String.format(Ui.MESSAGE_EXTRA_ARGUMENTS, command);
    }

    /** Formats the added-task reply, which includes the new number of tasks. */
    private String formatAddedMessage(Task task) {
        return MESSAGE_ADDED + "\n" + task + "\n" + taskCountMessage(taskList.size());
    }

    /** Formats the number-of-tasks sentence that closes most replies. */
    private String taskCountMessage(int count) {
        return MESSAGE_TASK_COUNT_PREFIX + count + " tasks in the list.";
    }

    /**
     * Saves the current tasks and reports any storage problem to the user.
     *
     * @return a warning line, or an empty string when the save succeeded
     */
    private String saveTasks() {
        storage.save(taskList.getAll());
        String warning = storage.consumeErrorMessage();
        return warning.isEmpty() ? "" : "\n" + warning;
    }

    /** Formats all tasks using their one-based task numbers. */
    private String formatTaskList() {
        return IntStream.range(0, taskList.size())
                .mapToObj(i -> (i + 1) + "." + taskList.get(i))
                .collect(Collectors.joining("\n"));
    }

    /** Formats the matching tasks of a find command, numbered from 1. */
    private String formatFindResults(ArrayList<Task> matches) {
        String body = IntStream.range(0, matches.size())
                .mapToObj(i -> (i + 1) + "." + matches.get(i))
                .collect(Collectors.joining("\n"));
        return body.isEmpty() ? MESSAGE_FIND_HEADER : MESSAGE_FIND_HEADER + "\n" + body;
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
        assert taskCount >= 0 : "Task count must not be negative";
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
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
