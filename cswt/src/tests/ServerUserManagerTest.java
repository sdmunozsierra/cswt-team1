package tests;

import cswt.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.ServerUserManager;
import server.UserStorer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class ServerUserManagerTest {
    UserStorer storer;
    ServerUserManager manager;
    final String TEST_DIR = Paths.get(System.getProperty("user.dir"), "sampleUsers").toString();
    final String TEST_FILE = "testuser.json";

    @Before
    public void SetUp() {
        storer = new UserStorer();
        storer.USER_DIR = TEST_DIR;
        manager = new ServerUserManager();
        manager.setStorer(storer);
    }

    @Test
    public void testCreateAccount() {
        User user = manager.createAccount("testuser2", "testpassword2", "Support", "Test User 2", "test@email.com");
        assertNotEquals(null, user);
        assertEquals("testuser2", user.getUsername());
        assertEquals("testpassword2", user.getPassword());
        assertEquals("Support", user.getType());
    }

    @Test
    public void testEditUser() {
        User user = manager.createAccount("testuser2", "testpassword2", "Support", "Test User 2", "test@email.com");
        User edited = manager.editUser(user.getUsername(), "newpassword", user.getType(), user.getActualName(), user.getEmail());
        assertNotEquals(null, edited);
        assertEquals("newpassword", edited.getPassword());
        assertEquals("Support", edited.getType());
    }

    @Test
    public void testValidateUser() {
        assertEquals(true, manager.validateUser("testuser", "testpassword"));
        assertEquals(false, manager.validateUser("invaliduser", "testpassword"));
        assertEquals(false, manager.validateUser("invaliduser", "invalidpassword"));
        assertEquals(false, manager.validateUser("testuser", "invalidpassword"));
    }

    @Test
    public void testDeleteUser() {
        User user = manager.createAccount("testuser2", "testpassword2", "Support", "Test User 2", "test@email.com");
        manager.deleteUser(user.getUsername());
        assertEquals(false, Files.exists(Paths.get(TEST_DIR, "testuser2.json")));
    }

    @Test
    public void testGetTicket() {
        User user = manager.getUser("testuser");
        assertNotEquals(null, user);
        assertEquals("testuser",user.getUsername());
        assertEquals("testpassword", user.getPassword());
    }

    @Test
    public void getAllUsers() {
        List<User> users = manager.getAllUsers();
        assertEquals(1, users.size());
        User user = users.get(0);
        assertEquals("testuser",user.getUsername());
        assertEquals("testpassword", user.getPassword());
    }

    private void clearTestTicketDirectory() {
        File dir = new File(TEST_DIR);
        for (File file: dir.listFiles()) {
            if(!file.getName().equals(TEST_FILE)) {
                file.delete();
            }
        }
    }

    @After
    public void tearDown() {
        clearTestTicketDirectory();
    }
}
