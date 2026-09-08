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
 * Entry of Sel chatbot
 */
public class Sel {

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /**
     * Creates a new Sel instance, loading any previously saved tasks from
     * the given file path. If loading fails, starts with an empty task
     * list instead.
     *
     * @param filePath path to the save file to load tasks from and save
     *     tasks to.
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
     * Processes a user command and returns Sel's response.
     *
     * @param input the raw command entered by the user.
     * @return Sel's response to the command.
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
                    return getMarkResponse(command);
                case UNMARK:
                    return getUnmarkResponse(command);
                case DELETE:
                    return getDeleteResponse(command);
                case TODO:
                    return getTodoResponse(command);
                case DEADLINE:
                    return getDeadlineResponse(command);
                case EVENT:
                    return getEventResponse(command);
                case FIND:
                    return getFindResponse(command);
                default:
                    return "Rephrase your words, no idea what u mean bro.";
            }
        } catch (SelException e) {
            return e.getMessage();
        }
    }

    /**
     * Runs the main command loop: greets the user, repeatedly reads and
     * handles commands until {@code bye} is entered or input ends, then
     * says goodbye.
     */
    public void run() {
        ui.showWelcome();

        boolean isRunning = true;
        while (isRunning) {
            String command = ui.readCommand();
            if (command == null) {
                break;
            }

            CommandType commandType = Parser.parseCommandType(command);

            try {
                switch (commandType) {
                    case BYE:
                        ui.showGoodbye();
                        isRunning = false;
                        break;
                    case LIST:
                        ui.showTaskList(tasks);
                        break;
                    case MARK:
                        handleMark(command);
                        break;
                    case UNMARK:
                        handleUnmark(command);
                        break;
                    case DELETE:
                        handleDelete(command);
                        break;
                    case TODO:
                        handleTodo(command);
                        break;
                    case DEADLINE:
                        handleDeadline(command);
                        break;
                    case EVENT:
                        handleEvent(command);
                        break;
                    case FIND:
                        handleFind(command);
                        break;
                    default:
                        ui.showError("Rephrase your words, no idea what u mean bro.");
                }
            } catch (SelException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.close();
    }

    /**
     * Marks the task identified by the command and saves the updated task list.
     *
     * @param command the raw mark command.
     * @return the task that was marked.
     * @throws SelException if the task index is missing, invalid, or out of range.
     */
    private Task markTask(String command) throws SelException {
        int index = Parser.parseIndex(command, "mark",
                "Bro, you need to tell me which task to mark :(",
                "Bro, give me a valid task number :(");

        if (!tasks.isValidIndex(index)) {
            throw new SelException("Bro, that task doesn't exist :(");
        }

        tasks.mark(index);
        storage.save(tasks.asList());
        return tasks.get(index);
    }

    /**
     * Handles a {@code mark} command: marks the referenced task as done,
     * persists the change, and shows confirmation.
     *
     * @param command the raw command line typed by the user.
     * @throws SelException if the task index is missing, invalid, or does
     *     not refer to an existing task.
     */
    private void handleMark(String command) throws SelException {
        Task task = markTask(command);
        ui.showTaskMarked(task);
    }

    /**
     * Handles an {@code unmark} command: marks the referenced task as not
     * done, persists the change, and shows confirmation.
     *
     * @param command the raw command line typed by the user.
     * @throws SelException if the task index is missing, invalid, or does
     *     not refer to an existing task.
     */
    private void handleUnmark(String command) throws SelException {
        int index = Parser.parseIndex(command, "unmark",
            "Bro, you need to tell me which task to unmark :(",
            "Bro, give me a valid task number :(");

        if (!tasks.isValidIndex(index)) {
            throw new SelException("Bro, that task doesn't exist :(");
        }

        tasks.unmark(index);
        storage.save(tasks.asList());
        ui.showTaskUnmarked(tasks.get(index));
    }

    /**
     * Handles a {@code delete} command: removes the referenced task,
     * persists the change, and shows confirmation.
     *
     * @param command the raw command line typed by the user.
     * @throws SelException if the task index is missing, invalid, or does
     *     not refer to an existing task.
     */
    private void handleDelete(String command) throws SelException {
        int index = Parser.parseIndex(command, "delete",
            "Bro, you need to tell me which task to delete :(",
            "Bro, give me a valid task number :(");

        if (!tasks.isValidIndex(index)) {
            throw new SelException("Bro, that task doesn't exist :(");
        }

        var deletedTask = tasks.delete(index);
        storage.save(tasks.asList());
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

    /**
     * Handles a {@code todo} command: adds a new todo task, persists the
     * change, and shows confirmation.
     *
     * @param command the raw command line typed by the user.
     * @throws SelException if no task description was given.
     */
    private void handleTodo(String command) throws SelException {
        String description = Parser.parseSimpleArgument(command, "todo",
            "Bro, you need to tell me what's the task :(");

        tasks.add(new ToDo(description));
        storage.save(tasks.asList());
        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    /**
     * Handles a {@code deadline} command: adds a new deadline task,
     * persists the change, and shows confirmation.
     *
     * @param command the raw command line typed by the user.
     * @throws SelException if the description, {@code /by} marker, or
     *     date/time is missing or invalid.
     */
    private void handleDeadline(String command) throws SelException {
        String[] args = Parser.parseDeadlineArgs(command);
        String description = args[0];
        LocalDateTime ddl = Parser.parseDateTime(args[1]);

        tasks.add(new Deadline(description, ddl));
        storage.save(tasks.asList());
        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    /**
     * Handles an {@code event} command: adds a new event task, persists
     * the change, and shows confirmation.
     *
     * @param command the raw command line typed by the user.
     * @throws SelException if the description, {@code /from}/{@code /to}
     *     markers, or date/times are missing or invalid.
     */
    private void handleEvent(String command) throws SelException {
        String[] args = Parser.parseEventArgs(command);
        String description = args[0];
        LocalDateTime from = Parser.parseDateTime(args[1]);
        LocalDateTime to = Parser.parseDateTime(args[2]);

        tasks.add(new Event(description, from, to));
        storage.save(tasks.asList());
        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    private void handleFind(String command) throws SelException {
        String keyword = Parser.parseSimpleArgument(command, "find",
            "Bro, you need to tell me what to search for :(");

        List<Task> matches = tasks.find(keyword);
        ui.showMatchingTasks(matches);
    }

    private String getTaskListResponse() {
        if (tasks.size() == 0) {
            return "Your task list is empty bro.";
        }

        StringBuilder response = new StringBuilder(
                "Bro why do you want to see the list??? anyway here it is:\n");

        for (int i = 0; i < tasks.size(); i++) {
            response.append(i + 1)
                    .append(".")
                    .append(tasks.get(i));

            if (i < tasks.size() - 1) {
                response.append("\n");
            }
        }

        return response.toString();
    }

    private String getMarkResponse(String command) throws SelException {
        Task task = markTask(command);
        return "Marked task as done:\n" + task;
    }

    private String getUnmarkResponse(String command) throws SelException {
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

    private String getDeleteResponse(String command) throws SelException {
        int index = Parser.parseIndex(command, "delete",
                "Bro, you need to tell me which task to delete :(",
                "Bro, give me a valid task number :(");

        if (!tasks.isValidIndex(index)) {
            throw new SelException("Bro, that task doesn't exist :(");
        }

        Task deletedTask = tasks.delete(index);
        storage.save(tasks.asList());

        return "Yay! You have fewer tasks now!\n"
                + deletedTask
                + "\nNow "
                + tasks.size()
                + " task(s) on your list bruh...";
    }

    private String getTodoResponse(String command) throws SelException {
        String description = Parser.parseSimpleArgument(command, "todo",
                "Bro, you need to tell me what's the task :(");

        Task task = new ToDo(description);
        tasks.add(task);
        storage.save(tasks.asList());

        return getAddedTaskResponse(task);
    }

    private String getDeadlineResponse(String command) throws SelException {
        String[] args = Parser.parseDeadlineArgs(command);
        LocalDateTime ddl = Parser.parseDateTime(args[1]);

        Task task = new Deadline(args[0], ddl);
        tasks.add(task);
        storage.save(tasks.asList());

        return getAddedTaskResponse(task);
    }

    private String getEventResponse(String command) throws SelException {
        String[] args = Parser.parseEventArgs(command);
        LocalDateTime from = Parser.parseDateTime(args[1]);
        LocalDateTime to = Parser.parseDateTime(args[2]);

        Task task = new Event(args[0], from, to);
        tasks.add(task);
        storage.save(tasks.asList());

        return getAddedTaskResponse(task);
    }

    private String getFindResponse(String command) throws SelException {
        String keyword = Parser.parseSimpleArgument(command, "find",
                "Bro, you need to tell me what to search for :(");

        List<Task> matches = tasks.find(keyword);

        if (matches.isEmpty()) {
            return "Bro, nothing in your list matches that keyword :(";
        }

        StringBuilder response =
                new StringBuilder("Here are the matching tasks in your list:\n");

        for (int i = 0; i < matches.size(); i++) {
            response.append(i + 1)
                    .append(".")
                    .append(matches.get(i));

            if (i < matches.size() - 1) {
                response.append("\n");
            }
        }

        return response.toString();
    }

    private String getAddedTaskResponse(Task task) {
        return "Why more work for you?!?!\n"
                + task
                + "\nNow "
                + tasks.size()
                + " task(s) on your list bruh...";
    }

    /**
     * Starts the Sel application, loading and saving tasks to
     * {@code data/sel.txt} relative to the working directory.
     *
     * @param args unused.
     */
    public static void main(String[] args) {
        new Sel("data/sel.txt").run();
    }
}
