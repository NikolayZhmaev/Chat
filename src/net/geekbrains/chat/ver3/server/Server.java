package net.geekbrains.chat.ver3.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clients; // реализуем рассылку с помощью коллекции клиентов

    public Server() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен!");
            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключен!");
                subscribe( new ClientHandler(this, socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(String msg) {
        for (ClientHandler o: clients) { // пробегаемся по коллекции и вызываем метод рассылки
            o.sendMsg(msg);
        }
    }

    // добавим методы по добавлению и удалению клиентов в список
    public void subscribe (ClientHandler client) {
        clients.add(client);
    }

    public void unsubscribe (ClientHandler client) {
        clients.remove(client);
    }

}
