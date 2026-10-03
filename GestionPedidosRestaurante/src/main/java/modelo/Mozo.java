
package modelo;

import modelo.Empleado;

public class Mozo extends Empleado {

	private Pedido pedido;

	private Empleado empleado;

        
        public Mozo() {
        super();
        }

    public Mozo(int id, String nombre, String dni, String turno) {
        super(id, nombre, dni, turno);
    }
        
        
	public void tomarPedido() {

	}

	public void egregarPlato() {

	}

	public void enviarACocina() {

	}

	public void entregarPedido() {

	}

	public void registrarPago() {

	}

}
