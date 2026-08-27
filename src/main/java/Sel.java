import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
    private static final Path DATA_FILE = Paths.get("data", "sel.txt");

    private static final DateTimeFormatter input_format =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private static LocalDateTime parseDateTime(String input) {
        try {
            return LocalDateTime.parse(input.trim(), input_format);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Invalid date/time format. Please use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800).");
        }
    }

    private static LocalDateTime parseStoredDateTime(String input) {
        try {
            return LocalDateTime.parse(input.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid stored date/time: " + input);
        }
    }

    private static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<Task>();

        try {
            Path parent = DATA_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(DATA_FILE)) {
                Files.createFile(DATA_FILE);
                return tasks;
            }

            List<String> lines = Files.readAllLines(DATA_FILE);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.isEmpty()) {
                    continue;
                }

                try {
                    Task loadedTask = parseTask(line);
                    tasks.add(loadedTask);
                } catch (IllegalArgumentException e) {
                    System.out.println("WARNING: skipped corrupted data on line " + (i + 1) + ".");
                }
            }
        } catch (IOException e) {
            System.out.println("WARNING: failed to load tasks from " + DATA_FILE + ".");
        }
        return tasks;
    }

    private static Task parseTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);

        if (parts.length < 3) {
            throw new IllegalArgumentException("Not enough fields");
        }

        String type = parts[0];
        String status = parts[1];
        String description = parts[2];

        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("Invalid status");
        }

        if (description.isEmpty()) {
            throw new IllegalArgumentException("Missing description");
        }

        Task loadedTask;

        switch (type) {
        case "T":
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid todo format");
            }
            loadedTask = new ToDo(description);
            break;

        case "D":
            if (parts.length != 4 || parts[3].isEmpty()) {
                throw new IllegalArgumentException("Invalid deadline format");
            }
            loadedTask = new Deadline(description, parseStoredDateTime(parts[3]));
            break;

        case "E":
            if (parts.length == 5 && !parts[3].isEmpty() && !parts[4].isEmpty()) {
                loadedTask = new Event(description, parseStoredDateTime(parts[3]), parseStoredDateTime(parts[4]));
            } else if (parts.length == 4 && !parts[3].isEmpty()) {
                String[] range = splitLegacyEventRange(parts[3]);
                loadedTask = new Event(description, parseStoredDateTime(range[0]), parseStoredDateTime(range[1]));
            } else {
                throw new IllegalArgumentException("Invalid event format");
            }
            break;

        default:
            throw new IllegalArgumentException("Unknown task type");
        }

        if (status.equals("1")) {
            loadedTask.mark();
        }

        return loadedTask;
    }

    private static String[] splitLegacyEventRange(String timeRange) {
        int toIndex = timeRange.indexOf(" to ");
        if (toIndex >= 0) {
            String from = timeRange.substring(0, toIndex).trim();
            String to = timeRange.substring(toIndex + 4).trim();
            if (!from.isEmpty() && !to.isEmpty()) {
                return new String[] {from, to};
            }
        }

        int dashIndex = timeRange.lastIndexOf('-');
        if (dashIndex > 0 && dashIndex < timeRange.length() - 1) {
            String from = timeRange.substring(0, dashIndex).trim();
            String to = timeRange.substring(dashIndex + 1).trim();
            if (!from.isEmpty() && !to.isEmpty()) {
                return new String[] {from, to};
            }
        }

        throw new IllegalArgumentException("Invalid event range");
    }

    private static void saveTasks(List<Task> tasks) {
        List<String> lines = new ArrayList<String>();

        for (Task currentTask : tasks) {
            String status = currentTask.isDone ? "1" : "0";

            if (currentTask instanceof ToDo) {
                lines.add("T | " + status + " | " + currentTask.description);
            } else if (currentTask instanceof Deadline) {
                Deadline deadline = (Deadline) currentTask;
                lines.add("D | " + status + " | " + deadline.description
                    + " | " + deadline.getDDL());
            } else if (currentTask instanceof Event) {
                Event event = (Event) currentTask;
                lines.add("E | " + status + " | " + event.description
                    + " | " + event.getFrom() + " | " + event.getTo());
            }
        }

        try {
            Path parent = DATA_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(DATA_FILE, lines);
        } catch (IOException e) {
            System.out.println("WARNING: failed to save tasks to " + DATA_FILE + ".");
        }
    }

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

        List<Task> task = loadTasks();

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
                    saveTasks(task);

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
                    saveTasks(task);

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
                    saveTasks(task);

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
                saveTasks(task);

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

                LocalDateTime parsedDdl;
                try {
                    parsedDdl = parseDateTime(ddl);
                } catch (IllegalArgumentException e) {
                    System.out.println(new SelException("Bro, " + e.getMessage()));
                    continue;
                }

                task.add(new Deadline(description, parsedDdl));
                saveTasks(task);

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

                LocalDateTime parsedFrom;
                LocalDateTime parsedTo;
                try {
                    parsedFrom = parseDateTime(from);
                    parsedTo = parseDateTime(to);
                } catch (IllegalArgumentException e) {
                    System.out.println(new SelException("Bro, " + e.getMessage()));
                    continue;
                }

                task.add(new Event(description, parsedFrom, parsedTo));
                saveTasks(task);

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