package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL =
            "jdbc:postgresql://144.91.74.225:5434/master_db";

    private static final String USER = "admin";
    private static final String PASSWORD = "12345678Ab";

    public static Connection conectar() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            return con;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL no encontrado", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos: " + e.getMessage(), e);
        }
    }

    public static void getInstance() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}