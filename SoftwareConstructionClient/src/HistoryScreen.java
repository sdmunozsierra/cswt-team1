import cswt.Ticket;
import cswt.TicketSnapshot;
import server.TicketHistoryStorer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static client.ClientHandler.SUCCESSFUL;

public class HistoryScreen {
    public JPanel mainScreen;
    public JPanel historyViewManagement;
    public JComboBox filter;
    public JList historyList;
    private JList ticketHistoryList;
    private JLabel ticketName;
    private JPanel ticketHistory;
    private JButton backButton;
    private JTextField historySearchBar;

    private static List<Ticket> tickets = new ArrayList();
    private static DefaultListModel managementModel = new DefaultListModel();

    private static List<Ticket> history = new ArrayList();
    private static DefaultListModel historyModel = new DefaultListModel();


    public HistoryScreen(){
        ticketHistoryList.setModel(historyModel);

        createModel();
        historyList.setModel(managementModel);
        ticketHistory.setVisible(false);

        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() ==2 ){
                    Ticket t = tickets.get(historyList.getSelectedIndex());
                    String id = t.getId();
                    if ((MainWindow.clientHandler.collectTicketHistory(id)).equals(SUCCESSFUL)) {
                        List<TicketSnapshot> snapshots = MainWindow.clientHandler.getTicketHistory();
                        for (TicketSnapshot snapshot: snapshots) {
                            historyModel.addElement(snapshot.getDateModified() + " " + snapshot.getModifier() + " : " + snapshot.getWhatModified());
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(MainWindow.mainWindow, "Error: Unable to load history. Please try again later.");
                    }
                    ticketName.setText(t.getTitle());
                    showHistory();
                }
            }
        });

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                hideHistory();
            }
        });

        historySearchBar.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent arg0) { }
            public void insertUpdate(DocumentEvent arg0) { search(); }
            public void removeUpdate(DocumentEvent arg0)
            {
                search();
            }
        });
    }

    private static void createModel(){
        managementModel.clear();
        tickets.clear();
        for (Ticket ticket: MainWindow.clientHandler.getAllTickets()){
            managementModel.addElement(ticket.getTitle());
            tickets.add(ticket);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        ImageIcon img = new ImageIcon("back_arrow.png");
        Image image = img.getImage(); // transform it
        Image newImg = image.getScaledInstance(20, 10,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        img = new ImageIcon(newImg);
        backButton = new JButton(img);
        backButton.setFocusable(false);

    }

    private void showHistory(){
        ticketHistory.setVisible(true);
        historyViewManagement.setVisible(false);

    }

    private void hideHistory(){
        historyModel.clear();
        ticketHistory.setVisible(false);
        historyViewManagement.setVisible(true);
    }

    private void search() {
        DefaultListModel matching = new DefaultListModel();

        String word = historySearchBar.getText();
        for (int i = 0; i < historyModel.getSize(); i++){
            String temp = historyModel.elementAt(i).toString();

            if (temp.contains(word)){
                matching.addElement(historyModel.elementAt(i));
            }
        }
        ticketHistoryList.setModel(matching);
    }
}