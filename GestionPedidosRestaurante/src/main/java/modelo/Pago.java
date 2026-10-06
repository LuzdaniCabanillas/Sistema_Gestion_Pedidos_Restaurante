
package modelo;
import java.time.LocalDateTime;

import modelo.Cajero;

public class Pago {

	private int id;

	private LocalDateTime fechaHora;

	private MetodoPago metodoPago;

	private double monto;

	private Cajero cajero;

	public void registrarPago() {

	}

	public boolean validarMetodo() {
		return false;
	}

}
