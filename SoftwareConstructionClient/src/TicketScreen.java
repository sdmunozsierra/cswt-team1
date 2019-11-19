import cswt.Ticket;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static client.ClientHandler.*;

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
    private JComboBox filter;
    private JToolBar toolbar;
    private JButton historyButton;
    private JButton manageUsersButton;
    private JButton signOutButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton searchTicketsButton;

    private static List<Ticket> tickets = new ArrayList();
    private static DefaultListModel model = new DefaultListModel();
    private boolean editState = true;

    public TicketScreen() {
        toolbar.setFloatable(false);
        removeFocus();
        makeInvisible();
        hideEditProperties();
        createModel();



// Action Listeners
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
                else {
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
                // open add ticket window
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
                    String status = MainWindow.clientHandler.openTicket(t.getId(),t.getPriority(),t.getAssignedTo());
                    if (SUCCESSFUL.equals(status)) {
                        tickets.set(ticketList.getSelectedIndex(), MainWindow.clientHandler.getTicket(t.getId()));
                        clear();
                        createModel();
                    }
                    else if (FAILED.equals(status)) {
                        JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Operation failed. Please try again later.");
                    }
                    else if (OLD.equals(status)) {
                        JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Refresh ticket and try again.");
                    }
                    else {
                        JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: You do not have the permissions to perform this operation.");
                    }

                }
            }
        });

        resolvedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Ticket t = tickets.get(ticketList.getSelectedIndex());

                String status = MainWindow.clientHandler.markTicketAsFixed(t.getId(),resolutionTextPane.getText());
                if (status.equals(SUCCESSFUL)) {
                    tickets.set(ticketList.getSelectedIndex(), MainWindow.clientHandler.getTicket(t.getId()));
                    clear();
                    createModel();
                }
                else if (FAILED.equals(status)) {
                    JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Operation failed. Please try again later.");
                }
                else if (OLD.equals(status)) {
                    JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Refresh ticket and try again.");
                }
                else {
                    JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: You do not have the permissions to perform this operation.");
                }
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Ticket t = tickets.get(ticketList.getSelectedIndex());
                if (!t.getStatus().equals("NEW")){
                    JOptionPane.showMessageDialog(mainScreen, "Error: Cannot reject ticket that is not new.");
                }
                else {
                    String status = MainWindow.clientHandler.rejectTicket(t.getId());
                    if (status.equals(SUCCESSFUL)){
                        tickets.set(ticketList.getSelectedIndex(), MainWindow.clientHandler.getTicket(t.getId()));
                        clear();
                        createModel();
                    }
                    else if (FAILED.equals(status)) {
                        JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Operation failed. Please try again later.");
                    }
                    else if (OLD.equals(status)) {
                        JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Refresh ticket and try again.");
                    }
                    else {
                        JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: You do not have the permissions to perform this operation.");
                    }
                }

            }
        });

        closedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Ticket t = tickets.get(ticketList.getSelectedIndex());
                if (!t.getStatus().equals("OPEN") || !t.getStatus().equals("FIXED")) {
                    JOptionPane.showMessageDialog(mainScreen, "Error: Ticket status must be 'OPEN' in order to be closed");
                }
                else {
                    String status = MainWindow.clientHandler.closeTicket(t.getId());
                    if (status.equals(SUCCESSFUL)) {
                        tickets.set(ticketList.getSelectedIndex(), MainWindow.clientHandler.getTicket(t.getId()));
                        clear();
                        createModel();
                    }
                    else if (FAILED.equals(status)) {
                        JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Operation failed. Please try again later.");
                    }
                    else if (OLD.equals(status)) {
                        JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Refresh ticket and try again.");
                    }
                    else {
                        JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: You do not have the permissions to perform this operation.");
                    }
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Ticket t = tickets.get(ticketList.getSelectedIndex());
                MainWindow.clientHandler.updateTicket(t.getId());
                clear();
                createModel();
            }
        });

        filter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int i = filter.getSelectedIndex();
                if (i != -1){
                    filter.setFocusable(true);
                    filter.requestFocus();
                    searchAttribute(filter.getItemAt(i).toString());
                }
            }
        });

        manageUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame ticketWindow = new JFrame("Manage Users");
                ticketWindow.setMinimumSize(new Dimension(300, 500));
                ticketWindow.setContentPane(new UserManagementWindow().mainScreen);
                ticketWindow.setVisible(true);
            }
        });

        searchTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFrame ticketWindow = new JFrame("Search Tickets");
                ticketWindow.setMinimumSize(new Dimension(300, 500));
                ticketWindow.setContentPane(new SearchScreen().mainScreen);
                ticketWindow.setVisible(true);
            }
        });

        historyButton.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame historyWindow = new JFrame("History");
                historyWindow.setMinimumSize(new Dimension(500,500));
                historyWindow.setContentPane(new HistoryScreen().mainScreen);
                historyWindow.setVisible(true);
            }
        }));

        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComponent comp = (JComponent) e.getSource();
                Window win = SwingUtilities.getWindowAncestor(comp);
                win.dispose();
                MainWindow.mainWindow.setVisible(true);
            }
        });


// Focus Listener
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

