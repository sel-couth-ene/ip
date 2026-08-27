package sel.storage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import sel.exception.SelException;
import sel.task.Deadline;
import sel.task.Event;
import sel.task.Task;
import sel.task.ToDo;
import sel.ui.Ui;

public class Storage {
    private final Path filePath;
    private final Ui ui;

    public Storage(String filePath) {
        this(filePath, new Ui());
    }

    public Storage(String filePath, Ui ui) {
        this.filePath = Paths.get(filePath);
        this.ui = ui;
    }

    // @throws SelException
    public List<Task> load() throws SelException {
        List<Task> tasks = new ArrayList<>();

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                return tasks;
            }

            List<String> lines = Files.readAllLines(filePath);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.isEmpty()) {
                    continue;
                }

                try {
                    tasks.add(parseTask(line));
                } catch (IllegalArgumentException e) {
                    ui.showCorruptedLineWarning(i + 1);
                }
            }
        } catch (IOException e) {
            throw new SelException("Failed to load tasks from " + filePath + ".");
        }

        return tasks;
    }

    public void save(List<Task> tasks) {
        List<String> lines = new ArrayList<>();

        for (Task currentTask : tasks) {
            String status = currentTask.isDone() ? "1" : "0";

            if (currentTask instanceof ToDo) {
                lines.add("T | " + status + " | " + currentTask.getDescription());
            } else if (currentTask instanceof Deadline) {
                Deadline deadline = (Deadline) currentTask;
                lines.add("D | " + status + " | " + deadline.getDescription()
                    + " | " + deadline.getDDL());
            } else if (currentTask instanceof Event) {
                Event event = (Event) currentTask;
                lines.add("E | " + status + " | " + event.getDescription()
                    + " | " + event.getFrom() + " | " + event.getTo());
            }
        }

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            ui.showSavingError();
        }
    }

    private Task parseTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);

        if (parts.length < 3) {
            throw new IllegalArgumentException("Not enough fields");
        }

        String type = parts[0];
        String status = parts[1];
        String description = parts[2];

        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("Invalid status");
        }

        if (description.isEmpty()) {
            throw new IllegalArgumentException("Missing description");
        }

        Task loadedTask;

        switch (type) {
        case "T":
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid todo format");
            }
            loadedTask = new ToDo(description);
            break;

        case "D":
            if (parts.length != 4 || parts[3].isEmpty()) {
                throw new IllegalArgumentException("Invalid deadline format");
            }
            loadedTask = new Deadline(description, parseStoredDateTime(parts[3]));
            break;

        case "E":
            if (parts.length == 5 && !parts[3].isEmpty() && !parts[4].isEmpty()) {
                loadedTask = new Event(description, 
        parseStoredDateTime(parts[3]), parseStoredDateTime(parts[4]));
            } else if (parts.length == 4 && !parts[3].isEmpty()) {
                String[] range = splitLegacyEventRange(parts[3]);
                loadedTask = new Event(description, 
        parseStoredDateTime(range[0]), parseStoredDateTime(range[1]));
            } else {
                throw new IllegalArgumentException("Invalid event format");
            }
            break;

        default:
            throw new IllegalArgumentException("Unknown task type");
        }

        if (status.equals("1")) {
            loadedTask.mark();
        }

        return loadedTask;
    }

    private String[] splitLegacyEventRange(String timeRange) {
        int toIndex = timeRange.indexOf(" to ");
        if (toIndex >= 0) {
            String from = timeRange.substring(0, toIndex).trim();
            String to = timeRange.substring(toIndex + 4).trim();
            if (!from.isEmpty() && !to.isEmpty()) {
                return new String[] {from, to};
            }
        }

        int dashIndex = timeRange.lastIndexOf('-');
        if (dashIndex > 0 && dashIndex < timeRange.length() - 1) {
            String from = timeRange.substring(0, dashIndex).trim();
            String to = timeRange.substring(dashIndex + 1).trim();
            if (!from.isEmpty() && !to.isEmpty()) {
                return new String[] {from, to};
            }
        }

        throw new IllegalArgumentException("Invalid event range");
    }

    private LocalDateTime parseStoredDateTime(String input) {
        try {
            return LocalDateTime.parse(input.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid stored date/time: " + input);
        }
    }
}