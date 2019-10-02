import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.*;
import java.util.List;

public class TicketScreen {
    public JPanel mainScreen;
    private JTextField ticketSearchBar;
    private JList ticketList;
    private JLabel titleText;
    private JTextPane descriptionTextPane;
    private JTextPane resolutionTextPane;
    private JButton editButton;
    private JButton addButton;
    private JPanel ticketManagement;
    private JPanel mainPanel;
    private JPanel ticketProperties;
    private JLabel statusLabel;
    private JLabel priorityLabel;
    private JLabel severityLabel;
    private JLabel assignedToLabel;
    private JLabel clientLabel;
    private JPanel datePanel;
    private JPanel labels;
    private JLabel openDateLabel;
    private JLabel closedDateLabel;
    private JLabel daysOpenLabel;
    private JPanel ticketManagementButtons;
    private static List<Ticket> tickets = new ArrayList();
    private static DefaultListModel model = new DefaultListModel();

    public TicketScreen() {
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // open ticket creation window
                JFrame ticketWindow = new JFrame("TicketDetailScreen");
                ticketWindow.setContentPane(new TicketDetailScreen().mainScreen);
                ticketWindow.pack();
                ticketWindow.setVisible(true);
            }
        });



        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // open edit creation window
                JFrame ticketWindow = new JFrame("AddTicketScreen");
                ticketWindow.setMinimumSize(new Dimension(300, 500));
                ticketWindow.setContentPane(new AddTicketScreen().mainScreen);
                ticketWindow.setVisible(true);
            }
        });

        ticketSearchBar.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                if (ticketSearchBar.getText().contains("Search..")) {
                    ticketSearchBar.setText("");
                    ticketList.setModel(model);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (ticketSearchBar.getText().isEmpty()) {
                    ticketSearchBar.setText("Search..");
                    ticketList.setModel(model);
                }
            }
        });

        ticketSearchBar.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent arg0) { }
            public void insertUpdate(DocumentEvent arg0) { search(); }
            public void removeUpdate(DocumentEvent arg0)
            {
                search();
            }
        });
    }

    private void search() {
        DefaultListModel matching = new DefaultListModel();

        String word = ticketSearchBar.getText();
        for (int i = 0; i < model.getSize(); i++){
            String temp = model.elementAt(i).toString();

            if (temp.contains(word)){
                matching.addElement(model.elementAt(i));
            }
        }

        ticketList.setModel(matching);
    }


    public static void getTicket(Ticket t){
        tickets.add(t);

    }

    public static void createModel(){
        model.clear();
        for (int i = 0; i < tickets.size(); i++) {
            Ticket temp = tickets.get(i);
            model.addElement(temp.getName());
        }
    }


}
