package client;

import cswt.Ticket;
import cswt.User;

public class Driver {
    public static void main(String [] args) {
    	ClientHandler client = new ClientHandler();
    	Ticket ticket = new Ticket();
    	ticket.setPriority("2");
    	System.out.println(client.createTicket(ticket));
    }
}
