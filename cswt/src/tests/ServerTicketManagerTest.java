package tests;

import cswt.Ticket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.ServerTicketManager;
import server.TicketStorer;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class ServerTicketManagerTest {
    TicketStorer storer;
    ServerTicketManager manager;
    final String TEST_DIR = Paths.get(System.getProperty("user.dir"), "sampleTickets").toString();
    final String TEST_FILE = "1.json";

    @Before
    public void SetUp() {
        storer = new TicketStorer();
        storer.TICKET_DIR = TEST_DIR;
        manager = new ServerTicketManager();
        manager.setStorer(storer);
    }

    @Test
    public void testCreateTicket() {
        Ticket ticket = manager.createTicket("Create Test", "Create Test Description", "Client", "", "", "");
        assertNotEquals(null, ticket);
        assertEquals("Create Test", ticket.getTitle());
        assertEquals("Create Test Description", ticket.getDescription());
        assertEquals("Client", ticket.getClient());
    }

    @Test
    public void testEditTicket() {
        Ticket ticket = manager.createTicket("Test", "Test Description", "Client", "", "", "");
        Ticket edited = manager.editTicket(ticket.getId(), "Updated Test", "Updated Test Description", "", "Updated Client", "", "", "");
        assertNotEquals(null, edited);
        assertEquals("Updated Test", edited.getTitle());
        assertEquals("Updated Test Description", edited.getDescription());
        assertEquals("Updated Client", edited.getClient());
    }

    @Test
    public void testOpenTicket() {
        Ticket ticket = manager.createTicket("Test", "Test Description", "Client", "", "", "");
        Ticket opened = manager.openTicket(ticket.getId(), "1", "tester");
        assertNotEquals(null, opened);
        assertEquals("Test", opened.getTitle());
        assertEquals("1", opened.getPriority());
        assertEquals("OPEN", opened.getStatus());
    }

    @Test
    public void testFixTicket() {
        Ticket ticket = manager.createTicket("Test", "Test Description", "Client", "", "", "");
        Ticket fixed = manager.markTicketAsFixed(ticket.getId(), "Resolved");
        assertNotEquals(null, fixed);
        assertEquals("Test", fixed.getTitle());
        assertEquals("Resolved", fixed.getResolution());
        assertEquals("FIXED", fixed.getStatus());
    }

    @Test
    public void testCloseTicket() {
        Ticket ticket = manager.createTicket("Test", "Test Description", "Client", "", "", "");
        Ticket closed = manager.closeTicket(ticket.getId());
        assertNotEquals(null, closed);
        assertEquals("Test", closed.getTitle());
        assertEquals("CLOSED", closed.getStatus());
    }

    @Test
    public void testRejectTicket() {
        Ticket ticket = manager.createTicket("Test", "Test Description", "Client", "", "", "");
        Ticket rejected = manager.rejectTicket(ticket.getId());
        assertNotEquals(null, rejected);
        assertEquals("Test", rejected.getTitle());
        assertEquals("REJECTED", rejected.getStatus());
    }

    @Test
    public void testGetTicket() {
        Ticket ticket = manager.getTicket("1");
        assertNotEquals(null, ticket);
        assertEquals("Test Ticket", ticket.getTitle());
        assertEquals("Description", ticket.getDescription());
    }

    @Test
    public void getAllTickets() {
        List<Ticket> tickets = manager.getAllTickets();
        assertEquals(1, tickets.size());
        Ticket testTicket = tickets.get(0);
        assertEquals("1", testTicket.getId());
        assertEquals("Test Ticket", testTicket.getTitle());
        assertEquals("Description", testTicket.getDescription());
    }

    @Test
    public void getAllIds() {
        List<String> ids = manager.getAllIds();
        assertEquals(1, ids.size());
        String testId = ids.get(0);
        assertEquals("1", testId);
    }

    private void clearTestTicketDirectory() {
        File dir = new File(TEST_DIR);
        for (File file: dir.listFiles()) {
            if(!file.getName().equals(TEST_FILE)) {
                file.delete();
            }
        }
    }

    @After
    public void tearDown() {
        clearTestTicketDirectory();
    }
}
