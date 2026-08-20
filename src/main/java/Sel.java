import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

enum CommandType {
    BYE,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    UNKNOWN
}

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

            String commandWord = command.trim().split("\\s+", 2)[0];
            CommandType commandType;

            switch (commandWord) {
            case "bye":
                commandType = CommandType.BYE;
                break;
            case "list":
                commandType = CommandType.LIST;
                break;
            case "mark":
                commandType = CommandType.MARK;
                break;
            case "unmark":
                commandType = CommandType.UNMARK;
                break;
            case "delete":
                commandType = CommandType.DELETE;
                break;
            case "todo":
                commandType = CommandType.TODO;
                break;
            case "deadline":
                commandType = CommandType.DEADLINE;
                break;
            case "event":
                commandType = CommandType.EVENT;
                break;
            default:
                commandType = CommandType.UNKNOWN;
            }
            
            if (commandType == CommandType.BYE) {
                System.out.println(line_break 
                    + "\nBye see ya later alligator.\n" 
                    + line_break);
                break;
            }

            else if (commandType == CommandType.LIST) {
                System.out.println(line_break 
                    + "\nBro why do you want to see the list??? anyway here it is:");
                
                for (int i = 0; i < task.size(); i++) {
                    Task t = task.get(i);
                    System.out.println((i + 1) + "." + t.toString());
                }

                System.out.println(line_break);
                continue;
            }

            else if (commandType == CommandType.MARK) {
                
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

            else if (commandType == CommandType.UNMARK) {

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

            else if (commandType == CommandType.DELETE) {

                if (command.equals("delete")) {
                    System.out.println(new SelException("Bro, you need to tell me which task to delete :("));
                    continue;
                }

                try {
                    int index = Integer.parseInt(command.substring(7).trim()) - 1;

                    if (index < 0 || index >= task.size()) {
                        System.out.println(
                            new SelException("Bro, that task doesn't exist :("));
                        continue;
                    }

                    Task deletedTask = task.get(index);
                    task.remove(index);

                    System.out.println(line_break
                        + "\nYay! You have fewer tasks now! \n"
                        + deletedTask.toString()
                        + "\nNow "
                        + task.size()
                        + " task(s) on your list bruh...\n"
                        + line_break);

                } catch (NumberFormatException e) {
                    System.out.println(
                        new SelException("Bro, give me a valid task number :("));
                }
                continue;
            }

            else if (commandType == CommandType.TODO) {

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

            else if (commandType == CommandType.DEADLINE) {

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

             else if (commandType == CommandType.EVENT) {

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