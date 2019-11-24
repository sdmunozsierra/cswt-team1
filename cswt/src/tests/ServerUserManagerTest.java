package tests;

import cswt.User;
import org.junit.Before;
import org.junit.Test;
import server.ServerUserManager;

import static org.junit.Assert.assertEquals;

public class ServerUserManagerTest {
    ServerUserManager manager;

    @Before
    public void SetUp() {
        manager = new ServerUserManager();
    }

    @Test
    public void testCreateAccount() {
        int before = manager.getAllUsers().size();
        manager.createAccount("testuser", "testpassword", "Ticket Admin", "Test User", "email@email.com");
        int after = manager.getAllUsers().size();
        assertEquals(before + 1, after);
        manager.deleteUser("testuser");
    }

    @Test
    public void testGetUser() {
        manager.createAccount("testuser", "testpassword", "Ticket Admin", "Test User", "email@email.com");
        User user = manager.getUser("testuser");
        assertEquals("testuser", user.getUsername());
        assertEquals("testpassword", user.getPassword());
        assertEquals("Ticket Admin", user.getType());
        assertEquals("email@email.com", user.getEmail());
        assertEquals("Test User", user.getActualName());
        manager.deleteUser("testuser");
    }

    @Test
    public void testEditUser() {
        manager.createAccount("testuser", "testpassword", "Ticket Admin", "Test User", "email@email.com");
        manager.editUser("testuser", "testpassword1", "Manager", "Test User Senior", "newemail@email.com");
        User user = manager.getUser("testuser");
        assertEquals("testuser", user.getUsername());
        assertEquals("testpassword1", user.getPassword());
        assertEquals("Manager", user.getType());
        assertEquals("newemail@email.com", user.getEmail());
        assertEquals("Test User Senior", user.getActualName());
        manager.deleteUser("testuser");
    }

    @Test
    public void testValidUser() {
        manager.createAccount("testuser", "testpassword", "Ticket Admin", "Test User", "email@email.com");
        assertEquals(true, manager.validateUser("testuser", "testpassword"));
        manager.deleteUser("testuser");
    }

    @Test
    public void testInvalidUser() {
        manager.createAccount("testuser", "testpassword", "Ticket Admin", "Test User", "email@email.com");
        assertEquals(false, manager.validateUser("testuser", "wrongpassword"));
        manager.deleteUser("testuser");
    }

    @Test
    public void testHasUser() {
        manager.createAccount("testuser", "testpassword", "Ticket Admin", "Test User", "email@email.com");
        assertEquals(true, manager.hasUser("testuser"));
        manager.deleteUser("testuser");
    }

    @Test
    public void testDeleteUser() {
        manager.createAccount("testuser", "testpassword", "Ticket Admin", "Test User", "email@email.com");
        assertEquals(true, manager.hasUser("testuser"));
        manager.deleteUser("testuser");
        assertEquals(false, manager.hasUser("testuser"));
    }

    @Test
    public void testGetAllUsers() {
        int total = manager.getAllUsers().size();
        manager.createAccount("testuser", "testpassword", "Ticket Admin", "Test User", "email@email.com");
        assertEquals(total + 1, manager.getAllUsers().size());
        manager.deleteUser("testuser");
    }
}
