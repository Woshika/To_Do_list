package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    //Apply singleton --> 3 steps
    private static DBConnection dbConnection; //1. Create class attribute
    private Connection connection;

    private DBConnection() throws ClassNotFoundException, SQLException {// 2. create constructor

        Class.forName("com.mysql.jdbc.Driver");

       connection =  DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist","root","");
    }

    public static DBConnection getInstance() throws ClassNotFoundException, SQLException {   //3. create return type - class name(DBConnection) , name - getInstance()

        return (dbConnection == null) ? dbConnection = new DBConnection() : dbConnection;
    }

    public Connection getConnection(){
        return connection;
    }
}
