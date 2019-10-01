package client;

import cswt.Ticket;

public class Driver {
    public static void main(String [] args) {
    	ClientHandler client = new ClientHandler();
    	client.createTicket("title", "description", "client", "severity");
    	for (Ticket ticket: client.getAllTickets()) {
    		System.out.println(ticket.getDescription());
    	}
    }
}
