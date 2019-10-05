package server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

import cswt.User;

public class UserStorer {
	private FileWriter writer;
	public static final String USER_DIR = Paths.get(System.getProperty("user.dir"), "users").toString();
	
	public UserStorer() {
        File userDirectory = new File(USER_DIR);
        if (!userDirectory.exists()) {
        	userDirectory.mkdir();
        }
	}
	
	/** Reads a user from a ticket file. 
	 * @param file The file to be read
	 * @return The user read from the file or null if there was an error
	 * */
	private synchronized User readUserFromFile(File file) {
		 try {
			 JSONTokener parser = new JSONTokener(new FileReader(file));
			 JSONObject obj = (JSONObject) parser.nextValue();
			 JSONObject itemJSON = (JSONObject) obj;
			 User user = fromJSON(itemJSON);
			 return user;
		 }
		 catch (Exception e) {
			 return null;
		 }
	}
	
	/** Writes an user to an user file.
	 * @param user The user that will be written to a file
	 * @throws IOExcpetion
	 * */
	private synchronized void writeUserToFile(User user) throws IOException {
		String filename = Paths.get(USER_DIR, user.getUsername().toString() + ".json").toString();
		File file = new File(filename);
		file.createNewFile();
		writer = new FileWriter(filename);
		writer.write((user.toJSON()).toString());
		writer.close();
	}
	
	/** Retrieves users from datastore.
	 * @return A list of all stored users
	 * */
	public synchronized List<User> loadUsers() {
		List<User> users = new ArrayList<User>();
		File folder = new File(USER_DIR);
		if(folder.listFiles() != null) {
			for(File file : folder.listFiles()) {
				if((file.getName()).contains(".json") && readUserFromFile(file) != null) {
					User user = readUserFromFile(file);
					users.add(user);
				}
			}
		}
		return users;
	}
	
	/** Stores a user in a datastore 
	 * @param user The user to be stored
	 * @return If the storer was able to store the user
	 * */
	public synchronized boolean storeUser(User user) {
		try{
			writeUserToFile(user);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	/** Removes a user from the manager and storage. 
	 * @param username The username of the user to be removed
	 * */
	public synchronized void deleteUser(String username) {
		File file = new File(Paths.get(USER_DIR, username + ".json").toString());
		file.delete();
	}
	
	/** Parses an user from a JSONObject 
	 * @param obj The JSONObject to be parsed
	 * @return The parsed User
	 * */
	private synchronized User fromJSON(JSONObject obj) {
		try {
		    User user = new User();
		    user.setActualName(obj.getString("actualName"));
		    user.setEmail(obj.getString("email"));
		    user.setPassword(obj.getString("password"));
		    user.setType(obj.getString("type"));
		    user.setUsername(obj.getString("username"));
		    return user;
		}
		catch (Exception e) {
			return null;
		}
	}

}
