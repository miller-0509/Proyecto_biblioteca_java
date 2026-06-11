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

        Connection con = null;

        try {

            Class.forName("org.postgresql.Driver");

            con = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Conexión exitosa");

        } catch (ClassNotFoundException e) {

            System.out.println("Driver PostgreSQL no encontrado");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("Error al conectar");
            e.printStackTrace();
        }

        return con;
    }
}