import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddTicketScreen {
    public JPanel mainScreen;
    private JPanel datePanel;
    private JPanel labels;
    private JLabel titleText;
    private JTextField nameText;
    private JComboBox statusComboBox;
    private JComboBox priorityComboBox;
    private JButton cancelButton;
    private JButton saveButton;
    private JTextField descriptionText;
    private JComboBox severityComboBox;
    private JTextField clientText;
    private JTextField assignedToText;
    Ticket ticket;

    public AddTicketScreen(){
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ticket = new Ticket();
                ticket.setName(nameText.getText());
                ticket.setDescription(descriptionText.getText());
                ticket.setStatus(statusComboBox.getSelectedItem().toString());
                ticket.setPriority(priorityComboBox.getSelectedItem().toString());
                ticket.setSeverity(severityComboBox.getSelectedItem().toString());
                ticket.setClient(clientText.getText());
                ticket.setAssignedTo(assignedToText.getText());

                TicketScreen.getTicket(ticket);
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
