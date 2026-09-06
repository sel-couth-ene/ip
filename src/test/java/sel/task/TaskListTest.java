package sel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void noArgConstructor_startsEmpty() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    public void listConstructor_wrapsGivenList() {
        List<Task> initial = new ArrayList<>();
        initial.add(new ToDo("existing task"));
        TaskList tasks = new TaskList(initial);
        assertEquals(1, tasks.size());
        assertEquals("existing task", tasks.get(0).getDescription());
    }

    @Test
    public void add_increasesSizeAndStoresTaskAtEnd() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        tasks.add(new ToDo("task 2"));
        assertEquals(2, tasks.size());
        assertEquals("task 2", tasks.get(1).getDescription());
    }

    @Test
    public void get_returnsTaskAtGivenIndex() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("task 1");
        tasks.add(task);
        assertSame(task, tasks.get(0));
    }

    @Test
    public void get_invalidIndex_throwsIndexOutOfBounds() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(5));
    }

    @Test
    public void delete_removesTaskAndReturnsIt() {
        TaskList tasks = new TaskList();
        Task task1 = new ToDo("task 1");
        Task task2 = new ToDo("task 2");
        tasks.add(task1);
        tasks.add(task2);

        Task deleted = tasks.delete(0);

        assertSame(task1, deleted);
        assertEquals(1, tasks.size());
        assertSame(task2, tasks.get(0));
    }

    @Test
    public void delete_invalidIndex_throwsIndexOutOfBounds() {
        TaskList tasks = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(0));
    }

    @Test
    public void isValidIndex_withinBounds_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        assertTrue(tasks.isValidIndex(0));
    }

    @Test
    public void isValidIndex_negative_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        assertFalse(tasks.isValidIndex(-1));
    }

    @Test
    public void isValidIndex_equalToSize_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        assertFalse(tasks.isValidIndex(1));
    }

    @Test
    public void isValidIndex_emptyList_returnsFalseForZero() {
        TaskList tasks = new TaskList();
        assertFalse(tasks.isValidIndex(0));
    }

    @Test
    public void mark_marksTaskAtGivenIndex() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        tasks.add(new ToDo("task 2"));

        tasks.mark(1);

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void unmark_unmarksTaskAtGivenIndex() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("task 1");
        task.mark();
        tasks.add(task);

        tasks.unmark(0);

        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void asList_reflectsLiveState() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));

        List<Task> snapshot = tasks.asList();
        assertEquals(1, snapshot.size());

        tasks.add(new ToDo("task 2"));
        assertEquals(2, snapshot.size(), "asList() should expose the live underlying list");
    }
}
