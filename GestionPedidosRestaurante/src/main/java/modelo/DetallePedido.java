package modelo;

public class DetallePedido {

	private int id;
    private int cantidad;
    private double precioUnitario;
    private double subTotal;
    private Plato plato;
    private Pedido pedido;

    public DetallePedido() {
    }

    public DetallePedido(int id, double precioUnitario, double subTotal, int cantidad, Pedido pedido, Plato plato) {
        this.id = id;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subTotal = subTotal;
        this.plato = plato;
        this.pedido = pedido;
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
        if (cantidad < 1) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public Plato getPlato() {
        return plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public void calcularSubtotal() {
        this.subTotal
                = this.cantidad * this.precioUnitario;

    }

    @Override
    public String toString() {

        return plato.getNombre()
                + " - Cantidad: " + cantidad
                + " - Subtotal: S/ "
                + subTotal;
    }

}
