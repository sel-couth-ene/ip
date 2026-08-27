import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {
    private static final DateTimeFormatter INPUT_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    public static Commandtype parseCommandType(String fullCommand) {
        String commandWord = fullCommand.trim().split("\\s+", 2)[0];

        switch (commandWord) {
        case "bye":
            return Commandtype.BYE;
        case "list":
            return Commandtype.LIST;
        case "mark":
            return Commandtype.MARK;
        case "unmark":
            return Commandtype.UNMARK;
        case "delete":
            return Commandtype.DELETE;
        case "todo":
            return Commandtype.TODO;
        case "deadline":
            return Commandtype.DEADLINE;
        case "event":
            return Commandtype.EVENT;
        default:
            return Commandtype.UNKNOWN;
        }
    }

    public static int parseIndex(String fullCommand, String commandWord,
            String missingArgMessage, String invalidNumberMessage) throws SelException {
        if (fullCommand.equals(commandWord)) {
            throw new SelException(missingArgMessage);
        }

        try {
            return Integer.parseInt(fullCommand.substring(commandWord.length() + 1).trim()) - 1;
        } catch (NumberFormatException e) {
            throw new SelException(invalidNumberMessage);
        }
    }

    public static String parseSimpleArgument(String fullCommand, String commandWord,
            String errorMessage) throws SelException {
        if (fullCommand.equals(commandWord)) {
            throw new SelException(errorMessage);
        }

        String argument = fullCommand.substring(commandWord.length()).trim();
        if (argument.isEmpty()) {
            throw new SelException(errorMessage);
        }
        return argument;
    }

    public static String[] parseDeadlineArgs(String fullCommand) throws SelException {
        if (fullCommand.equals("deadline")) {
            throw new SelException("Bro, you need to tell me what's the task :(");
        }

        int byIndex = fullCommand.indexOf("/by");
        if (byIndex < 0) {
            throw new SelException("Bro, you need to tell me when's the deadline :(");
        }

        String description = fullCommand.substring("deadline ".length(), byIndex).trim();
        String ddl = fullCommand.substring(byIndex + "/by".length()).trim();

        if (description.isEmpty()) {
            throw new SelException("Bro, you need to tell me what's the task :(");
        }
        if (ddl.isEmpty()) {
            throw new SelException("Bro, you need to tell me when's the deadline :(");
        }

        return new String[] {description, ddl};
    }

    public static String[] parseEventArgs(String fullCommand) throws SelException {
        if (fullCommand.equals("event")) {
            throw new SelException("Bro, you need to tell me what's the event :(");
        }

        int fromIndex = fullCommand.indexOf("/from");
        if (fromIndex < 0) {
            throw new SelException("Bro, you need to tell me when's the start date/time :(");
        }

        int toIndex = fullCommand.indexOf("/to", fromIndex + "/from".length());
        if (toIndex < 0) {
            throw new SelException("Bro, you need to tell me when's the end date/time :(");
        }

        String description = fullCommand.substring("event ".length(), fromIndex).trim();
        String from = fullCommand.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = fullCommand.substring(toIndex + "/to".length()).trim();

        if (description.isEmpty()) {
            throw new SelException("Bro, you need to tell me what's the event :(");
        }
        if (from.isEmpty()) {
            throw new SelException("Bro, you need to tell me when's the start date/time :(");
        }
        if (to.isEmpty()) {
            throw new SelException("Bro, you need to tell me when's the end date/time :(");
        }

        return new String[] {description, from, to};
    }

    public static LocalDateTime parseDateTime(String input) throws SelException {
        try {
            return LocalDateTime.parse(input.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new SelException(
                "Bro, invalid date/time format. Please use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800).");
        }
    }
}