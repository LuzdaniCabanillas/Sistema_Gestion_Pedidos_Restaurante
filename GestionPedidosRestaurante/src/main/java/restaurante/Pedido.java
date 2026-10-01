package restaurante;
public class Pedido {

	private int id;

	private LocalDateTime fechaHora;

	private EstadoPedido estado;

	private double subTotal;

	private Mesa mesa;

	private Cuenta cuenta;

	private DetallePedido[] detallePedido;

	public Pedido() {
		this.estado = EstadoPedido.PENDIENTE;
	}

	public Pedido(int id) {
		this.id = id;
		this.estado = EstadoPedido.PENDIENTE;
	}

	public void agregarPlato() {

	}

	public double calcularSubTotal() {
		return 0;
	}

	/**
	 * Registra el envío del pedido a cocina cambiando su estado.
	 * La preparación en cocina queda fuera del alcance del sistema.
	 */
	public void enviarACocina() {
		if (estado == EstadoPedido.PENDIENTE) {
			estado = EstadoPedido.EN_PREPARACION;
			System.out.println("Pedido " + id + " enviado a cocina.");
		} else {
			System.out.println("El pedido " + id
					+ " no puede ser enviado a cocina. Estado actual: " + estado);
		}
	}
	/**
	 * Registra la entrega del pedido una vez que este se encuentra
	 * en preparación. El proceso interno de cocina no se implementa.
	 */
	public void entregarPedido() {
		if (estado == EstadoPedido.EN_PREPARACION) {
			estado = EstadoPedido.ENTREGADO;
			System.out.println("Pedido " + id + " entregado al cliente.");
		} else {
			System.out.println("El pedido " + id
					+ " no puede ser entregado. Estado actual: " + estado);
		}
	}

	public int getId() {
		return id;
	}

	public EstadoPedido getEstado() {
		return estado;
	}

	public void setEstado(EstadoPedido estado) {
		this.estado = estado;
	}

	public void cerrarPedido() {

	}

}
