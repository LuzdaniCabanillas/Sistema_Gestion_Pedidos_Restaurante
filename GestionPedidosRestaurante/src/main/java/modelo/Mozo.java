
package modelo;

public class Mozo extends Empleado {

	private Pedido pedido;

	private Empleado empleado;

	public void tomarPedido() {

	}

	public void egregarPlato() {

	}
	/**
	 * El mozo solicita el envío del pedido a cocina.
	 * La lógica del cambio de estado pertenece a Pedido.
	 */
	public void enviarACocina() {
		if (pedido != null) {
			pedido.enviarACocina();
		} else {
			System.out.println("No hay un pedido asignado para enviar a cocina.");
		}
	}
	/**
	 * El mozo registra la entrega del pedido.
	 * El proceso interno de cocina no forma parte del sistema.
	 */
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

}
