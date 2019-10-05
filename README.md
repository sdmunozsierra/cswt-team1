# cswt-team1
Ticket tracker manager built on Java.

## How to run CSWT

### Pre-requisites

1. Install java version 12 for your operating system.
For linux-based systems `openjdk-12` will suffice.

2. Have ready an IDE. IntelliJ Idea is recommended.

### IntelliJ IDEA

__Set up the project__
1. Import from version control.
2. Use this repo as source `https://github.com/sdmunozsierra/cswt-team1.git`.
3. Select Java12 as the JDK.

__Start the server__
1. Go to `cswt/src/server/`
2. Right click on `ServerHandler` and click on `Run ServerHandler.main()`

__Open the GUI__
1. Go to `SoftwareConstructionClient/src/MainWindow`
2. Right click on `MainWindow` and click `Run MainWindow.main()`

## Project Structure
[ ] Add diagrams

### Server (Backend)
The backend code is stored in `cswt` module. 
This handles both the server handler and client handler for the project.
This module is also in charge or managing Tickets and Users via JSON objects.

#### cswt module
Contains the Ticket and User classes used across the project.

`Ticket()` class contains the following fields: title, description, status, resolution, severity,
 priority, client, assignedTo, openedDate, closedDate, timeSpent, and id.
 Also this class has the method `toJSON()` to transform the Ticket into a JSON object.
 
 `USER()` class contains the following fields: username, password, type, actualName, and email.
 This class has also the `toJSON()` method.
 
#### client module
Contains the Ticket and User client managers along with it's Handler.

Both `ClientTicketManager` and `ClientUserManager` are arrays containing the tickets and clients currently
stored in the project.

`ClientHandler` sends JSON requests to the server. 

Ticket Requests:
* Create Ticket
* Open Ticket
* Close Ticket
* Mark Ticket as fixed
* Reject Ticket
* Edit Ticket
* Search Ticket
* Update All Tickets
* Get All Tickets
* Get Ticket

User Requests:
* Create Account
* Validate User
* Edit User
* Delete User
* Update All Users
* Get All Users
* Get User

### Client (GUI)
