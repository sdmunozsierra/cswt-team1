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
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler {

    // Useful Constants
    private static final String CREATE_TICKET = "Create ticket";
    private static final String OPEN_TICKET = "Open ticket";
    private static final String SEARCH_TICKETS = "Search tickets";
    private static final String MARK_TICKET_AS_FIXED = "Mark ticket as fixed";
    private static final String CLOSE_TICKET = "Close ticket";
    private static final String UPDATE_TICKET = "Update ticket";
    private static final String UPDATE_USER = "Update user";
    private static final String REJECT_TICKET = "Reject ticket";
    private static final String EDIT_TICKET = "Edit ticket";
    private static final String GET_ALL_TICKETS = "Get all tickets";
    private static final String SUCCESSFUL = "Successful";
    private static final String FAILED = "Failed";
    private static final String EMPTY = "Empty";
    private static final String NO_PRIORITY = "-1";
    private static final String COMPLETE = "Complete";
    private static final String CREATE_ACCOUNT = "Create account";
    private static final String VALIDATE_USER = "Validate user";
    private static final String EDIT_USER = "Edit user";
    private static final String DELETE_USER = "Delete User";
    private static final String GET_ALL_USERS = "Get all users";
    private static final int PORT = 9880;
    private static final String ENCODING = "UTF-8";

    //Class Members
    private static ServerSocket server;
    private static ServerTicketManager serverTicketManager = new ServerTicketManager();
    private static ServerUserManager serverUserManager = new ServerUserManager();

    public static void main(String args[]) throws IOException {
        server = new ServerSocket(PORT);
        while(true) {
            Socket socket = server.accept();
            ServerThread thread = new ServerThread(socket);
            thread.start();
        }
    }


    public static class ServerThread extends Thread {

        private static Socket socket;
        private static DataInputStream ois;
        private static DataOutputStream oos;
        private static JSONReader rdr;
        private static JSONWriter wrtr;

        public ServerThread(Socket socket) throws IOException{
            this.socket = socket;
            ois = new DataInputStream(socket.getInputStream());
            rdr = new JSONPacketReader(ois, ENCODING);
            oos = new DataOutputStream(socket.getOutputStream());
            wrtr = new JSONPacketWriter(oos, ENCODING);
        }

        private synchronized static void createTicket(JSONObject message) {
            String title = message.getString("title");
            String description = message.getString("description");
            String client = message.getString("client");
            String severity =  message.getString("severity");
            String assignedTo = message.getString("assignedTo");
            String priority = ((Integer) message.get("priority")).toString();
            Ticket ticket = serverTicketManager.createTicket(title, description, client, severity, assignedTo, priority);
            if (ticket != null) {
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized static void editTicket(JSONObject message) {
            String id = ((Long) message.get("id")).toString();
            String resolution = message.getString("resolution").equals(EMPTY) ? "" : message.getString("resolution");
            String description = message.getString("description").equals(EMPTY) ? "" : message.getString("description");
            String client = message.getString("client").equals(EMPTY) ? "" : message.getString("client");
            String severity =  message.getString("severity").equals(EMPTY) ? "" : message.getString("severity");
            String assignedTo = message.getString("assignedTo").equals(EMPTY) ? "" : message.getString("assignedTo");
            String priority = ((Integer) message.get("priority")).toString().equals(EMPTY) ? "" : ((Integer) message.get("priority")).toString();
            Ticket ticket = serverTicketManager.editTicket(id, description, resolution, client, severity, assignedTo, priority);
            if (ticket != null) {
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized static void openTicket(JSONObject message) {
            String id = ((Long) message.get("id")).toString();
            String priority = ((Integer) message.get("priority")).toString();
            String assignedTo = message.getString("assignedTo");
            Ticket ticket = serverTicketManager.openTicket(id, priority, assignedTo);
            if (ticket != null) {
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized static void markTicketAsFixed(JSONObject message) {
            String id = ((Long) message.get("id")).toString();
            String resolution = message.getString("resolution");
            String timeSpent = ((Integer) message.get("timeSpent")).toString();
            Ticket ticket = serverTicketManager.markTicketAsFixed(id, resolution, timeSpent);
            if (ticket != null) {
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized static void closeTicket(JSONObject message) {
            String id = ((Long) message.get("id")).toString();
            Ticket ticket = serverTicketManager.closeTicket(id);
            if (ticket != null) {
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized static void rejectTicket(JSONObject message) {
            String id = ((Long) message.get("id")).toString();
            Ticket ticket = serverTicketManager.rejectTicket(id);
            if (ticket != null) {
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized static void updateTicket(JSONObject message) {
            String id = ((Long) message.get("id")).toString();
            Ticket ticket = serverTicketManager.getTicket(id);
            if (ticket != null) {
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized static void searchTickets(JSONObject message) {
            List<Ticket> validTickets = new ArrayList<Ticket>();
            String priority = ((Integer) message.get("priority")).toString();
            String severity = message.getString("severity");
            String client = message.getString("client");
            String assignedTo = message.getString("assignedTo");
            String status = message.getString("status");
            for (Ticket ticket: serverTicketManager.getAllTickets()) {
                boolean valid = true;
                if(!assignedTo.equals(EMPTY) && !ticket.getAssignedTo().equals(assignedTo)) {
                    valid = false;
                }
                if(!severity.equals(EMPTY) && !ticket.getSeverity().equals(severity)) {
                    valid = false;
                }
                if(!client.equals(EMPTY) && !ticket.getClient().equals(client)) {
                    valid = false;
                }
                if(!status.equals(EMPTY) && !ticket.getStatus().equals(status)) {
                    valid = false;
                }
                if(!priority.equals(NO_PRIORITY) && !ticket.getPriority().equals(priority)) {
                    valid = false;
                }
                if (valid) {
                    validTickets.add(ticket);
                }
            }
            for (Ticket ticket: validTickets) {
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            wrtr.write("{\"response\":" + COMPLETE + "}");
        }

        private synchronized void getAllTickets() {
            for (Ticket ticket: serverTicketManager.getAllTickets()) {
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            wrtr.write("{\"response\":" + COMPLETE + "}");
        }
        private synchronized void createAccount(JSONObject message) {
            String username = message.getString("username");
            if (serverUserManager.hasUser(username)) {
                String sendJson = "{\"response\":" + FAILED + "}";
            }
            String password = message.getString("password");
            String type = message.getString("type");
            String actualName = message.getString("actualName");
            String email = message.getString("email");
            User user = serverUserManager.createAccount(username, password, type, actualName, email);
            if (user != null) {
                String userString = user.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + userString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void validateUser(JSONObject message) {
            String username = message.getString("username");
            String password = message.getString("password");
            boolean valid = serverUserManager.validateUser(username, password);
            if (valid) {
                User user = serverUserManager.getUser(username);
                wrtr.write("{\"response\":" + SUCCESSFUL+ ", \"permissions\": " + user.getType() +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void editUser(JSONObject message) {
            String username = message.getString("username");
            String password = message.getString("password");
            String type = message.getString("type");
            String actualName = message.getString("actualName");
            String email = message.getString("email");
            User user = serverUserManager.editUser(username, password, type, actualName, email);
            if (user != null) {
                String userString = user.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + userString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void deleteUser(JSONObject message) {
            String username = message.getString("username");
            serverUserManager.deleteUser(username);
            wrtr.write("{\"response\":" + SUCCESSFUL + "}");
        }

        private synchronized void updateUser(JSONObject message) {
            String username = message.getString("username");
            User user = serverUserManager.getUser(username);
            if (user != null) {
                String userString = user.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + userString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void getAllUsers() {
            for (User user: serverUserManager.getAllUsers()) {
                String userString = user.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + userString +"}");
            }
            wrtr.write("{\"response\":" + COMPLETE + "}");
        }

        public void run() {
            boolean cont = true;
            while(cont) {
                try {
                    String retrievedJSON = rdr.read();
                    System.out.println("Retrieved JSON: " + retrievedJSON);
                    JSONObject message = null;
                    if (retrievedJSON != null){
                        message = new JSONObject(retrievedJSON);
                        String method = message.getString("request");
                        if (method.equals(CREATE_TICKET)) createTicket(message);
                        else if (method.equals(EDIT_TICKET)) editTicket(message);
                        else if (method.equals(OPEN_TICKET)) openTicket(message);
                        else if (method.equals(MARK_TICKET_AS_FIXED)) markTicketAsFixed(message);
                        else if (method.equals(CLOSE_TICKET)) closeTicket(message);
                        else if (method.equals(REJECT_TICKET)) rejectTicket(message);
                        else if (method.equals(UPDATE_TICKET)) updateTicket(message);
                        else if (method.equals(SEARCH_TICKETS)) searchTickets(message);
                        else if (method.equals(GET_ALL_TICKETS)) getAllTickets();
                        else if (method.equals(CREATE_ACCOUNT)) createAccount(message);
                        else if (method.equals(VALIDATE_USER)) validateUser(message);
                        else if (method.equals(EDIT_USER)) editUser(message);
                        else if (method.equals(DELETE_USER)) deleteUser(message);
                        else if (method.equals(UPDATE_USER)) updateUser(message);
                        else if (method.equals(GET_ALL_USERS)) getAllUsers();
                    }
                    else {
                        cont = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String sendJson = "{\"response\":" + FAILED + "}";
                    wrtr.write(sendJson);
                }
            }
        }
    }
}

