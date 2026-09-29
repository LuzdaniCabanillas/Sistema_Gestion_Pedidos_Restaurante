package restaurante;
import java.util.Date;

public class Cuenta {

	private int id;

	private Date fechaGeneracion;

	private double subTotal;

	private double IGV;

	private double total;

	private EstadoCuenta estado;

	private Pago pago;

	public void generarCuenta() {

	}

	public double calcularIGV() {
		return 0;
	}

	public double calcularTotal() {
		return 0;
	}

	public void registrarPago() {

	}

	public void cambiarEstado() {

	}

	public void cerrarCuenta() {

	}

}
