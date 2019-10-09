import cswt.Ticket;

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
    private JButton editButton;
    private JButton addButton;
    private JPanel mainPanel;
    private JPanel ticketProperties;
    private JLabel statusLabel;
    private JLabel priorityLabel;
    private JLabel severityLabel;
    private JLabel assignedToLabel;
    private JLabel clientLabel;
    private JLabel openDateLabel;
    private JLabel daysOpenLabel;
    private JLabel closedDateLabel;
    private JPanel ticketManagement;
    private JPanel ticketManagementButtons;
    private JTextPane descriptionTextPane;
    private JTextPane resolutionTextPane;
    private JButton resolvedButton;
    private JButton rejectButton;
    private JButton closedButton;
    private JButton saveButton;

    private JComboBox priorityComboBox;
    private JComboBox severityComboBox;
    private JTextField clientText;
    private JTextField assignedToText;

    private JTextField titleText;
    private JButton openButton;

    private static List<Ticket> tickets = new ArrayList();
    private static DefaultListModel model = new DefaultListModel();
    private boolean editState = true;

    public TicketScreen() {
        makeInvisible();
        hideEditProperties();
        createModel();


// Listeners
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (editState == true) {
                    makeVisible();
                    hideLabels();
                    showEditProperties();
                    editButton.setText("Exit");
                    editState = false;

                }
                else{
                    makeInvisible();
                    showLabels();
                    hideEditProperties();
                    createModel();
                    clear();
                    editButton.setText("Edit");
                    editState = true;
                }

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

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveChanges();
            }
        });

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(ticketList.getSelectedValue() != null){
                    Ticket t = tickets.get(ticketList.getSelectedIndex());
                    MainWindow.clientHandler.openTicket(t.getId(),t.getPriority(),t.getAssignedTo());
                    tickets.set(ticketList.getSelectedIndex(), MainWindow.clientHandler.getTicket(t.getId()));
                    clear();
                    createModel();

                }
            }
        });

        resolvedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        closedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

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

        ticketList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()){
                    if(ticketList.getSelectedValue() != null){
                        setText();
                        setEditPropertiesText();
                    }
                }
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

    private void clear(){
        titleText.setText("");
        descriptionTextPane.setText("");
        resolutionTextPane.setText("");
        statusLabel.setText("");
        priorityLabel.setText("");
        severityLabel.setText("");
        clientLabel.setText("");
        assignedToLabel.setText("");
        openDateLabel.setText("");
        daysOpenLabel.setText("");
        closedDateLabel.setText("");
    }

    private void makeVisible(){
        openButton.setVisible(true);
        resolvedButton.setVisible(true);
        rejectButton.setVisible(true);
        closedButton.setVisible(true);
        saveButton.setVisible(true);
    }

    private void makeInvisible(){
        openButton.setVisible(false);
        resolvedButton.setVisible(false);
        rejectButton.setVisible(false);
        closedButton.setVisible(false);
        saveButton.setVisible(false);
    }

    private void hideLabels(){
        priorityLabel.setVisible(false);
        severityLabel.setVisible(false);
        clientLabel.setVisible(false);
        assignedToLabel.setVisible(false);
    }

    private void showLabels(){
        priorityLabel.setVisible(true);
        severityLabel.setVisible(true);
        clientLabel.setVisible(true);
        assignedToLabel.setVisible(true);
    }

    private void setText(){
        Ticket t = tickets.get(ticketList.getSelectedIndex());
        titleText.setText(t.getTitle());
        descriptionTextPane.setText(t.getDescription());
        resolutionTextPane.setText(t.getResolution());
        statusLabel.setText(t.getStatus());
        priorityLabel.setText(t.getPriority());
        severityLabel.setText(t.getSeverity());
        clientLabel.setText(t.getClient());
        assignedToLabel.setText(t.getAssignedTo());
        openDateLabel.setText(t.getOpenedDate());



        //daysOpenLabel.setText(t.getDaysOpen();
    }

    private void setEditPropertiesText(){
        Ticket t = tickets.get(ticketList.getSelectedIndex());

        priorityComboBox.setSelectedItem(t.getPriority());
        severityComboBox.setSelectedItem(t.getSeverity());
        clientText.setText(t.getClient());
        assignedToText.setText(t.getAssignedTo());

    }

    private void saveChanges(){
        Ticket t = tickets.get(ticketList.getSelectedIndex());

        t.setTitle(titleText.getText());
        t.setPriority(priorityComboBox.getSelectedItem().toString());
        t.setSeverity(severityComboBox.getSelectedItem().toString());
        t.setClient(clientText.getText());
        t.setAssignedTo(assignedToText.getText());
        t.setDescription(descriptionTextPane.getText());
        t.setResolution(resolutionTextPane.getText());

      //  MainWindow.clientHandler.editTicket(t);
        tickets.set(ticketList.getSelectedIndex(),t);
        createModel();

    }

    private void hideEditProperties(){
        titleText.setEditable(false);

        priorityComboBox.setVisible(false);
        severityComboBox.setVisible(false);
        clientText.setVisible(false);
        assignedToText.setVisible(false);
        descriptionTextPane.setEditable(false);
        resolutionTextPane.setEditable(false);

    }

    private void showEditProperties(){
        titleText.setEditable(true);

        priorityComboBox.setVisible(true);
        severityComboBox.setVisible(true);
        clientText.setVisible(true);
        assignedToText.setVisible(true);

        descriptionTextPane.setEditable(true);
        resolutionTextPane.setEditable(true);
    }

    public static void createModel(){
        model.clear();
        for (Ticket ticket: MainWindow.clientHandler.getAllTickets()){
            model.addElement(ticket.getTitle());
            tickets.add(ticket);
        }
    }


}
