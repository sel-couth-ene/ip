package sel.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import sel.command.CommandType;
import sel.exception.SelException;

/**
 * Deals with making sense of the user command: identifying the command
 * type and extracting the arguments it needs.
 */
public class Parser {
    // Format expected when the user types a date/time on the command line,
    // e.g. "2019-12-02 1800"
    private static final DateTimeFormatter INPUT_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /** Identifies which command the user typed. */
    public static CommandType parseCommandType(String fullCommand) {
        String commandWord = fullCommand.trim().split("\\s+", 2)[0];

        switch (commandWord) {
        case "bye":
            return CommandType.BYE;
        case "list":
            return CommandType.LIST;
        case "mark":
            return CommandType.MARK;
        case "unmark":
            return CommandType.UNMARK;
        case "delete":
            return CommandType.DELETE;
        case "todo":
            return CommandType.TODO;
        case "deadline":
            return CommandType.DEADLINE;
        case "event":
            return CommandType.EVENT;
        case "find":
            return CommandType.FIND;
        default:
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Extracts the (0-based) task index from a "mark"/"unmark"/"delete"
     * style command, e.g. "mark 3" -> 2.
     */
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

    /**
     * Extracts a single free-text argument from a command, e.g.
     * "todo read book" -> "read book".
     */
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

    /**
     * Extracts { description, deadline } from a "deadline ... /by ..." command.
     */
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

    /**
     * Extracts { description, from, to } from an
     * "event ... /from ... /to ..." command.
     */
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

    /**
     * Parses a user-typed date/time string (e.g. "2019-12-02 1800") into a
     * LocalDateTime.
     */
    public static LocalDateTime parseDateTime(String input) throws SelException {
        try {
            return LocalDateTime.parse(input.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new SelException(
                "Bro, invalid date/time format. Please use yyyy-MM-dd HHmm (e.g. 2019-12-02 1800).");
        }
    }
}