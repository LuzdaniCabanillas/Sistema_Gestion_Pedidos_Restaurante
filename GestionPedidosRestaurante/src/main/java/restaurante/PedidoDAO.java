package restaurante;
import MySQL.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//DAO Data Access Object "Objeto encargado de acceder a los datos."
public class PedidoDAO {
    public boolean actualizarEstado(int idPedido, EstadoPedido estado) {
        String sql = "UPDATE Pedido SET estado = ? WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, estado.name());
            sentencia.setInt(2, idPedido);

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el estado del pedido: " + e.getMessage());
            return false;
        }
    }
}
