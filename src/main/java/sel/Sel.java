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

public class Sel {

    private Storage storage;
    private TaskList tasks;
    private Ui ui;

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

    private void handleMark(String command) throws SelException {
        int index = Parser.parseIndex(command, "mark",
            "Bro, you need to tell me which task to mark :(",
            "Bro, give me a valid task number :(");

        if (!tasks.isValidIndex(index)) {
            throw new SelException("Bro, that task doesn't exist :(");
        }

        tasks.mark(index);
        storage.save(tasks.asList());
        ui.showTaskMarked(tasks.get(index));
    }

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

    private void handleTodo(String command) throws SelException {
        String description = Parser.parseSimpleArgument(command, "todo",
            "Bro, you need to tell me what's the task :(");

        tasks.add(new ToDo(description));
        storage.save(tasks.asList());
        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    private void handleDeadline(String command) throws SelException {
        String[] args = Parser.parseDeadlineArgs(command);
        String description = args[0];
        LocalDateTime ddl = Parser.parseDateTime(args[1]);

        tasks.add(new Deadline(description, ddl));
        storage.save(tasks.asList());
        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

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

    public static void main(String[] args) {
        new Sel("data/sel.txt").run();
    }
}