package server;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cswt.Ticket;

public class ServerTicketManager {

	private HashMap<String, Ticket> mapping;
	private TicketDatabaseStorer storer;
	// Status constants
	public static final String STATUS_NEW = "NEW";
	public static final String STATUS_OPENED = "OPEN";
	public static final String STATUS_CLOSED = "CLOSED";
	public static final String STATUS_REJECTED = "REJECTED";
	public static final String STATUS_FIXED = "FIXED";
	
	
	public ServerTicketManager() {
		this.storer = new TicketDatabaseStorer();
		this.mapping = this.storer.loadTicketsFromDatabase();
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
		this.storer.storeTicket(ticket);
		return ticket;
	}

	/** Marks a ticket as open. 
	 * @param id The id of the ticket
	 * @param priority The priority of the ticket
	 * @param assignedTo The user the ticket will be assigned to
	 * @return The updated ticket or null if the system was unable to store the ticket
	 * */
	public synchronized Ticket openTicket(String id, String priority, String assignedTo) {
		Ticket ticket = mapping.get(id);
		ticket.setPriority(priority);
		ticket.setStatus(STATUS_OPENED);
		ticket.setAssignedTo(assignedTo);
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		ticket.setOpenedDate(simpleDateFormat.format(new Date()));
		ticket.setClosedDate("");
		this.storer.storeTicket(ticket);
		return ticket;
	}
	
	/** Marks ticket as fixed and updates resolution. 
	 * @param id The id of the ticket
	 * @param resolution The resolution of the ticket
	 * @return The updated ticket or null if the system was unable to store the ticket
	 * */
	public synchronized Ticket markTicketAsFixed(String id, String resolution) {
		Ticket ticket = mapping.get(id);
		ticket.setStatus(STATUS_FIXED);
		ticket.setResolution(resolution);
		this.storer.storeTicket(ticket);
		return ticket;
	}
	
	/** Marks a ticket as closed. 
	 * @param id The id of the ticket
	 * @return The updated ticket or null if the system was unable to store the ticket
	 * */
	public synchronized Ticket closeTicket(String id) {
		Ticket ticket = mapping.get(id);
		ticket.setStatus(STATUS_CLOSED);
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		ticket.setClosedDate(simpleDateFormat.format(new Date()));
		this.storer.storeTicket(ticket);
		return ticket;
	}
	
	/** Marks a ticket as rejected. 
	 * @param id The id of the ticket
	 * @return The updated ticket or null if the system was unable to store the ticket
	 * */
	public synchronized Ticket rejectTicket(String id) {
		Ticket ticket = mapping.get(id);
		ticket.setStatus(STATUS_REJECTED);
		this.storer.storeTicket(ticket);
		return ticket;
	}
	
	/** Adds a ticket to the ticket manager. 
	 * @param ticket The ticket to be added
	 * @return If the manager was able to add the ticket
	 * */
	private synchronized boolean addTicket(Ticket ticket) {
		this.storer.storeTicket(ticket);
		mapping.put(ticket.getId(), ticket);
		return true;
	}

	
	
	/** Gets a ticket based on provided ticket id
	 * @param id The provided id
	 * @return The ticket or null if no ticket with that username exists
	 * */
	public synchronized Ticket getTicket(String id) {
		if (!mapping.containsKey(id)) {
			return null;
		}
		return mapping.get(id);
	}

	/** Gets a copy of a ticket based on provided ticket id
	 * @param id The provided id
	 * @return The ticket or null if no ticket with that username exists
	 * */
	public synchronized Ticket getTicketAsCopy(String id) {
		if (!mapping.containsKey(id)) {
			return null;
		}
		Ticket original = mapping.get(id);
		Ticket copy = new Ticket();
		copy.setTitle(original.getTitle());
		copy.setId(original.getId());
		copy.setClient(original.getClient());
		copy.setDescription(original.getDescription());
		copy.setAssignedTo(original.getAssignedTo());
		copy.setPriority(original.getPriority());
		copy.setSeverity(original.getSeverity());
		copy.setClosedDate(original.getClosedDate());
		copy.setOpenedDate(original.getOpenedDate());
		copy.setStatus(original.getStatus());
		copy.setTimeSpent(original.getTimeSpent());
		return copy;
	}

	public synchronized void deleteTicket(String id) {
		if (mapping.containsKey(id)) {
			this.storer.deleteTicket(mapping.get(id));
			mapping.remove(id);
		}
		return;
	}
	
	/** Gets all tickets
	 * @return The list of all tickets
	 * */
	public synchronized List<Ticket> getAllTickets() {
		return new ArrayList<Ticket>(mapping.values());
	}

	/** Gets all ticket ids
	 * @return The list of all ids
	 * */
	public synchronized List<String> getAllIds() {
		List<String> ids = new ArrayList<String>();
		ids.addAll(mapping.keySet());
		return ids;
	}

}
