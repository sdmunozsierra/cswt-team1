package cswt;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class Ticket {
	private String title;
	private String description;
	private String status;
	private String resolution;
	private String severity;
	private String priority;
	private String client;
	private String assignedTo;
	private String openedDate;
	private String closedDate;
	private String timeSpent;
	private String id;
	
	public Ticket() {
		title = "";
		description = "";
		status = "";
		resolution = "";
		severity = "";
		priority = "";
		client = "";
		assignedTo = "";
		openedDate = "";
		closedDate = "";
		timeSpent = "";
		id = "";
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the resolution
	 */
	public String getResolution() {
		return resolution;
	}
	/**
	 * @param resolution the resolution to set
	 */
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}
	/**
	 * @return the client
	 */
	public String getClient() {
		return client;
	}
	/**
	 * @param client the client to set
	 */
	public void setClient(String client) {
		this.client = client;
	}
	/**
	 * @return the assignedTo
	 */
	public String getAssignedTo() {
		return assignedTo;
	}
	/**
	 * @param assignedTo the assignedTo to set
	 */
	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
	/**
	 * @return the severity
	 */
	public String getSeverity() {
		return severity;
	}
	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	/**
	 * @return the openedDate
	 */
	public String getOpenedDate() { return openedDate; }
	/**
	 * @param openedDate the openedDate to set
	 */
	public void setOpenedDate(String openedDate) {
		this.openedDate = openedDate;
	}
	/**
	 * @return the closedDate
	 */
	public String getClosedDate() {
		return closedDate;
	}
	/**
	 * @param closedDate the closedDate to set
	 */
	public void setClosedDate(String closedDate) {
		this.closedDate = closedDate;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the timeSpent
	 */
	public String getTimeSpent() {
		return timeSpent;
	}
	/**
	 * @param timeSpent the timeSpent to set
	 */
	public void setTimeSpent(String timeSpent) {
		this.timeSpent = timeSpent;
	}
	
	/**
	 * @return The ticket as a JSON Object
	 */
	public JSONObject toJSON() {
		Map<String, Object> map = new HashMap<>();
		map.put("title", title);
		map.put("description", description);
		map.put("status", status);
		map.put("resolution", resolution);
		map.put("severity", severity);
		map.put("priority", priority);
		map.put("client", client);
		map.put("assignedTo", assignedTo);
		map.put("openedDate", openedDate);
		map.put("closedDate", closedDate);
		map.put("id", id);
		map.put("timeSpent", timeSpent);
		return new JSONObject(map);
	}

	public static synchronized Ticket convertToTicket(JSONObject obj) {
		try {
			Ticket ticket = new Ticket();
			ticket.setTitle(obj.getString("title"));
			ticket.setDescription(obj.getString("description"));
			ticket.setAssignedTo(obj.getString("assignedTo"));
			ticket.setClient(obj.getString("client"));
			ticket.setClosedDate(obj.getString("closedDate"));
			ticket.setOpenedDate(obj.getString("openedDate"));
			ticket.setPriority(obj.getString("priority"));
			ticket.setStatus(obj.getString("status"));
			ticket.setResolution(obj.getString("resolution"));
			ticket.setSeverity(obj.getString("severity"));
			ticket.setId(obj.getString("id"));
			ticket.setTimeSpent(obj.getString("timeSpent"));
			return ticket;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int hashCode() {
		return (title.hashCode() + description.hashCode() + status.hashCode() + resolution.hashCode() +
				severity.hashCode() + priority.hashCode() + client.hashCode() + assignedTo.hashCode() +
				openedDate.hashCode() + closedDate.hashCode() + id.hashCode() + timeSpent.hashCode());
	}
	
}
