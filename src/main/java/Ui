import java.util.Scanner;

public class Ui {
    private static final String LINE_BREAK = "-----------------------------------------";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

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

    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    public void showGoodbye() {
        System.out.println(LINE_BREAK
            + "\nBye see ya later alligator.\n"
            + LINE_BREAK);
    }

    public void showLoadingError() {
        System.out.println("WARNING: failed to load tasks from disk.");
    }

    public void showCorruptedLineWarning(int lineNumber) {
        System.out.println("WARNING: skipped corrupted data on line " + lineNumber + ".");
    }

    public void showSavingError() {
        System.out.println("WARNING: failed to save tasks to disk.");
    }

    public void showError(String message) {
        System.out.println(new SelException(message));
    }

    public void showTaskList(TaskList tasks) {
        System.out.println(LINE_BREAK
            + "\nBro why do you want to see the list??? anyway here it is:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).toString());
        }
        System.out.println(LINE_BREAK);
    }

    public void showTaskMarked(Task task) {
        System.out.println(LINE_BREAK
            + "\nMarked task as done: \n"
            + task.toString()
            + "\n"
            + LINE_BREAK);
    }

    public void showTaskUnmarked(Task task) {
        System.out.println(LINE_BREAK
            + "\nUnmarked task as done: \n"
            + task.toString()
            + "\n"
            + LINE_BREAK);
    }

    public void showTaskDeleted(Task task, int remainingCount) {
        System.out.println(LINE_BREAK
            + "\nYay! You have fewer tasks now! \n"
            + task.toString()
            + "\nNow "
            + remainingCount
            + " task(s) on your list bruh...\n"
            + LINE_BREAK);
    }

    public void showTaskAdded(Task task, int newCount) {
        System.out.println(LINE_BREAK
            + "\nWhy more work for you?!?! \n"
            + task.toString()
            + "\nNow "
            + newCount
            + " task(s) on your list bruh...\n"
            + LINE_BREAK);
    }

    public void close() {
        scanner.close();
    }
}