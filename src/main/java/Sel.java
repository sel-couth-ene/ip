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
            if (!scanner.hasNextLine()) {
                break;
            }
            
            String command = scanner.nextLine();
            
            if (command.equals("bye")) {
                System.out.println(line_break 
                    + "\nBye see ya later alligator.\n" 
                    + line_break);
                break;
            }

            else if (command.equals("list")) {
                System.out.println(line_break 
                    + "\nBro why do you want to see the list???");
                
                for (int i = 0; i < task.size(); i++) {
                    Task t = task.get(i);
                    System.out.println((i + 1) + "." + t.toString());
                }

                System.out.println(line_break);
                continue;
            }

            else if (command.equals("mark") || command.startsWith("mark ")) {
                
                if (command.equals("mark")) {
                    System.out.println(
                        new SelException("Bro, you need to tell me which task to mark :("));
                    continue;
                }

                try {
                    int index = Integer.parseInt(command.substring(5).trim()) - 1;

                    if (index < 0 || index >= task.size()) {
                        System.out.println(
                            new SelException("Bro, that task doesn't exist :("));
                        continue;
                    }

                    task.get(index).mark();

                    System.out.println(line_break
                        + "\nMarked task as done: \n"
                        + task.get(index).toString()
                        + "\n"
                        + line_break);

                } catch (NumberFormatException e) {
                    System.out.println(
                        new SelException("Bro, give me a valid task number :("));
                }
                continue;
            }

            else if (command.equals("unmark") || command.startsWith("unmark ")) {

                if (command.equals("unmark")) {
                    System.out.println(
                        new SelException("Bro, you need to tell me which task to unmark :("));
                    continue;
                }

                try {
                    int index = Integer.parseInt(command.substring(7).trim()) - 1;

                    if (index < 0 || index >= task.size()) {
                        System.out.println(
                            new SelException("Bro, that task doesn't exist :("));
                        continue;
                    }

                    task.get(index).unmark();

                    System.out.println(line_break
                        + "\nUnmarked task as done: \n"
                        + task.get(index).toString()
                        + "\n"
                        + line_break
                    );

                } catch (NumberFormatException e) {
                    System.out.println(
                        new SelException("Bro, give me a valid task number :("));
                }
                continue;
            }

            else if (command.equals("todo") || command.startsWith("todo ")) {

                if (command.equals("todo")) {
                    System.out.println(
                        new SelException("Bro, you need to tell me what's the task :("));
                    continue;
                }

                String description = command.substring(5).trim();

                if (description.isEmpty()) {
                    System.out.println(
                        new SelException("Bro, you need to tell me what's the task :("));
                    continue;
                }

                task.add(new ToDo(description));

                System.out.println(line_break
                    + "\nWhy more work for you?!?! \n"
                    + task.get(task.size() - 1).toString()
                    + "\nNow "
                    + task.size()
                    + " task(s) on your list bruh...\n"
                    + line_break);
            }

            else if (command.equals("deadline") || command.startsWith("deadline ")) {

                if (command.equals("deadline")) {
                    System.out.println(
                        new SelException("Bro, you need to tell me what's the task :("));
                    continue;
                }

                int byIndex = command.indexOf("/by");

                if (byIndex < 0) {
                    System.out.println(
                        new SelException("Bro, you need to tell me when's the deadline :("));
                    continue;
                }

                String description = command.substring(9, byIndex).trim();

                String ddl = command.substring(byIndex + 3).trim();

                if (description.isEmpty()) {
                    System.out.println(
                        new SelException("Bro, you need to tell me what's the task :("));
                    continue;
                }

                if (ddl.isEmpty()) {
                    System.out.println(
                        new SelException("Bro, you need to tell me when's the deadline :("));
                    continue;
                }

                task.add(new Deadline(description, ddl));

                System.out.println(line_break
                    + "\nWhy more work for you?!?! \n"
                    + task.get(task.size() - 1).toString()
                    + "\nNow "
                    + task.size()
                    + " task(s) on your list bruh...\n"
                    + line_break);
            }

             else if (command.equals("event") || command.startsWith("event ")) {

                if (command.equals("event")) {
                    System.out.println(
                        new SelException("Bro, you need to tell me what's the event :("));
                    continue;
                }

                int fromIndex = command.indexOf("/from");

                if (fromIndex < 0) {
                    System.out.println(
                        new SelException("Bro, you need to tell me when's the start date/time :("));
                    continue;
                }

                int toIndex = command.indexOf(
                    "/to",
                    fromIndex + 5
                );

                if (toIndex < 0) {
                    System.out.println(
                        new SelException("Bro, you need to tell me when's the end date/time :("));
                    continue;
                }

                String description = command.substring(6, fromIndex).trim();

                String from =
                    command.substring(
                        fromIndex + 5,
                        toIndex
                    ).trim();

                String to = command.substring(toIndex + 3).trim();

                if (description.isEmpty()) {
                    System.out.println(
                        new SelException("Bro, you need to tell me what's the event :("));
                    continue;
                }

                if (from.isEmpty()) {
                    System.out.println(
                        new SelException("Bro, you need to tell me when's the start date/time :("));
                    continue;
                }

                if (to.isEmpty()) {
                    System.out.println(
                        new SelException("Bro, you need to tell me when's the end date/time :("));
                    continue;
                }

                task.add(new Event(description, from, to));

                System.out.println(line_break
                    + "\nWhy more work for you?!?! \n"
                    + task.get(task.size() - 1).toString()
                    + "\nNow "
                    + task.size()
                    + " task(s) on your list bruh...\n"
                    + line_break);
            }

            else {
                System.out.println(
                    new SelException("Rephrase your words, no idea what u mean bro."));
            }
        }
        scanner.close();
    }
}