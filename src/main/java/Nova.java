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
        Task[] tasks  = new Task[100];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String input = scanner.nextLine();
            String[] parts = input.split(" ", 2);
            String command = parts[0];
            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else if (command.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    String status = tasks[i].isDone() ? "[X]" : "[ ]";
                    System.out.println((i + 1) + "." + status + " " + tasks[i].getDescription());
                }

            } else if (command.equals("mark")) {
                if (parts.length == 2) {
                    int index = Integer.parseInt(parts[1]);
                    System.out.println("Nice! I've marked this task as done: ");
                    tasks[index - 1].markDone();
                    String status = tasks[index - 1].isDone() ? "[X]" : "[ ]";
                    System.out.println(status + " " + tasks[index - 1].getDescription());
                }
            } else if (command.equals("unmark")) {
                if (parts.length == 2) {
                    int index = Integer.parseInt(parts[1]);
                    System.out.println("Ok, I've marked this task as not done yet: ");
                    tasks[index - 1].markUndone();
                    String status = tasks[index - 1].isDone() ? "[X]" : "[ ]";
                    System.out.println(status + " " + tasks[index - 1].getDescription());
                }
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println("added: " + input);
            }
        }


    }
}
