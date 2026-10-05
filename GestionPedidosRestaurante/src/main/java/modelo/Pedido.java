package modelo;

import java.time.LocalDateTime;

public class Pedido {

	private int id;
	private LocalDateTime fechaHora;
	private String estado;
	private double subTotal;
	private Mesa mesa;
	private Mozo mozo;
	private Cuenta cuenta;
	private DetallePedido[] detallePedido;

	public Pedido() {
		this.estado = EstadoPedido.PENDIENTE;
		this.subTotal = 0;
	}

	public Pedido(int id) {
		this();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public Mesa getMesa() {
		return mesa;
	}

	public void setMesa(Mesa mesa) {
		this.mesa = mesa;
	}

	public int getMesaId() {
		return mesa == null ? 0 : mesa.getId();
	}

	public Mozo getMozo() {
		return mozo;
	}

	public void setMozo(Mozo mozo) {
		this.mozo = mozo;
	}

	public int getMozoId() {
		return mozo == null ? 0 : mozo.getId();
	}

	public Cuenta getCuenta() {
		return cuenta;
	}

	public void setCuenta(Cuenta cuenta) {
		this.cuenta = cuenta;
	}

	public DetallePedido[] getDetallePedido() {
		return detallePedido;
	}

	public void setDetallePedido(DetallePedido[] detallePedido) {
		this.detallePedido = detallePedido;
	}


	public void enviarACocina() {
		if (EstadoPedido.PENDIENTE.equals(estado)) {
			estado = EstadoPedido.EN_PREPARACION;
		}
	}

	public void entregarPedido() {
		if (EstadoPedido.EN_PREPARACION.equals(estado)) {
			estado = EstadoPedido.ENTREGADO;
		}
	}

	public boolean puedeEliminarse() {
		return EstadoPedido.PENDIENTE.equals(estado);
	}

	public double calcularSubTotal() {
		if (detallePedido == null) {
			subTotal = 0;
			return subTotal;
		}
		double total = 0;
		for (DetallePedido detalle : detallePedido) {
			if (detalle != null) {
				total += detalle.calcularSubTotal();
			}
		}
		subTotal = total;
		return 0;
	}

	public void agregarPlato() {
		// La gestión de detalles se realiza mediante DetallePedido.
	}
	public void cerrarPedido() {
		// El cierre de cuenta corresponde a otro flujo.
	}

	@Override
	public String toString() {
		return "Pedido #" + id + " - " + estado;
	}
}
