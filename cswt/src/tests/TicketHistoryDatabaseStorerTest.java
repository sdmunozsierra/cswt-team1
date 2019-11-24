package tests;

import cswt.Ticket;
import cswt.TicketSnapshot;
import org.junit.Before;
import org.junit.Test;
import server.TicketHistoryDatabaseStorer;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class TicketHistoryDatabaseStorerTest {

    TicketHistoryDatabaseStorer storer;
    @Before
    public void SetUp() {
        storer = new TicketHistoryDatabaseStorer();
    }

    @Test
    public void testLoadTicketHistory() {
        Ticket t = new Ticket();
        t.setId("1");
        storer.updateTicketHistory(t, "manager", "Create ticket");
        List<TicketSnapshot> snapshots = storer.loadTicketHistory("1");
        assertEquals(1, snapshots.size());
        TicketSnapshot snapshot = snapshots.get(0);
        Ticket ticket = snapshot.getTicket();
        assertEquals("1", ticket.getId());
        assertEquals("Create ticket", snapshot.getWhatModified());
        assertEquals("manager", snapshot.getModifier());
        storer.deleteTicketHistory("1");
    }

    @Test
    public void testUpdateTicketHistory() {
        Ticket t = new Ticket();
        t.setId("1");
        storer.updateTicketHistory(t, "manager", "Create ticket");
        storer.updateTicketHistory(t, "manager", "Open Ticket");
        List<TicketSnapshot> snapshots = storer.loadTicketHistory("1");
        assertEquals(2, snapshots.size());
        storer.deleteTicketHistory("1");
    }

    @Test
    public void testDeleteTicketHistory() {
        Ticket t = new Ticket();
        t.setId("1");
        storer.updateTicketHistory(t, "manager", "Create ticket");
        storer.updateTicketHistory(t, "manager", "Open Ticket");
        List<TicketSnapshot> snapshots = storer.loadTicketHistory("1");
        assertEquals(true, storer.deleteTicketHistory("1"));
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
