import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public UserManagementWindow(){
        edit.setVisible(false);

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
    }

}
