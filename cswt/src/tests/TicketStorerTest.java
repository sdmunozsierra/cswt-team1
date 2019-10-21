package tests;

import cswt.Ticket;
import org.junit.Before;
import org.junit.Test;
import server.TicketStorer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class TicketStorerTest {
    TicketStorer storer;
    final String TEST_DIR = Paths.get(System.getProperty("user.dir"), "sampleTickets").toString();

    @Before
    public void SetUp() {
        storer = new TicketStorer();
        storer.TICKET_DIR = TEST_DIR;
    }

    @Test
    public void testLoadTickets() {
        List<Ticket> tickets = storer.loadTickets();
        assertEquals(1, tickets.size());
        Ticket ticket = tickets.get(0);
        assertEquals("1", ticket.getId());
        assertEquals("Description", ticket.getDescription());
        assertEquals("Test Ticket", ticket.getTitle());
    }

    @Test
    public void testStoreTicket() {
        Ticket ticket = new Ticket();
        ticket.setId("2");
        ticket.setDescription("Another description");
        ticket.setTitle("Test Ticket Title 2");
        boolean added = storer.storeTicket(ticket);
        assertEquals(true, added);
        List<Ticket> tickets = storer.loadTickets();
        assertEquals(2, tickets.size());
        try {
            Files.deleteIfExists(Paths.get(TEST_DIR, "2.json"));
        } catch (Exception e) {
            fail();
        }
    }
}
