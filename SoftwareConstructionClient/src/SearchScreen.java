import cswt.Ticket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static client.ClientHandler.FAILED;
import static client.ClientHandler.SUCCESSFUL;

public class SearchScreen {
    private static final String ANY = "Any";
    public JPanel mainScreen;
    private JTextField nameText;
    private JComboBox priorityComboBox;
    private JButton cancelButton;
    private JButton enterButton;
    private JTextField resolutionText;
    private JComboBox severityComboBox;
    private JTextField clientText;
    private JTextField assignedToText;
    private JComboBox statusComboBox;
    private JTextField descriptionText;
    private JPanel datePanel;
    private JPanel labels;
    private JLabel titleText;

    public SearchScreen(){
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = nameText.getText();
                String resolution = resolutionText.getText();
                String client = clientText.getText();
                String severity;
                if (severityComboBox.getSelectedItem().toString().equals(ANY)) {
                    severity = "";
                } else {
                    severity = severityComboBox.getSelectedItem().toString();
                }
                String priority;
                if (priorityComboBox.getSelectedItem().toString().equals(ANY)) {
                    priority = "";
                } else {
                    priority = priorityComboBox.getSelectedItem().toString();
                }
                String status;
                if (statusComboBox.getSelectedItem().toString().equals(ANY)) {
                    status = "";
                } else {
                    status = statusComboBox.getSelectedItem().toString();
                }
                String assignedTo = assignedToText.getText();
                String description = descriptionText.getText();


                MainWindow.clientHandler.searchTickets(title, description, resolution, status, priority, severity, client, assignedTo);
                TicketScreen.createModel();
                JComponent comp = (JComponent) e.getSource();
                Window win = SwingUtilities.getWindowAncestor(comp);
                win.dispose();
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
