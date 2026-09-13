package sel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end checks that a bad command produces an error response rather
 * than a wrong result, going through the same entry point the GUI uses.
 */
public class SelTest {

    @TempDir
    Path tempDir;

    private Path saveFile;

    @BeforeEach
    public void setUp() {
        saveFile = tempDir.resolve("sel.txt");
    }

    private Sel newSel() {
        return new Sel(saveFile.toString());
    }

    // ---------- command format ----------

    @Test
    public void getResponse_blankInput_isAnError() {
        Sel sel = newSel();
        assertTrue(sel.getResponse("").isError());
        assertTrue(sel.getResponse("    ").isError());
        assertTrue(sel.getResponse(null).isError());
    }

    @Test
    public void getResponse_unknownCommand_isAnError() {
        assertTrue(newSel().getResponse("frobnicate").isError());
    }

    @Test
    public void getResponse_untidySpacing_stillWorks() {
        Sel sel = newSel();
        assertFalse(sel.getResponse("   todo    read    book   ").isError());
        // The description is stored with its spacing tidied up.
        assertTrue(sel.getResponse("list").text().contains("read book"));
    }

    @Test
    public void getResponse_argumentsAfterAnArgumentlessCommand_isAnError() {
        Sel sel = newSel();
        assertTrue(sel.getResponse("list all").isError());
        assertTrue(sel.getResponse("bye now").isError());
        assertFalse(sel.getResponse("list").isError());
        assertFalse(sel.getResponse("bye").isError());
    }

    @Test
    public void getResponse_missingParameters_areErrors() {
        Sel sel = newSel();
        assertTrue(sel.getResponse("todo").isError());
        assertTrue(sel.getResponse("find").isError());
        assertTrue(sel.getResponse("deadline return book").isError());
        assertTrue(sel.getResponse("deadline return book /by").isError());
        assertTrue(sel.getResponse("event party /from 2019-12-02 1400").isError());
        assertTrue(sel.getResponse("mark").isError());
    }

    @Test
    public void getResponse_parameterGivenTwice_isAnError() {
        Sel sel = newSel();
        assertTrue(sel.getResponse(
            "deadline x /by 2019-12-02 1800 /by 2019-12-03 1800").isError());
        assertTrue(sel.getResponse(
            "event x /from 2019-12-02 1400 /from 2019-12-02 1500 /to 2019-12-02 1600").isError());
    }

    @Test
    public void getResponse_reservedCharacterInDescription_isAnError() {
        assertTrue(newSel().getResponse("todo tea | coffee").isError());
    }

    // ---------- task numbers ----------

    @Test
    public void getResponse_taskNumberOutOfRange_isAnErrorNamingTheValidRange() {
        Sel sel = newSel();
        sel.getResponse("todo read book");

        Response response = sel.getResponse("mark 5");

        assertTrue(response.isError());
        assertTrue(response.text().contains("1 to 1"));
    }

    @Test
    public void getResponse_taskNumberZeroOrNegative_isAnError() {
        Sel sel = newSel();
        sel.getResponse("todo read book");
        assertTrue(sel.getResponse("mark 0").isError());
        assertTrue(sel.getResponse("delete -1").isError());
    }

    @Test
    public void getResponse_taskNumberNotANumber_isAnError() {
        Sel sel = newSel();
        assertTrue(sel.getResponse("mark abc").isError());
        assertTrue(sel.getResponse("mark 1 2").isError());
        assertTrue(sel.getResponse("mark 99999999999999999999").isError());
    }

    @Test
    public void getResponse_markingAnAlreadyDoneTask_isAnError() {
        Sel sel = newSel();
        sel.getResponse("todo read book");

        assertFalse(sel.getResponse("mark 1").isError());
        assertTrue(sel.getResponse("mark 1").isError());
    }

    @Test
    public void getResponse_unmarkingATaskThatWasNotDone_isAnError() {
        Sel sel = newSel();
        sel.getResponse("todo read book");

        assertTrue(sel.getResponse("unmark 1").isError());
    }

    // ---------- data that does not make sense ----------

