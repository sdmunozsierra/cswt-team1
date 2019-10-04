import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static client.ClientHandler.FAILED;
import static client.ClientHandler.SUCCESSFUL;

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

    public AddTicketScreen(){
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = MainWindow.clientHandler.createTicket(nameText.getText(), descriptionText.getText(), clientText.getText(), severityComboBox.getSelectedItem().toString());
                if (result == SUCCESSFUL) {
                    TicketScreen.createModel();
                    JComponent comp = (JComponent) e.getSource();
                    Window win = SwingUtilities.getWindowAncestor(comp);
                    win.dispose();
                }
                else if (result == FAILED) {
                    JOptionPane.showMessageDialog(MainWindow.mainWindow, "Unable to create ticket. Please try again later.");
                }
                else {
                    JOptionPane.showMessageDialog(MainWindow.mainWindow, "Invalid permissions. Cannot fulfill request.");
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
