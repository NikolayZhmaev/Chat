package net.geekbrains.chat.ver5.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clients; // реализуем рассылку с помощью коллекции клиентов

    public Server() throws SQLException {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {
            AuthService.connect();
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен!");
            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключен!");
                new ClientHandler(this, socket);
                //  clients.add( new ClientHandler(this, socket));
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
            AuthService.disconnect();
        }
    }

    public void broadcastMsg(ClientHandler from, String msg) { // пробегаемся по коллекции и вызываем метод рассылки
        for (ClientHandler o : clients) {
            if (!o.checkBlackList(from.getNik())) // проверка на отсутствие клиента в черном списке
                o.sendMsg(msg);
        }
    }

    // добавим методы по добавлению и удалению клиентов в список
    public void subscribe(ClientHandler client) {
        clients.add(client);
        broadCastClientList();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        broadCastClientList();
    }

    //метод определяющий подключился ли ранее клиент с таким ником
    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNik().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    // метод отправки персонального сообщения
    public void sendPersonalMsg(ClientHandler from, String nikTo, String msg) {
        for (ClientHandler o : clients) {
            if (o.getNik().equals(nikTo) && !o.checkBlackList(from.getNik())) {
                o.sendMsg("from " + from.getNik() + ": " + msg);
                from.sendMsg("to " + nikTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Клиент с ником " + nikTo + " не подключен");
    }

    // метод рассылки клиенту всех подключенных к чату клиентов
    public void broadCastClientList() {
        StringBuilder sb = new StringBuilder(); //чтобы не плодить множество объектов воспользуемся StringBuilder и методом append()
        sb.append("/clientList ");
        for (ClientHandler o : clients) {
            sb.append(o.getNik() + " "); // собираем все ники в StringBuilder через пробелы, так как сплитаем по пробелам
        }
        String list = sb.toString();
        for (ClientHandler o : clients) {
            o.sendMsg(list);
        }
    }
}