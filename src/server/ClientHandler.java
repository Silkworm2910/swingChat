package server;

import client.ClientWindow;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private Server server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private Socket clientSocket = null;
    private static int clients_count = 0;
    private static int id = 0;
    private String defaultUserName;

    public static int getClients_count() {
        return clients_count;
    }

    public String getDefaulteUserName() {
        return defaultUserName;
    }


    public ClientHandler(Socket socket, Server server) {
        try {
            clients_count++;
            id++;
            this.defaultUserName = "user" + id;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                server.sendMessageToUser("", this.defaultUserName, this.defaultUserName);
                server.sendMessageToAllClients(this.defaultUserName + " вошёл в чат!");
                server.sendMessageToAllClients("Клиентов в чате = " + clients_count);

                server.sendMessageToAllClients("Пользователи он-лайн: " );
                for (int i =0; i < Server.users.size(); i++) {
                    server.sendMessageToAllClients(Server.users.get(i));
                }
                server.sendMessageToAllClients("\n");


                break;
            }
            while (true) {
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    if (clientMessage.equals("##exit##")) {
                        server.sendMessageToAllClients(this.defaultUserName + " покинул чат!");
                        break;
                    }

                    if (clientMessage.toUpperCase().startsWith("USER")) {
                        String to = clientMessage.substring(0, 15).trim();
                        String msg = clientMessage.substring(15).trim();
                        System.out.println("Личное сообщение пользователю " + to + " от " +
                                this.getDefaulteUserName()+ ": " + msg);
                        server.sendMessageToUser("Личное сообщение пользователю " + to.toUpperCase() + " от " +
                                this.getDefaulteUserName().toUpperCase()+ ": " + msg, this.defaultUserName ,to);
                    } else {
                        System.out.println(this.getDefaulteUserName() + ": " + clientMessage);
                        server.sendMessageToAllClients(this.getDefaulteUserName().toUpperCase() + ": " + clientMessage);
                    }
                }

                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }

    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        server.removeClient(this);
        server.removeUser(this.getDefaulteUserName());
        clients_count--;
        server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
        server.sendMessageToAllClients("Пользователи он-лайн: " + Server.users.toString());
    }

}
