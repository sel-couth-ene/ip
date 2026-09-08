package sel.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import sel.ui.Ui;

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

        CountingUi ui = new CountingUi();
        Storage storage = new Storage(file.toString(), ui);

        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals("join sports club", loaded.get(1).getDescription());
        assertEquals(1, ui.corruptedLineWarnings);
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

    private static class CountingUi extends Ui {
        private int corruptedLineWarnings = 0;

        @Override
        public void showCorruptedLineWarning(int lineNumber) {
            corruptedLineWarnings++;
        }
    }
}
