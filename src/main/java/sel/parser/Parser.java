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

    /**
     * Identifies the command type from the first word of the input.
     *
     * @param fullCommand the raw command entered by the user.
     * @return the corresponding command type.
     */
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
     * style command, e.g. {@code "mark 3"} becomes {@code 2}.
     *
     * @param fullCommand the raw command line typed by the user.
     * @param commandWord the command word to strip off (e.g. {@code "mark"}).
     * @param missingArgMessage message to use if no index was given at all.
     * @param invalidNumberMessage message to use if the given index is not
     *     a valid number.
     * @return the zero-based task index.
     * @throws SelException if the index is missing or not a valid number.
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
     * {@code "todo read book"} becomes {@code "read book"}.
     *
     * @param fullCommand the raw command line typed by the user.
     * @param commandWord the command word to strip off (e.g. {@code "todo"}).
     * @param errorMessage message to use if no argument was given.
     * @return the trimmed argument text.
     * @throws SelException if the argument is missing or blank.
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
     * Extracts the description and deadline from a
     * {@code "deadline ... /by ..."} command.
     *
     * @param fullCommand the raw command line typed by the user.
     * @return a two-element array of {@code {description, deadline}}.
     * @throws SelException if the {@code /by} marker, description, or
     *     deadline text is missing.
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
     * Extracts the description, start time, and end time from an
     * {@code "event ... /from ... /to ..."} command.
     *
     * @param fullCommand the raw command line typed by the user.
     * @return a three-element array of {@code {description, from, to}}.
     * @throws SelException if the {@code /from}/{@code /to} markers,
     *     description, start time, or end time is missing.
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
     * Parses a user-typed date/time string (e.g. {@code "2019-12-02 1800"})
     * into a {@link LocalDateTime}.
     *
     * @param input the raw date/time text typed by the user.
     * @return the parsed date/time.
     * @throws SelException if the text does not match the expected
     *     {@code yyyy-MM-dd HHmm} format.
     */
    public static LocalDateTime parseDateTime(String input) throws SelException {
        try {
            return LocalDateTime.parse(input.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new SelException(
                    "Bro, invalid date/time format. Please use yyyy-MM-dd HHmm "
                    + "(e.g. 2019-12-02 1800).");
        }
    }
}
