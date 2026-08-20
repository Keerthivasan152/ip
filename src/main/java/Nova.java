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

            } else if (command.equals("mark")) {
                if (parts.length == 2) {
                    int index = Integer.parseInt(parts[1]);
                    System.out.println("Nice! I've marked this task as done: ");
                    tasks[index - 1].markDone();
                    System.out.println(tasks[index - 1].toString());
                }
            } else if (command.equals("unmark")) {
                if (parts.length == 2) {
                    int index = Integer.parseInt(parts[1]);
                    System.out.println("Ok, I've marked this task as not done yet: ");
                    tasks[index - 1].markUndone();
                    System.out.println(tasks[index - 1].toString());
                }
            } else {
                if (command.equals("todo")) {
                    String rest = parts[1];
                    tasks[taskCount] = new Todo(rest);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks[taskCount].toString());
                    System.out.println("Now you have " + (taskCount + 1) + " tasks in the list.");
                    taskCount++;
                } else if (command.equals("deadline")) {
                    String rest = parts[1];
                    String[] byParts = rest.split("/by", 2);
                    String desc = byParts[0].trim();
                    String by = byParts[1].trim();
                    tasks[taskCount] = new Deadline(desc, by);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks[taskCount].toString());
                    System.out.println("Now you have " + (taskCount + 1) + " tasks in the list.");
                    taskCount++;
                } else if (command.equals("event")) {
                    String rest = parts[1];
                    String[] fromParts = rest.split("/from", 2);
                    String desc = fromParts[0].trim();
                    String[] toParts = fromParts[1].split("/to", 2);
                    String from = toParts[0].trim();
                    String to = toParts[1].trim();
                    tasks[taskCount] = new Event(desc, from, to);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks[taskCount].toString());
                    System.out.println("Now you have " + (taskCount + 1) + " tasks in the list.");
                    taskCount++;
                }
                else {
                    tasks[taskCount] = new Todo(input);
                    taskCount++;
                    System.out.println("added: " + input);
                }

            }
       }

    }
    }
