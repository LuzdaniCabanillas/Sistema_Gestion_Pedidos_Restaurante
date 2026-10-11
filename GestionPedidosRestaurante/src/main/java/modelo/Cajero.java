package modelo;

public class Cajero extends Empleado {

    public Cajero() {
    }

    public Cajero(int id, String nombre, String dni, String turno) {
        super(id, nombre, dni, turno);
    }
    
    
    @Override
    public String toString() {
        return getId() + " - " + getNombre();
    }

	

	public void RegistrarPago() {

	}

	public void CerrarCuenta() {

	}

}
