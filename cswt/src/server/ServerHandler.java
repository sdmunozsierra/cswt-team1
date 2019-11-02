package server;

import cswt.Ticket;
import cswt.TicketSnapshot;
import cswt.User;
import json.JSONPacketReader;
import json.JSONPacketWriter;
import json.JSONReader;
import json.JSONWriter;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
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
    private static final String DELETE_TICKET = "Delete ticket";
    private static final String GET_ALL_TICKETS = "Get all tickets";
    private static final String GET_TICKET_HASH = "Get ticket hash";
    private static final String GET_USER_HASH = "Get user hash";
    private static final String GET_RECENT_TICKETS = "Get recent tickets";
    private static final String GET_TICKET_HISTORY = "Get ticket history";
    private static final String SUCCESSFUL = "Successful";
    private static final String FAILED = "Failed";
    private static final String COMPLETE = "Complete";
    public static final String DELETED = "Deleted";
    private static final String CREATE_ACCOUNT = "Create account";
    private static final String VALIDATE_USER = "Validate user";
    private static final String EDIT_USER = "Edit user";
    private static final String DELETE_USER = "Delete User";
    private static final String GET_ALL_USERS = "Get all users";
    private static final int PORT = 9880;
    private static final String ENCODING = "UTF-8";
    private static final int NUM_RECENT = 10;

    //Class Members
    private static ServerSocket server;
    private static ServerTicketManager serverTicketManager = new ServerTicketManager();
    private static ServerUserManager serverUserManager = new ServerUserManager();
    private static TicketHistoryDatabaseStorer ticketHistoryStorer = new TicketHistoryDatabaseStorer();
    private static final String key = "77789BXarcy77777";
    private static Cipher encrypt;
    private static Cipher decrypt;

    public static void main(String args[]) throws IOException {
        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            encrypt = Cipher.getInstance("AES");
            encrypt.init(Cipher.ENCRYPT_MODE, aesKey);
            decrypt = Cipher.getInstance("AES");
            decrypt.init(Cipher.DECRYPT_MODE, aesKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        server = new ServerSocket(PORT);
        while(true) {
            Socket socket = server.accept();
            ServerThread thread = new ServerThread(socket);
            thread.start();
        }
    }

    public static synchronized String decodeMessage(String message) {
        try {
            return new String(decrypt.doFinal(Base64.getDecoder().decode(message)));
        } catch (Exception e) {
            return message;
        }
    }

    public static synchronized String encodeMessage(String message) {
        try {
            return Base64.getEncoder().encodeToString(encrypt.doFinal(message.getBytes()));
        } catch (Exception e) {
            return message;
        }
    }


    public static class ServerThread extends Thread {

        private Socket socket;
        private DataInputStream ois;
        private DataOutputStream oos;
        private JSONReader rdr;
        private JSONWriter wrtr;

        public ServerThread(Socket socket) throws IOException{
            this.socket = socket;
            ois = new DataInputStream(socket.getInputStream());
            rdr = new JSONPacketReader(ois, ENCODING);
            oos = new DataOutputStream(socket.getOutputStream());
            wrtr = new JSONPacketWriter(oos, ENCODING);
        }

        private synchronized void createTicket(JSONObject message) {
            String title = message.getString("title");
            String description = message.getString("description");
            String client = message.getString("client");
            String severity =  message.getString("severity");
            String assignedTo = message.getString("assignedTo");
            String priority = message.getString("priority");
            String modifier = message.getString("modifier");
            Ticket ticket = serverTicketManager.createTicket(title, description, client, severity, assignedTo, priority);
            if (ticket != null) {
                String ticketString = ticket.toJSON().toString();
                ticketHistoryStorer.updateTicketHistory(ticket, modifier, CREATE_TICKET);
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void getTicketHashCode(JSONObject message) {
            String id = message.getString("id");
            Ticket ticket = serverTicketManager.getTicket(id);
            if (ticket != null) {
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"hash\": \"" + ticket.hashCode() +"\"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void getUserHashCode(JSONObject message) {
            String username = decodeMessage(message.getString("username"));
            User user = serverUserManager.getUser(username);
            if (user != null) {
                String encryptedPassword = user.getPassword();
                user.setPassword(decodeMessage(encryptedPassword));
                int hash =  user.hashCode();
                user.setPassword(encryptedPassword);
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"hash\": \"" + hash +"\"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void editTicket(JSONObject message) {
            String id = message.getString("id");
            String resolution = message.getString("resolution");
            String title = message.getString("title");
            String description = message.getString("description");
            String client = message.getString("client");
            String severity =  message.getString("severity");
            String assignedTo = message.getString("assignedTo");
            String priority = message.getString("priority");
            String modifier = message.getString("modifier");
            Ticket original = serverTicketManager.getTicketAsCopy(id);
            Ticket ticket = serverTicketManager.editTicket(id, title, description, resolution, client, severity, assignedTo, priority);
            if (ticket != null) {
                String whatModified = ticketHistoryStorer.determineDifference(original, ticket);
                ticketHistoryStorer.updateTicketHistory(ticket, modifier, whatModified);
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void openTicket(JSONObject message) {
            String id = message.getString("id");
            String priority = message.getString("priority");
            String assignedTo = message.getString("assignedTo");
            String modifier = message.getString("modifier");
            Ticket ticket = serverTicketManager.openTicket(id, priority, assignedTo);
            if (ticket != null) {
                ticketHistoryStorer.updateTicketHistory(ticket, modifier, OPEN_TICKET);
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void markTicketAsFixed(JSONObject message) {
            String id = message.getString("id");
            String resolution = message.getString("resolution");
            String modifier = message.getString("modifier");
            Ticket ticket = serverTicketManager.markTicketAsFixed(id, resolution);
            if (ticket != null) {
                ticketHistoryStorer.updateTicketHistory(ticket, modifier, MARK_TICKET_AS_FIXED);
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void closeTicket(JSONObject message) {
            String id = message.getString("id");
            String modifier = message.getString("modifier");
            Ticket ticket = serverTicketManager.closeTicket(id);
            if (ticket != null) {
                ticketHistoryStorer.updateTicketHistory(ticket, modifier, CLOSE_TICKET);
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void rejectTicket(JSONObject message) {
            String id = message.getString("id");
            String modifier = message.getString("modifier");
            Ticket ticket = serverTicketManager.rejectTicket(id);
            if (ticket != null) {
                ticketHistoryStorer.updateTicketHistory(ticket, modifier, REJECT_TICKET);
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void updateTicket(JSONObject message) {
            String id = message.getString("id");
            Ticket ticket = serverTicketManager.getTicket(id);
            if (ticket != null) {
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
            }
            else {
                wrtr.write("{\"response\":" + DELETED + "}");
            }
        }

        private synchronized void getTicketHistory(JSONObject message) {
            List<TicketSnapshot> history;
            String id = message.getString("id");
            history = ticketHistoryStorer.loadTicketHistory(id);
            if (history != null) {
                for (TicketSnapshot ticketSnapshot : history) {
                    String snapshotString = ticketSnapshot.toJSON().toString();
                    wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + snapshotString +"}");
                }
                wrtr.write("{\"response\":" + COMPLETE + "}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void deleteTicket(JSONObject message) {
            String id = message.getString("id");
            Ticket ticket = serverTicketManager.getTicket(id);
            if (ticket != null) {
                serverTicketManager.deleteTicket(id);
                if (ticketHistoryStorer.deleteTicketHistory(id)) {
                    wrtr.write("{\"response\":" + SUCCESSFUL + "}");
                }
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void searchTickets(JSONObject message) {
            List<Ticket> validTickets = new ArrayList<Ticket>();
            String title = message.getString("title");
            String description = message.getString("description");
            String resolution = message.getString("resolution");
            String priority = message.getString("priority");
            String severity = message.getString("severity");
            String client = message.getString("client");
            String assignedTo = message.getString("assignedTo");
            String status = message.getString("status");
            for (Ticket ticket: serverTicketManager.getAllTickets()) {
                boolean valid = true;
                if (!title.equals("") && !ticket.getTitle().contains(title)) valid = false;
                if (!description.equals("") && !ticket.getDescription().contains(description)) valid = false;
                if (!resolution.equals("") && !ticket.getResolution().contains(resolution)) valid = false;
                if(!assignedTo.equals("") && !ticket.getAssignedTo().equals(assignedTo)) valid = false;
                if(!severity.equals("") && !ticket.getSeverity().equals(severity)) valid = false;
                if(!client.equals("") && !ticket.getClient().equals(client)) valid = false;
                if(!status.equals("") && !ticket.getStatus().equals(status)) valid = false;
                if(!priority.equals("") && !ticket.getPriority().equals(priority)) valid = false;
                if (valid) validTickets.add(ticket);
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

        private synchronized void getRecentTickets() {
            List<String> ids = serverTicketManager.getAllIds();
            Collections.sort(ids);
            Collections.reverse(ids);
            int counter = NUM_RECENT;
            for (String id: ids) {
                counter--;
                Ticket ticket = serverTicketManager.getTicket(id);
                String ticketString = ticket.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": " + ticketString +"}");
                if (counter <= 0) break;
            }
            wrtr.write("{\"response\":" + COMPLETE + "}");
        }
        private synchronized void createAccount(JSONObject message) {
            String username = decodeMessage(message.getString("username"));
            if (serverUserManager.hasUser(username)) {
                String sendJson = "{\"response\":" + FAILED + "}";
            }
            String password = message.getString("password");
            String type = decodeMessage(message.getString("type"));
            String actualName = decodeMessage(message.getString("actualName"));
            String email = decodeMessage(message.getString("email"));
            User user = serverUserManager.createAccount(username, password, type, actualName, email);
            if (user != null) {
                String userString = user.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": \"" + encodeMessage(userString) +"\"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void validateUser(JSONObject message) {
            String username = decodeMessage(message.getString("username"));
            String password = message.getString("password");
            boolean valid = serverUserManager.validateUser(username, password);
            if (valid) {
                User user = serverUserManager.getUser(username);
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"permissions\": \"" + encodeMessage(user.getType()) + "\"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void editUser(JSONObject message) {
            String username = decodeMessage(message.getString("username"));
            String password = message.getString("password");
            String type = decodeMessage(message.getString("type"));
            String actualName = decodeMessage(message.getString("actualName"));
            String email = decodeMessage(message.getString("email"));
            User user = serverUserManager.editUser(username, password, type, actualName, email);
            if (user != null) {
                String userString = user.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": \"" + encodeMessage(userString) +"\"}");
            }
            else {
                wrtr.write("{\"response\":" + FAILED + "}");
            }
        }

        private synchronized void deleteUser(JSONObject message) {
            String username = decodeMessage(message.getString("username"));
            serverUserManager.deleteUser(username);
            wrtr.write("{\"response\":" + SUCCESSFUL + "}");
        }

        private synchronized void updateUser(JSONObject message) {
            String username = decodeMessage(message.getString("username"));
            User user = serverUserManager.getUser(username);
            if (user != null) {
                String userString = user.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": \"" + encodeMessage(userString) +"\"}");
            }
            else {
                wrtr.write("{\"response\":" + DELETED + "}");
            }
        }

        private synchronized void getAllUsers() {
            for (User user: serverUserManager.getAllUsers()) {
                String userString = user.toJSON().toString();
                wrtr.write("{\"response\":" + SUCCESSFUL + ", \"result\": \"" + encodeMessage(userString) +"\"}");
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
                        else if (method.equals(GET_RECENT_TICKETS)) getRecentTickets();
                        else if (method.equals(DELETE_TICKET)) deleteTicket(message);
                        else if (method.equals(CREATE_ACCOUNT)) createAccount(message);
                        else if (method.equals(VALIDATE_USER)) validateUser(message);
                        else if (method.equals(EDIT_USER)) editUser(message);
                        else if (method.equals(DELETE_USER)) deleteUser(message);
                        else if (method.equals(UPDATE_USER)) updateUser(message);
                        else if (method.equals(GET_ALL_USERS)) getAllUsers();
                        else if (method.equals(GET_TICKET_HASH)) getTicketHashCode(message);
                        else if (method.equals(GET_USER_HASH)) getUserHashCode(message);
                        else if (method.equals(GET_TICKET_HISTORY)) getTicketHistory(message);
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

