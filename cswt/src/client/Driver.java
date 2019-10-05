package client;

import cswt.Ticket;

public class Driver {
    public static void main(String [] args) {
    	ClientHandler client = new ClientHandler();
    	client.createTicket(new Ticket());
    	for (Ticket ticket: client.getAllTickets()) {
    		System.out.println(ticket.getDescription());
    	}
    }
}
