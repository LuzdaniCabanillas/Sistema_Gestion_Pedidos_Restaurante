
package MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL {
    
    private static final String URL = "jdbc:mysql://localhost:3306/sistemaGestionRestaurante";
    private static final String USUARIO = "root";
    private static final String CLAVE = "mysql";

    public static Connection obtenerConexion() throws SQLException {

        return DriverManager.getConnection(URL, USUARIO, CLAVE);

    }

    public static void main(String[] args) {
        try (Connection conexion = obtenerConexion()) {
            System.out.println("¡Conexión exitosa a MySQL!");
        } catch (SQLException e) {
            System.out.println("Error en la conexión: " + e.getMessage());
        }
    }
    
}
