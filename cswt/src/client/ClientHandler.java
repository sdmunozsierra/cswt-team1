package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import cswt.TicketSnapshot;
import org.json.JSONObject;

import cswt.Ticket;
import cswt.User;
import json.JSONPacketReader;
import json.JSONPacketWriter;
import json.JSONReader;
import json.JSONWriter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static cswt.TicketSnapshot.convertToSnapshot;

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
    private static final String UPDATE_TICKET = "Update ticket";
    private static final String UPDATE_USER = "Update user";
    private static final String EDIT_TICKET = "Edit ticket";
    private static final String GET_ALL_TICKETS = "Get all tickets";
    private static final String GET_RECENT_TICKETS = "Get recent tickets";
    public static final String SUCCESSFUL = "Successful";
    public static final String FAILED = "Failed";
    private static final String GET_TICKET_HASH = "Get ticket hash";
    private static final String GET_USER_HASH = "Get user hash";
    public static final String INVALID = "Invalid";
    private static final String GET_TICKET_HISTORY = "Get ticket history";
    public static final String OLD = "Old";
    private static final String COMPLETE = "Complete";
    private static final String CREATE_ACCOUNT = "Create account";
    private static final String VALIDATE_USER = "Validate user";
    private static final String EDIT_USER = "Edit user";
    private static final String DELETE_USER = "Delete User";
    private static final String GET_ALL_USERS = "Get all users";
    public static final String MANAGER = "Manager";
    public static final String TICKET_ADMIN = "Ticket Admin";
    public static final String DEPARTMENT_SUPPORT = "Department Support";
    private static final String key = "77789BXarcy77777";

    // Class Members
    private String currentUserType = MANAGER;
    private String currentUser = "manager";
    private List<TicketSnapshot> ticketHistory = new ArrayList<>();
    private Socket socket = null;
    private DataOutputStream oos = null;
    private DataInputStream ois = null;
    private static final String ENCODING = "UTF-8";
    private static final int PORT = 9880;
    private JSONWriter wrtr = null;
    private JSONReader rdr = null;
    private ClientTicketManager ticketManager = null;
    private ClientUserManager userManager = null;
    private Cipher encrypt;
    private Cipher decrypt;


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
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            encrypt = Cipher.getInstance("AES");
            encrypt.init(Cipher.ENCRYPT_MODE, aesKey);
            decrypt = Cipher.getInstance("AES");
            decrypt.init(Cipher.DECRYPT_MODE, aesKey);
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
    public synchronized String createTicket(Ticket ticket) {
        if (currentUserType.equals(TICKET_ADMIN)) {
          return INVALID;
        }
        String sendJson = "{\"request\": " + CREATE_TICKET + ", \"title\": \"" + ticket.getTitle() + "\", \"modifier\": \"" + currentUser +
                "\", \"description\": \"" + ticket.getDescription() + "\", \"client\": \"" + ticket.getClient() + "\", \"severity\": \"" +
                ticket.getSeverity() + "\", \"priority\": \"" + ticket.getPriority() + "\", \"assignedTo\": \"" + ticket.getAssignedTo() + "\"}";
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
     * Compares client version of ticket to server version to determine if a ticket is editable.
     *
     * @param id         The id of the ticket
     * @return Whether of not the ticket can be edited
     */
    public synchronized boolean isTicketEditable(String id) {
        String sendJson = "{\"request\": " + GET_TICKET_HASH + ", \"id\": \"" + id + "\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            String recent = message.getString("hash");
            return recent.equals(Integer.toString(ticketManager.getTicket(id).hashCode()));
        }
        return false;
    }

    /**
     * Compares client version of user to server version to determine if a user is editable.
     *
     * @param username        The username of the user
     * @return Whether of not the ticket can be edited
     */
    public synchronized boolean isUserEditable(String username) {
        String sendJson = "{\"request\": " + GET_USER_HASH + ", \"username\": \"" + encodeMessage(username) + "\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            String recent = message.getString("hash");
            return recent.equals(Integer.toString(userManager.getUser(username).hashCode()));
        }
        return false;
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
        if (!currentUserType.equals(MANAGER)) {
        	return INVALID;
        }
        if (assignedTo.equals("")){
            return FAILED;
        }
        if (!isTicketEditable(id)) {
            return OLD;
        }
        String sendJson = "{\"request\": " + OPEN_TICKET + ", \"id\": \"" + id + "\", \"modifier\": \"" + currentUser +
                            "\", \"priority\": \"" + priority + "\", \"assignedTo\": \"" + assignedTo + "\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.updateTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
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
        if (!currentUserType.equals(MANAGER)) {
            return INVALID;
        }
        if (!isTicketEditable(id)) {
            return OLD;
        }
        String sendJson = "{\"request\": " + CLOSE_TICKET + ", \"id\": \"" + id + "\", \"modifier\": \"" + currentUser +"\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.updateTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends a fix ticket request to server.
     *
     * @param id         The id of the ticket
     * @param resolution The resolution of ticket
     * @return A String that represents the result of the request
     */
    public synchronized String markTicketAsFixed(String id, String resolution) {
        if (currentUserType.equals(TICKET_ADMIN)) {
            return INVALID;
        }
        if (resolution.equals("")) {
            return FAILED;
        }
        if (!isTicketEditable(id)) {
            return OLD;
        }
        String sendJson = "{\"request\": " + MARK_TICKET_AS_FIXED + ", \"id\": \"" + id + "\", \"resolution\": \"" + resolution +
                            "\", \"modifier\": \"" + currentUser +"\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.updateTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
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
        if (!currentUserType.equals(MANAGER)) {
            return INVALID;
        }
        if (!isTicketEditable(id)) {
            return OLD;
        }
        String sendJson = "{\"request\": " + REJECT_TICKET + ", \"id\": \"" + id + "\", \"modifier\": \"" + currentUser +"\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.updateTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
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
    	if (currentUserType.equals(TICKET_ADMIN)) {
    		return INVALID;
    	}
        if (!isTicketEditable(ticket.getId())) {
            return OLD;
        }
        Ticket original = ticketManager.getTicket(ticket.getId());
        if (original.hashCode() == ticket.hashCode()) {
            return SUCCESSFUL;
        }
        String title = ticket.getTitle();
        String description = ticket.getDescription();
        String client = ticket.getClient();
        String severity = ticket.getSeverity();
        String priority = ticket.getPriority();
        String assignedTo = ticket.getAssignedTo();
        String resolution = ticket.getResolution();
        String sendJson = "{\"request\": " + EDIT_TICKET + ", \"resolution\": \"" + resolution + "\", \"description\": \"" +
        		description + "\", \"client\": \"" + client + "\", \"severity\": \"" + severity + "\", \"modifier\": \"" + currentUser +
        		"\", \"priority\": \"" + priority + "\", \"assignedTo\": \"" + assignedTo + "\", \"title\": \"" + title +
        		"\", \"id\": \"" + ticket.getId() +"\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.updateTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }


    /**
     * Sends a search all tickets request to server.
     * @param title The wanted title of the tickets
     * @param description The wanted description of the tickets
     * @param resolution The wanted resolution of the tickets
     * @param status The wanted status of the tickets
     * @param priority The wanted priority of the tickets
     * @param severity The wanted severity of the tickets
     * @param client The wanted client of the tickets
     * @param assignedTo The wanted assignee of the ticket
     */
    public synchronized void searchTickets(String title, String description, String resolution, String status, String priority, String severity, String client, String assignedTo) {
        String sendJson = "{\"request\": " + SEARCH_TICKETS + ", \"description\": \"" + description + "\", \"title\": " + title + "\", \"resolution\": " + resolution + "\", \"priority\": \"" + priority + "\", \"severity\": \"" + severity + "\", \"status\": \"" + status + "\", \"client\": \"" + client + "\", \"assignedTo\": \"" + assignedTo + "\"}";
        wrtr.write(sendJson);
        ticketManager.clearManager();
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
    public synchronized void collectAllTickets() {
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
     * Sends a get recent tickets request to server.
     */
    public synchronized void collectRecentTickets() {
        ticketManager.clearManager();
        String sendJson = "{\"request\": " + GET_RECENT_TICKETS + "}";
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
     * Gets the most recent version of a ticket from the server..
     *
     * @param id         The id of the ticket to be updated
     * @return A String that represents the result of the request
     */
    public synchronized String updateTicket(String id) {
        String sendJson = "{\"request\": " + UPDATE_TICKET + ", \"id\": \"" + id  + "\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            ticketManager.updateTicket(ticketManager.fromJSON(new JSONObject(message.get("result").toString())));
            return SUCCESSFUL;
        }
        return FAILED;
    }

    public synchronized String collectTicketHistory(String id) {
        ticketHistory.clear();
        String sendJson = "{\"request\": " + GET_TICKET_HISTORY + ", \"modifier\": \"" + currentUser + "\", \"id\": \"" + id +"\"}";
        wrtr.write(sendJson);
        while (true) {
            String retrievedJSON = rdr.read();
            JSONObject message = new JSONObject(retrievedJSON);
            if (message.getString("response").equals(COMPLETE)) {
                break;
            }
            else if (message.getString("response").equals(FAILED)) {
                return FAILED;
            }
            ticketHistory.add(convertToSnapshot(new JSONObject(message.get("result").toString())));
        }
        return SUCCESSFUL;
    }

    public synchronized List<TicketSnapshot> getTicketHistory() {
        return ticketHistory;
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
        if (!currentUserType.equals(TICKET_ADMIN)) {
            return INVALID;
        }
        if (userManager.hasUser(username)) {
            return FAILED;
        }
        String sendJson = "{\"request\": " + CREATE_ACCOUNT + ", \"username\": \"" + encodeMessage(username) + "\", \"password\": \"" + encodeMessage(password) + "\", \"type\": \"" + encodeMessage(type) + "\", \"actualName\": \"" + encodeMessage(actualName) + "\", \"email\": \"" + encodeMessage(actualName) + "\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            User user = userManager.fromJSON(new JSONObject(decodeMessage(message.get("result").toString())));
            user.setPassword(decodeMessage(user.getPassword()));
            userManager.addUser(user);
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
        String sendJson = "{\"request\": " + VALIDATE_USER + ", \"username\": \"" + encodeMessage(username) + "\", \"password\": \"" + encodeMessage(password) + "\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            currentUser = username;
            currentUserType = decodeMessage(message.getString("permissions"));
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
        if (!currentUserType.equals(TICKET_ADMIN)) {
            return INVALID;
        }
        if (!isUserEditable(username)) {
            return OLD;
        }
        String sendJson = "{\"request\": " + EDIT_USER + ", \"username\": \"" + encodeMessage(username) + "\", \"password\": \"" + encodeMessage(password) + "\", \"type\": \"" + encodeMessage(type) + "\", \"actualName\": \"" + encodeMessage(actualName) + "\", \"email\": \"" + encodeMessage(email) + "\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            User user = userManager.fromJSON(new JSONObject(decodeMessage(message.get("result").toString())));
            user.setPassword(decodeMessage(user.getPassword()));
            userManager.updateUser(user);
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
        if (!currentUserType.equals(TICKET_ADMIN)) {
            return INVALID;
        }
        String sendJson = "{\"request\": " + DELETE_USER + ", \"username\": \"" + encodeMessage(username) + "\"}";
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
     * Gets the most recent version of a user from the server.
     *
     * @param username         The id of the ticket to be updated
     * @return A String that represents the result of the request
     */
    public synchronized String updateUser(String username) {
        String sendJson = "{\"request\": " + UPDATE_USER + ", \"username\": \"" + encodeMessage(username)  + "\"}";
        wrtr.write(sendJson);
        String retrievedJSON = rdr.read();
        JSONObject message = new JSONObject(retrievedJSON);
        if (message.getString("response").equals(SUCCESSFUL)) {
            User user = userManager.fromJSON(new JSONObject(decodeMessage(message.get("result").toString())));
            user.setPassword(decodeMessage(user.getPassword()));
            userManager.updateUser(user);
            return SUCCESSFUL;
        }
        return FAILED;
    }

    /**
     * Sends a get all users request to server.
     */
    public synchronized String collectAllUsers() {
        userManager.clearManager();
        String sendJson = "{\"request\": " + GET_ALL_USERS + "}";
        wrtr.write(sendJson);
        while (true) {
            String retrievedJSON = rdr.read();
            JSONObject message = new JSONObject(retrievedJSON);
            if (message.getString("response").equals(COMPLETE)) {
                break;
            }
            User user = userManager.fromJSON(new JSONObject(decodeMessage(message.get("result").toString())));
            user.setPassword(decodeMessage(user.getPassword()));
            userManager.addUser(user);
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

    public synchronized String getCurrentUserType() {
        return currentUserType;
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

    private synchronized String decodeMessage(String message) {
        try {
            return new String(decrypt.doFinal(Base64.getDecoder().decode(message)));
        } catch (Exception e) {
            e.printStackTrace();
            return message;
        }
    }

    private synchronized String encodeMessage(String message) {
        try {
            return Base64.getEncoder().encodeToString(encrypt.doFinal(message.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return message;
        }
    }
}
