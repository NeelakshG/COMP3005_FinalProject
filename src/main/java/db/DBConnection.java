package db;
import java.sql.*;

//instead of rerunning the connection in every method, every class uses the same connection
//also good coding practice as we abstract the connection
public class DBConnection {

    static String URL = "jdbc:postgresql://localhost:5432/temp";
    static String USER = "postgres";
    static String PASSWORD = "St20050510!0";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

