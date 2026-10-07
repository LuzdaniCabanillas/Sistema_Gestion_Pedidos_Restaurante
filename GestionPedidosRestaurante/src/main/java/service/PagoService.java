
package service;

import controlador.CuentaDAO;
import controlador.PagoDAO;

import modelo.Cuenta;
import modelo.EstadoCuenta;
import modelo.MetodoPago;
import modelo.Pago;


public class PagoService {
    
  
    // DAO
    private PagoDAO pagoDAO;
    private CuentaDAO cuentaDAO;

    // Constructor
    public PagoService() {

        pagoDAO = new PagoDAO();
        cuentaDAO = new CuentaDAO();
    }

    // REGISTRAR PAGO
    public boolean registrarPago(
            int cuentaId,
            MetodoPago metodoPago) {

        // Buscar la cuenta
        Cuenta cuenta =
                cuentaDAO.buscarPorId(cuentaId);

        if (cuenta == null) {

            System.out.println(
                    "La cuenta no existe."
            );

            return false;
        }

        // Validar si ya está pagada
        if (cuenta.getEstado()
                == EstadoCuenta.PAGADA) {

            System.out.println(
                    "La cuenta ya está pagada."
            );

            return false;
        }

        // Validar método de pago
        if (metodoPago == null) {

            System.out.println(
                    "Seleccione un método de pago."
            );

            return false;
        }

        // El monto sale del total de la cuenta
        double monto =
                cuenta.getTotal();

        // Crear el pago
        Pago pago =
                new Pago(
                        metodoPago,
                        monto,
                        cuentaId
                );

        // Registrar pago
        if (pagoDAO.insertar(pago)) {

            // Cambiar estado de la cuenta
            cuenta.setEstado(
                    EstadoCuenta.PAGADA
            );

            cuentaDAO.actualizar(cuenta);

            return true;
        }

        return false;
    }
    
}
