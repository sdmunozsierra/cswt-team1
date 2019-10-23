import cswt.Ticket;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class HistoryScreen {
    public JPanel mainScreen;
    public JPanel historyViewManagement;
    public JComboBox filter;
    public JList historyList;
    private JList list1;
    private JLabel ticketName;
    private JComboBox comboBox1;

    private static List<Ticket> tickets = new ArrayList();
    private static DefaultListModel model = new DefaultListModel();

    public HistoryScreen(){
        createModel();
        historyList.setModel(model);

        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() ==2 ){
                    System.out.println(historyList.getSelectedIndex());
                }
            }
        });

    }

    private static void createModel(){
        model.clear();
        tickets.clear();
        for (Ticket ticket: MainWindow.clientHandler.getAllTickets()){
            model.addElement(ticket.getTitle());
            tickets.add(ticket);
        }
    }
}
/*
    Create Ticket
    Open Ticket
    Close Ticket
    Mark Ticket as fixed
    Reject Ticket
    Edit Ticket
    Search Ticket
    Update All Tickets
    Get Ticket
    Create Account
    Validate User
    Edit User
    Delete User
    Update All Users
    Get All Users
    Get User
*/