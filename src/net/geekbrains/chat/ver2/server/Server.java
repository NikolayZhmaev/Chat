package net.geekbrains.chat.ver2.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(8189); // инициализируем сервер и указываем порт
            System.out.println("Сервер запущен!");

            socket = server.accept(); // сработает в момент подключения клиента
            System.out.println("Клиент подключен!");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

//            Scanner scanner = new Scanner(socket.getInputStream()); // говорим сканеру читать входящий поток
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // эхосервер (перегруженный конструктор true, для чистки сообщений, чтобы не копились)

            while (true) { // бесконечный цикл, чтобы слушать клиента
                String str = in.readUTF();

                if (str.equals("/end")) { // для прекращения работы сервера
                    break;
                }
                System.out.println("Client: " + str);
                out.writeUTF(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
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
}
