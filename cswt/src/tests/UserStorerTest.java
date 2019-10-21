package tests;

import cswt.User;
import org.junit.Before;
import org.junit.Test;
import server.UserStorer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class UserStorerTest {
    UserStorer storer;
    final String TEST_DIR = Paths.get(System.getProperty("user.dir"), "sampleUsers").toString();

    @Before
    public void SetUp() {
        storer = new UserStorer();
        storer.USER_DIR = TEST_DIR;
    }

    @Test
    public void testLoadUsers() {
        List<User> users = storer.loadUsers();
        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals("testuser", user.getUsername());
        assertEquals("testpassword", user.getPassword());
        assertEquals("Ticket Admin", user.getType());
        assertEquals("email@email.com", user.getEmail());
        assertEquals("Test User", user.getActualName());
    }

    @Test
    public void testStoreUser() {
        User user = new User();
        user.setActualName("Test User 2");
        user.setUsername("testuser2");
        user.setPassword("testpassword2");
        user.setType("Ticket Admin");
        user.setEmail("email2@email.com");
        boolean added = storer.storeUser(user);
        assertEquals(true, added);
        List<User> users = storer.loadUsers();
        assertEquals(2, users.size());
        try {
            Files.deleteIfExists(Paths.get(TEST_DIR, "testuser2.json"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setActualName("Test User 2");
        user.setUsername("testuser2");
        user.setPassword("testpassword2");
        user.setType("Ticket Admin");
        user.setEmail("email2@email.com");
        boolean added = storer.storeUser(user);
        List<User> users = storer.loadUsers();
        assertEquals(2, users.size());
        storer.deleteUser(user.getUsername());
        users = storer.loadUsers();
        assertEquals(1, users.size());
    }

}
