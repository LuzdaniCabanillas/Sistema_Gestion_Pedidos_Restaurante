
package modelo;

public class Mozo extends Empleado {

	private Pedido pedido;

	private Empleado empleado;


	public void tomarPedido() {

	}

	public void egregarPlato() {

	}

	public void enviarACocina() {
		if (pedido != null) {
			pedido.enviarACocina();
		} else {
			System.out.println("No hay un pedido asignado para enviar a cocina.");
		}
	}

	public void entregarPedido() {
		if (pedido != null) {
			pedido.entregarPedido();
		} else {
			System.out.println("No hay un pedido asignado para entregar.");
		}
	}
	public void asignarPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Pedido getPedido() {
		return pedido;
	}
	public void registrarPago() {

	}

	@Override
	public String toString() {
		return getNombre();
	}

}
