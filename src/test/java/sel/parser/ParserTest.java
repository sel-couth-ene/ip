package sel.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sel.command.Commandtype;
import sel.exception.SelException;

public class ParserTest {

    @Test
    public void parseCommandtype_recognisesAllKnownCommands() {
        assertEquals(Commandtype.BYE, Parser.parseCommandType("bye"));
        assertEquals(Commandtype.LIST, Parser.parseCommandType("list"));
        assertEquals(Commandtype.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(Commandtype.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(Commandtype.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(Commandtype.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(Commandtype.DEADLINE, Parser.parseCommandType("deadline return book /by 2019-12-02 1800"));
        assertEquals(Commandtype.EVENT, Parser.parseCommandType("event meeting /from 2019-12-02 1400 /to 1600"));
    }

    @Test
    public void parseCommandType_unknownWord_returnsUnknown() {
        assertEquals(Commandtype.UNKNOWN, Parser.parseCommandType("frobnicate"));
    }

    @Test
    public void parseCommandType_isCaseSensitiveAndOnlyLooksAtFirstWord() {
        assertEquals(Commandtype.UNKNOWN, Parser.parseCommandType("BYE"));
        assertEquals(Commandtype.LIST, Parser.parseCommandType("list please"));
    }

    @Test
    public void parseCommandType_leadingWhitespace_stillRecognised() {
        assertEquals(Commandtype.BYE, Parser.parseCommandType("   bye"));
    }

    @Test
    public void parseIndex_validNumber_returnsZeroBasedIndex() throws SelException {
        int index = Parser.parseIndex("mark 3", "mark", "missing", "invalid");
        assertEquals(2, index);
    }

    @Test
    public void parseIndex_missingArgument_throwsWithMissingArgMessage() {
        SelException e = assertThrows(SelException.class,
            () -> Parser.parseIndex("mark", "mark", "missing arg", "invalid number"));
        assertEquals("missing arg", e.getMessage());
    }

    @Test
    public void parseIndex_nonNumericArgument_throwsWithInvalidNumberMessage() {
        SelException e = assertThrows(SelException.class,
            () -> Parser.parseIndex("mark abc", "mark", "missing arg", "invalid number"));
        assertEquals("invalid number", e.getMessage());
    }

    @Test
    public void parseIndex_extraWhitespaceAroundNumber_isTrimmed() throws SelException {
        int index = Parser.parseIndex("mark   3", "mark", "missing", "invalid");
        assertEquals(2, index);
    }

    @Test
    public void parseSimpleArgument_validArgument_returnsTrimmedText() throws SelException {
        String description = Parser.parseSimpleArgument("todo read book", "todo", "error");
        assertEquals("read book", description);
    }

    @Test
    public void parseSimpleArgument_commandWordOnly_throws() {
        SelException e = assertThrows(SelException.class,
            () -> Parser.parseSimpleArgument("todo", "todo", "empty task error"));
        assertEquals("empty task error", e.getMessage());
    }

    @Test
    public void parseSimpleArgument_onlyWhitespaceAfterCommandWord_throws() {
        SelException e = assertThrows(SelException.class,
            () -> Parser.parseSimpleArgument("todo    ", "todo", "empty task error"));
        assertEquals("empty task error", e.getMessage());
    }

    @Test
    public void parseDeadlineArgs_validCommand_extractsDescriptionAndDeadline() throws SelException {
        String[] args = Parser.parseDeadlineArgs("deadline return book /by 2019-12-02 1800");
        assertEquals("return book", args[0]);
        assertEquals("2019-12-02 1800", args[1]);
    }

    @Test
    public void parseDeadlineArgs_missingByMarker_throws() {
        assertThrows(SelException.class, () -> Parser.parseDeadlineArgs("deadline return book"));
    }

    @Test
    public void parseDeadlineArgs_emptyDescription_throws() {
        assertThrows(SelException.class, () -> Parser.parseDeadlineArgs("deadline /by 2019-12-02 1800"));
    }

    @Test
    public void parseDeadlineArgs_emptyDeadline_throws() {
        assertThrows(SelException.class, () -> Parser.parseDeadlineArgs("deadline return book /by"));
    }

    @Test
    public void parseDeadlineArgs_bareCommandWord_throws() {
        assertThrows(SelException.class, () -> Parser.parseDeadlineArgs("deadline"));
    }

    @Test
    public void parseEventArgs_validCommand_extractsAllThreeFields() throws SelException {
        String[] args = Parser.parseEventArgs("event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        assertEquals("project meeting", args[0]);
        assertEquals("2019-12-02 1400", args[1]);
        assertEquals("2019-12-02 1600", args[2]);
    }

    @Test
    public void parseEventArgs_missingFromMarker_throws() {
        assertThrows(SelException.class,
            () -> Parser.parseEventArgs("event project meeting /to 2019-12-02 1600"));
    }

    @Test
    public void parseEventArgs_missingToMarker_throws() {
        assertThrows(SelException.class,
            () -> Parser.parseEventArgs("event project meeting /from 2019-12-02 1400"));
    }

    @Test
    public void parseEventArgs_emptyDescription_throws() {
        assertThrows(SelException.class,
            () -> Parser.parseEventArgs("event /from 2019-12-02 1400 /to 2019-12-02 1600"));
    }

    @Test
    public void parseEventArgs_emptyFrom_throws() {
        assertThrows(SelException.class,
            () -> Parser.parseEventArgs("event meeting /from /to 2019-12-02 1600"));
    }

    @Test
    public void parseEventArgs_emptyTo_throws() {
        assertThrows(SelException.class,
            () -> Parser.parseEventArgs("event meeting /from 2019-12-02 1400 /to"));
    }

    @Test
    public void parseDateTime_validInput_parsesCorrectly() throws SelException {
        LocalDateTime result = Parser.parseDateTime("2019-12-02 1800");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }

    @Test
    public void parseDateTime_missingTimeComponent_throws() {
        assertThrows(SelException.class, () -> Parser.parseDateTime("2019-12-02"));
    }

    @Test
    public void parseDateTime_wrongDateFormat_throws() {
        assertThrows(SelException.class, () -> Parser.parseDateTime("02-12-2019 1800"));
    }

    @Test
    public void parseDateTime_garbageInput_throws() {
        assertThrows(SelException.class, () -> Parser.parseDateTime("not a date"));
    }

    @Test
    public void parseDateTime_extraWhitespace_isTrimmedAndStillParses() throws SelException {
        LocalDateTime result = Parser.parseDateTime("  2019-12-02 1800  ");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }
}