package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import org.json.JSONObject;

import cswt.Ticket;
import cswt.User;
import json.JSONPacketReader;
import json.JSONPacketWriter;
import json.JSONReader;
import json.JSONWriter;

/**
 * Push encoded JSON data packets through a file byte stream.
 *
 * @author Ryan Beckett, Sergio Sierra
 * @version 1.0
 * @since Dec 23, 2011
 */
public class ClientHandler {

    // Useful constants for permission validation and server protocol calls
    private static final String CREATE_TICKET = "Create ticket";
    private static final String OPEN_TICKET = "Open ticket";
    private static final String SEARCH_TICKETS = "Search tickets";
    private static final String MARK_TICKET_AS_FIXED = "Mark ticket as fixed";
    private static final String CLOSE_TICKET = "Close ticket";
    private static final String REJECT_TICKET = "Reject ticket";
    private static final String EDIT_TICKET = "Edit ticket";
    private static final String GET_ALL_TICKETS = "Get all tickets";
    public static final String SUCCESSFUL = "Successful";
    public static final String FAILED = "Failed";
    private static final String EMPTY = "Empty";
    private static final String NO_PRIORITY = "-1";
    public static final String INVALID = "Invalid";
    private static final String COMPLETE = "Complete";
    private static final String CREATE_ACCOUNT = "Create account";
    private static final String VALIDATE_USER = "Validate user";
    private static final String EDIT_USER = "Edit user";
    private static final String DELETE_USER = "Delete User";
    private static final String GET_ALL_USERS = "Get all users";
    private static final String MANAGER = "Manager";
    private static final String TICKET_ADMIN = "Ticket Admin";
    private static final String DEPARTMENT_SYSADMIN = "Department SysAdmin";

    // Class Members
    private String currentUserType = "";
    private Socket socket = null;
    private DataOutputStream oos = null;
    private DataInputStream ois = null;
    private static final String ENCODING = "UTF-8";
    private static final int PORT = 9880;
    private JSONWriter wrtr = null;
    private JSONReader rdr = null;
    private ClientTicketManager ticketManager = null;
    private ClientUserManager userManager = null;

