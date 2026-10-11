package controlador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import modelo.Cuenta;
import utilitarios.ConexionMySQL;

public class CuentaDAO {

    // Listar todas las cuentas
    public List<Cuenta> listar() throws SQLException {
        List<Cuenta> lista = new ArrayList<>();

        String sql = """
            SELECT id, fechaGeneracion, subTotal, igv, total,
                   estado, pedido_id, metodoPago, fechaPago
            FROM Cuenta
            ORDER BY id DESC
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(convertirCuenta(rs));
            }
        }

        return lista;
    }

    // Buscar una cuenta por su ID
    public Cuenta buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT id, fechaGeneracion, subTotal, igv, total,
                   estado, pedido_id, metodoPago, fechaPago
            FROM Cuenta
            WHERE id = ?
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertirCuenta(rs);
                }
            }
        }

        return null;
    }

    // Generar una cuenta para un pedido entregado
    public int generarCuenta(int pedidoId) throws SQLException {
        if (pedidoId <= 0) {
            throw new IllegalArgumentException(
                    "El ID del pedido no es válido."
            );
        }

        String sqlPedido = """
            SELECT estado, subTotal
            FROM Pedido
            WHERE id = ?
            FOR UPDATE
            """;

        String sqlCuentaExistente = """
                    SELECT id, estado
                    FROM Cuenta
                    WHERE pedido_id = ?
                    FOR UPDATE
                    """;

        String sqlActualizar = """
                                UPDATE Cuenta
                                SET subTotal = ?,
                                    igv = ?,
                                    total = ?
                                WHERE id = ?
                                  AND estado = 'PENDIENTE'
                                """;

        String sqlInsertar = """
            INSERT INTO Cuenta
                (fechaGeneracion, subTotal, igv, total,
                 estado, pedido_id)
            VALUES (?, ?, ?, ?, 'PENDIENTE', ?)
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion()) {
            boolean autocommitOriginal = conexion.getAutoCommit();

            try {
                conexion.setAutoCommit(false);

                BigDecimal subtotal;

                // Consultar y bloquear el pedido durante la operación
                try (PreparedStatement ps
                        = conexion.prepareStatement(sqlPedido)) {

                    ps.setInt(1, pedidoId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "El pedido no existe."
                            );
                        }

                        String estado = rs.getString("estado");

                        if (!"ENTREGADO".equalsIgnoreCase(estado)) {
                            throw new IllegalStateException(
                                    "Solo se puede generar una cuenta "
                                    + "para un pedido ENTREGADO."
                            );
                        }

                        subtotal = rs.getBigDecimal("subTotal");
                    }
                }

                if (subtotal == null || subtotal.signum() < 0) {
                    throw new IllegalStateException(
                            "El subtotal del pedido no es válido."
                    );
                }

                // El subtotal del pedido ya incluye el IGV.
                BigDecimal total = subtotal.setScale(
                        2, RoundingMode.HALF_UP
                );

// Extraer el subtotal sin IGV.
                BigDecimal subtotalSinIGV = total
                        .divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);

// Calcular el IGV incluido en el total.
                BigDecimal igv = total.subtract(subtotalSinIGV)
                        .setScale(2, RoundingMode.HALF_UP);

