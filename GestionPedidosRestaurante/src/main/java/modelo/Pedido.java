package modelo;

import modelo.EstadoPedido;
import modelo.DetallePedido;
import modelo.Cuenta;

public class Pedido {

	private int id;

	private LocalDateTime fechaHora;

	private EstadoPedido estado;

	private double subTotal;

	private Mesa mesa;

	private Cuenta cuenta;

	private DetallePedido[] detallePedido;

	public void agregarPlato() {

	}

	public double calcularSubTotal() {
		return 0;
	}

	public void enviarACocina() {

	}

	public void entregarPedido() {

	}

	public void cerrarPedido() {

	}

}
