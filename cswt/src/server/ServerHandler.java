package server;

import json.JSONPacketReader;
import json.JSONPacketWriter;
import json.JSONReader;
import json.JSONWriter;
import org.json.JSONObject;

import cswt.Ticket;
import cswt.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandler {
    private static ServerSocket server;
    private static final String CREATE_TICKET = "Create ticket";
    private static final String OPEN_TICKET = "Open ticket";
    private static final String MARK_TICKET_AS_FIXED = "Mark ticket as fixed";
    private static final String CLOSE_TICKET = "Close ticket";
    private static final String REJECT_TICKET = "Reject ticket";
    private static final String ASSIGN_TICKET = "Assign ticket";
    private static final String SET_TICKET_SEVERITY = "Set ticket severity";
    private static final String SET_TICKET_PRIORITY = "Set ticket priority";
    private static final String UPDATE_TICKET_RESOLUTION = "Update ticket resolution";
    private static final String UPDATE_TIME_SPENT = "Update time spent";
    private static final String GET_ALL_TICKETS = "Get all tickets";
    private static final String SUCCESSFUL = "Successful";
    private static final String FAILED = "Failed";
    private static final String COMPLETE = "Complete";
    private static final String CREATE_ACCOUNT = "Create account";
    private static final String VALIDATE_USER = "Validate user";
    private static final String UPDATE_PERMISSIONS = "Update permissions";
    private static final String DELETE_USER = "Delete User";
    private static final String GET_ALL_USERS = "Get all users.";
    private static final int PORT = 9880;
    private static final String ENCODING = "UTF-8";

    public static void main(String args[]) throws IOException, ClassNotFoundException{
        ServerTicketManager serverTicketManager = new ServerTicketManager();
        ServerUserManager serverUserManager = new ServerUserManager();
        File ticketDirectory = new File(ServerTicketManager.TICKET_DIR);
        File userDirectory = new File(ServerUserManager.USER_DIR);
        if (!ticketDirectory.exists()) {
        	ticketDirectory.mkdir();
        }
        if (!userDirectory.exists()) {
        	userDirectory.mkdir();
        }
        //create the socket server object
        server = new ServerSocket(PORT);
        //creating socket and waiting for client connection
        Socket socket = server.accept();
        //read from socket to ObjectInputStream object
        DataInputStream ois = new DataInputStream(socket.getInputStream());

        //convert InputStream object to String
        JSONReader rdr = new JSONPacketReader(ois, ENCODING);
        //create OutputStream object
        DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
        //write object to Socket
        JSONWriter wrtr = new JSONPacketWriter(oos, ENCODING);
        boolean cont = true;
        while(cont) {
        	try {
	            String retrievedJSON = rdr.read();
	            System.out.println("Retrieved JSON: " + retrievedJSON);
	            JSONObject message = null;
	            if (retrievedJSON != null){
	                message = new JSONObject(retrievedJSON);
	                String method = message.getString("request");
	                if (method.equals(CREATE_TICKET)) {
	                	String title = message.getString("title");
	                	String description = message.getString("description");
	                	String client = message.getString("client");
	                	String severity =  message.getString("severity");
	                	Ticket ticket = serverTicketManager.createTicket(title, description, client, severity);
	                	if (ticket != null) {
	                    	String ticketString = ticket.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(OPEN_TICKET)) {
	                	String id = ((Long) message.get("id")).toString();
	                	String priority = ((Integer) message.get("priority")).toString();
	                	String assignedTo = message.getString("assignedTo");
	                	Ticket ticket = serverTicketManager.openTicket(id, priority, assignedTo);
	                	if (ticket != null) {
	                    	String ticketString = ticket.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(MARK_TICKET_AS_FIXED)) {
	                	String id = ((Long) message.get("id")).toString();
	                	String resolution = message.getString("resolution");
	                	String timeSpent = ((Integer) message.get("timeSpent")).toString();
	                	Ticket ticket = serverTicketManager.markTicketAsFixed(id, resolution, timeSpent);
	                	if (ticket != null) {
	                    	String ticketString = ticket.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(CLOSE_TICKET)) {
	                	String id = ((Long) message.get("id")).toString();
	                	Ticket ticket = serverTicketManager.closeTicket(id);
	                	if (ticket != null) {
	                    	String ticketString = ticket.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(REJECT_TICKET)) {
	                	String id = ((Long) message.get("id")).toString();
	                	Ticket ticket = serverTicketManager.rejectTicket(id);
	                	if (ticket != null) {
	                    	String ticketString = ticket.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(ASSIGN_TICKET)) {
	                	String id = ((Long) message.get("id")).toString();
	                	String assignedTo = message.getString("assignedTo");
	                	Ticket ticket = serverTicketManager.assignTicket(id, assignedTo);
	                	if (ticket != null) {
	                    	String ticketString = ticket.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(SET_TICKET_SEVERITY)) {
	                	String id = ((Long) message.get("id")).toString();
	                	String severity = message.getString("severity");
	                	Ticket ticket = serverTicketManager.setTicketSeverity(id, severity);
	                	if (ticket != null) {
	                    	String ticketString = ticket.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(SET_TICKET_PRIORITY)) {
	                	String id = message.getString("id");
	                	String priority = ((Integer) message.get("priority")).toString();
	                	Ticket ticket = serverTicketManager.setTicketPriority(id, priority);
	                	if (ticket != null) {
	                    	String ticketString = ticket.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(UPDATE_TICKET_RESOLUTION)) {
	                	String id = ((Long) message.get("id")).toString();
	                	String resolution = message.getString("resolution");
	                	Ticket ticket = serverTicketManager.updateTicketResolution(id, resolution);
	                	if (ticket != null) {
	                    	String ticketString = ticket.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(UPDATE_TIME_SPENT)) {
	                	String id = ((Long) message.get("id")).toString();
	                	String timeSpent = message.getString("timeSpent");
	                	Ticket ticket = serverTicketManager.updateTicketTimeSpent(id, timeSpent);
	                	if (ticket != null) {
	                    	String ticketString = ticket.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(GET_ALL_TICKETS)) {
	                	for (Ticket ticket: serverTicketManager.getAllTickets()) {
	                        String ticketString = ticket.toJSON().toString();
	                        String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}";
	                        wrtr.write(sendJson);
	                	}
	                	wrtr.write("{\"response\":" + COMPLETE + "}");
	                }
	                else if (method.equals(CREATE_ACCOUNT)) {
	                	String username = message.getString("username");
	                	if (serverUserManager.hasUser(username)) {
	                		String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                	String password = message.getString("password");
	                	String type = message.getString("type");
	                	String actualName = message.getString("actualName");
	                	String email = message.getString("email");
	                	User user = serverUserManager.createAccount(username, password, type, actualName, email);
	                	if (user != null) {
	                    	String userString = user.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + userString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(VALIDATE_USER)) {
	                	String username = message.getString("username");
	                	String password = message.getString("password");
	                	boolean valid = serverUserManager.validateUser(username, password);
	                	if (valid) {
	                		User user = serverUserManager.getUser(username);
	                    	String sendJson = "{\"response\":" + SUCCESSFUL+ ", \"permissions\": " + user.getType() +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(UPDATE_PERMISSIONS)) {
	                	String username = message.getString("username");
	                	String newType = message.getString("newType");
	                	User user = serverUserManager.updateUserPermissions(username, newType);
	                	if (user != null) {
	                    	String userString = user.toJSON().toString();
	                    	String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + userString +"}";
	                    	wrtr.write(sendJson);
	                	}
	                	else {
	                    	String sendJson = "{\"response\":" + FAILED + "}";
	                    	wrtr.write(sendJson);
	                	}
	                }
	                else if (method.equals(DELETE_USER)) {
	                	String username = message.getString("username");
	                	serverUserManager.deleteUser(username);
	                    String sendJson = "{\"response\":" + SUCCESSFUL + "}";
	                    wrtr.write(sendJson);
	                }
	                else if (method.equals(GET_ALL_USERS)) {
	                	for (User user: serverUserManager.getAllUsers()) {
	                        String userString = user.toJSON().toString();
	                        String sendJson = "{\"response\":" + SUCCESSFUL + ", \"result\": " + userString +"}";
	                        wrtr.write(sendJson);
	                	}
	                	wrtr.write("{\"response\":" + COMPLETE + "}");
	                }
	            }
	            else {
	            	cont = false;
	            }
        	}catch (Exception e) {
                e.printStackTrace();
            	String sendJson = "{\"response\":" + FAILED + "}";
            	wrtr.write(sendJson);
        	}
        }
    }

}

