package nova;

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
    public static final String MESSAGE_INVALID_COMMAND =
            "I don't know that command. Try: todo, deadline, event, list, mark, unmark, delete, bye";

    private static final String BANNER = " _   _\n"
            + "| \\ | | _____   ____ _\n"
            + "|  \\| |/ _ \\ \\ / / _` |\n"
            + "| |\\  | (_) \\ V / (_| |\n"
            + "|_| \\_|\\___/ \\_/ \\__,_|\n";

    public void greet() {
        System.out.println(BANNER);
        System.out.println("Hello! I'm Nova. What can I do for you?");
    }

    public void showBye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    public void showTaskAdded(Task task, int totalCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + totalCount + " tasks in the list.");
    }

    public void showTaskDeleted(Task task, int totalCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + totalCount + " tasks in the list.");
    }

    public void showTaskStatus(Task task, boolean done) {
        if (done) {
            System.out.println("Nice! I've marked this task as done: ");
        } else {
            System.out.println("Ok, I've marked this task as not done yet: ");
        }
        System.out.println(task.toString());
    }

    public void showList(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).toString());
        }
    }

    public void showError(String message) {
        System.out.println(message);
    }

    public void showNoTaskError(int number, int taskCount) {
        System.out.println("There's no task at number " + number + ". You have " + taskCount + " tasks.");
    }
}