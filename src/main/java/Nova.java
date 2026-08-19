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
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            }
            System.out.println(input);
        }


    }
}
