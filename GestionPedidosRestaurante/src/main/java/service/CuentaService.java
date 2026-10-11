
package service;

import controlador.CuentaDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Cuenta;
public class CuentaService {


    private final CuentaDAO cuentaDAO;

    public CuentaService() {
        this.cuentaDAO = new CuentaDAO();
    }

    // LISTAR TODAS LAS CUENTAS
    public List<Cuenta> listarCuentas() throws SQLException {
        return cuentaDAO.listar();
    }

    // BUSCAR UNA CUENTA POR ID
    public Cuenta buscarCuenta(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                "Seleccione una cuenta válida."
            );
        }

        return cuentaDAO.buscarPorId(id);
    }

    // LISTAR CUENTAS PENDIENTES DE PAGO
    public List<Cuenta> listarCuentasPendientes()
            throws SQLException {

        List<Cuenta> pendientes = new ArrayList<>();

        for (Cuenta cuenta : cuentaDAO.listar()) {
            if ("PENDIENTE".equalsIgnoreCase(cuenta.getEstado())) {
                pendientes.add(cuenta);
            }
        }

        return pendientes;
    }

    // GENERAR CUENTA AUTOMÁTICAMENTE
    public int generarCuenta(int pedidoId) throws SQLException {

        if (pedidoId <= 0) {
            throw new IllegalArgumentException(
                "Debe seleccionar un pedido válido."
            );
        }

        // El DAO verifica que el pedido esté ENTREGADO,
        // calcula el IGV y evita cuentas duplicadas.
        return cuentaDAO.generarCuenta(pedidoId);
    }

    // REGISTRAR PAGO
    public void registrarPago(int cuentaId, String metodoPago)
            throws SQLException {

        if (cuentaId <= 0) {
            throw new IllegalArgumentException(
                "Debe seleccionar una cuenta válida."
            );
        }

        if (metodoPago == null
                || (!"EFECTIVO".equalsIgnoreCase(metodoPago)
                && !"TARJETA".equalsIgnoreCase(metodoPago))) {

            throw new IllegalArgumentException(
                "Seleccione Efectivo o Tarjeta."
            );
        }

        cuentaDAO.registrarPago(cuentaId, metodoPago);
    }

    // CONSULTAR SI UNA CUENTA ESTÁ PAGADA
    public boolean estaPagada(int cuentaId) throws SQLException {

        Cuenta cuenta = buscarCuenta(cuentaId);

        return cuenta != null
                && "PAGADO".equalsIgnoreCase(cuenta.getEstado());
    }
}


