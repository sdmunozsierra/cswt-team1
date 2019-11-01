import client.ClientHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import static client.ClientHandler.*;

public class MainWindow {
    private JPanel mainPanel;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    public static ClientHandler clientHandler = new ClientHandler();
    private JLabel LoginTextBox;
    public static JFrame mainWindow;
    private String username;
    private String password;

    public MainWindow() {
        clientHandler.collectRecentTickets();
        clientHandler.collectAllUsers();
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check if values for username and password is valid
                if (usernameTextField.getText().trim().isEmpty() && passwordField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(mainWindow, "Username and Password are empty.");
                } else if (usernameTextField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(mainWindow, "Username is empty.");
                } else if (passwordField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(mainWindow, "Password is empty.");
                } else {
                    // store values for valid username and password
                    username = usernameTextField.getText().trim();
                    password = passwordField.getText().trim();
                    if(clientHandler.validateUser(username, password).equals(SUCCESSFUL)) {
                        if (clientHandler.getCurrentUserType().equals(MANAGER)) {
                            UserManager.getCurrent().setKindOfUser(UserManager.kindOfUser.manager);
                        }
                        else if (clientHandler.getCurrentUserType().equals(TICKET_ADMIN)) {
                            UserManager.getCurrent().setKindOfUser(UserManager.kindOfUser.ticketAdmin);
                        }
                        else if (clientHandler.getCurrentUserType().equals(DEPARTMENT_SUPPORT)) {
                            UserManager.getCurrent().setKindOfUser(UserManager.kindOfUser.studentSupport);
                        }
                        else {
                            UserManager.getCurrent().setKindOfUser(UserManager.kindOfUser.manager);
                        }
                        JFrame ticketWindow = new JFrame("TicketScreen");
                        ticketWindow.setContentPane(new TicketScreen().mainScreen);
                        ticketWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        ticketWindow.pack();
                        ticketWindow.setVisible(true);
                        TicketScreen.createModel();

                        JComponent comp = (JComponent) e.getSource();
                        Window win = SwingUtilities.getWindowAncestor(comp);
                        win.dispose();
                        clear();
                    }
                    else {
                        JOptionPane.showMessageDialog(mainWindow, "Invalid username or password.");
                    }
                }
            }
        });
    }
    public void clear(){
        usernameTextField.setText("");
        passwordField.setText("");
    }

    public static void main (String [] args){
        mainWindow = new JFrame("MainWindow");
        mainWindow.setContentPane(new MainWindow().mainPanel);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.pack();
        mainWindow.setVisible(true);
    }
}
