package net.geekbrains.chat.ver1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyWindow extends JFrame {
    JTextArea jta;
    JTextField jtf;

    public MyWindow() {
        setTitle("Client");
        setBounds(800, 300, 400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        jtf = new JTextField();
        JButton jb = new JButton("Send message");
        bottomPanel.add(jtf, BorderLayout.CENTER);
        bottomPanel.add(jb, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        jta = new JTextArea();
        JScrollPane jsp = new JScrollPane(jta);
        add(jsp, BorderLayout.CENTER);
        jta.setEditable(false);
        setVisible(true);
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });
        jtf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });
    }

    public void sendMsg() {
        jta.append(jtf.getText() + "\n");
        jtf.setText("");
        jtf.requestFocus();
    }
}
