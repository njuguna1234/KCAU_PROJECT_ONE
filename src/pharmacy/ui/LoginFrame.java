package pharmacy.ui;

import pharmacy.dao.UserDao;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField username = new JTextField(18);
    private final JPasswordField password = new JPasswordField(18);

    public LoginFrame() {
        setTitle("Pharmacy Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());

        root.setBorder(
            BorderFactory.createEmptyBorder(24, 30, 24, 30)
        );
        root.setBackground(new Color(245, 248, 252));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 7, 7, 7);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel(
            "PHARMACY MANAGEMENT SYSTEM",
            SwingConstants.CENTER
        );

        title.setFont(
            new Font("SansSerif", Font.BOLD, 18)
        );

        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        root.add(title, g);

        g.gridwidth = 1;

        g.gridx = 0;
        g.gridy = 1;
        root.add(new JLabel("Username"), g);

        g.gridx = 1;
        root.add(username, g);

        g.gridx = 0;
        g.gridy = 2;
        root.add(new JLabel("Password"), g);

        g.gridx = 1;
        root.add(password, g);

        JButton login = new JButton("Login");
        login.addActionListener(e -> login());

        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 2;
        root.add(login, g);

        JLabel hint = new JLabel(
            "Demo: admin / admin123",
            SwingConstants.CENTER
        );

        hint.setForeground(Color.DARK_GRAY);

        g.gridy = 4;
        root.add(hint, g);

        setContentPane(root);
        getRootPane().setDefaultButton(login);
    }

    private void login() {
        String user = username.getText().trim();
        String pass = new String(password.getPassword());

        if (user.isBlank() || pass.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "Enter both username and password."
            );
            return;
        }

        try {
            if (new UserDao().authenticate(user, pass)) {
                dispose();
                new DashboardFrame(user).setVisible(true);

            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Incorrect username or password.",
                    "Login failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Could not connect to MySQL. " +
                "Check the database and connection settings.\n" +
                ex.getMessage(),
                "Database error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}