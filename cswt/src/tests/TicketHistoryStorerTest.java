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

    @Test
    public void testUpdateTicketHistory() {
        Ticket ticket = new Ticket();
        ticket.setId("2");
        ticket.setDescription("Description");
        storer.updateTicketHistory(ticket, "Modifier", "Modified");
        List<TicketSnapshot> snapshots = storer.loadTicketHistory("2");
        TicketSnapshot snapshot = snapshots.get(0);
        Ticket t = snapshot.getTicket();
        assertEquals("2", t.getId());
        assertEquals("Modifier", snapshot.getModifier());
        assertEquals("Modified", snapshot.getWhatModified());
        storer.deleteTicketHistory("2");
    }

    @Test
    public void testDeleteTicketHistory() {
        Ticket ticket = new Ticket();
        ticket.setId("2");
        ticket.setDescription("Description");
        storer.updateTicketHistory(ticket, "Modifier", "Modified");
        assertEquals(true, storer.deleteTicketHistory("2"));
    }

    @Test
    public void testChangeClient() {
        Ticket ticket1 = new Ticket();
        ticket1.setTitle("Title");
        ticket1.setDescription("Description");
        ticket1.setAssignedTo("Assigned to");
        ticket1.setStatus("OPEN");
        ticket1.setClient("Client");
        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Title");
        ticket2.setDescription("Description");
        ticket2.setAssignedTo("Assigned to");
        ticket2.setStatus("OPEN");
        ticket2.setClient("New Client");
        String result = storer.determineDifference(ticket1, ticket2);
        assertEquals(storer.CHANGE_CLIENT, result);
    }

    @Test
    public void testChangeTitle() {
        Ticket ticket1 = new Ticket();
        ticket1.setTitle("Title 1");
        ticket1.setDescription("Description");
        ticket1.setAssignedTo("Assigned to");
        ticket1.setStatus("OPEN");
        ticket1.setClient("Client");
        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Title 2");
        ticket2.setDescription("Description");
        ticket2.setAssignedTo("Assigned to");
        ticket2.setStatus("OPEN");
        ticket2.setClient("Client");
        String result = storer.determineDifference(ticket1, ticket2);
        assertEquals(storer.CHANGE_TITLE, result);
    }

    @Test
    public void testChangeDescription() {
        Ticket ticket1 = new Ticket();
        ticket1.setTitle("Title");
        ticket1.setDescription("Description 1");
        ticket1.setAssignedTo("Assigned to");
        ticket1.setStatus("OPEN");
        ticket1.setClient("Client");
        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Title");
        ticket2.setDescription("Description 2");
        ticket2.setAssignedTo("Assigned to");
        ticket2.setStatus("OPEN");
        ticket2.setClient("Client");
        String result = storer.determineDifference(ticket1, ticket2);
        assertEquals(storer.CHANGE_DESCRIPTION, result);
    }

    @Test
    public void testChangeAssignedTo() {
        Ticket ticket1 = new Ticket();
        ticket1.setTitle("Title");
        ticket1.setDescription("Description");
        ticket1.setAssignedTo("Assigned to 1");
        ticket1.setStatus("OPEN");
        ticket1.setClient("Client");
        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Title");
        ticket2.setDescription("Description");
        ticket2.setAssignedTo("Assigned to 2");
        ticket2.setStatus("OPEN");
        ticket2.setClient("Client");
        String result = storer.determineDifference(ticket1, ticket2);
        assertEquals(storer.CHANGE_ASSIGNED_TO, result);
    }

    @Test
    public void testChangeResolution() {
        Ticket ticket1 = new Ticket();
        ticket1.setTitle("Title");
        ticket1.setResolution("Resolution 1");
        ticket1.setAssignedTo("Assigned to");
        ticket1.setStatus("OPEN");
        ticket1.setClient("Client");
        Ticket ticket2 = new Ticket();
        ticket2.setTitle("Title");
        ticket2.setResolution("Resolution 2");
        ticket2.setAssignedTo("Assigned to");
        ticket2.setStatus("OPEN");
        ticket2.setClient("Client");
        String result = storer.determineDifference(ticket1, ticket2);
        assertEquals(storer.CHANGE_RESOLUTION, result);
    }

    @Test
    public void testChangePriority() {
        Ticket ticket1 = new Ticket();
        ticket1.setPriority("1");
        Ticket ticket2 = new Ticket();
        ticket2.setPriority("2");
        String result = storer.determineDifference(ticket1, ticket2);
        assertEquals(storer.CHANGE_PRIORITY, result);
    }

    @Test
    public void testChangeSeverity() {
        Ticket ticket1 = new Ticket();
        ticket1.setSeverity("Critical");
        Ticket ticket2 = new Ticket();
        ticket2.setSeverity("Routine");
        String result = storer.determineDifference(ticket1, ticket2);
        assertEquals(storer.CHANGE_SEVERITY, result);
    }

    @Test
    public void testMultipleChanges() {
        Ticket ticket1 = new Ticket();
        ticket1.setPriority("1");
        ticket1.setDescription("Description 1");
        Ticket ticket2 = new Ticket();
        ticket2.setPriority("2");
        ticket2.setDescription("Description 2");
        String result = storer.determineDifference(ticket1, ticket2);
        assertEquals(storer.MULTIPLE_CHANGES, result);
    }
}
