package tests;

import cswt.Ticket;
import cswt.TicketSnapshot;
import org.junit.Before;
import org.junit.Test;
import server.TicketHistoryStorer;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TicketHistoryStorerTest {
    TicketHistoryStorer storer;
    final String TEST_DIR = Paths.get(System.getProperty("user.dir"), "sampleTicketsHistory").toString();

    @Before
    public void SetUp() {
        storer = new TicketHistoryStorer();
        storer.TICKET_HISTORY_DIR = TEST_DIR;
    }

    @Test
    public void testLoadTicketHistory() {
        List<TicketSnapshot> snapshots = storer.loadTicketHistory("1");
        assertEquals(5, snapshots.size());
        TicketSnapshot snapshot = snapshots.get(0);
        Ticket ticket = snapshot.getTicket();
        assertEquals("1", ticket.getId());
        assertEquals("Create ticket", snapshot.getWhatModified());
        assertEquals("manager", snapshot.getModifier());
    }
}
