package modelo;

public class DetallePedido {

	private int id;
	private int cantidad;
	private double precioUnitario;
	private double subTotal;
	private Pedido pedido;
	private Plato plato;

	public DetallePedido() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public int getPedidoId() {
		return pedido == null ? 0 : pedido.getId();
	}

	public Plato getPlato() {
		return plato;
	}

	public void setPlato(Plato plato) {
		this.plato = plato;
	}

	public int getPlatoId() {
		return plato == null ? 0 : plato.getId();
	}

	public double calcularSubTotal() {
		subTotal = cantidad * precioUnitario;

		return subTotal;
	}

	@Override
	public String toString() {

		if (plato != null) {
			return plato.toString();
		}

		return "Detalle #" + id;
	}
}
