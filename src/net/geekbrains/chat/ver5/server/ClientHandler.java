package net.geekbrains.chat.ver5.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {

    private Server server;
    private Socket socket;
    DataInputStream in;
    DataOutputStream out;
    String nik = null;
    List<String> blackList; // список заблокированных пользователей

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blackList = new ArrayList<String>();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            // аутентификация
                            if (str.startsWith("/auth")) {
                                String[] token = str.split(" "); // разобъем строку по пробелам
                                String newNik = AuthService.getNickByLoginAndPass(token[1], token[2]);
                                if (newNik != null) {
                                    if (!server.isNickBusy(newNik)) {
                                        sendMsg("/authOk");
                                        nik = newNik;
                                        server.subscribe(ClientHandler.this); // делаем подписку
                                        break;
                                    } else {
                                        sendMsg("Данная учетная запись уже используется");
                                    }
                                } else {
                                    sendMsg("Неверный логин/пароль");
                                }
                            }
                        }
                        while (true) {
                            String str = in.readUTF();
             /* так как со временем количество сервисных команд может вырасти вынесем их в отдельный блок, говорящий
                о принадлежности всех наших команд к служебным
             */
                            if (str.startsWith("/")) {
                                if (str.equals("/end")) {
                                    out.writeUTF("/serverclosed");
                                    break;
                                }

                                // отправка личного сообщения
                                if (str.startsWith("/w ")) {
                                    String[] tokens = str.split(" ", 3);
                                    server.sendPersonalMsg(ClientHandler.this, tokens[1], tokens[2]);

                                }

                                // добавление клиента в черный список
                                if (str.startsWith("/blacklist ")) {
                                    String[] tokens = str.split(" ");
                                    blackList.add(tokens[1]);
                                    sendMsg("Вы добавили пользователя " + tokens[1] + " в черный список");
                                }

                            } else {
                                server.broadcastMsg(ClientHandler.this, nik + ": " + str);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.unsubscribe(ClientHandler.this);
                    }
                }
            });
            thread.setDaemon(true); // для завершения потока при закрытии окна клиента
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) { // реализуем отправку сообщения клиенту
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNik() {
        return nik;
    }

   public boolean checkBlackList (String nick) {
        return blackList.contains(nick);
   }
}