// Document Listener
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
                        Ticket t = tickets.get(ticketList.getSelectedIndex());
                        MainWindow.clientHandler.updateTicket(t.getId());
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

    public void clear(){
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
        UserManager.kindOfUser currentUser = UserManager.getCurrent().getKindOfUser();
        if(currentUser == UserManager.kindOfUser.manager) {
            resolvedButton.setVisible(true);
            saveButton.setVisible(true);
            rejectButton.setVisible(true);
            closedButton.setVisible(true);
            openButton.setVisible(true);
            deleteButton.setVisible(true);
            refreshButton.setVisible(true);

        } else if(currentUser == UserManager.kindOfUser.studentSupport){
            resolvedButton.setVisible(true);
            saveButton.setVisible(true);
            refreshButton.setVisible(true);

        }else if (currentUser == UserManager.kindOfUser.ticketAdmin) {

        }else{
            resolvedButton.setVisible(true);
            saveButton.setVisible(true);
            refreshButton.setVisible(true);
        }
    }

    private void makeInvisible(){
        openButton.setVisible(false);
        resolvedButton.setVisible(false);
        rejectButton.setVisible(false);
        closedButton.setVisible(false);
        saveButton.setVisible(false);
        deleteButton.setVisible(false);
        refreshButton.setVisible(false);

        // if user is admin, add or edit buttons will not be available
        UserManager.kindOfUser currentUser = UserManager.getCurrent().getKindOfUser();
        if (currentUser == UserManager.kindOfUser.ticketAdmin){
            addButton.setVisible(false);
            editButton.setVisible(false);
        }
        else{
            manageUsersButton.setVisible(false);
        }
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
        closedDateLabel.setText(t.getClosedDate());
        if (t.getClosedDate().equals("") && !t.getOpenedDate().equals("")){
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            try {
                Date openedDate = df.parse(t.getOpenedDate());
                Date currentDate = df.parse(df.format(new Date()));
                int daysOpened = (int) ((currentDate.getTime() - openedDate.getTime()) / (1000 * 60 * 60 * 24));
                daysOpenLabel.setText(Integer.toString(daysOpened));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(!t.getClosedDate().equals("") && !t.getOpenedDate().equals("")) {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            try {
                Date openedDate = df.parse(t.getOpenedDate());
                Date closedDate = df.parse(t.getClosedDate());
                int daysOpened = (int) ((closedDate.getTime() - openedDate.getTime()) / (1000 * 60 * 60 * 24));
                daysOpenLabel.setText(Integer.toString(daysOpened));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            daysOpenLabel.setText("");
        }
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
        String status = MainWindow.clientHandler.editTicket(t);
        if (status.equals(SUCCESSFUL)) {
            MainWindow.clientHandler.updateTicket(t.getId());
            tickets.set(ticketList.getSelectedIndex(), MainWindow.clientHandler.getTicket(t.getId()));
            Ticket temp = tickets.get(ticketList.getSelectedIndex());
            createModel();
        }
        else if (status.equals(FAILED)){
            JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Invalid fields entered. Please try again.");
        }
        else if (status.equals(OLD)) {
            JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Ticket has not been updated. Refresh ticket and try again.");
        }
        else {
            JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: You do not have the permissions to perform this operation.");
        }
    }

    private void hideEditProperties(){
        titleText.setEditable(false);
        descriptionTextPane.setEditable(false);
        resolutionTextPane.setEditable(false);

        priorityComboBox.setVisible(false);
        severityComboBox.setVisible(false);
        clientText.setVisible(false);
        assignedToText.setVisible(false);
    }

    private void showEditProperties(){
        titleText.setEditable(true);
        descriptionTextPane.setEditable(true);
        resolutionTextPane.setEditable(true);

        priorityComboBox.setVisible(true);
        severityComboBox.setVisible(true);
        clientText.setVisible(true);
        assignedToText.setVisible(true);
    }

    public static void createModel(){
        model.clear();
        tickets.clear();
        for (Ticket ticket: MainWindow.clientHandler.getAllTickets()){
            model.addElement(ticket.getTitle());
            tickets.add(ticket);
        }
    }

    private void searchAttribute(String a) {
        model.clear();
        tickets.clear();
        for (Ticket ticket: MainWindow.clientHandler.getAllTickets()){
            if (a.matches("Title")){
                model.addElement(ticket.getTitle());
                tickets.add(ticket);
            }
            else if (a.matches("Status")){
                model.addElement(ticket.getStatus());
                tickets.add(ticket);
            }
            else if (a.matches("Priority")){
                model.addElement(ticket.getPriority());
                tickets.add(ticket);
            }
            else if (a.matches("Severity")){
                model.addElement(ticket.getSeverity());
                tickets.add(ticket);
            }
            else if (a.matches("Client")){
                model.addElement(ticket.getClient());
                tickets.add(ticket);
            }
            else if (a.matches("Assigned to")){
                model.addElement(ticket.getAssignedTo());
                tickets.add(ticket);
            }
        }
    }

    private void removeFocus(){
        manageUsersButton.setFocusable(false);
        historyButton.setFocusable(false);
        searchTicketsButton.setFocusable(false);
        filter.setFocusable(false);
        signOutButton.setFocusable(false);
    }
}
