
package MySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMySQL {
    
    public static void main(String[] args) {
        // estos son los Datos de conexión
        String url = "jdbc:mysql://localhost:3306/sistemaGestionRestaurante";
        String usuario = "root";
        String clave = "mysql";

        try {
            // aqui Establecemos la conexión
            Connection conn = DriverManager.getConnection(url, usuario, clave);
            System.out.println("¡Conexión exitosa a MySQL!");
            
            // aqui se cierra la conexión
            conn.close(); 
        } catch (SQLException e) {
            System.out.println("Error en la conexión: " + e.getMessage());
        }
    }
    
}
