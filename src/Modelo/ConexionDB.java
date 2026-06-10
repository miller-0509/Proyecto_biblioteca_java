package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    public static Connection conexion;  
    
private ConexionDB() {
        try {
            String driverBD  = "com.mysql.cj.jdbc.Driver";
            String urlBD     = "jdbc:mysql://144.91.74.225:3307/sistemaclientes";
            String usuarioBD = "admin";
            String claveBD   = "12345678";
            Class.forName(driverBD);
            conexion = DriverManager.getConnection(urlBD, usuarioBD, claveBD);
        } catch (ClassNotFoundException ex) {
            System.err.println("No encuentra el driver:" + ex.getMessage());
        } catch (SQLException ex) {
            System.err.println("Error al conectarme:" + ex.getMessage());
        }
    }

    public static void desconectar() {
        try {
            conexion.close();
        } catch (SQLException ex) {
            System.err.println("Error al desconectarme" + ex.getMessage());
        }
    }

    public static ConexionDB getInstance() {
        return ConexionDBHolder.INSTANCE;
    }

    private static class ConexionDBHolder {
        private static final ConexionDB INSTANCE = new ConexionDB();
    }

}