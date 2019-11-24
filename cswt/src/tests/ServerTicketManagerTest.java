package tests;

import cswt.Ticket;
import org.junit.Before;
import org.junit.Test;
import server.ServerTicketManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ServerTicketManagerTest {
    ServerTicketManager manager;

    @Before
    public void SetUp() {
        manager = new ServerTicketManager();
    }

    @Test
    public void testCreateTicket() {
        int before = manager.getAllTickets().size();
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        int after = manager.getAllTickets().size();
        manager.deleteTicket(ticket.getId());
        assertEquals(before + 1, after);
    }

    @Test
    public void testGetTicket() {
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        Ticket t = manager.getTicket(ticket.getId());
        manager.deleteTicket(ticket.getId());
        assertEquals(ticket.getId(), t.getId());
        assertEquals("Title", t.getTitle());
        assertEquals("Description", t.getDescription());
        assertEquals("Client", t.getClient());
        assertEquals("Severity", t.getSeverity());
        assertEquals("Assigned To", t.getAssignedTo());
        assertEquals("Priority", t.getPriority());
        assertEquals(manager.STATUS_NEW, t.getStatus());
    }

    @Test
    public void testEditTicket() {
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        manager.editTicket(ticket.getId(), "Title 1", "Description 1", "Client 1", "Client 1", "Severity 1", "Assigned To 1", "Priority 1");
        Ticket t = manager.getTicket(ticket.getId());
        manager.deleteTicket(ticket.getId());
        assertEquals(ticket.getId(), t.getId());
        assertEquals("Title 1", t.getTitle());
        assertEquals("Description 1", t.getDescription());
        assertEquals("Client 1", t.getClient());
        assertEquals("Severity 1", t.getSeverity());
        assertEquals("Assigned To 1", t.getAssignedTo());
        assertEquals("Priority 1", t.getPriority());
        assertEquals(manager.STATUS_NEW, t.getStatus());
    }

    @Test
    public void testOpenTicket() {
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        manager.openTicket(ticket.getId(), ticket.getPriority(), ticket.getAssignedTo());
        Ticket t = manager.getTicket(ticket.getId());
        manager.deleteTicket(ticket.getId());
        assertEquals(manager.STATUS_OPENED, t.getStatus());
    }

    @Test
    public void testFixTicket() {
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        manager.markTicketAsFixed(ticket.getId(), "Resolution");
        Ticket t = manager.getTicket(ticket.getId());
        manager.deleteTicket(ticket.getId());
        assertEquals(manager.STATUS_FIXED, t.getStatus());
        assertEquals("Resolution", t.getResolution());
    }

    @Test
    public void testRejectTicket() {
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        manager.rejectTicket(ticket.getId());
        Ticket t = manager.getTicket(ticket.getId());
        manager.deleteTicket(ticket.getId());
        assertEquals(manager.STATUS_REJECTED, t.getStatus());
    }
    @Test
    public void testCloseTicket() {
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        manager.closeTicket(ticket.getId());
        Ticket t = manager.getTicket(ticket.getId());
        manager.deleteTicket(ticket.getId());
        assertEquals(manager.STATUS_CLOSED, t.getStatus());
    }

    @Test
    public void testGetTicketAsCopy() {
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        Ticket t = manager.getTicketAsCopy(ticket.getId());
        manager.deleteTicket(ticket.getId());
        assertNotEquals(ticket, t);
        assertEquals(ticket.getId(), t.getId());
        assertEquals(ticket.getTitle(), t.getTitle());
        assertEquals(ticket.getDescription(), t.getDescription());
        assertEquals(ticket.getClient(), t.getClient());
        assertEquals(ticket.getSeverity(), t.getSeverity());
        assertEquals(ticket.getAssignedTo(), t.getAssignedTo());
        assertEquals(ticket.getPriority(), t.getPriority());
    }

    @Test
    public void testDeleteTicket() {
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        manager.deleteTicket(ticket.getId());
        assertEquals(null, manager.getTicket(ticket.getId()));
    }

    @Test
    public void testGetAllIds() {
        int before = manager.getAllIds().size();
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        int after = manager.getAllIds().size();
        manager.deleteTicket(ticket.getId());
        assertEquals(before + 1, after);
    }

    @Test
    public void testGetAllTickets() {
        int before = manager.getAllTickets().size();
        Ticket ticket = manager.createTicket("Title", "Description", "Client", "Severity", "Assigned To", "Priority");
        int after = manager.getAllTickets().size();
        manager.deleteTicket(ticket.getId());
        assertEquals(before + 1, after);
    }

}
