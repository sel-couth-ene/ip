package sel.task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = 
        DateTimeFormatter.ofPattern("MMM d yyy, h:mma", Locale.ENGLISH);
    
    protected LocalDateTime ddl;

    public Deadline(String description, LocalDateTime ddl) {
        super(description);
        this.ddl = ddl;
    }

    public LocalDateTime getDDL() {
        return this.ddl;
    }

    @Override
    public String toString() {
        return "[D][" + this.getStatusIcon() + "] " + this.description + "(by:" + this.ddl.format(DISPLAY_FORMAT) + ")";
    }
}