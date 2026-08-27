package sel.task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = 
        DateTimeFormatter.ofPattern("MMM d yyyy, h:mma", Locale.ENGLISH);

    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() {
        return this.from;
    }

     public LocalDateTime getTo() {
        return this.to;
    }

    @Override
    public String toString() {
        return "[E][" + this.getStatusIcon() + "] " + this.description 
        + "(from:" + this.from.format(DISPLAY_FORMAT) + " to:" + this.to.format(DISPLAY_FORMAT) + ")";
    }
}