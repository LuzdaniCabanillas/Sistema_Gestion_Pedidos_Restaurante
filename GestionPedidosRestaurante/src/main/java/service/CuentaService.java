
package service;

import controlador.CuentaDAO;
import java.util.List;
import modelo.Cuenta;
import modelo.EstadoCuenta;

public class CuentaService {
    
     private CuentaDAO cuentaDAO;

    public CuentaService() {
        cuentaDAO = new CuentaDAO();
    }

    // REGISTRAR
    public boolean registrarCuenta(double subtotal) {

        // Validaciones
        if (subtotal <= 0) {
            System.out.println(
                    "El subtotal debe ser mayor que 0."
            );
            return false;
        }

        Cuenta cuenta = new Cuenta(
                subtotal,
                EstadoCuenta.PENDIENTE
        );

        return cuentaDAO.insertar(cuenta);
    }

    // LISTAR
    public List<Cuenta> listarCuentas() {

        return cuentaDAO.listar();
    }

    // BUSCAR
    public Cuenta buscarCuenta(int id) {

        if (id <= 0) {
            return null;
        }

        return cuentaDAO.buscarPorId(id);
    }

    // ACTUALIZAR
    public boolean actualizarCuenta(
            int id,
            double subtotal,
            EstadoCuenta estado) {

        // Validaciones
        if (id <= 0) {
            return false;
        }

        if (subtotal <= 0) {
            System.out.println(
                    "El subtotal debe ser mayor que 0."
            );
            return false;
        }

        if (estado == null) {
            System.out.println(
                    "Debe seleccionar un estado."
            );
            return false;
        }

        Cuenta cuenta =
                cuentaDAO.buscarPorId(id);

        if (cuenta == null) {
            System.out.println(
                    "La cuenta no existe."
            );
            return false;
        }

        cuenta.setSubTotal(subtotal);

        cuenta.setIgv(
                cuenta.calcularIGV()
        );

        cuenta.setTotal(
                cuenta.calcularTotal()
        );

        cuenta.setEstado(estado);

        return cuentaDAO.actualizar(cuenta);
    }

    // ELIMINAR
    public boolean eliminarCuenta(int id) {

        if (id <= 0) {
            return false;
        }

        Cuenta cuenta =
                cuentaDAO.buscarPorId(id);

        if (cuenta == null) {
            System.out.println(
                    "La cuenta no existe."
            );
            return false;
        }

        if (cuenta.getEstado()
                == EstadoCuenta.PAGADA) {

            System.out.println(
                    "No se puede eliminar una cuenta pagada."
            );

            return false;
        }

        return cuentaDAO.eliminar(id);
    }
    
}
