package nova;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The chatbot application. Reads commands from the user, updates the task list,
 * persists it to disk via {@link Storage} and prints responses via {@link Ui}.
 */
public class Nova {
    /**
     * Runs the chatbot loop until the user types "bye".
     *
     * @param args unused
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage();
        TaskList taskList = new TaskList(storage.load());
        Scanner scanner = new Scanner(System.in);
        ui.greet();
        while (true) {
            String input = scanner.nextLine();
            String[] parts = input.split(" ", 2);
            String command = parts[0];

            if (command.equals("bye")) {
                storage.save(taskList.getAll());
                ui.showBye();
                break;
            } else if (command.equals("list")) {
                ui.showList(taskList);
            } else if (command.equals("todo")) {
                if (parts.length == 2 && !parts[1].trim().isEmpty()) {
                    Task task = new Todo(parts[1].trim());
                    taskList.add(task);
                    ui.showTaskAdded(task, taskList.size());
                    storage.save(taskList.getAll());
                } else {
                    ui.showError(Ui.MESSAGE_TODO_EMPTY);
                }
            } else if (command.equals("deadline")) {
                String rest = parts.length == 2 ? parts[1] : "";
                String[] byParts = rest.split("/by", 2);
                boolean valid = parts.length == 2
                        && byParts.length == 2
                        && !byParts[0].trim().isEmpty()
                        && !byParts[1].trim().isEmpty();
                if (valid) {
                    LocalDate by = parseDate(byParts[1]);
                    if (by != null) {
                        Task task = new Deadline(byParts[0].trim(), by);
                        taskList.add(task);
                        ui.showTaskAdded(task, taskList.size());
                        storage.save(taskList.getAll());
                    } else {
                        ui.showError(Ui.MESSAGE_INVALID_DATE);
                    }
                } else {
                    ui.showError(Ui.MESSAGE_INVALID_DEADLINE);
                }
            } else if (command.equals("event")) {
                String rest = parts.length == 2 ? parts[1] : "";
                String[] fromParts = rest.split("/from", 2);
                String[] toParts = fromParts.length == 2 ? fromParts[1].split("/to", 2) : new String[0];
                boolean valid = parts.length == 2
                        && fromParts.length == 2
                        && toParts.length == 2
                        && !fromParts[0].trim().isEmpty()
                        && !toParts[0].trim().isEmpty()
                        && !toParts[1].trim().isEmpty();
                if (valid) {
                    LocalDate from = parseDate(toParts[0]);
                    LocalDate to = parseDate(toParts[1]);
                    if (from != null && to != null) {
                        Task task = new Event(fromParts[0].trim(), from, to);
                        taskList.add(task);
                        ui.showTaskAdded(task, taskList.size());
                        storage.save(taskList.getAll());
                    } else {
                        ui.showError(Ui.MESSAGE_INVALID_DATE);
                    }
                } else {
                    ui.showError(Ui.MESSAGE_INVALID_EVENT);
                }
            } else if (command.equals("mark") || command.equals("unmark")) {
                if (parts.length == 2) {
                    int index = getTaskIndex(parts[1], taskList.size(), ui);
                    if (index >= 0) {
                        Task task = taskList.get(index);
                        boolean done = command.equals("mark");
                        if (done) {
                            task.markDone();
                        } else {
                            task.markUndone();
                        }
                        ui.showTaskStatus(task, done);
                        storage.save(taskList.getAll());
                    }
                } else {
                    ui.showError(Ui.MESSAGE_NUMBER_REQUIRED);
                }
            } else if (command.equals("delete")) {
                if (parts.length == 2) {
                    int index = getTaskIndex(parts[1], taskList.size(), ui);
                    if (index >= 0) {
                        Task removed = taskList.remove(index);
                        ui.showTaskDeleted(removed, taskList.size());
                        storage.save(taskList.getAll());
                    }
                } else {
                    ui.showError(Ui.MESSAGE_NUMBER_REQUIRED);
                }
            } else if (command.equals("find")) {
                if (parts.length == 2 && !parts[1].trim().isEmpty()) {
                    String keyword = parts[1].trim();
                    ArrayList<Task> matches = new ArrayList<>();
                    for (int i = 0; i < taskList.size(); i++) {
                        Task task = taskList.get(i);
                        if (task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                            matches.add(task);
                        }
                    }
                    ui.showFindResults(matches);
                } else {
                    ui.showError(Ui.MESSAGE_FIND_EMPTY);
                }
            } else {
                ui.showError(Ui.MESSAGE_INVALID_COMMAND);
            }
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

    /**
     * Converts a one-based task number from the user to a zero-based index.
     * Prints an error message if the argument is not a number or is out of range.
     *
     * @param argument the task number given by the user
     * @param taskCount the number of tasks in the list
     * @param ui the ui used to print error messages
     * @return the zero-based index, or -1 if the argument is invalid
     */
    private static int getTaskIndex(String argument, int taskCount, Ui ui) {
        int number;
        try {
            number = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            ui.showError(Ui.MESSAGE_INVALID_NUMBER);
            return -1;
        }
        if (number < 1 || number > taskCount) {
            ui.showNoTaskError(number, taskCount);
            return -1;
        }
        return number - 1;
    }
}