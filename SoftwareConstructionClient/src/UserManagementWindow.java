import cswt.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class UserManagementWindow {
    public JPanel mainScreen;
    private JPanel list;
    private JPanel userValues;
    private JList userList;
    private JButton editButton;
    private JTextField nameText;
    private JTextField usernameText;
    private JComboBox comboBox1;
    private JButton cancelButton;
    private JTextField passwordText;
    private JTextField emailText;
    private JButton removeButton;
    private JButton addButton;
    private JButton saveButton;
    private JPanel edit;
    private static List<User> users = new ArrayList();
    private static DefaultListModel model = new DefaultListModel();

    public UserManagementWindow(){
        edit.setVisible(false);
        createModel();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                list.setVisible(false);
                edit.setVisible(true);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                edit.setVisible(false);
                list.setVisible(true);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                edit.setVisible(false);
                list.setVisible(true);
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                list.setVisible(false);
                edit.setVisible(true);
            }
        });
        userList.setModel(model);
    }

    public static void createModel(){
        model.clear();
        users.clear();
        for (User user: MainWindow.clientHandler.getAllUsers()){
            model.addElement(user.getUsername());
            users.add(user);
        }
    }

}
