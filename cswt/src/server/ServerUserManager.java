package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cswt.User;

public class ServerUserManager {
	
	private HashMap<String, User> mapping;
	private UserDatabaseStorer storer;
	
	public ServerUserManager() {
		this.storer = new UserDatabaseStorer();
		this.mapping = this.storer.loadUsersFromDatabase();
	}
	
	/** Creates an account for a new user. 
	 * @param username The username of the user to be added
	 * @param password The password of the user to be added
	 * @param type The type of the user to be added
	 * @param actualName The actual name of the user to be added
	 * @param email The email of the user to be added
	 * @return The new user or null if the system was unable to store the user
	 * */
	public synchronized User createAccount(String username, String password, String type, String actualName, String email) {
		User user = new User();
		user.setActualName(actualName);
		user.setEmail(email);
		user.setPassword(password);
		user.setType(type);
		user.setUsername(username);
		boolean added = addUser(user);
		if (added) {
			return user;
		}
		return null;
	}
	
	/** Edits a user's information. 
	 * @param username The username of the user
	 * @param password The new password of the user
	 * @param type The new type of the user
	 * @param actualName The new actual name of the user
	 * @param email The new email of the user
	 * @return The edited user or null if the system was unable to store the user
	 * */
	public synchronized User editUser(String username, String password, String type, String actualName, String email) {
		User user = this.getUser(username);
		user.setActualName(actualName);
		user.setEmail(email);
		user.setPassword(password);
		user.setType(type);
		this.storer.storeUser(user);
		return user;
	}
	
	/** Checks if a user has entered the correct username and password. 
	 * @param username The provided username
	 * @param password The provided password
	 * @return If the username and password are valid
	 * * */
	public synchronized boolean validateUser(String username, String password) {
		if (!mapping.containsKey(username)) {
			return false;
		}
		User user = mapping.get(username);
		return user.getPassword().equals(password);
	}
	

	/** Adds a user to the manager. 
	 * @param user The user to be added
	 * @return If the manager was able to add the user
	 * */
	private boolean addUser(User user) {
		this.storer.storeUser(user);
		mapping.put(user.getUsername(), user);
		return true;
	}
	
	/** Removes a user from the manager and storage. 
	 * @param username The username of the user to be removed
	 * */
	public synchronized void deleteUser(String username) {
		if (!mapping.containsKey(username)) {
			return;
		}
		mapping.remove(username);
		this.storer.deleteUser(username);
	}
	
	
	/** Gets a user based on provided username
	 * @param username The provided username
	 * @return The user or null if no user with that username exists
	 * */
	public synchronized User getUser(String username) {
		if (!mapping.containsKey(username)) {
			return null;
		}
		return mapping.get(username);
	}
	
	/** Checks if a username has been registered in the manager
	 * @param username The provided username
	 * @return If the user is in the manager or not
	 * */
	public synchronized boolean hasUser(String username) {
		if (!mapping.containsKey(username)) {
			return false;
		}
		return true;
	}

	/** Sets the storer for the users
	 * @param storer The storer to be set
	 * */
	public synchronized  void setStorer(UserDatabaseStorer storer) {
		this.storer = storer;
	}
	
	/** Gets all current users
	 * @return The list of all users
	 * */
	public synchronized List<User> getAllUsers() {
		return new ArrayList<User>(mapping.values());
	}
}
