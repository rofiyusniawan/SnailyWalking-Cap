
package com.snailywalking.snailywalking;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Login - Snaily Walking Cap");
        setSize(420, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("SNAILY WALKING");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(55, 30, 310, 35);
        panel.add(title);

        JLabel subtitle = new JLabel("ERP Management System");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setBounds(115, 65, 200, 25);
        panel.add(subtitle);

        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(50, 110, 100, 25);
        panel.add(userLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(50, 135, 305, 30);
        panel.add(usernameField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(50, 175, 100, 25);
        panel.add(passLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(50, 200, 305, 30);
        panel.add(passwordField);

        JButton loginButton = new JButton("LOGIN");
        loginButton.setBounds(50, 250, 305, 35);
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            
        if (username.equals("admin") && password.equals("123")) {
        JOptionPane.showMessageDialog(this,
        "Login berhasil! Selamat datang, Admin.");

        dispose();
        new DashboardFrame().setVisible(true);

        } else {
        JOptionPane.showMessageDialog(this,
        "Username atau password salah!");
        }

        });

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
