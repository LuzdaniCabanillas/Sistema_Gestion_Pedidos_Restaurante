package controlador;

/**
 * Sergio
 */
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import modelo.Pago;
import utilitarios.ConexionMySQL;

public class PagoDAO {

    public int registrarIntento(Pago pago) throws SQLException {
        validarDatos(pago);

        String sqlCuenta = """
            SELECT estado, total
            FROM Cuenta
            WHERE id = ?
            FOR UPDATE
            """;

        String sqlPagoConfirmado = """
            SELECT id
            FROM Pago
            WHERE cuenta_id = ?
              AND estadoPago = 'CONFIRMADO'
            LIMIT 1
            """;

        String sqlInsertar = """
            INSERT INTO Pago (
                fechaHora, metodoPago, monto, cuenta_id,
                mozo_id, cajero_id, modalidadPago, estadoPago,
                montoRecibido, vuelto, referencia, observacion
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        String sqlActualizarCuenta = """
            UPDATE Cuenta
            SET estado = 'PAGADO',
                metodoPago = ?,
                fechaPago = CURRENT_TIMESTAMP
            WHERE id = ?
              AND estado = 'PENDIENTE'
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion()) {
            boolean autocommitOriginal = conexion.getAutoCommit();

            try {
                conexion.setAutoCommit(false);

                BigDecimal totalCuenta;

                try (PreparedStatement ps
                        = conexion.prepareStatement(sqlCuenta)) {

                    ps.setInt(1, pago.getCuentaId());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "La cuenta seleccionada no existe."
                            );
                        }

                        if (!"PENDIENTE".equalsIgnoreCase(
                                rs.getString("estado"))) {
                            throw new IllegalStateException(
                                    "La cuenta ya no está pendiente de pago."
                            );
                        }

                        totalCuenta = rs.getBigDecimal("total");
                    }
                }

                if (pago.getMonto().compareTo(totalCuenta) != 0) {
                    throw new IllegalArgumentException(
                            "El monto del pago no coincide con el total de la cuenta."
                    );
                }

                if ("CONFIRMADO".equalsIgnoreCase(
                        pago.getEstadoPago())) {

                    try (PreparedStatement ps
                            = conexion.prepareStatement(sqlPagoConfirmado)) {

                        ps.setInt(1, pago.getCuentaId());

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                throw new IllegalStateException(
                                        "Esta cuenta ya tiene un pago confirmado."
                                );
                            }
                        }
                    }
                }

                try (PreparedStatement ps = conexion.prepareStatement(
                        sqlInsertar,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

                    ps.setTimestamp(1, Timestamp.valueOf(
                            pago.getFechaHora() == null
                            ? java.time.LocalDateTime.now()
                            : pago.getFechaHora()
                    ));
                    ps.setString(2, pago.getMetodoPago());
                    ps.setBigDecimal(3, pago.getMonto());
                    ps.setInt(4, pago.getCuentaId());

                    if (pago.getMozoId() != null) {
                        ps.setInt(5, pago.getMozoId());
                    } else {
                        ps.setNull(5, java.sql.Types.INTEGER);
                    }

                    if (pago.getCajeroId() != null) {
                        ps.setInt(6, pago.getCajeroId());
                    } else {
                        ps.setNull(6, java.sql.Types.INTEGER);
                    }

                    ps.setString(7, pago.getModalidadPago());
                    ps.setString(8, pago.getEstadoPago());

                    if (pago.getMontoRecibido() != null) {
                        ps.setBigDecimal(9, pago.getMontoRecibido());
                    } else {
                        ps.setNull(9, java.sql.Types.DECIMAL);
                    }

                    if (pago.getVuelto() != null) {
                        ps.setBigDecimal(10, pago.getVuelto());
                    } else {
                        ps.setNull(10, java.sql.Types.DECIMAL);
                    }
                    ps.setString(11, pago.getReferencia());
                    ps.setString(12, pago.getObservacion());

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID del pago."
                            );
                        }

                        pago.setId(rs.getInt(1));
                    }
                }

                if ("CONFIRMADO".equalsIgnoreCase(
                        pago.getEstadoPago())) {

                    try (PreparedStatement ps
                            = conexion.prepareStatement(sqlActualizarCuenta)) {

                        ps.setString(1, pago.getMetodoPago());
                        ps.setInt(2, pago.getCuentaId());

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "No se pudo actualizar el estado de la cuenta."
                            );
                        }
                    }
                }

                conexion.commit();
                return pago.getId();

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

    public List<Pago> listarPorCuenta(int cuentaId)
            throws SQLException {

        List<Pago> lista = new ArrayList<>();

        String sql = """
            SELECT id, fechaHora, metodoPago, monto, cuenta_id,
                   mozo_id, cajero_id, modalidadPago, estadoPago,
                   montoRecibido, vuelto, referencia, observacion
            FROM Pago
            WHERE cuenta_id = ?
            ORDER BY id DESC
            """;

        try (Connection conexion = ConexionMySQL.obtenerConexion(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, cuentaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pago pago = new Pago();

                    pago.setId(rs.getInt("id"));

                    Timestamp fecha = rs.getTimestamp("fechaHora");
                    if (fecha != null) {
                        pago.setFechaHora(fecha.toLocalDateTime());
                    }

                    pago.setMetodoPago(rs.getString("metodoPago"));
                    pago.setMonto(rs.getBigDecimal("monto"));
                    pago.setCuentaId(rs.getInt("cuenta_id"));

                    int mozoId = rs.getInt("mozo_id");
                    pago.setMozoId(rs.wasNull() ? null : mozoId);

                    int cajeroId = rs.getInt("cajero_id");
                    pago.setCajeroId(rs.wasNull() ? null : cajeroId);

                    pago.setModalidadPago(rs.getString("modalidadPago"));
                    pago.setEstadoPago(rs.getString("estadoPago"));
                    pago.setMontoRecibido(rs.getBigDecimal("montoRecibido"));
                    pago.setVuelto(rs.getBigDecimal("vuelto"));
                    pago.setReferencia(rs.getString("referencia"));
                    pago.setObservacion(rs.getString("observacion"));

                    lista.add(pago);
                }
            }
        }

        return lista;
    }

    private void validarDatos(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException(
                    "Los datos del pago son obligatorios."
            );
        }

        if (pago.getCuentaId() <= 0) {
            throw new IllegalArgumentException(
                    "La cuenta no es válida."
            );
        }

        if (pago.getMonto() == null
                || pago.getMonto().signum() <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero."
            );
        }

        if (pago.getMetodoPago() == null
                || (!"EFECTIVO".equalsIgnoreCase(pago.getMetodoPago())
                && !"TARJETA".equalsIgnoreCase(pago.getMetodoPago()))) {
            throw new IllegalArgumentException(
                    "El método debe ser EFECTIVO o TARJETA."
            );
        }

        if (pago.getModalidadPago() == null
                || (!"DIRECTO_MOZO".equalsIgnoreCase(
                        pago.getModalidadPago())
                && !"EN_CAJA".equalsIgnoreCase(
                        pago.getModalidadPago()))) {
            throw new IllegalArgumentException(
                    "Seleccione pago directo al mozo o pago en caja."
            );
        }

        boolean directo = "DIRECTO_MOZO".equalsIgnoreCase(
                pago.getModalidadPago());

        if (directo && (pago.getMozoId() == null
                || pago.getCajeroId() != null)) {
            throw new IllegalArgumentException(
                    "El pago directo debe identificar solo al mozo."
            );
        }

        if (!directo && (pago.getCajeroId() == null
                || pago.getMozoId() != null)) {
            throw new IllegalArgumentException(
                    "El pago en caja debe identificar solo al cajero."
            );
        }

        if (pago.getEstadoPago() == null
                || (!"PENDIENTE".equalsIgnoreCase(pago.getEstadoPago())
                && !"CONFIRMADO".equalsIgnoreCase(pago.getEstadoPago())
                && !"RECHAZADO".equalsIgnoreCase(pago.getEstadoPago()))) {
            throw new IllegalArgumentException(
                    "El estado del pago no es válido."
            );
        }

        if ("EFECTIVO".equalsIgnoreCase(pago.getMetodoPago())) {
            if (!"CONFIRMADO".equalsIgnoreCase(pago.getEstadoPago())) {
                throw new IllegalArgumentException(
                        "Un pago en efectivo debe validarse antes de registrarse."
                );
            }

            if (pago.getMontoRecibido() == null
                    || pago.getMontoRecibido().compareTo(
                            pago.getMonto()) < 0) {
                throw new IllegalArgumentException(
                        "El monto recibido no cubre el total."
                );
            }

            BigDecimal vueltoEsperado = pago.getMontoRecibido()
                    .subtract(pago.getMonto());

            if (pago.getVuelto() == null
                    || pago.getVuelto().compareTo(vueltoEsperado) != 0) {
                throw new IllegalArgumentException(
                        "El vuelto calculado no coincide."
                );
            }
        }
    }
}
