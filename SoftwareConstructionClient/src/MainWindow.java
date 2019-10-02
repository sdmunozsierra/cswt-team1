


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow {
    private JPanel mainPanel;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel LoginTextBox;
    static JFrame mainWindow;
    private String username;
    private String password;

    public MainWindow() {
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

                    JFrame ticketWindow = new JFrame("TicketScreen");
                    ticketWindow.setContentPane(new TicketScreen().mainScreen);
                    ticketWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    ticketWindow.pack();
                    ticketWindow.setVisible(true);
                    mainWindow.setVisible(false);
                }
            }
        });
    }

    public static void main (String [] args){
        mainWindow = new JFrame("MainWindow");
        mainWindow.setContentPane(new MainWindow().mainPanel);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.pack();
        mainWindow.setVisible(true);
    }
}
