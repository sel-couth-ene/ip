package sel.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the task list and operations to add/delete/mark tasks in it.
 */
public class TaskList {
    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task delete(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    public void mark(int index) {
        tasks.get(index).mark();
    }

    public void unmark(int index) {
        tasks.get(index).unmark();
    }

    /** Returns the underlying list, e.g. so Storage can persist it. */
    public List<Task> asList() {
        return tasks;
    }

    /**
     * Returns all tasks whose description contains the given keyword
     * (case-insensitive, substring match).
     *
     * @param keyword the keyword to search for.
     * @return a new list of matching tasks, in their original order.
     */
    public List<Task> find(String keyword) {
        List<Task> matches = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowerKeyword)) {
                matches.add(task);
            }
        }
        return matches;
    }
}