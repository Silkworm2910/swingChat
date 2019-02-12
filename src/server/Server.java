package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    static final int PORT = 8080;
    private Map<String, ClientHandler> clients = new HashMap<>();
    public static List<String> users = new ArrayList<>();


    public Server() {

        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер начал работу");

            while (true) {
                clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, this);
                System.out.println(client.getDefaulteUserName() + " вошёл в чат");
                System.out.println("Клиентов в чате = " + ClientHandler.getClients_count());
                clients.put(client.getDefaulteUserName(), client);
                users.add(client.getDefaulteUserName());
                System.out.println("Пользователи он-лайн: " + Server.users.toString());
                new Thread(client).start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                clientSocket.close();
                System.out.println("Сервер завершил работу");
                serverSocket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public void sendMessageToAllClients(String msg) {
        for (ClientHandler client : clients.values()) {
            client.sendMsg(msg);
        }
    }


    public void sendMessageToUser(String msg, String author, String username) {

        ClientHandler authorClient = null;
        boolean isUserExist = false;

        for (Map.Entry<String, ClientHandler> clients : clients.entrySet()) {
            if (clients.getKey().equalsIgnoreCase(author)) {
                authorClient = clients.getValue();
            }
        }

        if (!author.equalsIgnoreCase(username)) {

            for (Map.Entry<String, ClientHandler> clients2 : clients.entrySet()) {
                if (clients2.getKey().equalsIgnoreCase(username)) {
                    isUserExist = true;
                    clients2.getValue().sendMsg(msg + "\n");
                    authorClient.sendMsg(msg + "\n");
                }
            }

            if (!isUserExist) {
                authorClient.sendMsg("Среди участников нет пользователя с именем  " + username + "\n");
            }
        } else {
            authorClient.sendMsg(username + " - это Вы\n");
        }
    }


    public void removeClient(ClientHandler client) {

        Iterator<Map.Entry<String, ClientHandler>> iterator = clients.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ClientHandler> entry = iterator.next();
            ClientHandler clientHandler = entry.getValue();
            if (clientHandler.equals(client)) {
                System.out.println(client.getDefaulteUserName() + " покинул чат");
                iterator.remove();
            }

        }

        System.out.println("Клиентов в чате = " + (ClientHandler.getClients_count()-1));

    }

    public void removeUser(String user) {

        Iterator<String> iterator = users.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (s.equals(user)) {
                iterator.remove();
            }

        }

        System.out.println("Пользователи он-лайн: " + Server.users.toString());

    }

}