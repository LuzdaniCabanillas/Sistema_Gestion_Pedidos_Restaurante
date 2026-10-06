package modelo;
public class Mesa {

	private int id;
	private int numero;
	private int capacidad;
	private String estado;

	public void ocupar() {
		estado = "OCUPADA";
	}

	public void liberar() {
		estado = "DISPONIBLE";
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "Mesa " + numero + " - " + estado;
	}
}
