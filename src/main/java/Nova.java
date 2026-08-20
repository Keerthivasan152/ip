import java.util.Scanner;

public class Nova {
    public static void main(String[] args) {
        String banner = " _   _\n"
                + "| \\ | | _____   ____ _\n"
                + "|  \\| |/ _ \\ \\ / / _` |\n"
                + "| |\\  | (_) \\ V / (_| |\n"
                + "|_| \\_|\\___/ \\_/ \\__,_|\n";

        System.out.println(banner);
        System.out.println("Hello! I'm Nova. What can I do for you?");
        Task[] tasks = new Task[100];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            String[] parts = input.split(" ", 2);
            String command = parts[0];

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else if (command.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i].toString());
                }
            } else if (command.equals("todo")) {
                if (parts.length == 2 && !parts[1].trim().isEmpty()) {
                    String description = parts[1].trim();
                    tasks[taskCount] = new Todo(description);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks[taskCount].toString());
                    System.out.println("Now you have " + (taskCount + 1) + " tasks in the list.");
                    taskCount++;
                } else {
                    System.out.println("The description of a todo cannot be empty.");
                }
            } else if (command.equals("deadline")) {
                String rest = parts.length == 2 ? parts[1] : "";
                String[] byParts = rest.split("/by", 2);
                boolean valid = parts.length == 2
                        && byParts.length == 2
                        && !byParts[0].trim().isEmpty()
                        && !byParts[1].trim().isEmpty();
                if (valid) {
                    String description = byParts[0].trim();
                    String by = byParts[1].trim();
                    tasks[taskCount] = new Deadline(description, by);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks[taskCount].toString());
                    System.out.println("Now you have " + (taskCount + 1) + " tasks in the list.");
                    taskCount++;
                } else {
                    System.out.println("Please give a deadline like: deadline <description> /by <date>");
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
                    String description = fromParts[0].trim();
                    String from = toParts[0].trim();
                    String to = toParts[1].trim();
                    tasks[taskCount] = new Event(description, from, to);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks[taskCount].toString());
                    System.out.println("Now you have " + (taskCount + 1) + " tasks in the list.");
                    taskCount++;
                } else {
                    System.out.println("Please give an event like: event <description> /from <start> /to <end>");
                }
            } else if (command.equals("mark") || command.equals("unmark")) {
                if (parts.length == 2) {
                    int index = getTaskIndex(parts[1], taskCount);
                    if (index >= 0) {
                        if (command.equals("mark")) {
                            tasks[index].markDone();
                            System.out.println("Nice! I've marked this task as done: ");
                        } else {
                            tasks[index].markUndone();
                            System.out.println("Ok, I've marked this task as not done yet: ");
                        }
                        System.out.println(tasks[index].toString());
                    }
                } else {
                    System.out.println("Please give a task number, e.g. mark 2");
                }
            } else {
                System.out.println("I don't know that command. Try: todo, deadline, event, list, mark, unmark, bye");
            }
        }
    }

    private static int getTaskIndex(String argument, int taskCount) {
        int number;
        try {
            number = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            System.out.println("That doesn't look like a valid task number, e.g. mark 2");
            return -1;
        }
        if (number < 1 || number > taskCount) {
            System.out.println("There's no task at number " + number + ". You have " + taskCount + " tasks.");
            return -1;
        }
        return number - 1;
    }
}
