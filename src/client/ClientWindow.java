package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientWindow extends JFrame {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket clientSocket;
    private Scanner inMessage;
    private PrintWriter outMessage;

    private JTextField jtfMessage;
    private JTextField jtfToWhom;
    private JTextArea jtaTextAreaMessage;
    private String clientName = "";


    public ClientWindow() {
        try {
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        setBounds(100, 100, 1000, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
        add(jsp, BorderLayout.CENTER);
        JLabel jlNumberOfClients = new JLabel("Количество клиентов в чате: ");
        add(jlNumberOfClients, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSendMessage = new JButton("Отправить");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMessage = new JTextField("Введите ваше сообщение: ");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        jtfToWhom = new JTextField("Кому:                       ");
        bottomPanel.add(jtfToWhom, BorderLayout.WEST);


        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                clientName = jtfToWhom.getText();
                if (!jtfMessage.getText().trim().isEmpty()) {
                        sendMsg();
                    jtfMessage.grabFocus();
                }

            }
        });

        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });
       jtfToWhom.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfToWhom.setText("");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (inMessage.hasNext()) {
                            String inMes = inMessage.nextLine();
                            String clientsInChat = "Клиентов в чате = ";
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {
                                jtaTextAreaMessage.append(inMes);
                                jtaTextAreaMessage.append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    outMessage.println("##exit##");
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException exc) {

                }
            }
        });

        setVisible(true);
    }


    public void sendMsg() {
        clientName = jtfToWhom.getText();

        if (clientName.isEmpty() || clientName.equals("Кому:                       ")) {
            outMessage.println(jtfMessage.getText());
        } else if (!clientName.isEmpty() && !clientName.toUpperCase().startsWith("USER")) {
            outMessage.println("user" + clientName + "          " + jtfMessage.getText());
        }

        else {
            outMessage.println(clientName + "          " + jtfMessage.getText());
        }
        outMessage.flush();
        jtfMessage.setText("");
    }

}