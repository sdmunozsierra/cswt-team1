package server;

import cswt.Ticket;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Connects to a MongoDB database and can get tickets from the database
 * as well as upload them in the form of Documents.
 *
 * @author Jonathan Roman
 * @author Christopher Uriel
 */
public class TicketDatabaseStorer {

    private final String TICKET_DATABASE = "Tickets";
    private final String TICKET_COLLECTION = "TicketCollection";
    private final String USER_COLLECTION = "UserCollection";
    private final MongoClientURI DATABASEURI = new MongoClientURI(
            "mongodb+srv://root:admin@cluster0-bcwly.azure.mongodb.net/test?retryWrites=true&w=majority");
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    /**
     * Establishes connection to database and accesses collection containing tickets
     */
    public TicketDatabaseStorer() {
        mongoClient = new MongoClient(DATABASEURI);
        database = mongoClient.getDatabase("Tickets");
        collection = database.getCollection("Doesntexist");
    }

    /*
     *Used in storeTicket to determine if a new ticket should be added or
     * if existing should be updated
     * @param ticket
     * @return
     */
    private boolean ticketInDatabase(Ticket ticket) {
        FindIterable<Document> names = collection.find();
        for (Document document : names) {
//            System.out.println(document.toString());
            if (document.getString("id").equals(ticket.getId()))
                return true;
        }
        return false;
    }

    /**
     * Takes the collection of tickets in the database and
     * converts them to Ticket objects
     * @return List of Tickets
     */
    public List<Ticket> loadTicketsFromDatabase() {
        List<Ticket> tickets = new ArrayList<>();
        FindIterable<Document> docs = collection.find(); //SELECT * FROM sample;
        for (Document doc : docs) {
            Ticket ticket = fromDocumentToTicket(doc);
            tickets.add(ticket);
        }
        return tickets;
    }

    /**
     * Stores ticket in the database collection of Tickets.
     * @param ticket being stored
     */
    public void storeTicket(Ticket ticket) {
        if (ticketInDatabase(ticket))
            updateTicketInDatabase(ticket);
        else
            addNewTicket(ticket);
    }

    /*
     * Used in storeTicket method to update existing ticket.
     * @param ticket
     */
    private synchronized void updateTicketInDatabase(Ticket ticket) {
        Bson filter = eq("id", ticket.getId());
        Bson query = combine(
                set("title", ticket.getTitle()),
                set("description", ticket.getDescription()),
                set("assignedTo", ticket.getAssignedTo()),
                set("client", ticket.getClient()),
                set("closedDate", ticket.getClosedDate()),
                set("openedDate", ticket.getOpenedDate()),
                set("priority", ticket.getPriority()),
                set("status", ticket.getStatus()),
                set("resolution", ticket.getResolution()),
                set("severity", ticket.getSeverity()),
                set("timeSpent", ticket.getTimeSpent()));
        collection.findOneAndUpdate(filter, query);
    }

    /*
     * Used in storeTicket to insert a new ticket into the collection of tickets in the database.
     * @param ticket
     */
    private void addNewTicket(Ticket ticket) {
        Document newTicket = fromTicketToDocument(ticket);
        collection.insertOne(newTicket);
    }

    /*
     * Document is an equivalent to a JSONObject with a key and a value
     *
     * @param json JSONObject to convert
     * @return Document file to be converted
     */
    private synchronized Document fromJSONToDocument(JSONObject json) {
        Document document = new Document()
                .append("title", json.getString("title"))
                .append("description", json.getString("description"))
                .append("assignedTo", json.getString("assignedTo"))
                .append("client", json.getString("client"))
                .append("closedDate", json.getString("closedDate"))
                .append("openedDate", json.getString("openedDate"))
                .append("priority", json.getString("priority"))
                .append("status", json.getString("status"))
                .append("resolution", json.getString("resolution"))
                .append("severity", json.getString("severity"))
                .append("id", json.getString("id"))
                .append("timeSpent", json.getString("timeSpent"));
        return document;
    }

    /* Used when storing tickets in the databse as documents
     * @param ticket
     * @return
     */
    private synchronized Document fromTicketToDocument(Ticket ticket) {
        Document document = new Document()
                .append("title", ticket.getTitle())
                .append("description", ticket.getDescription())
                .append("assignedTo", ticket.getAssignedTo())
                .append("client", ticket.getClient())
                .append("closedDate", ticket.getClosedDate())
                .append("openedDate", ticket.getOpenedDate())
                .append("priority", ticket.getPriority())
                .append("status", ticket.getStatus())
                .append("resolution", ticket.getResolution())
                .append("severity", ticket.getSeverity())
                .append("id", ticket.getId())
                .append("timeSpent", ticket.getTimeSpent());
        return document;
    }

    /*
     * Converts a Document to a JSONObject
     * @param document
     * @return
     */
    private synchronized JSONObject fromDocumentToJSONObject(Document document) {
        JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
        JSONObject json = new JSONObject(document.toJson(relaxed));
        return json;
    }

    /* Used when getting Tickets that are stored in the database as Documents.
     *
     * @param document
     * @return
     */
    private synchronized Ticket fromDocumentToTicket(Document document) {
        try {
            Ticket ticket = new Ticket();
            ticket.setTitle(document.getString("title"));
            ticket.setDescription(document.getString("description"));
            ticket.setAssignedTo(document.getString("assignedTo"));
            ticket.setClient(document.getString("client"));
            ticket.setClosedDate(document.getString("closedDate"));
            ticket.setOpenedDate(document.getString("openedDate"));
            ticket.setPriority(document.getString("priority"));
            ticket.setStatus(document.getString("status"));
            ticket.setResolution(document.getString("resolution"));
            ticket.setSeverity(document.getString("severity"));
            ticket.setId(document.getString("id"));
            ticket.setTimeSpent(document.getString("timeSpent"));
            return ticket;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}