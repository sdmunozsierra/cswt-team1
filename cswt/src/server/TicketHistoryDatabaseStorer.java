package server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import cswt.Ticket;
import cswt.TicketSnapshot;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

import static cswt.Ticket.convertToTicket;

/**
 * Connects to a MongoDB database and can get tickets from the database
 * as well as upload them in the form of Documents.
 *
 * @author Jonathan Roman
 * @author Christopher Uriel
 */
public class TicketHistoryDatabaseStorer {

    private static final String CHANGE_DESCRIPTION = "Change description";
    private static final String CHANGE_PRIORITY = "Change priority";
    private static final String CHANGE_SEVERITY = "Change severity";
    private static final String CHANGE_ASSIGNED_TO = "Change assigned to";
    private static final String CHANGE_RESOLUTION = "Change resolution";
    private static final String CHANGE_CLIENT = "Change client";
    private static final String CHANGE_TITLE = "Change title";
    private static final String MULTIPLE_CHANGES = "Multiple changes";

    private final String DATABASE_NAME = "Database";
    private final String TICKET_COLLECTION = "TicketHistoryCollection";
    private final MongoClientURI DATABASEURI = new MongoClientURI(
            "mongodb+srv://root:admin@cluster0-bcwly.azure.mongodb.net/test?retryWrites=true&w=majority");
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    /**
     * Establishes connection to database and accesses collection containing tickets
     */
    public TicketHistoryDatabaseStorer() {
        mongoClient = new MongoClient(DATABASEURI);
        database = mongoClient.getDatabase(DATABASE_NAME);
        collection = database.getCollection(TICKET_COLLECTION);
    }

    private boolean ticketHistoryInDatabase(String id) {
        Bson filter = eq("id", id);
        FindIterable<Document> documents = collection.find(filter);
        MongoCursor<Document> cursor = documents.cursor();
        if (cursor.hasNext()) {
            return true;
        }
        return false;
    }

    /**
     * Takes the collection of tickets in the database and
     * converts them to Ticket objects
     *
     * @return List of Tickets
     */
    public List<TicketSnapshot> loadTicketHistory(String id) {
        List<TicketSnapshot> history = new ArrayList<>();
        Bson filter = eq("id", id);
        FindIterable<Document> documents = collection.find(filter);
        MongoCursor<Document> cursor = documents.cursor();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            history.add(fromDocumentToTicketSnapshot(document));
        }
        return history;
    }


    /*
     * Used in storeTicket to insert a new ticket into the collection of tickets in the database.
     * @param ticket
     */
    private void addNewSnapshot(TicketSnapshot snapshot, String id) {
        Document newSnapshot = fromTicketSnapshotToDocument(snapshot, id);
        collection.insertOne(newSnapshot);
    }

    public synchronized boolean deleteTicketHistory(String id) {
        if (ticketHistoryInDatabase(id)) {
            Bson filter = eq("id", id);
            collection.deleteMany(filter);
            return true;
        }
        return false;
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
            addNewSnapshot(history, ticket.getId());
            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /* Used when storing tickets in the database as documents
     * @param ticket
     * @return
     */
    private synchronized Document fromTicketSnapshotToDocument(TicketSnapshot snapshot, String id) {
        JSONObject snapshotJSON = snapshot.toJSON();
        Document document = new Document()
                .append("id", id)
                .append("ticket", snapshotJSON.get("ticket").toString())
                .append("whatModified", snapshotJSON.getString("whatModified"))
                .append("modifier", snapshotJSON.getString("modifier"))
                .append("dateModified", snapshotJSON.getString("dateModified"));
        return document;
    }

    /* Used when getting Tickets that are stored in the database as Documents.
     *
     * @param document
     * @return
     */
    private synchronized TicketSnapshot fromDocumentToTicketSnapshot(Document document) {
        try {
            TicketSnapshot snapshot = new TicketSnapshot();
            snapshot.setDateModified(document.getString("dateModified"));
            snapshot.setModifier(document.getString("modifier"));
            snapshot.setWhatModified(document.getString("whatModified"));
            snapshot.setTicket(convertToTicket(new JSONObject(document.getString("ticket"))));
            return snapshot;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
