package sel.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sel.exception.SelException;
import sel.task.Deadline;
import sel.task.Event;
import sel.task.Task;
import sel.task.ToDo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void load_fileDoesNotExist_createsFileAndReturnsEmptyList() throws SelException {
        Path file = tempDir.resolve("sel.txt");
        Storage storage = new Storage(file.toString());

        List<Task> tasks = storage.load();

        assertTrue(Files.exists(file));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void load_nestedMissingDirectories_areCreated() throws SelException {
        Path file = tempDir.resolve("nested/dir/sel.txt");
        Storage storage = new Storage(file.toString());

        storage.load();

        assertTrue(Files.exists(file));
    }

    @Test
    public void saveThenLoad_roundTripsAllThreeTaskTypes() throws SelException {
        Path file = tempDir.resolve("sel.txt");
        Storage storage = new Storage(file.toString());

        ToDo todo = new ToDo("read book");
        todo.mark();
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("project meeting",
            LocalDateTime.of(2019, 12, 2, 14, 0), LocalDateTime.of(2019, 12, 2, 16, 0));

        List<Task> original = new ArrayList<>();
        original.add(todo);
        original.add(deadline);
        original.add(event);

        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());

        assertTrue(loaded.get(0) instanceof ToDo);
        assertTrue(loaded.get(0).isDone());
        assertEquals("read book", loaded.get(0).getDescription());

        assertTrue(loaded.get(1) instanceof Deadline);
        assertFalse(loaded.get(1).isDone());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), ((Deadline) loaded.get(1)).getDdl());

        assertTrue(loaded.get(2) instanceof Event);
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), ((Event) loaded.get(2)).getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), ((Event) loaded.get(2)).getTo());
    }

    @Test
    public void load_corruptedLine_isSkippedButOtherLinesStillLoad() throws IOException, SelException {
        Path file = tempDir.resolve("sel.txt");
        Files.createFile(file);
        Files.write(file, List.of(
            "T | 1 | read book",
            "X | garbage | this line is corrupted",
            "T | 0 | join sports club"
        ));

        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals("join sports club", loaded.get(1).getDescription());
        assertEquals(1, storage.getLoadWarnings().size());
    }

    @Test
    public void load_blankLinesAreIgnored() throws IOException, SelException {
        Path file = tempDir.resolve("sel.txt");
        Files.createFile(file);
        Files.write(file, List.of(
            "T | 0 | read book",
            "",
            "   ",
            "T | 0 | join sports club"
        ));

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
    }

    @Test
    public void save_overwritesPreviousContentsRatherThanAppending() throws SelException {
        Path file = tempDir.resolve("sel.txt");
        Storage storage = new Storage(file.toString());

        List<Task> first = new ArrayList<>();
        first.add(new ToDo("first task"));
        storage.save(first);

        List<Task> second = new ArrayList<>();
        second.add(new ToDo("second task"));
        storage.save(second);

        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("second task", loaded.get(0).getDescription());
    }

    @Test
    public void save_emptyList_producesFileThatLoadsAsEmpty() throws SelException {
        Path file = tempDir.resolve("sel.txt");
        Storage storage = new Storage(file.toString());

        storage.save(new ArrayList<>());
        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    public void load_duplicateLines_keepsOnlyTheFirstAndWarns() throws IOException, SelException {
        Path file = tempDir.resolve("sel.txt");
        Files.write(file, List.of(
            "T | 0 | read book",
            "T | 1 | READ BOOK",
            "T | 0 | join sports club"));

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals(1, storage.getLoadWarnings().size());
    }

    @Test
    public void load_eventEndingBeforeItStarts_isSkippedAndOtherLinesSurvive()
            throws IOException, SelException {
        Path file = tempDir.resolve("sel.txt");
        Files.write(file, List.of(
            "E | 0 | backwards | 2019-12-02T16:00 | 2019-12-02T14:00",
            "T | 0 | survivor"));

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("survivor", loaded.get(0).getDescription());
        assertEquals(1, storage.getLoadWarnings().size());
    }

    @Test
    public void load_separatorInsideStoredDescription_isReadBackWhole()
            throws IOException, SelException {
        // A file written by an older version, or edited by hand, may hold a
        // description containing the field separator. It should come back in
        // one piece rather than being dropped as unparseable.
        Path file = tempDir.resolve("sel.txt");
        Files.write(file, List.of(
            "T | 0 | tea | coffee",
            "D | 0 | a | b | 2019-12-02T18:00",
            "E | 0 | x | y | 2019-12-02T14:00 | 2019-12-02T16:00"));

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("tea | coffee", loaded.get(0).getDescription());
        assertEquals("a | b", loaded.get(1).getDescription());
        assertEquals("x | y", loaded.get(2).getDescription());
        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    public void load_pathIsADirectory_throwsWithAClearMessage() throws IOException {
        Path directory = tempDir.resolve("iam-a-dir");
        Files.createDirectories(directory);

        Storage storage = new Storage(directory.toString());

        SelException e = assertThrows(SelException.class, storage::load);
        assertTrue(e.getMessage().contains("folder"));
    }

    @Test
    public void load_cleanFile_reportsNoWarnings() throws IOException, SelException {
        Path file = tempDir.resolve("sel.txt");
        Files.write(file, List.of("T | 0 | read book"));

        Storage storage = new Storage(file.toString());
        storage.load();

        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    public void save_failing_leavesThePreviousFileIntactAndThrows() throws IOException, SelException {
        Path directory = tempDir.resolve("locked");
        Files.createDirectories(directory);
        Path file = directory.resolve("sel.txt");

        Storage storage = new Storage(file.toString());
        storage.save(List.of(new ToDo("original task")));

        // Making the directory read-only stops both the temporary file and
        // the move, which is what a denied-permission save looks like.
        // Windows ignores the read-only attribute on directories, so this
        // check is skipped rather than failed where it cannot be set up.
        assumeTrue(directory.toFile().setWritable(false),
            "this OS does not support making a directory read-only");
        try {
            assertThrows(SelException.class, () -> storage.save(List.of(new ToDo("new task"))));

            assertEquals(List.of("T | 0 | original task"), Files.readAllLines(file));
        } finally {
            directory.toFile().setWritable(true);
        }
    }

    @Test
    public void save_leavesNoTemporaryFilesBehind() throws SelException, IOException {
        Path file = tempDir.resolve("sel.txt");
        Storage storage = new Storage(file.toString());

        storage.save(List.of(new ToDo("read book")));

        try (var entries = Files.list(tempDir)) {
            assertEquals(List.of("sel.txt"),
                entries.map(path -> path.getFileName().toString()).sorted().toList());
        }
    }

    @Test
    public void save_parentPathIsNotADirectory_throws() throws IOException {
        // A regular file where a directory is expected cannot be created
        // into. Unlike the permission test above, this failure can be set
        // up on every OS.
        Path blocker = tempDir.resolve("blocker");
        Files.write(blocker, List.of("i am a file, not a folder"));

        Storage storage = new Storage(blocker.resolve("sel.txt").toString());

        assertThrows(SelException.class, () -> storage.save(List.of(new ToDo("read book"))));
    }

    @Test
    public void load_parentPathIsNotADirectory_throws() throws IOException {
        Path blocker = tempDir.resolve("blocker");
        Files.write(blocker, List.of("i am a file, not a folder"));

        Storage storage = new Storage(blocker.resolve("sel.txt").toString());

        assertThrows(SelException.class, storage::load);
    }
}
