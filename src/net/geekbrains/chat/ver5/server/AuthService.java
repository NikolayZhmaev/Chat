package net.geekbrains.chat.ver5.server;

import java.sql.*;

public class AuthService {

    private static Connection connection;
    private static Statement stmt;

    //метод подключения к БД
    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection( "jdbc:sqlite:userDB.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // поиск в БД по лоигину и паролю
    public static String getNickByLoginAndPass (String login, String pass) throws SQLException {
        String query = String.format("SELECT nickname FROM userTable WHERE LOGIN = '%s' and PASSWORD = '%s'", login, pass);
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            return rs.getString(1);
        }
        return null;
    }

    //метод отключения от БД
    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
