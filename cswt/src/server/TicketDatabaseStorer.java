package server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import cswt.Ticket;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.HashMap;

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

    private final String DATABASE_NAME = "Database";
    private final String TICKET_COLLECTION = "TicketCollection";
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
        database = mongoClient.getDatabase(DATABASE_NAME);
        collection = database.getCollection(TICKET_COLLECTION);
    }

    /*
     *Used in storeTicket to determine if a new ticket should be added or
     * if existing should be updated
     * @param ticket
     * @return
     */
    private boolean ticketInDatabase(Ticket ticket) {
        JSONObject jsonTicket = ticket.toJSON();
        FindIterable<Document> names = collection.find();
        for (Document document : names) {
            if (document.getString("id").equals(jsonTicket.getString("id")))
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
    public HashMap<String, Ticket> loadTicketsFromDatabase() {
        FindIterable<Document> docs = collection.find();
        HashMap<String, Ticket> mapping = new HashMap<>();
        for (Document doc : docs) {
            Ticket ticket = fromDocumentToTicket(doc);
            mapping.put(ticket.getId(), ticket);
        }
        return mapping;
    }

    /**
     * Stores ticket in the database collection of Tickets.
     *
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
                set("description",ticket.getDescription()),
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

    /**
     * @param ticket being deleted from the collection.
     * @return True if Ticket deletion was successful. False otherwise.
     */
    public boolean deleteTicket(Ticket ticket) {
        JSONObject jsonTicket = ticket.toJSON();
        if (ticketInDatabase(ticket)) {
            Bson filter = eq("id", jsonTicket.getString("id"));
            collection.deleteOne(filter);
            return true;
        }
        return false;
    }


    /* Used when storing tickets in the databse as documents
     * @param ticket
     * @return
     */
    private synchronized Document fromTicketToDocument(Ticket ticket) {
        JSONObject ticketJSON = ticket.toJSON();
        Document document = new Document()
                .append("title", ticketJSON.getString("title"))
                .append("description", ticketJSON.getString("description"))
                .append("assignedTo", ticketJSON.getString("assignedTo"))
                .append("client", ticketJSON.getString("client"))
                .append("closedDate", ticketJSON.getString("closedDate"))
                .append("openedDate", ticketJSON.getString("openedDate"))
                .append("priority", ticketJSON.getString("priority"))
                .append("status", ticketJSON.getString("status"))
                .append("resolution", ticketJSON.getString("resolution"))
                .append("severity", ticketJSON.getString("severity"))
                .append("id", ticketJSON.getString("id"))
                .append("timeSpent", ticketJSON.getString("timeSpent"));
        return document;
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
