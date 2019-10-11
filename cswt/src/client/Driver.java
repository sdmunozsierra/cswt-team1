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

    	for (Ticket t: client.getAllTickets()) {
			client.openTicket(t.getId(), "7", t.getAssignedTo());
    	}
		for (Ticket t: client.getAllTickets()) {
			System.out.println(t.getOpenedDate());
		}
    }
}
