package service;

/**
 * SERGIO
 */
import controlador.PagoDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import modelo.Pago;

public class PagoService {

    private final PagoDAO pagoDAO;

    public PagoService() {
        this.pagoDAO = new PagoDAO();
    }

    // REGISTRAR PAGO EN EFECTIVO
    public int registrarPagoEfectivo(
            int cuentaId,
            BigDecimal total,
            BigDecimal montoRecibido,
            String modalidadPago,
            Integer mozoId,
            Integer cajeroId) throws SQLException {

        BigDecimal[] importes = validarImportes(total, montoRecibido);

        total = importes[0];
        montoRecibido = importes[1];

        BigDecimal vuelto = montoRecibido.subtract(total)
                .setScale(2, RoundingMode.HALF_UP);

        Pago pago = crearPagoBase(
                cuentaId, total, "EFECTIVO",
                modalidadPago, mozoId, cajeroId
        );

        pago.setEstadoPago("CONFIRMADO");
        pago.setMontoRecibido(
                montoRecibido.setScale(2, RoundingMode.HALF_UP)
        );
        pago.setVuelto(vuelto);

        pago.setObservacion("Pago en efectivo registrado.");

        return pagoDAO.registrarIntento(pago);
    }

    // REGISTRAR EL RESULTADO DE UN INTENTO CON TARJETA.
    // "aprobadaPorProveedor" solo debe ser true si existe
    // una confirmación real del proveedor de pagos.
    public int registrarResultadoTarjeta(
            int cuentaId,
            BigDecimal total,
            String modalidadPago,
            Integer mozoId,
            Integer cajeroId,
            boolean aprobadaPorProveedor,
            String referencia) throws SQLException {

        if (total == null || total.signum() <= 0) {
            throw new IllegalArgumentException(
                    "El total de la cuenta no es válido."
            );
        }

        if (aprobadaPorProveedor
                && (referencia == null || referencia.isBlank())) {
            throw new IllegalArgumentException(
                    "Se requiere una referencia de la transacción aprobada."
            );
        }

        Pago pago = crearPagoBase(
                cuentaId, total, "TARJETA",
                modalidadPago, mozoId, cajeroId
        );

        pago.setEstadoPago(
                aprobadaPorProveedor ? "CONFIRMADO" : "RECHAZADO"
        );
        pago.setReferencia(referencia);

        pago.setObservacion(
                aprobadaPorProveedor
                        ? "Transacción confirmada por el proveedor."
                        : "Transacción no aprobada o no confirmada."
        );

        return pagoDAO.registrarIntento(pago);
    }

    // HISTORIAL DE INTENTOS DE UNA CUENTA
    public java.util.List<Pago> listarPorCuenta(int cuentaId)
            throws SQLException {

        if (cuentaId <= 0) {
            throw new IllegalArgumentException(
                    "Seleccione una cuenta válida."
            );
        }

        return pagoDAO.listarPorCuenta(cuentaId);
    }

    private Pago crearPagoBase(
            int cuentaId,
            BigDecimal total,
            String metodo,
            String modalidad,
            Integer mozoId,
            Integer cajeroId) {

        if (cuentaId <= 0) {
            throw new IllegalArgumentException(
                    "La cuenta seleccionada no es válida."
            );
        }

        if (total == null || total.signum() <= 0) {
            throw new IllegalArgumentException(
                    "El total debe ser mayor que cero."
            );
        }

        if (modalidad == null
                || (!"DIRECTO_MOZO".equalsIgnoreCase(modalidad)
                && !"EN_CAJA".equalsIgnoreCase(modalidad))) {
            throw new IllegalArgumentException(
                    "Seleccione la modalidad del pago."
            );
        }

        boolean directo = "DIRECTO_MOZO".equalsIgnoreCase(modalidad);

        if (directo && (mozoId == null || mozoId <= 0
                || cajeroId != null)) {
            throw new IllegalArgumentException(
                    "Identifique al mozo que recibe el pago."
            );
        }

        if (!directo && (cajeroId == null || cajeroId <= 0
                || mozoId != null)) {
            throw new IllegalArgumentException(
                    "Identifique al cajero que recibe el pago."
            );
        }

        Pago pago = new Pago();
        pago.setFechaHora(LocalDateTime.now());
        pago.setCuentaId(cuentaId);
        pago.setMonto(total.setScale(2, RoundingMode.HALF_UP));
        pago.setMetodoPago(metodo);
        pago.setModalidadPago(modalidad.toUpperCase());
        pago.setMozoId(mozoId);
        pago.setCajeroId(cajeroId);

        return pago;
    }

    private BigDecimal[] validarImportes(
            BigDecimal total,
            BigDecimal recibido) {

        if (total == null || total.signum() <= 0) {
            throw new IllegalArgumentException(
                    "El total de la cuenta no es válido."
            );
        }

        if (recibido == null || recibido.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Ingrese el monto recibido."
            );
        }

        BigDecimal totalRedondeado
                = total.setScale(2, RoundingMode.HALF_UP);

        BigDecimal recibidoRedondeado
                = recibido.setScale(2, RoundingMode.HALF_UP);

        if (recibidoRedondeado.compareTo(totalRedondeado) < 0) {
            throw new IllegalArgumentException(
                    "El monto recibido es menor que el total de la cuenta."
            );
        }

        return new BigDecimal[]{
            totalRedondeado,
            recibidoRedondeado
        };
    }

}
