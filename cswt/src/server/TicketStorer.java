package server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

import cswt.Ticket;

import static cswt.Ticket.convertToTicket;

public class TicketStorer {
	public static String TICKET_DIR = Paths.get(System.getProperty("user.dir"), "tickets").toString();
	private FileWriter writer;
	
	public TicketStorer() {
        File ticketDirectory = new File(TICKET_DIR);
        if (!ticketDirectory.exists()) {
        	ticketDirectory.mkdir();
        }
	}
	
	/** Retrieves tickets from datastore.
	 * @return A list of all stored tickets
	 * */
	public synchronized List<Ticket> loadTickets() {
		List<Ticket> tickets = new ArrayList<Ticket>();
		File folder = new File(TICKET_DIR);
		if (folder.listFiles() != null) {
			for(File file : folder.listFiles()) {
				if(file.getName().contains(".json")) {
					Ticket ticket = readTicketFromFile(file);
					if (ticket != null) {
						tickets.add(ticket);
					}
				}
			}
		}
		return tickets;
	}
	
	/** Reads a ticket from a ticket file. 
	 * @param file The file to be read
	 * @return The ticket read form the file or null if there was an error
	 * */
	private synchronized Ticket readTicketFromFile(File file) {
		 try {
		 	FileReader reader = new FileReader(file);
		 	JSONTokener parser = new JSONTokener(reader);
		 	JSONObject obj = (JSONObject) parser.nextValue();
		 	JSONObject itemJSON = (JSONObject) obj;
		 	Ticket ticket = convertToTicket(itemJSON);
		 	reader.close();
		 	return ticket;
		 }
		 catch (Exception e) {
			 e.printStackTrace();
			 return null;
		 }
	}
	
	/** Stores a ticket in a datastore 
	 * @param ticket The user to be stored
	 * @return If the storer was able to store the ticket
	 * */
	public synchronized boolean storeTicket(Ticket ticket) {
		try{
			writeTicketToFile(ticket);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	/** Writes a ticket to a file.
	 * @param ticket The ticket that will be written to a file
	 * @throws IOException
	 * */
	private synchronized void writeTicketToFile(Ticket ticket) throws IOException {
		String filename = Paths.get(TICKET_DIR, ticket.getId() + ".json").toString();
		File file = new File(filename);
		file.createNewFile();
		writer = new FileWriter(filename);
		writer.write((ticket.toJSON()).toString());
		writer.close();
	}


}
