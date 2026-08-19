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
        String[] tasks  = new String[100];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else if (input.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println("added: " + input);
            }
        }


    }
}