// Guardar el subtotal sin IGV en la cuenta.
                subtotal = subtotalSinIGV;

                try (PreparedStatement psExistente
                        = conexion.prepareStatement(sqlCuentaExistente)) {

                    psExistente.setInt(1, pedidoId);

                    try (ResultSet rsExistente
                            = psExistente.executeQuery()) {

                        if (rsExistente.next()) {
                            int cuentaId = rsExistente.getInt("id");
                            String estadoCuenta
                                    = rsExistente.getString("estado");

                            if (!"PENDIENTE".equalsIgnoreCase(
                                    estadoCuenta)) {
                                throw new IllegalStateException(
                                        "El pedido ya tiene una cuenta pagada. "
                                        + "No se puede generar otra."
                                );
                            }

                            try (PreparedStatement psActualizar
                                    = conexion.prepareStatement(
                                            sqlActualizar)) {

                                psActualizar.setBigDecimal(1, subtotal);
                                psActualizar.setBigDecimal(2, igv);
                                psActualizar.setBigDecimal(3, total);
                                psActualizar.setInt(4, cuentaId);
                                psActualizar.executeUpdate();
                            }

                            conexion.commit();
                            return cuentaId;
                        }
                    }
                }

                int idGenerado;

                try (PreparedStatement ps = conexion.prepareStatement(
                        sqlInsertar,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

                    ps.setDate(1, Date.valueOf(
                            java.time.LocalDate.now()
                    ));
                    ps.setBigDecimal(2, subtotal);
                    ps.setBigDecimal(3, igv);
                    ps.setBigDecimal(4, total);
                    ps.setInt(5, pedidoId);

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID de la cuenta."
                            );
                        }

                        idGenerado = rs.getInt(1);
                    }
                }

                conexion.commit();
                return idGenerado;

            } catch (SQLException | RuntimeException ex) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    ex.addSuppressed(errorRollback);
                }

                throw ex;

            } finally {
                try {
                    conexion.setAutoCommit(autocommitOriginal);
                } catch (SQLException ignored) {
                    // La conexión se cerrará al salir del try.
                }
            }
        }
    }

    // Actualizar los importes de la cuenta pendiente de un pedido.
    public void actualizarCuentaPendiente(int pedidoId)
            throws SQLException {

        if (pedidoId <= 0) {
            throw new IllegalArgumentException(
                    "El ID del pedido no es válido."
            );
        }

        String sqlPedido = """
            SELECT subTotal
            FROM Pedido
            WHERE id = ?
            """;

        String sqlCuenta = """
            UPDATE Cuenta
            SET subTotal = ?,
                igv = ?,
                total = ?
            WHERE pedido_id = ?
              AND estado = 'PENDIENTE'
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion()) {

            BigDecimal subtotal;

            // Obtener el subtotal actual del pedido.
            try (PreparedStatement ps
                    = conexion.prepareStatement(sqlPedido)) {

                ps.setInt(1, pedidoId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException(
                                "El pedido no existe."
                        );
                    }

                    subtotal = rs.getBigDecimal("subTotal");
                }
            }

            if (subtotal == null || subtotal.signum() < 0) {
                throw new IllegalStateException(
                        "El subtotal del pedido no es válido."
                );
            }

            // El subtotal del pedido ya incluye el IGV.
            BigDecimal total = subtotal.setScale(
                    2, RoundingMode.HALF_UP
            );

// Extraer el subtotal sin IGV.
            BigDecimal subtotalSinIGV = total
                    .divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);

// Calcular el IGV incluido en el total.
            BigDecimal igv = total.subtract(subtotalSinIGV)
                    .setScale(2, RoundingMode.HALF_UP);

// Guardar el subtotal sin IGV en la cuenta.
            subtotal = subtotalSinIGV;

            try (PreparedStatement ps
                    = conexion.prepareStatement(sqlCuenta)) {

                ps.setBigDecimal(1, subtotal);
                ps.setBigDecimal(2, igv);
                ps.setBigDecimal(3, total);
                ps.setInt(4, pedidoId);

                int filasActualizadas = ps.executeUpdate();

                if (filasActualizadas == 0) {
                    throw new IllegalStateException(
                            "No existe una cuenta pendiente para este pedido. "
                            + "Verifica si la cuenta ya fue pagada."
                    );
                }
            }
        }
    }

    // Registrar el pago de una cuenta
    public void registrarPago(
            int cuentaId, String metodoPago) throws SQLException {

        if (cuentaId <= 0) {
            throw new IllegalArgumentException(
                    "El ID de la cuenta no es válido."
            );
        }

        if (metodoPago == null
                || (!"EFECTIVO".equalsIgnoreCase(metodoPago)
                && !"TARJETA".equalsIgnoreCase(metodoPago))) {
            throw new IllegalArgumentException(
                    "El método de pago debe ser EFECTIVO o TARJETA."
            );
        }

        String sql = """
            UPDATE Cuenta
            SET estado = 'PAGADO',
                metodoPago = ?,
                fechaPago = CURRENT_TIMESTAMP
            WHERE id = ?
              AND estado = 'PENDIENTE'
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, metodoPago.toUpperCase());
            ps.setInt(2, cuentaId);

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new IllegalStateException(
                        "La cuenta no existe o ya no está pendiente de pago."
                );
            }
        }
    }

    // Convertir el resultado SQL en un objeto Cuenta
    private Cuenta convertirCuenta(ResultSet rs) throws SQLException {
        Cuenta cuenta = new Cuenta();

        cuenta.setId(rs.getInt("id"));

        Date fecha = rs.getDate("fechaGeneracion");
        if (fecha != null) {
            cuenta.setFechaGeneracion(fecha.toLocalDate());
        }

        cuenta.setSubTotal(rs.getBigDecimal("subTotal"));
        cuenta.setIGV(rs.getBigDecimal("IGV"));
        cuenta.setTotal(rs.getBigDecimal("total"));
        cuenta.setEstado(rs.getString("estado"));
        cuenta.setPedidoId(rs.getInt("pedido_id"));
        cuenta.setMetodoPago(rs.getString("metodoPago"));

        Timestamp fechaPago = rs.getTimestamp("fechaPago");
        if (fechaPago != null) {
            cuenta.setFechaPago(fechaPago.toLocalDateTime());
        }

        return cuenta;
    }

    // Buscar la cuenta asociada a un pedido.
    public Cuenta buscarPorPedidoId(int pedidoId) throws SQLException {

        String sql = """
            SELECT id, fechaGeneracion, subTotal, igv, total,
                   estado, pedido_id, metodoPago, fechaPago
            FROM Cuenta
            WHERE pedido_id = ?
            ORDER BY id DESC
            LIMIT 1
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertirCuenta(rs);
                }
            }
        }

        return null;
    }

}
