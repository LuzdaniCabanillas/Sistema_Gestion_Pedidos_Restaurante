package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelo.DetallePedido;
import modelo.Pedido;
import modelo.Plato;
import utilitarios.ConexionMySQL;

public class DetallePedidoDAO {

    public boolean insertar(DetallePedido detalle) {

        String sql = "INSERT INTO DetallePedido " + "(cantidad, precioUnitario, subTotal, " + "pedido_id, plato_id) " + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, detalle.getCantidad());
            ps.setDouble(2, detalle.getPrecioUnitario());
            ps.setDouble(3, detalle.getSubTotal());
            ps.setInt(4, detalle.getPedido().getId());
            ps.setInt(5, detalle.getPlato().getId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar detalle: " + e.getMessage());
            return false;
        }
    }

    public List<DetallePedido> listar() {

        List<DetallePedido> lista = new ArrayList<>();
        String sql = "SELECT id, cantidad, precioUnitario, " + "subTotal, pedido_id, plato_id " + "FROM DetallePedido " + "ORDER BY id DESC";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(crearDetalle(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar detalles: " + e.getMessage());
        }
        return lista;
    }

    public List<DetallePedido> listarPorPedido(int pedidoId) {

        List<DetallePedido> lista = new ArrayList<>();
        String sql = "SELECT id, cantidad, precioUnitario, " + "subTotal, pedido_id, plato_id " + "FROM DetallePedido " + "WHERE pedido_id = ? " + "ORDER BY id";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(crearDetalle(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar detalles: " + e.getMessage());
        }
        return lista;
    }

    public DetallePedido buscarPorId(int id) {

        String sql = "SELECT id, cantidad, precioUnitario, " + "subTotal, pedido_id, plato_id " + "FROM DetallePedido " + "WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearDetalle(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar detalle: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(DetallePedido detalle) {

        String sql = "UPDATE DetallePedido " + "SET cantidad = ?, " + "precioUnitario = ?, " + "subTotal = ?, " + "pedido_id = ?, " + "plato_id = ? " + "WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, detalle.getCantidad());
            ps.setDouble(2, detalle.getPrecioUnitario());
            ps.setDouble(3, detalle.getSubTotal());
            ps.setInt(4, detalle.getPedido().getId());
            ps.setInt(5, detalle.getPlato().getId());
            ps.setInt(6, detalle.getId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar detalle: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM DetallePedido " + "WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar detalle: " + e.getMessage());
            return false;
        }
    }

    private DetallePedido crearDetalle(ResultSet rs) throws SQLException {
        DetallePedido detalle = new DetallePedido();
        detalle.setId(rs.getInt("id"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getDouble("precioUnitario"));
        detalle.setSubTotal(rs.getDouble("subTotal"));
        Pedido pedido = new Pedido();
        pedido.setId(rs.getInt("pedido_id"));
        detalle.setPedido(pedido);
        Plato plato = new Plato();
        plato.setId(rs.getInt("plato_id"));
        detalle.setPlato(plato);
        return detalle;
    }
}