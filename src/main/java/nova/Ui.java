package nova;

import java.util.List;

/**
 * Handles all user-facing output of the chatbot.
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
     * Prints each given message on its own line.
     *
     * @param messages the messages to print
     */
    public void showMessage(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

    /**
     * Prints the chatbot banner and the greeting message.
     */
    public void greet() {
        showMessage(BANNER, "Hello! I'm Nova. What can I do for you?");
    }

    /**
     * Prints the goodbye message.
     */
    public void showBye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    /**
     * Prints the confirmation for a newly added task and the new total count.
     *
     * @param task the task that was added
     * @param totalCount the number of tasks after adding
     */
    public void showTaskAdded(Task task, int totalCount) {
        showMessage("Got it. I've added this task:",
                task.toString(),
                "Now you have " + totalCount + " tasks in the list.");
    }

    /**
     * Prints the confirmation for a removed task and the new total count.
     *
     * @param task the task that was removed
     * @param totalCount the number of tasks after removing
     */
    public void showTaskDeleted(Task task, int totalCount) {
        showMessage("Noted. I've removed this task:",
                task.toString(),
                "Now you have " + totalCount + " tasks in the list.");
    }

    /**
     * Prints the confirmation for marking a task as done or not done.
     *
     * @param task the task whose status changed
     * @param done true if the task was marked done, false if marked not done
     */
    public void showTaskStatus(Task task, boolean done) {
        if (done) {
            showMessage("Nice! I've marked this task as done:", task.toString());
        } else {
            showMessage("Ok, I've marked this task as not done yet:", task.toString());
        }
    }

    /**
     * Prints all tasks in the list, numbered from 1.
     *
     * @param tasks the task list to print
     */
    public void showList(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).toString());
        }
    }

    /**
     * Prints the tasks matching a search keyword, numbered from 1.
     *
     * @param matches the tasks found by the search
     */
    public void showFindResults(List<Task> matches) {
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            System.out.println((i + 1) + "." + matches.get(i).toString());
        }
    }

    /**
     * Prints an error or status message.
     *
     * @param message the message to print
     */
    public void showError(String message) {
        showMessage(message);
    }

    /**
     * Prints the error for a task number that is out of range.
     *
     * @param number the invalid task number given by the user
     * @param taskCount the number of tasks in the list
     */
    public void showNoTaskError(int number, int taskCount) {
        showMessage("There's no task at number " + number + ". You have " + taskCount + " tasks.");
    }
}