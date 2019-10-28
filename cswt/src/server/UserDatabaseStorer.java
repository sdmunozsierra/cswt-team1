package server;

import cswt.User;

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
 * Connects to a MongoDB database and can get users from the database
 * as well as upload them in the form of Documents.
 * @author Jonathan Roman
 * @author Christopher Uriel
 *
 */
public class UserDatabaseStorer {

    private final String DATABASE_NAME = "Database";
    private final String USER_COLLECTION = "UserCollection";
    private final MongoClientURI DATABASEURI = new MongoClientURI(
            "mongodb+srv://root:admin@cluster0-bcwly.azure.mongodb.net/test?retryWrites=true&w=majority");
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    /**
     * Establishes a connection to the database and the collection of Users.
     */
    public UserDatabaseStorer() {
        mongoClient = new MongoClient(DATABASEURI);
        database = mongoClient.getDatabase(DATABASE_NAME);
        collection = database.getCollection(USER_COLLECTION);
    }

    /*
     * Method is used to decide whether to insert a new User or to update an existing User.
     * @param user being searched for.
     * @return true if User exists in collection
     */
    private boolean userInDatabase(User user) {
        FindIterable<Document> names = collection.find();
        for (Document document : names) {
            if (document.getString("username").equals(user.getUsername()))
                return true;
        }
        return false;
    }

    /**
     * Converts Documents stored in the database into Users.
     * @return List of Users stored in the database.
     */
    public List<User> loadUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        FindIterable<Document> docs = collection.find();
        for (Document doc : docs) {
            User user = fromDocumentToUser(doc);
            users.add(user);
        }
        return users;
    }

    /**
     * Updates database with the User provided.
     * @param user
     */
    public void storeUser(User user) {
        if (userInDatabase(user))
            updateUserInDatabase(user);
        else
            addNewUser(user);
    }

    /*
     * Updates an existing user in the database, used in storeUser
     * @param user
     */
    private synchronized void updateUserInDatabase(User user) {
        Bson filter = eq("username", user.getUsername());
        Bson query = combine(
                set("username", user.getUsername()),
                set("password", user.getPassword()),
                set("actualName", user.getActualName()),
                set("type", user.getType()),
                set("email", user.getEmail()));
        collection.findOneAndUpdate(filter, query);
    }
    /*
     * Deletes an existing user in the database
     * @param user
     */
    public synchronized void deleteUser(String username) {
        Bson filter = eq("username", username);
        collection.deleteOne(filter);
    }


    /*
     * Adds a new user to the database used in storeUser
     * @param user
     */
    private void addNewUser(User user) {
        Document newUser = fromUserToDocument(user);
        collection.insertOne(newUser);
    }

    /*
     * Document is an equivalent to a JSONObject with a key and a value
     *
     * @param userJSON JSONObject to convert
     * @return Document file to be converted
     */
    private synchronized Document fromJSONToDocument(JSONObject userJSON) {
        Document document = new Document()
                .append("username", userJSON.getString("username"))
                .append("password", userJSON.getString("password"))
                .append("type", userJSON.getString("type"))
                .append("actualName", userJSON.getString("actualName"))
                .append("email", userJSON.getString("email"));
        return document;
    }

    /* Used to store user as a document in database
     *
     * @param user
     * @return
     */

    private synchronized Document fromUserToDocument(User user) {
        Document document = new Document()
                .append("username", user.getUsername())
                .append("password", user.getPassword())
                .append("type", user.getType())
                .append("actualName", user.getActualName())
                .append("email", user.getEmail());
        return document;
    }


    private synchronized JSONObject fromDocumentToJSONObject(Document document) {
        JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
        JSONObject json = new JSONObject(document.toJson(relaxed));
        return json;
    }

    /* Used when fetching Users from the database as documents
     * @param document
     * @return
     */
    private synchronized User fromDocumentToUser(Document document) {
        try {
            User user = new User();
            user.setUsername(document.getString("username"));
            user.setPassword(document.getString("password"));
            user.setType(document.getString("type"));
            user.setActualName(document.getString("actualName"));
            user.setEmail(document.getString("email"));
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
