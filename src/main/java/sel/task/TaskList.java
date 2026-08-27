package sel.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the list of tasks currently tracked by the application,
 * with operations to add, remove, mark, and query tasks by index.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates a new, empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the given list, e.g. one loaded from
     * disk by {@link sel.storage.Storage}.
     *
     * @param tasks the initial tasks to populate this list with.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index the zero-based index of the task to remove.
     * @return the removed task.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index the zero-based index of the task to retrieve.
     * @return the task at that index.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether the given index refers to an existing task.
     *
     * @param index the zero-based index to check.
     * @return {@code true} if the index is within range, {@code false} otherwise.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Marks the task at the given index as done.
     *
     * @param index the zero-based index of the task to mark.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public void mark(int index) {
        tasks.get(index).mark();
    }

    /**
     * Marks the task at the given index as not done.
     *
     * @param index the zero-based index of the task to unmark.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public void unmark(int index) {
        tasks.get(index).unmark();
    }

    /**
     * Returns the underlying list of tasks. The returned list is live;
     * changes to it are reflected in this TaskList, e.g. so
     * {@link sel.storage.Storage} can persist the current tasks.
     *
     * @return the underlying list of tasks.
     */
    public List<Task> asList() {
        return tasks;
    }
}