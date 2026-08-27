package sel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void gettersReturnExactValuesPassedIn() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        Event event = new Event("project meeting", from, to);
        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
    }

    @Test
    public void toString_notDone_formatsBothDatesCorrectly() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        Event event = new Event("project meeting", from, to);
        assertEquals("[E][ ] project meeting(from:Dec 2 2019, 2:00PM to:Dec 2 2019, 4:00PM)",
            event.toString());
    }

    @Test
    public void toString_marked_showsXInStatusBox() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        Event event = new Event("project meeting", from, to);
        event.mark();
        assertEquals("[E][X] project meeting(from:Dec 2 2019, 2:00PM to:Dec 2 2019, 4:00PM)",
            event.toString());
    }

    @Test
    public void toString_spanningDifferentDays_showsBothDatesFully() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 23, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 3, 1, 0);
        Event event = new Event("overnight shift", from, to);
        assertEquals("[E][ ] overnight shift(from:Dec 2 2019, 11:00PM to:Dec 3 2019, 1:00AM)",
            event.toString());
    }
}