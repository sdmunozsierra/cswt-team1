package Testing;
import client.ClientHandler;
import junit.framework.*;

public class ClientHandlerTest {
    ClientHandler clientHandler = new ClientHandler();
    //create user

    @Test
    public void testClientHandler (){
        //create Account tests
        System.out.println("Test for createAccount");
        System.out.println(assertEquals(SUCCESSFUL, ClientHandler.createAccount("JONROMAN1","PASSWORD1","Manager","JONATHANROMAN","email@email.com")));

        System.out.println("Test for createAccount");
        System.out.println(assertEquals(SUCCESSFUL, ClientHandler.createAccount("1","1","Manager","1","1")));

        //Get all tickets
        System.out.println("Test for getAllTickets");
        System.out.println(assertNotEquals(NULL,ClientHandler.getAllTickets()));

        //get Ticket
        System.out.println("Test for getTicket");
        System.out.println(assertEquals(NULL, ClientHandler.getTicket("1")));
        //ID should be an existing tickets ID
        System.out.println("Test for getTicket");
        System.out.println(assertNotEquals(NULL, ClientHandler.getTicket("1570676451533")));

        //update ticket tests
        System.out.println("Test for updateTickicket");
        System.out.println(assertNotEquals(SUCCESSFUL, ClientHandler.updateTicket("1570676451533")));

        System.out.println("Test for updateTicket");
        System.out.println(assertEquals(FAILED, ClientHandler.updateTicket("1")));

        //getUser
        System.out.println("Test for getUser");
        System.out.println(assertEquals(NULL, ClientHandler.getUser("1570676451533")));
        //Tests successful retrieval of user
        System.out.println("Test for getUser");
        System.out.println(assertNotEquals(NULL, ClientHandler.getUser("1")));
        
        //Get all users
        System.out.println("Test for getAllUsers");
        System.out.println(assertNotEquals(NULL, ClientHandler.getAllUsers()));

        //updateAllUsers
        System.out.println("Test for updateAllUsers");
        System.out.println(assertEquals(SUCCESSFUL, ClientHandler.updateAllUsers()));

        //updateUser
        System.out.println("Test for updateUser");
        System.out.println(assertEquals(SUCCESSFUL, ClientHandler.updateUser("")));

        System.out.println("Test for updateUser");
        System.out.println(assertEquals(SUCCESSFUL, ClientHandler.updateUser("JONATHANROMAN")));

        //editUser tests
        System.out.println("Test for editUser");
        System.out.println(assertEquals(FAILED, ClientHandler.editUser("JONROMAN","PASSWORD","Manager","JONATHANROMAN","email@email.com")));

        System.out.println("Test for editUser");
        System.out.println(assertEquals(SUCCESSFUL, ClientHandler.editUser("","","","","")));

        //delete User tests
        System.out.println("Test for delteUser");
        System.out.println(assertEquals(SUCCESSFUL, ClientHandler.deleteUser("")));

        System.out.println("Test for deleteUser");
        System.out.println(assertEquals(FAILED, ClientHandler.deleteUser("JONATHANROMAN")));

        //validate user tests
        System.out.println("Test for editUser");
        System.out.println(assertEquals(FAILED, ClientHandler.validateUser("JONROMAN","PASSWORD")));

        System.out.println("Test for editUser");
        System.out.println(assertEquals(SUCCESSFUL, ClientHandler.validateUser("","")));


        //SUCCESSFUL
        System.out.println("Test for createTicket");
        System.out.println(assertTrue(ClientHandler.createTicket(ticket).equals(SUCCESSFUL)));
        
        System.out.println("Test for openTickets");
        System.out.println(assertTrue(ClientHandler.openTickets("1234",priority, assignedTo).equals(SUCCESSFUL)));


        System.out.println("Test for closeTicket");
        System.out.println(assertTrue(ClientHandler.closeTicket("1234").equals(SUCCESSFUL)));


        //FAILED
        System.out.println("Test for openTickets");
        System.out.println(assertTrue(ClientHandler.openTickets(123,priority, assignedTo).equals(FAILED)));

        System.out.println("Test for createTicket");
        System.out.println(assertTrue(ClientHandler.createTicket(ticket).equals(FAILED)));

        System.out.println("Test for closeTicket");
        System.out.println(assertTrue(ClientHandler.closeTicket(id).equals(FAILED)));


        //INVALID
        System.out.println("Test for openTickets");
        System.out.println(assertTrue(ClientHandler.openTickets(1234,priority, assignedTo).equals(INVALID)));

        System.out.println("Test for createTicket");
        System.out.println(assertTrue(ClientHandler.createTicket(5678).equals(INVALID)));

        System.out.println("Test for closeTicket");
        System.out.println(assertTrue(ClientHandler.closeTicket(1234).equals(INVALID)));








    }



}