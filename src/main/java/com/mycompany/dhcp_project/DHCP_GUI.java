 package com.mycompany.dhcp_project;
 

 import javax.swing.*;
import java.awt.*;

public class DHCP_GUI extends JFrame {

    private DHCP_Server server = new DHCP_Server();

    public DHCP_GUI() {
        setTitle("DHCP Server GUI");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton startBtn = new JButton("Start Server");
        JButton addResBtn = new JButton("Add Reservation");

        JTextField macField = new JTextField();
        JTextField ipField = new JTextField();

        setLayout(new GridLayout(5, 1));
        add(startBtn);
        add(new JLabel("MAC:")); add(macField);
        add(new JLabel("IP:"));  add(ipField);
        add(addResBtn);

        startBtn.addActionListener(e -> new Thread(() -> server.start()).start());

        addResBtn.addActionListener(e -> {
            server.addStaticReservation(macField.getText(), ipField.getText());
            JOptionPane.showMessageDialog(this, "Reservation Added!");
        });
    }

    public static void main(String[] args) {
        new DHCP_GUI().setVisible(true);
    }
}
