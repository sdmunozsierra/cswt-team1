package client;

import cswt.TicketSnapshot;
import server.TicketHistoryStorer;

import java.util.List;

public class Driver {
    public static void main(String [] args) {
    	TicketHistoryStorer storer = new TicketHistoryStorer();
		List<TicketSnapshot> history = storer.loadTicketHistory("1");
		for (TicketSnapshot ticketSnapshot : history) {
			System.out.println(ticketSnapshot.getTicket().getId());
			System.out.println(ticketSnapshot.getModifier());
			System.out.println(ticketSnapshot.getWhatModified());
			System.out.println(ticketSnapshot.getDateModified());
		}
    }
}
