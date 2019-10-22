package server;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cswt.Ticket;

public class ServerTicketManager {
	
	private List<Ticket> tickets;
	private List<String> ids;
	private TicketStorer storer;
	// Status constants
	private static final String STATUS_NEW = "NEW";
	private static final String STATUS_OPENED = "OPEN";
	private static final String STATUS_CLOSED = "CLOSED";
	private static final String STATUS_REJECTED = "REJECTED";
	private static final String STATUS_FIXED = "FIXED";
	
	
	public ServerTicketManager() {
		this.storer = new TicketStorer();
		this.tickets = this.storer.loadTickets();
		getIds();
	}
	
	/** Creates a new ticket and stores it. 
	 * @param title The title of the ticket to be created
	 * @param description The description of the ticket to be created
	 * @param client The client of the ticket to be created
	 * @param severity The severity of the ticket to be created
	 * @param assignedTo The assignee of the ticket to be created
	 * @param priority The priority of the ticket to be created
	 * @return The new ticket or null if the system was unable to store the ticket
	 * */
	public synchronized Ticket createTicket(String title, String description, String client, String severity, String assignedTo, String priority) {
		Ticket ticket = new Ticket();
		ticket.setTitle(title);
		ticket.setDescription(description);
		ticket.setClient(client);
		ticket.setSeverity(severity);
		ticket.setPriority(priority);
		ticket.setAssignedTo(assignedTo);
		ticket.setStatus(STATUS_NEW);
		Date date = new Date();
		ticket.setId(Long.toString(date.getTime()));
		boolean added = addTicket(ticket);
		if (added) {
			return ticket;
		}
		return null;
	}
	
	
	/** Edits a ticket
	 * @param id The id of the ticket to be edited
	 * @param title The title of the ticked to be edited
	 * @param description The description of the ticket to be edited
	 * @param resolution The resolution of the ticket to be edited
	 * @param client The client of the ticket to be edited
	 * @param severity The severity of the ticket to be edited
	 * @param assignedTo The assignee of the ticket to be edited
	 * @param priority The priority of the ticket to be edited
	 * @return The edited ticket or null if the system was unable to edit the ticket
	 * */
	public synchronized Ticket editTicket(String id, String title, String description, String resolution, String client, String severity, String assignedTo, String priority) {
		Ticket ticket = this.getTicket(id);
		ticket.setTitle(title);
		ticket.setDescription(description);
		ticket.setResolution(resolution);
		ticket.setAssignedTo(assignedTo);
		ticket.setClient(client);
		ticket.setSeverity(severity);
		ticket.setPriority(priority);
		ticket.setAssignedTo(assignedTo);
		boolean updated= this.storer.storeTicket(ticket);
		if (updated) {
			return ticket;
		}
		return null;
	}

	/** Marks a ticket as open. 
	 * @param id The id of the ticket
	 * @param priority The priority of the ticket
	 * @param assignedTo The user the ticket will be assigned to
	 * @return The updated ticket or null if the system was unable to store the ticket
	 * */
	public synchronized Ticket openTicket(String id, String priority, String assignedTo) {
		int index = ids.indexOf(id);
		Ticket ticket = tickets.get(index);
		ticket.setPriority(priority);
		ticket.setStatus(STATUS_OPENED);
		ticket.setAssignedTo(assignedTo);
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		ticket.setOpenedDate(simpleDateFormat.format(new Date()));
		ticket.setClosedDate("");
		boolean updated = this.storer.storeTicket(ticket);
		if (updated) {
			return ticket;
		}
		return null;
	}
	
	/** Marks ticket as fixed and updates resolution. 
	 * @param id The id of the ticket
	 * @param resolution The resolution of the ticket
	 * @return The updated ticket or null if the system was unable to store the ticket
	 * */
	public synchronized Ticket markTicketAsFixed(String id, String resolution) {
		int index = ids.indexOf(id);
		Ticket ticket = tickets.get(index);
		ticket.setStatus(STATUS_FIXED);
		ticket.setResolution(resolution);
		boolean updated = this.storer.storeTicket(ticket);
		if (updated) {
			return ticket;
		}
		return null;
	}
	
	/** Marks a ticket as closed. 
	 * @param id The id of the ticket
	 * @return The updated ticket or null if the system was unable to store the ticket
	 * */
	public synchronized Ticket closeTicket(String id) {
		int index = ids.indexOf(id);
		Ticket ticket = tickets.get(index);
		ticket.setStatus(STATUS_CLOSED);
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		ticket.setClosedDate(simpleDateFormat.format(new Date()));
		boolean updated = this.storer.storeTicket(ticket);
		if (updated) {
			return ticket;
		}
		return null;
	}
	
	/** Marks a ticket as rejected. 
	 * @param id The id of the ticket
	 * @return The updated ticket or null if the system was unable to store the ticket
	 * */
	public synchronized Ticket rejectTicket(String id) {
		int index = ids.indexOf(id);
		Ticket ticket = tickets.get(index);
		ticket.setStatus(STATUS_REJECTED);
		boolean updated = this.storer.storeTicket(ticket);
		if (updated) {
			return ticket;
		}
		return null;
	}
	
	/** Adds a ticket to the ticket manager. 
	 * @param ticket The ticket to be added
	 * @return If the manager was able to add the ticket
	 * */
	private synchronized boolean addTicket(Ticket ticket) {
		if(!this.storer.storeTicket(ticket)) {
			return false;
		}
		this.tickets.add(ticket);
		this.ids.add(ticket.getId());
		return true;
	}
	
	/** Gets the list of ids for the manager 
	 * */
	private synchronized void getIds() {
		this.ids = new ArrayList<String>();
		for (Ticket ticket: this.tickets) {
			ids.add(ticket.getId());
		}
	}
	
	
	/** Gets a ticket based on provided ticket id
	 * @param id The provided id
	 * @return The ticket or null if no ticket with that username exists
	 * */
	public synchronized Ticket getTicket(String id) {
		int index = this.ids.indexOf(id);
		if (index == -1) {
			return null;
		}
		return this.tickets.get(index);
	}
	
	/** Gets all tickets
	 * @return The list of all tickets
	 * */
	public synchronized List<Ticket> getAllTickets() {
		return this.tickets;
	}

	/** Gets all ticket ids
	 * @return The list of all ids
	 * */
	public synchronized List<String> getAllIds() {
		return this.ids;
	}

	/** Sets the storer for the tickets
	 * @param storer The storer to be set
	 * */
	public synchronized  void setStorer(TicketStorer storer) {
		this.storer = storer;
	}
}
