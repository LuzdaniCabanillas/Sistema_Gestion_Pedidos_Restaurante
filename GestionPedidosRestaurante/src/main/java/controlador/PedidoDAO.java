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

    //Insertar
    public int insertar(Pedido pedido) throws SQLException {
        String sql = """ 
                     INSERT INTO Pedido (fechaHora, estado, subTotal, mesa_id, mozo_id) VALUES (?, ?, ?, ?, ?) 
                     """;
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(pedido.getFechaHora()));
            ps.setString(2, pedido.getEstado());
            ps.setDouble(3, pedido.getSubTotal());
            ps.setInt(4, pedido.getMesa().getId());
            ps.setInt(5, pedido.getMozo().getId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    pedido.setId(idGenerado);
                    return idGenerado;
                }
            }
        }
        return 0;
    }

    //Listar
    public List<Pedido> listar() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
                     SELECT p.id, p.fechaHora, p.estado, p.subTotal, m.id AS mesa_id, m.numero, m.capacidad, m.estado AS mesa_estado, mo.id AS mozo_id, e.nombre AS mozo_nombre, e.dni, e.turno FROM Pedido p INNER JOIN Mesa m ON p.mesa_id = m.id INNER JOIN Mozo mo ON p.mozo_id = mo.id INNER JOIN Empleado e ON mo.id = e.id ORDER BY p.id DESC """;
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pedido pedido = convertirPedido(rs);
                lista.add(pedido);
            }
        }
        return lista;
    }

    //Buscar por ID
    public Pedido buscarPorId(int id) throws SQLException {
        String sql = """ 
                     SELECT p.id, p.fechaHora, p.estado, p.subTotal, m.id AS mesa_id, m.numero, m.capacidad, m.estado AS mesa_estado, mo.id AS mozo_id, e.nombre AS mozo_nombre, e.dni, e.turno FROM Pedido p INNER JOIN Mesa m ON p.mesa_id = m.id INNER JOIN Mozo mo ON p.mozo_id = mo.id INNER JOIN Empleado e ON mo.id = e.id WHERE p.id = ? """;
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertirPedido(rs);
                }
            }
        }
        return null;
    }

    //Actualizar
    public void actualizar(Pedido pedido) throws SQLException {
        String sql = """ 
                     UPDATE Pedido SET fechaHora = ?, estado = ?, subTotal = ?, mesa_id = ?, mozo_id = ? WHERE id = ? """;
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(pedido.getFechaHora()));
            ps.setString(2, pedido.getEstado());
            ps.setDouble(3, pedido.getSubTotal());
            ps.setInt(4, pedido.getMesa().getId());
            ps.setInt(5, pedido.getMozo().getId());
            ps.setInt(6, pedido.getId());
            ps.executeUpdate();
        }
    }

    //Eliminar
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Pedido WHERE id = ?";
        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    //Construir Objeto Pedido
    private Pedido convertirPedido(ResultSet rs) throws SQLException {
        Mesa mesa = new Mesa();
        mesa.setId(rs.getInt("mesa_id"));
        mesa.setNumero(rs.getInt("numero"));
        mesa.setCapacidad(rs.getInt("capacidad"));
        mesa.setEstado(rs.getString("mesa_estado"));
        Mozo mozo = new Mozo();
        mozo.setId(rs.getInt("mozo_id"));
        mozo.setNombre(rs.getString("mozo_nombre"));
        mozo.setDni(rs.getString("dni"));
        mozo.setTurno(rs.getString("turno"));
        Pedido pedido = new Pedido();
        pedido.setId(rs.getInt("id"));
        pedido.setFechaHora(rs.getTimestamp("fechaHora").toLocalDateTime());
        pedido.setEstado(rs.getString("estado"));
        pedido.setSubTotal(rs.getDouble("subTotal"));
        pedido.setMesa(mesa);
        pedido.setMozo(mozo);
        return pedido;
    }

}
