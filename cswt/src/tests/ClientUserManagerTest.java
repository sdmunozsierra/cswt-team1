package tests;

import client.ClientUserManager;
import cswt.User;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ClientUserManagerTest {
    ClientUserManager manager;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("1");
        manager.addUser(user);
        assertEquals(user, manager.getUser("1"));
    }

    @Test
    public void testGetUser() {
        User user1 = new User();
        user1.setUsername("1");
        user1.setActualName("Name");
        manager.addUser(user1);
        User user2 = manager.getUser("1");
        assertEquals(user1.getActualName(), user2.getActualName());
    }

    @Test
    public void testGetAllUsers() {
        User user = new User();
        user.setUsername("1");
        manager.addUser(user);
        List<User> users = new ArrayList<User>();
        users.add(user);
        assertEquals(users, manager.getAllUsers());
    }

    @Test
    public void testRemoveUser() {
        User user = new User();
        user.setUsername("1");
        manager.addUser(user);
        manager.removeUser("1");
        assertEquals(0, manager.getAllUsers().size());
    }

    @Test
    public void testUpdateUser() {
        User user1 = new User();
        user1.setUsername("1");
        user1.setActualName("Name");
        manager.addUser(user1);
        User user2 = new User();
        user2.setUsername("1");
        user2.setActualName("New Name");
        manager.updateUser(user2);
        assertEquals("New Name", manager.getUser("1").getActualName());
    }

    @Test
    public void testClearManager() {
        User user1 = new User();
        user1.setUsername("1");
        user1.setActualName("Name");
        manager.addUser(user1);
        manager.clearManager();
        assertEquals(0, manager.getAllUsers().size());
    }

    @Test
    public void testFromJson() {
        User user = manager.fromJSON(new JSONObject("{\"username\":\"User\",\"password\":\"\",\"type\":\"\",\"actualName\":\"\",\"email\":\"\"}"));
        assertEquals("User", user.getUsername());
    }


    @Before
    public void SetUp() {
        manager = new ClientUserManager();
    }
}
