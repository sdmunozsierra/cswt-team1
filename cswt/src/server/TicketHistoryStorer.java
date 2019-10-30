package server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cswt.TicketSnapshot;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import cswt.Ticket;

import static cswt.TicketSnapshot.convertToSnapshot;

public class TicketHistoryStorer {
    private static final String CHANGE_DESCRIPTION = "Change description";
    private static final String CHANGE_PRIORITY = "Change priority";
    private static final String CHANGE_SEVERITY = "Change severity";
    private static final String CHANGE_ASSIGNED_TO = "Change assigned to";
    private static final String CHANGE_RESOLUTION = "Change resolution";
    private static final String CHANGE_CLIENT = "Change client";
    private static final String CHANGE_TITLE = "Change title";
    private static final String MULTIPLE_CHANGES = "Multiple changes";
    public static String TICKET_HISTORY_DIR = Paths.get(System.getProperty("user.dir"), "ticketsHistory").toString();
    private FileWriter writer;

    public TicketHistoryStorer() {
        File ticketDirectory = new File(TICKET_HISTORY_DIR);
        if (!ticketDirectory.exists()) {
            ticketDirectory.mkdir();
        }
    }

    public synchronized List<TicketSnapshot> loadTicketHistory(String id) {
        File file = new File(TICKET_HISTORY_DIR + "/" + id + ".json");
        List<TicketSnapshot> history = readHistoryFromFile(file);
        return history;
    }

    private synchronized List<TicketSnapshot> readHistoryFromFile(File file) {
        List<TicketSnapshot> history = new ArrayList<>();
        try {
            FileReader reader = new FileReader(file);
            JSONTokener parser = new JSONTokener(reader);
            JSONObject obj = (JSONObject) parser.nextValue();
            JSONObject itemJSON = (JSONObject) obj;
            JSONArray array = itemJSON.getJSONArray("history");
            int counter = 0;
            while (counter < array.length()) {
                history.add(convertToSnapshot(array.getJSONObject(counter)));
                counter++;
            }
            reader.close();
            return history;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized boolean updateTicketHistory(Ticket ticket, String modifier, String whatModified) {
        try{
            String pattern = "yyyy.MM.dd 'at' HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            TicketSnapshot history = new TicketSnapshot();
            history.setTicket(ticket);
            history.setDateModified(simpleDateFormat.format(new Date()));
            history.setModifier(modifier);
            history.setWhatModified(whatModified);
            writeHistoryToFile(ticket.getId(), history);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    private synchronized void writeHistoryToFile(String id, TicketSnapshot history) throws IOException {
        String filename = Paths.get(TICKET_HISTORY_DIR, id + ".json").toString();
        File file = new File(filename);
        if (file.exists()) {
            FileReader reader = new FileReader(file);
            JSONTokener parser = new JSONTokener(reader);
            JSONObject obj = (JSONObject) parser.nextValue();
            reader.close();
            JSONObject itemJSON = (JSONObject) obj;
            JSONArray array = itemJSON.getJSONArray("history");
            array.put(history.toJSON());
            file.createNewFile();
            writer = new FileWriter(filename);
            writer.write("{\"history\": " + array.toString() + "}");
            writer.close();
        }
        else {
            JSONArray array = new JSONArray();
            array.put(history.toJSON());
            file.createNewFile();
            writer = new FileWriter(filename);
            writer.write("{\"history\": " + array.toString() + "}");
            writer.close();
        }
    }

    public synchronized String determineDifference(Ticket original, Ticket updated) {
        int numChanges = 0;
        String difference = MULTIPLE_CHANGES;
        if (!original.getTitle().equals(updated.getTitle())) {
            difference = CHANGE_TITLE;
            numChanges++;
        }
        if (!original.getDescription().equals(updated.getDescription())) {
            difference = CHANGE_DESCRIPTION;
            numChanges++;
        }
        if (!original.getAssignedTo().equals(updated.getAssignedTo())) {
            difference = CHANGE_ASSIGNED_TO;
            numChanges++;
        }
        if (!original.getClient().equals(updated.getClient())) {
            difference = CHANGE_CLIENT;
            numChanges++;
        }
        if (!original.getPriority().equals(updated.getPriority())) {
            difference = CHANGE_PRIORITY;
            numChanges++;
        }
        if (!original.getResolution().equals(updated.getResolution())) {
            difference = CHANGE_RESOLUTION;
            numChanges++;
        }
        if (!original.getSeverity().equals(updated.getSeverity())) {
            difference = CHANGE_SEVERITY;
            numChanges++;
        }
        if (numChanges > 1 ) return MULTIPLE_CHANGES;
        return difference;
    }
}