    public ClientHandler() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            socket = new Socket(host.getHostName(), PORT);
            oos = new DataOutputStream(socket.getOutputStream());
            wrtr = new JSONPacketWriter(oos, ENCODING);
            ois = new DataInputStream(socket.getInputStream());
            rdr = new JSONPacketReader(ois, ENCODING);
            userManager = new ClientUserManager();
            ticketManager = new ClientTicketManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Sends a create ticket request to server.
     *
     * @param ticket The ticket to be created
     * @return A String that represents the result of the request
     */
    public synchronized String createTicket(Ticket ticket ) {
        if (currentUserType != DEPARTMENT_SYSADMIN) {
        	return INVALID;
        }
        String sendJson = "{\"request\": " + CREATE_TICKET + ", \"title\": " + ticket.getTitle() + ", \"description\": " + 
        		ticket.getDescription() + ", \"client\": " + ticket.getClient() + ", \"severity\": " + ticket.getSeverity() + 
        		", \"priority\": " + ticket.getPriority() + ", \"assignedTo\": " + ticket.getAssignedTo() + "}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.addTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends an open ticket request to server.
     *
     * @param id         The id of the ticket
     * @param priority   The priority of ticket
     * @param assignedTo The assignee of the ticket
     * @return A String that represents the result of the request
     */
    public synchronized String openTicket(String id, String priority, String assignedTo) {
        if(currentUserType != DEPARTMENT_SYSADMIN){
            return INVALID;
        }
        String sendJson = "{\"request\": " + OPEN_TICKET + ", \"id\": " + id + ", \"priority\": " + priority + ", \"assignedTo\": " + assignedTo + "}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.removeTicket(id);
            ticketManager.addTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends a close ticket request to server.
     *
     * @param id The id of the ticket
     * @return A String that represents the result of the request
     */
    public synchronized String closeTicket(String id) {
        if (currentUserType != MANAGER) {
            return INVALID;
        }
        String sendJson = "{\"request\": " + CLOSE_TICKET + ", \"id\": " + id + "}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.removeTicket(id);
            ticketManager.addTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends a fix ticket request to server.
     *
     * @param id         The id of the ticket
     * @param resolution The resolution of ticket
     * @param timeSpent  The time spent on the ticket
     * @return A String that represents the result of the request
     */
    public synchronized String markTicketAsFixed(String id, String resolution, String timeSpent) {
        if (currentUserType != DEPARTMENT_SYSADMIN) {
            return INVALID;
        }
        String sendJson = "{\"request\": " + MARK_TICKET_AS_FIXED + ", \"id\": " + id + ", \"resolution\": " + resolution + ", \"timeSpent\": " + timeSpent + "}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.removeTicket(id);
            ticketManager.addTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends a reject ticket request to server.
     *
     * @param id The id of the ticket
     * @return A String that represents the result of the request
     */
    public synchronized String rejectTicket(String id) {
        if (currentUserType != MANAGER && currentUserType != DEPARTMENT_SYSADMIN) {
            return INVALID;
        }
        String sendJson = "{\"request\": " + REJECT_TICKET + ", \"id\": " + id + "}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.removeTicket(id);
            ticketManager.addTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends an edit ticket request to server.
     *
     * @param ticket The ticket to be edited
     * @return A String that represents the result of the request
     */
    public synchronized String editTicket(Ticket ticket) {
        if(currentUserType == TICKET_ADMIN){
            return INVALID;
        }
        String sendJson = "{\"request\": " + EDIT_TICKET + ", \"resolution\": " + ticket.getResolution() + ", \"description\": " + 
        		ticket.getDescription() + ", \"client\": " + ticket.getClient() + ", \"severity\": " + ticket.getSeverity() + 
        		", \"priority\": " + ticket.getPriority() + ", \"assignedTo\": " + ticket.getAssignedTo() + 
        		", \"id\": " + ticket.getId() +"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.addTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }


    /**
     * Sends a search all tickets request to server.
     * @param status The wanted status of the tickets
     * @param priority The wanted priority of the tickets
     * @param severity The wanted severity of the tickets
     * @param client The wanted client of the tickets
     * @param assignedTo The wanted assignee of the ticket
     */
    public synchronized void searchTickets(String status, String priority, String severity, String client, String assignedTo) {
        ticketManager.clearManager();
        if (status.equals("")) {
        	status = EMPTY;
        }
        if (client.equals("")) {
        	client = EMPTY;
        }
        if (assignedTo.equals("")) {
        	assignedTo = EMPTY;
        }
        if (severity.equals("")) {
        	severity = EMPTY;
        }
        if (priority.equals("")) {
        	priority = NO_PRIORITY;
        }
        String sendJson = "{\"request\": " + SEARCH_TICKETS + ", \"priority\": " + priority + ", \"severity\": " + severity + ", \"status\": " + status + ", \"client\": " + client + ", \"assignedTo\": " + assignedTo + "}";
        wrtr.write(sendJson);
        while (true) {
            String retrievedJSON = rdr.read();
            JSONObject message = new JSONObject(retrievedJSON);
            if (message.getString("response").equals(COMPLETE)) {
                break;
            }
            ticketManager.addTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
        }
    }

    /**
     * Sends a get all tickets request to server.
     */
    public synchronized void updateAllTickets() {
        ticketManager.clearManager();
        String sendJson = "{\"request\": " + GET_ALL_TICKETS + "}";
        wrtr.write(sendJson);
        while (true) {
            String retrievedJSON = rdr.read();
            JSONObject message = new JSONObject(retrievedJSON);
            if (message.getString("response").equals(COMPLETE)) {
                break;
            }
            ticketManager.addTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
        }
    }

    /**
     * Sends a create account request to server.
     *
     * @param username   The username of the new user
     * @param password   The password of the new user
     * @param type       The type of the new user
     * @param actualName The actual name of the new user
     * @param email      The email of the new user
     * @return A String that represents the result of the request
     */
    public synchronized String createAccount(String username, String password, String type, String actualName, String email) {
        if (currentUserType != TICKET_ADMIN) {
            return INVALID;
        }
        if (userManager.hasUser(username)) {
            return FAILED;
        }
        String sendJson = "{\"request\": " + CREATE_ACCOUNT + ", \"username\": " + username + ", \"password\": " + password + ", \"type\": " + type + ", \"actualName\": " + actualName + ", \"email\": " + email + "}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            userManager.addUser(userManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends a validate user request to server.
     *
     * @param username The username of the user
     * @param password The password of the user
     * @return A String that represents the result of the request
     */
    public synchronized String validateUser(String username, String password) {
        String sendJson = "{\"request\": " + VALIDATE_USER + ", \"username\": " + username + ", \"password\": " + password + "}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            currentUserType = message.getString("permissions");
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends a edit user request to server.
     *
     * @param username   The username of the user
     * @param password   The new password of the user
     * @param type       The new type of the user
     * @param actualName The new actual name of the user
     * @param email      The new email of the user
     * @return A String that represents the result of the request
     */
    public synchronized String editUser(String username, String password, String type, String actualName, String email) {
        if (currentUserType != TICKET_ADMIN) {
            return INVALID;
        }
        String sendJson = "{\"request\": " + EDIT_USER + ", \"username\": " + username + ", \"password\": " + password + ", \"type\": " + type + ", \"actualName\": " + actualName + ", \"email\": " + email + "}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            userManager.removeUser(username);
            userManager.addUser(userManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends a delete user request to server.
     *
     * @param username The username of the user
     * @return A String that represents the result of the request
     */
    public synchronized String deleteUser(String username) {
        if (currentUserType != TICKET_ADMIN) {
            return INVALID;
        }
        String sendJson = "{\"request\": " + DELETE_USER + ", \"username\": " + username + "}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            userManager.removeUser(username);
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends a get all users request to server.
     */
    public synchronized String updateAllUsers() {
        userManager.clearManager();
        String sendJson = "{\"request\": " + GET_ALL_USERS + "}";
        wrtr.write(sendJson);
        while (true) {
            String retrievedJSON = rdr.read();
            JSONObject message = new JSONObject(retrievedJSON);
            if (message.getString("response").equals(COMPLETE)) {
                break;
            }
            userManager.addUser(userManager.fromJSON(new JSONObject(message.get("result").toString())));
        }
        return SUCCESSFUL;
    }

    /**
     * Gets all tickets.
     *
     * @return A List of all tickets
     */
    public synchronized List<Ticket> getAllTickets() {
        return ticketManager.getAllTickets();
    }

    /**
     * Gets a ticket from the local ticket manager
     *
     * @param id The id of the ticket
     * @return The ticket or null if no ticket with that id exists
     */
    public synchronized Ticket getTicket(String id) {
        return ticketManager.getTicket(id);
    }

    /**
     * Gets all users.
     *
     * @return A List of all users
     */
    public synchronized List<User> getAllUsers() {
        return userManager.getAllUsers();
    }

    /**
     * Gets a user from the local user manager.
     *
     * @param username The username of the user
     * @return The user or null if no user with that username exists
     */
    public synchronized User getUser(String username) {
        return userManager.getUser(username);
    }

    /**
     * Closes client readers and writers
     */
    public synchronized void closeClient() {
        wrtr.close();
        rdr.close();
    }
}
