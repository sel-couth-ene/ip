package sel.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs over a fixed time range, from a start
 * date/time to an end date/time.
 */
public class Event extends Task {
    private static final DateTimeFormatter display_format =
        DateTimeFormatter.ofPattern("MMM d yyyy, h:mma", Locale.ENGLISH);

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates a new, not-done event task.
     *
     * @param description text describing the event.
     * @param from the date and time the event starts.
     * @param to the date and time the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the date and time this event starts.
     *
     * @return the event's start date and time.
     */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /**
     * Returns the date and time this event ends.
     *
     * @return the event's end date and time.
     */
    public LocalDateTime getTo() {
        return this.to;
    }

    /**
     * Returns the display form of this event, e.g.
     * {@code "[E][ ] meeting(from:Dec 2 2019, 2:00PM to:Dec 2 2019, 4:00PM)"}.
     *
     * @return the formatted string representation of this event.
     */
    @Override
    public String toString() {
        return "[E][" + this.getStatusIcon() + "] " + this.description + "(from:" + this.from.format(display_format) + " to:" + this.to.format(display_format) + ")";
    }
}