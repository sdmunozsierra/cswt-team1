package tests;

import client.ClientTicketManager;
import cswt.Ticket;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ClientTicketManagerTest {
    ClientTicketManager manager;

    @Test
    public void testAddTicket() {
        Ticket ticket = new Ticket();
        ticket.setId("1");
        manager.addTicket(ticket);
        assertEquals(ticket, manager.getTicket("1"));
    }

    @Test
    public void testGetAllTickets() {
        Ticket ticket = new Ticket();
        ticket.setId("1");
        manager.addTicket(ticket);
        List<Ticket> tickets = new ArrayList<Ticket>();
        tickets.add(ticket);
        assertEquals(tickets, manager.getAllTickets());
    }

    @Test
    public void testGetTicket() {
        Ticket ticket1 = new Ticket();
        ticket1.setId("1");
        ticket1.setDescription("Descripiton");
        manager.addTicket(ticket1);
        Ticket ticket2 = manager.getTicket("1");
        assertEquals(ticket1.getDescription(), ticket2.getDescription());
    }



    @Test
    public void testRemoveTicket() {
        Ticket ticket = new Ticket();
        ticket.setId("1");
        manager.addTicket(ticket);
        manager.removeTicket("1");
        assertEquals(0, manager.getAllTickets().size());
    }

    @Test
    public void testUpdateTicket() {
        Ticket ticket1 = new Ticket();
        ticket1.setId("1");
        ticket1.setDescription("Original");
        manager.addTicket(ticket1);
        Ticket ticket2 = new Ticket();
        ticket2.setId("1");
        ticket2.setDescription("Altered");
        manager.updateTicket(ticket2);
        assertEquals("Altered", manager.getTicket("1").getDescription());
    }

    @Test
    public void testClearManager() {
        Ticket ticket1 = new Ticket();
        ticket1.setId("1");
        ticket1.setDescription("Original");
        manager.addTicket(ticket1);
        manager.clearManager();
        assertEquals(0, manager.getAllTickets().size());
    }

    @Test
    public void testFromJson() {
        Ticket ticket = manager.fromJSON(new JSONObject("{\"severity\":\"\",\"openedDate\":\"\",\"closedDate\":\"\",\"timeSpent\":\"\",\"description\":\"\",\"client\":\"\",\"id\":\"1\",\"title\":\"Ticket\",\"priority\":\"\",\"resolution\":\"\",\"assignedTo\":\"\",\"status\":\"\"}"));
        assertNotEquals("Ticket", ticket.getDescription());
    }


    @Before
    public void SetUp() {
        manager = new ClientTicketManager();
    }
}
