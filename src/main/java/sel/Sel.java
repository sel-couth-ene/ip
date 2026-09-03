package sel;

import java.time.LocalDateTime;
import java.util.List;

import sel.command.CommandType;
import sel.exception.SelException;
import sel.parser.Parser;
import sel.storage.Storage;
import sel.task.Deadline;
import sel.task.Event;
import sel.task.Task;
import sel.task.TaskList;
import sel.task.ToDo;
import sel.ui.Ui;

/**
 * Entry point and command processor for the Sel chatbot.
 */
public class Sel {

    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Creates a new Sel instance and loads saved tasks.
     *
     * @param filePath path to the save file.
     */
    public Sel(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath, ui);
        try {
            tasks = new TaskList(storage.load());
        } catch (SelException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Returns Sel's response to one command. This method is used by the GUI.
     *
     * @param input command entered by the user.
     * @return response to display in the chat window.
     */
    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Bro, type something first :(";
        }

        String command = input.trim();
        CommandType commandType = Parser.parseCommandType(command);

        try {
            switch (commandType) {
                case BYE:
                    return "Bye see ya later alligator.";
                case LIST:
                    return getTaskListResponse();
                case MARK:
                    return handleMark(command);
                case UNMARK:
                    return handleUnmark(command);
                case DELETE:
                    return handleDelete(command);
                case TODO:
                    return handleTodo(command);
                case DEADLINE:
                    return handleDeadline(command);
                case EVENT:
                    return handleEvent(command);
                case FIND:
                    return handleFind(command);
                default:
                    return "Rephrase your words, no idea what u mean bro.";
            }
        } catch (SelException e) {
            return e.getMessage();
        }
    }

    /**
     * Runs the original console version of Sel.
     */
    public void run() {
        ui.showWelcome();

        boolean isRunning = true;
        while (isRunning) {
            String command = ui.readCommand();
            if (command == null) {
                break;
            }

            if (Parser.parseCommandType(command) == CommandType.BYE) {
                ui.showGoodbye();
                isRunning = false;
            } else {
                System.out.println(getResponse(command));
            }
        }

        ui.close();
    }

    private String handleMark(String command) throws SelException {
        int index = Parser.parseIndex(command, "mark",
                "Bro, you need to tell me which task to mark :(",
                "Bro, give me a valid task number :(");

        if (!tasks.isValidIndex(index)) {
            throw new SelException("Bro, that task doesn't exist :(");
        }

        tasks.mark(index);
        storage.save(tasks.asList());
        return "Marked task as done:\n" + tasks.get(index);
    }

    private String handleUnmark(String command) throws SelException {
        int index = Parser.parseIndex(command, "unmark",
                "Bro, you need to tell me which task to unmark :(",
                "Bro, give me a valid task number :(");

        if (!tasks.isValidIndex(index)) {
            throw new SelException("Bro, that task doesn't exist :(");
        }

        tasks.unmark(index);
        storage.save(tasks.asList());
        return "Unmarked task:\n" + tasks.get(index);
    }

    private String handleDelete(String command) throws SelException {
        int index = Parser.parseIndex(command, "delete",
                "Bro, you need to tell me which task to delete :(",
                "Bro, give me a valid task number :(");

        if (!tasks.isValidIndex(index)) {
            throw new SelException("Bro, that task doesn't exist :(");
        }

        Task deletedTask = tasks.delete(index);
        storage.save(tasks.asList());
        return "Yay! You have fewer tasks now!\n"
                + deletedTask + "\nNow " + tasks.size() + " task(s) on your list bruh...";
    }

    private String handleTodo(String command) throws SelException {
        String description = Parser.parseSimpleArgument(command, "todo",
                "Bro, you need to tell me what's the task :(");

        Task task = new ToDo(description);
        tasks.add(task);
        storage.save(tasks.asList());
        return getAddedTaskResponse(task);
    }

    private String handleDeadline(String command) throws SelException {
        String[] args = Parser.parseDeadlineArgs(command);
        LocalDateTime ddl = Parser.parseDateTime(args[1]);

        Task task = new Deadline(args[0], ddl);
        tasks.add(task);
        storage.save(tasks.asList());
        return getAddedTaskResponse(task);
    }

    private String handleEvent(String command) throws SelException {
        String[] args = Parser.parseEventArgs(command);
        LocalDateTime from = Parser.parseDateTime(args[1]);
        LocalDateTime to = Parser.parseDateTime(args[2]);

        Task task = new Event(args[0], from, to);
        tasks.add(task);
        storage.save(tasks.asList());
        return getAddedTaskResponse(task);
    }

    private String handleFind(String command) throws SelException {
        String keyword = Parser.parseSimpleArgument(command, "find",
                "Bro, you need to tell me what to search for :(");

        List<Task> matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            return "Bro, nothing in your list matches that keyword :(";
        }

        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 0; i < matches.size(); i++) {
            response.append(i + 1).append(".").append(matches.get(i));
            if (i < matches.size() - 1) {
                response.append("\n");
            }
        }
        return response.toString();
    }

    private String getTaskListResponse() {
        if (tasks.size() == 0) {
            return "Your task list is empty bro.";
        }

        StringBuilder response = new StringBuilder(
                "Bro why do you want to see the list??? anyway here it is:\n");
        for (int i = 0; i < tasks.size(); i++) {
            response.append(i + 1).append(".").append(tasks.get(i));
            if (i < tasks.size() - 1) {
                response.append("\n");
            }
        }
        return response.toString();
    }

    private String getAddedTaskResponse(Task task) {
        return "Why more work for you?!?!\n"
                + task + "\nNow " + tasks.size() + " task(s) on your list bruh...";
    }

    /**
     * Starts the original console version.
     *
     * @param args unused command-line arguments.
     */
    public static void main(String[] args) {
        new Sel("data/sel.txt").run();
    }
}
