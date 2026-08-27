package sel.ui;

import java.util.List;
import java.util.Scanner;

import sel.exception.SelException;
import sel.task.Task;
import sel.task.TaskList;

/**
 * Deals with interactions with the user: showing messages and reading input.
 */
public class Ui {
    private static final String LINE_BREAK = "-----------------------------------------";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Prints the startup banner and greeting. */
    public void showWelcome() {
        String banner = " ____  _____ _     \n"
                      + "/ ___|| ____| |    \n"
                      + "\\___ \\|  _| | |    \n"
                      + " ___) | |___| |___ \n"
                      + "|____/|_____|_____|\n";
        System.out.println(banner);
        System.out.println("Sup, I'm Sel.");
        System.out.println(LINE_BREAK);
    }

    /** Reads the next line of user input, or null if there is none left. */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    /** Prints the farewell message. */
    public void showGoodbye() {
        System.out.println(LINE_BREAK
            + "\nBye see ya later alligator.\n"
            + LINE_BREAK);
    }

    /** Prints a warning that saved tasks could not be loaded. */
    public void showLoadingError() {
        System.out.println("WARNING: failed to load tasks from disk.");
    }

    /** Prints a corrupted-line warning encountered while loading. */
    public void showCorruptedLineWarning(int lineNumber) {
        System.out.println("WARNING: skipped corrupted data on line " + lineNumber + ".");
    }

    /** Prints a warning that tasks could not be saved to disk. */
    public void showSavingError() {
        System.out.println("WARNING: failed to save tasks to disk.");
    }

    /** Prints an error message, wrapped the same way SelException prints. */
    public void showError(String message) {
        System.out.println(new SelException(message));
    }

    /** Prints the full task list. */
    public void showTaskList(TaskList tasks) {
        System.out.println(LINE_BREAK
            + "\nBro why do you want to see the list??? anyway here it is:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).toString());
        }
        System.out.println(LINE_BREAK);
    }

    /**
     * Prints the tasks matching a find command's keyword, numbered from 1.
     * If there are no matches, says so instead.
     *
     * @param matches the matching tasks to display.
     */
    public void showMatchingTasks(List<Task> matches) {
        System.out.println(LINE_BREAK);
        if (matches.isEmpty()) {
            System.out.println("Bro, nothing in your list matches that keyword :(");
        } else {
            System.out.println("Here are the matching tasks in your list:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.println((i + 1) + "." + matches.get(i).toString());
            }
        }
        System.out.println(LINE_BREAK);
    }

    /** Prints confirmation that a task was marked done. */
    public void showTaskMarked(Task task) {
        System.out.println(LINE_BREAK
            + "\nMarked task as done: \n"
            + task.toString()
            + "\n"
            + LINE_BREAK);
    }

    /** Prints confirmation that a task was unmarked. */
    public void showTaskUnmarked(Task task) {
        System.out.println(LINE_BREAK
            + "\nUnmarked task as done: \n"
            + task.toString()
            + "\n"
            + LINE_BREAK);
    }

    /** Prints confirmation that a task was deleted. */
    public void showTaskDeleted(Task task, int remainingCount) {
        System.out.println(LINE_BREAK
            + "\nYay! You have fewer tasks now! \n"
            + task.toString()
            + "\nNow "
            + remainingCount
            + " task(s) on your list bruh...\n"
            + LINE_BREAK);
    }

    /** Prints confirmation that a task was added. */
    public void showTaskAdded(Task task, int newCount) {
        System.out.println(LINE_BREAK
            + "\nWhy more work for you?!?! \n"
            + task.toString()
            + "\nNow "
            + newCount
            + " task(s) on your list bruh...\n"
            + LINE_BREAK);
    }

    /** Releases the input resource. */
    public void close() {
        scanner.close();
    }
}