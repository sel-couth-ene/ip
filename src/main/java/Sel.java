import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Sel {
    public static void main(String[] args) {
        String banner = " ____  _____ _     \n"
                      + "/ ___|| ____| |    \n"
                      + "\\___ \\|  _| | |    \n"
                      + " ___) | |___| |___ \n"
                      + "|____/|_____|_____|\n";
        System.out.println(banner);

        String line_break = "-----------------------------------------";
        System.out.println("Sup, I'm Sel.");
        System.out.println(line_break);

        Scanner scanner = new Scanner(System.in);

        List<Task> task = new ArrayList<Task>();

        while (true) {

            String command = scanner.nextLine();
            
            if (command.equals("bye")) {
                System.out.println(line_break + "\nBye see ya later alligator.\n" + line_break);
                break;
            }

            if (command.equals("list")) {
                System.out.println(line_break);
                for (int i = 0; i < task.size(); i++) {
                    Task t = task.get(i);
                    System.out.println((i + 1) + ".[" + t.getStatusIcon() + "] " + t.getDescription());
                }
                System.out.println(line_break);
                continue;
            }

            if (command.startsWith("mark ")) {
                int index = Integer.parseInt(command.substring(5)) - 1;
                task.get(index).mark();
                System.out.println(line_break + "\nMarked task as done: \n" + "[" + task.get(index).getStatusIcon() + "] " + task.get(index).getDescription() + "\n" + line_break);
                continue;
            }

            if (command.startsWith("unmark ")) {
                int index = Integer.parseInt(command.substring(7)) - 1;
                task.get(index).unmark();
                System.out.println(line_break + "\nUnmarked task as done: \n" + "[" + task.get(index).getStatusIcon() + "] " + task.get(index).getDescription() + "\n" + line_break);
                continue;
            }

            task.add(new Task(command));
            System.out.println(line_break + "\nadded: " + command + "\n" + line_break);
        }
        scanner.close();
    }
}