    @Test
    public void getResponse_duplicateTask_isRejected() {
        Sel sel = newSel();
        assertFalse(sel.getResponse("todo read book").isError());

        assertTrue(sel.getResponse("todo read book").isError());
        assertTrue(sel.getResponse("todo READ BOOK").isError());
        // Differing details are not duplicates.
        assertFalse(sel.getResponse("todo read magazine").isError());
    }

    @Test
    public void getResponse_duplicateDeadlineOnlyWhenTheDateMatchesToo() {
        Sel sel = newSel();
        assertFalse(sel.getResponse("deadline pay bill /by 2019-12-02 1800").isError());
        assertTrue(sel.getResponse("deadline pay bill /by 2019-12-02 1800").isError());
        assertFalse(sel.getResponse("deadline pay bill /by 2019-12-03 1800").isError());
    }

    @Test
    public void getResponse_eventEndingBeforeOrWhenItStarts_isAnError() {
        Sel sel = newSel();
        assertTrue(sel.getResponse(
            "event party /from 2019-12-02 1800 /to 2019-12-02 1600").isError());
        assertTrue(sel.getResponse(
            "event party /from 2019-12-02 1800 /to 2019-12-02 1800").isError());
        assertFalse(sel.getResponse(
            "event party /from 2019-12-02 1600 /to 2019-12-02 1800").isError());
    }

    @Test
    public void getResponse_dateThatDoesNotExist_isAnError() {
        Sel sel = newSel();
        assertTrue(sel.getResponse("deadline x /by 2019-02-30 1800").isError());
        assertTrue(sel.getResponse("deadline x /by 2019-04-31 1800").isError());
        assertTrue(sel.getResponse("deadline x /by 2019-12-02 2400").isError());
        assertTrue(sel.getResponse("deadline x /by 2019-13-01 1800").isError());
    }

    @Test
    public void getResponse_rejectedCommand_leavesTheListUnchanged() {
        Sel sel = newSel();
        sel.getResponse("todo read book");

        sel.getResponse("todo read book");
        sel.getResponse("event x /from 2019-12-02 1800 /to 2019-12-02 1600");
        sel.getResponse("deadline y /by 2019-02-30 1800");

        assertTrue(sel.getResponse("list").text().contains("1.[T][ ] read book"));
        assertFalse(sel.getResponse("list").text().contains("2."));
    }

    // ---------- the save file ----------

    @Test
    public void startupWarning_cleanFile_isAbsent() {
        assertTrue(newSel().getStartupWarning().isEmpty());
    }

    @Test
    public void startupWarning_unusableLines_areReportedToTheUser() throws IOException {
        Files.write(saveFile, List.of(
            "T | 0 | read book",
            "X | garbage | unknown type",
            "T | 0 | read book"));

        Sel sel = newSel();

        assertTrue(sel.getStartupWarning().isPresent());
        // The good line still loaded.
        assertTrue(sel.getResponse("list").text().contains("read book"));
    }

    @Test
    public void getResponse_addedTask_survivesARestart() {
        newSel().getResponse("todo read book");

        Sel restarted = newSel();

        assertTrue(restarted.getResponse("list").text().contains("read book"));
        assertTrue(restarted.getStartupWarning().isEmpty());
    }

    @Test
    public void getResponse_savingIntoAReadOnlyDirectory_reportsTheProblem() throws IOException {
        Path directory = tempDir.resolve("locked");
        Files.createDirectories(directory);
        Sel sel = new Sel(directory.resolve("sel.txt").toString());

        assertTrue(directory.toFile().setWritable(false), "could not make the directory read-only");
        try {
            Response response = sel.getResponse("todo read book");

            assertTrue(response.isError());
            assertTrue(response.text().contains("save"));
        } finally {
            directory.toFile().setWritable(true);
        }
    }

    @Test
    public void getResponse_listAfterEveryKindOfTask_showsThemAll() {
        Sel sel = newSel();
        sel.getResponse("todo read book");
        sel.getResponse("deadline return book /by 2019-12-02 1800");
        sel.getResponse("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");

        String list = sel.getResponse("list").text();

        assertEquals(3, list.lines().filter(line -> line.matches("^\\d+\\..*")).count());
    }
}
