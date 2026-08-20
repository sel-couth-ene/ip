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
                System.out.println(line_break + "\nBro why do you want to see the list???");
                for (int i = 0; i < task.size(); i++) {
                    Task t = task.get(i);
                    System.out.println((i + 1) + "." + t.toString());
                }
                System.out.println(line_break);
                continue;
            }

            if (command.startsWith("mark ")) {
                int index = Integer.parseInt(command.substring(5)) - 1;
                task.get(index).mark();
                System.out.println(line_break + "\nMarked task as done: \n" + task.get(index).toString() + "\n" + line_break);
                continue;
            }

            if (command.startsWith("unmark ")) {
                int index = Integer.parseInt(command.substring(7)) - 1;
                task.get(index).unmark();
                System.out.println(line_break + "\nUnmarked task as done: \n" + task.get(index).toString() + "\n" + line_break);
                continue;
            }

            if (command.startsWith("todo ")) {
                String description = command.substring(5);
                task.add(new ToDo(description));
            }

            if (command.startsWith("deadline ")) {
                String description = command.substring(9, command.indexOf("/by"));
                String ddl = command.substring(command.indexOf("/by") + 3);
                task.add(new Deadline(description, ddl));
            }

            if (command.startsWith("event ")) {
                String description = command.substring(6, command.indexOf("/from"));
                String from = command.substring(command.indexOf("/from") + 5, command.indexOf("/to"));
                String to = command.substring(command.indexOf("/to") + 3);
                task.add(new Event(description, from, to));
            }

            System.out.println(line_break + "\nWhy more work for you?!?! \n" + task.get(task.size() - 1).toString() + "\n" + "Now " + task.size() + " task(s) on your list bruh...\n" + line_break);
        }
        scanner.close();
    }
}