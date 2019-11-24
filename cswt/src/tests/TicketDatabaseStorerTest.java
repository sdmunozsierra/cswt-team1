package tests;

import cswt.Ticket;
import org.junit.Before;
import org.junit.Test;
import server.TicketDatabaseStorer;

import java.util.HashMap;

import static org.junit.Assert.*;

public class TicketDatabaseStorerTest {
    TicketDatabaseStorer storer;
    @Before
    public void SetUp() {
        storer = new TicketDatabaseStorer();
    }

    @Test
    public void testLoadTickets() {
        int before = storer.loadTicketsFromDatabase().size();
        Ticket ticket = new Ticket();
        ticket.setId("1");
        storer.storeTicket(ticket);
        int after = storer.loadTicketsFromDatabase().size();
        assertEquals(before + 1, after);
        storer.deleteTicket(ticket);
    }

    @Test
    public void testStoreTicket() {
        Ticket ticket = new Ticket();
        ticket.setId("1");
        ticket.setDescription("Description");
        ticket.setTitle("Title");
        storer.storeTicket(ticket);
        HashMap<String, Ticket> mapping = storer.loadTicketsFromDatabase();
        Ticket t = mapping.get("1");
        assertEquals("1", t.getId());
        assertEquals("Description", t.getDescription());
        assertEquals("Title", t.getTitle());
        storer.deleteTicket(ticket);
    }

    @Test
    public void testDeleteTicket() {
        Ticket ticket = new Ticket();
        ticket.setId("1");
        ticket.setDescription("Description");
        ticket.setTitle("Title");
        storer.storeTicket(ticket);
        storer.deleteTicket(ticket);
        HashMap<String, Ticket> mapping = storer.loadTicketsFromDatabase();
        assertEquals(false, mapping.containsKey("1"));
    }


}
