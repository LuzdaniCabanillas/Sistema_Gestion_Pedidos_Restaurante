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

    // Insertar un detalle de pedido.
    public int insertar(DetallePedido detalle) throws SQLException {
        String sql = """
            INSERT INTO DetallePedido
            (cantidad, precioUnitario, subTotal, pedido_id, plato_id, estado)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, detalle.getCantidad());
            ps.setDouble(2, detalle.getPrecioUnitario());
            ps.setDouble(3, detalle.getSubTotal());
            ps.setInt(4, detalle.getPedido().getId());
            ps.setInt(5, detalle.getPlato().getId());
            ps.setString(6, detalle.getEstado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    detalle.setId(idGenerado);
                    return idGenerado;
                }
            }
        }

        return 0;
    }

    // Listar los detalles de un pedido, incluyendo el estado individual.
    public List<DetallePedido> listarPorPedido(int pedidoId)
            throws SQLException {

        List<DetallePedido> lista = new ArrayList<>();

        String sql = """
            SELECT d.id, d.cantidad, d.precioUnitario, d.subTotal,
                   d.estado AS estadoDetalle,
                   d.pedido_id,
                   p.id AS plato_id, p.nombre, p.descripcion,
                   p.precio, p.categoria, p.estado AS estadoPlato,
                   p.stock, p.imagen
            FROM DetallePedido d
            INNER JOIN Plato p ON d.plato_id = p.id
            WHERE d.pedido_id = ?
            ORDER BY d.id
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedido detalle = crearDetalleDesdeResultado(rs);

                    Pedido pedido = new Pedido();
                    pedido.setId(rs.getInt("pedido_id"));
                    detalle.setPedido(pedido);

                    lista.add(detalle);
                }
            }
        }

        return lista;
    }

    // Buscar un detalle por su ID.
    public DetallePedido buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT d.id, d.cantidad, d.precioUnitario, d.subTotal,
                   d.estado AS estadoDetalle,
                   d.pedido_id,
                   p.id AS plato_id, p.nombre, p.descripcion,
                   p.precio, p.categoria, p.estado AS estadoPlato,
                   p.stock, p.imagen
            FROM DetallePedido d
            INNER JOIN Plato p ON d.plato_id = p.id
            WHERE d.id = ?
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DetallePedido detalle = crearDetalleDesdeResultado(rs);

                    Pedido pedido = new Pedido();
                    pedido.setId(rs.getInt("pedido_id"));
                    detalle.setPedido(pedido);

                    return detalle;
                }
            }
        }

        return null;
    }

    // Actualizar los datos generales del detalle, incluido su estado.
    public void actualizar(DetallePedido detalle) throws SQLException {
        String sql = """
            UPDATE DetallePedido
            SET cantidad = ?,
                precioUnitario = ?,
                subTotal = ?,
                plato_id = ?,
                estado = ?
            WHERE id = ?
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, detalle.getCantidad());
            ps.setDouble(2, detalle.getPrecioUnitario());
            ps.setDouble(3, detalle.getSubTotal());
            ps.setInt(4, detalle.getPlato().getId());
            ps.setString(5, detalle.getEstado());
            ps.setInt(6, detalle.getId());

            ps.executeUpdate();
        }
    }

    // Actualizar solamente el estado del plato seleccionado.
    public void actualizarEstado(DetallePedido detalle)
            throws SQLException {

        String sql = """
            UPDATE DetallePedido
            SET estado = ?
            WHERE id = ?
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, detalle.getEstado());
            ps.setInt(2, detalle.getId());

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new SQLException(
                        "No se encontró el detalle de pedido con ID "
                                + detalle.getId()
                );
            }
        }
    }

    // Eliminar un detalle específico.
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM DetallePedido WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Buscar un plato dentro de un pedido.
    public DetallePedido buscarPorPedidoYPlato(
            int pedidoId, int platoId) throws SQLException {

        String sql = """
            SELECT d.id, d.cantidad, d.precioUnitario, d.subTotal,
                   d.estado AS estadoDetalle,
                   d.pedido_id,
                   p.id AS plato_id, p.nombre, p.descripcion,
                   p.precio, p.categoria, p.estado AS estadoPlato,
                   p.stock, p.imagen
            FROM DetallePedido d
            INNER JOIN Plato p ON d.plato_id = p.id
            WHERE d.pedido_id = ? AND d.plato_id = ?
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);
            ps.setInt(2, platoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DetallePedido detalle = crearDetalleDesdeResultado(rs);

                    Pedido pedido = new Pedido();
                    pedido.setId(rs.getInt("pedido_id"));
                    detalle.setPedido(pedido);

                    return detalle;
                }
            }
        }

        return null;
    }

    // Construir un DetallePedido a partir de una fila de MySQL.
    private DetallePedido crearDetalleDesdeResultado(ResultSet rs)
            throws SQLException {

        Plato plato = new Plato();
        plato.setId(rs.getInt("plato_id"));
        plato.setNombre(rs.getString("nombre"));
        plato.setDescripcion(rs.getString("descripcion"));
        plato.setPrecio(rs.getDouble("precio"));
        plato.setCategoria(rs.getString("categoria"));
        plato.setEstado(rs.getString("estadoPlato"));
        plato.setStock(rs.getInt("stock"));
        plato.setImagen(rs.getString("imagen"));

        DetallePedido detalle = new DetallePedido();
        detalle.setId(rs.getInt("id"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getDouble("precioUnitario"));
        detalle.setSubTotal(rs.getDouble("subTotal"));
        detalle.setEstado(rs.getString("estadoDetalle"));
        detalle.setPlato(plato);

        return detalle;
    }
}