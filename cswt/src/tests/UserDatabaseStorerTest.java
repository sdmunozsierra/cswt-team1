package tests;

import cswt.User;
import org.junit.Before;
import org.junit.Test;
import server.UserDatabaseStorer;

import java.util.HashMap;

import static org.junit.Assert.*;

public class UserDatabaseStorerTest {
    UserDatabaseStorer storer;

    @Before
    public void SetUp() {
        storer = new UserDatabaseStorer();
    }

    @Test
    public void testStoreUser() {
        User user = new User();
        user.setActualName("Test User");
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("email@email.com");
        user.setType("Ticket Admin");
        storer.storeUser(user);
        HashMap<String, User> mapping = storer.loadUsersFromDatabase();
        User u = mapping.get("testuser");
        assertEquals("testuser", u.getUsername());
        assertEquals("testpassword", u.getPassword());
        assertEquals("Ticket Admin", u.getType());
        assertEquals("email@email.com", u.getEmail());
        assertEquals("Test User", u.getActualName());
        storer.deleteUser("testuser");
    }

    @Test
    public void testLoadUsers() {
        User user = new User();
        user.setActualName("Test User");
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("email@email.com");
        user.setType("Ticket Admin");
        int before = storer.loadUsersFromDatabase().size();
        storer.storeUser(user);
        int after = storer.loadUsersFromDatabase().size();
        assertEquals(before + 1, after);
        storer.deleteUser("testuser");
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setActualName("Test User");
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("email@email.com");
        user.setType("Ticket Admin");
        storer.storeUser(user);
        storer.deleteUser("testuser");
        HashMap<String, User> mapping = storer.loadUsersFromDatabase();
        assertEquals(false, mapping.containsKey("testuser"));
    }

}
