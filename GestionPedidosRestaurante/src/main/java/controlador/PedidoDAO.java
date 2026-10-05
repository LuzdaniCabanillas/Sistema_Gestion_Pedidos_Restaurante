package controlador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import modelo.Mesa;
import modelo.Mozo;
import modelo.Pedido;
import utilitarios.ConexionMySQL;

public class PedidoDAO {
    public boolean insertar(Pedido pedido) {

        String sql = "INSERT INTO Pedido " + "(mesa_id, mozo_id, estado, subTotal) " + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, pedido.getMesa().getId());
            ps.setInt(2, pedido.getMozo().getId());
            ps.setString(3, pedido.getEstado());
            ps.setDouble(4, pedido.getSubTotal());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {

            System.out.println("Error al insertar pedido: " + e.getMessage());
            return false;
        }
    }

    public List<Pedido> listar() {

        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT id, fechaHora, estado, subTotal, " + "mesa_id, mozo_id " + "FROM Pedido ORDER BY id DESC";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                Timestamp fecha = rs.getTimestamp("fechaHora");

                if (fecha != null) {
                    pedido.setFechaHora(fecha.toLocalDateTime());
                }

                pedido.setEstado(rs.getString("estado"));
                pedido.setSubTotal(rs.getDouble("subTotal"));
                Mesa mesa = new Mesa();
                mesa.setId(rs.getInt("mesa_id"));
                pedido.setMesa(mesa);
                Mozo mozo = new Mozo();
                mozo.setId(rs.getInt("mozo_id"));
                pedido.setMozo(mozo);
                lista.add(pedido);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pedidos: " + e.getMessage());
        }
        return lista;
    }

    public Pedido buscarPorId(int id) {

        String sql = "SELECT id, fechaHora, estado, subTotal, " + "mesa_id, mozo_id " + "FROM Pedido WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setId(rs.getInt("id"));
                    Timestamp fecha = rs.getTimestamp("fechaHora");

                    if (fecha != null) {
                        pedido.setFechaHora(fecha.toLocalDateTime());
                    }

                    pedido.setEstado(rs.getString("estado"));
                    pedido.setSubTotal(rs.getDouble("subTotal"));
                    Mesa mesa = new Mesa();
                    mesa.setId(rs.getInt("mesa_id"));
                    pedido.setMesa(mesa);
                    Mozo mozo = new Mozo();
                    mozo.setId(rs.getInt("mozo_id"));
                    pedido.setMozo(mozo);
                    return pedido;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar pedido: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(Pedido pedido) {

        String sql = "UPDATE Pedido " + "SET mesa_id = ?, " + "mozo_id = ?, " + "estado = ?, " + "subTotal = ? " + "WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, pedido.getMesa().getId());
            ps.setInt(2, pedido.getMozo().getId());
            ps.setString(3, pedido.getEstado());
            ps.setDouble(4, pedido.getSubTotal());
            ps.setInt(5, pedido.getId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar pedido: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int id, String estado) {

        String sql = "UPDATE Pedido " + "SET estado = ? " + "WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarSubTotal(int id, double subTotal) {

        String sql = "UPDATE Pedido " + "SET subTotal = ? " + "WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDouble(1, subTotal);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar subtotal: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {

        String sql = "DELETE FROM Pedido " + "WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar pedido: " + e.getMessage());
            return false;
        }
    }
}
