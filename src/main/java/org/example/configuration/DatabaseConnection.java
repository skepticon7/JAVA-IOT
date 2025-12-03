package org.example.configuration;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private final String URL = "jdbc:mysql://localhost:3306/iotdb";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    private DatabaseConnection() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL , USER ,PASSWORD);
        }catch (Exception e){
            throw new RuntimeException("Failed to connect to the database : " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if(instance == null)
            instance = new DatabaseConnection();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

}
