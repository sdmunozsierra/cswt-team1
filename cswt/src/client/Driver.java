package client;

import cswt.Ticket;

public class Driver {
    public static void main(String [] args) {
    	ClientHandler client = new ClientHandler();
    	Ticket ticket = new Ticket();
    	ticket.setTitle("Hello");
    	ticket.setDescription("here");
    	ticket.setClient("k");
    	ticket.setSeverity("i");
    	ticket.setPriority("1");
    	ticket.setAssignedTo("g");
    	client.createTicket(ticket);
    	String id = "";
    	for (Ticket t: client.getAllTickets()) {
    		System.out.println(t.getTitle());
    		id = t.getId();
    	}
        Ticket ticket2 = new Ticket();
    	ticket2.setId(id);
        ticket2.setTitle("DED");
        ticket2.setDescription("here");
        ticket2.setClient("k");
        ticket2.setSeverity("i");
        ticket2.setPriority("1");
        ticket2.setAssignedTo("g");
        client.editTicket(ticket2);
		System.out.println(client.getTicket(ticket2.getId()).getTitle());
    }
}
