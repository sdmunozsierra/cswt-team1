import cswt.Ticket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static client.ClientHandler.FAILED;
import static client.ClientHandler.SUCCESSFUL;

public class AddTicketScreen {
    public JPanel mainScreen;
    private JPanel datePanel;
    private JPanel labels;
    private JLabel titleText;
    private JTextField nameText;
    private JComboBox priorityComboBox;
    private JButton cancelButton;
    private JButton saveButton;
    private JTextField descriptionText;
    private JComboBox severityComboBox;
    private JTextField clientText;
    private JTextField assignedToText;

    public AddTicketScreen(){
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ticket ticket = new Ticket();
                ticket.setTitle(nameText.getText());
                ticket.setDescription(descriptionText.getText());
                ticket.setClient(clientText.getText());
                ticket.setSeverity(severityComboBox.getSelectedItem().toString());
                ticket.setPriority(priorityComboBox.getSelectedItem().toString());
                ticket.setAssignedTo(assignedToText.getText());



                String result = MainWindow.clientHandler.createTicket(ticket);
                if (result.equals(SUCCESSFUL)) {
                    TicketScreen.createModel();
                    JComponent comp = (JComponent) e.getSource();
                    Window win = SwingUtilities.getWindowAncestor(comp);
                    win.dispose();
                }
                else if (result.equals(FAILED)) {
                    JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Unable to create ticket. Please try again later.");
                }
                else {
                    JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: You do not have the permissions to perform this operation.");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComponent comp = (JComponent) e.getSource();
                Window win = SwingUtilities.getWindowAncestor(comp);
                win.dispose();
            }
        });


    }
}
