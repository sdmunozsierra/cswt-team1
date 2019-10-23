import cswt.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static client.ClientHandler.FAILED;
import static client.ClientHandler.SUCCESSFUL;

public class UserManagementWindow {
    public JPanel mainScreen;
    private JPanel list;
    private JPanel userValues;
    private JList userList;
    private JButton editButton;
    private JTextField nameText;
    private JTextField usernameText;
    private JComboBox type;
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
        clear();
        edit.setVisible(false);
        createModel();
        userList.setModel(model);

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (userList.getSelectedValue() != null){
                    User user = users.get(userList.getSelectedIndex());
                    MainWindow.clientHandler.deleteUser(user.getUsername());
                    createModel();
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editModeON();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clear();
                editModeOFF();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boolean empty = nameText.getText().isEmpty() || usernameText.getText().isEmpty() || passwordText.getText().isEmpty() || emailText.getText().isEmpty();

                if (!empty){

                    String result = MainWindow.clientHandler.createAccount(usernameText.getText(), passwordText.getText(), type.getSelectedItem().toString(), nameText.getText(), emailText.getText());
                    if (result.equals(SUCCESSFUL)) {
                        createModel();
                        userList.setModel(model);
                        clear();
                        editModeOFF();
                    }
                    else if (result.equals(FAILED)) {
                        JOptionPane.showMessageDialog(mainScreen, "Error: Unable to create user. Please try again later.");
                    }
                    else {
                        JOptionPane.showMessageDialog(mainScreen, "Error: You do not have the permissions to perform this operation.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(mainScreen, "Error: All fields need to be populated in order to create user.");
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (userList.getSelectedValue() != null){
                    editModeON();
                    setEditPropertiesText();
                }else {
                    JOptionPane.showMessageDialog(mainScreen, "Error: Select user to edit.");
                }
            }
        });
    }

    private void editModeON(){
        list.setVisible(false);
        edit.setVisible(true);
    }


    private void editModeOFF(){
        list.setVisible(true);
        edit.setVisible(false);
    }

    private void setEditPropertiesText(){
        User user = users.get(userList.getSelectedIndex());

        nameText.setText(user.getActualName());
        usernameText.setText(user.getUsername());
        passwordText.setText(user.getPassword());
        emailText.setText(user.getEmail());
        type.setSelectedItem(user.getType());
    }

    private void clear(){
        nameText.setText("");
        usernameText.setText("");
        passwordText.setText("");
        emailText.setText("");
        type.setSelectedItem(type.getItemAt(0));
    }

    private void createModel(){
        model.clear();
        users.clear();
        for (User user: MainWindow.clientHandler.getAllUsers()){
            model.addElement(user.getUsername());
            users.add(user);
        }
    }

}
