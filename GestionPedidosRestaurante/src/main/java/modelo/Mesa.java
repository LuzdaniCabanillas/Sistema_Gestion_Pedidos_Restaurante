package modelo;
public class Mesa {

	private int id;

	private int numero;

	private int capacidad;

	private String estado;

    public Mesa() {
    }

    public Mesa(int id, int numero, int capacidad, String estado) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
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

        
	public void ocupar() {
            this.estado= "OCUPADO";

	}

	public void liberar() {
            this.estado= "DISPONIBLE";

	}
        
        public boolean estaDisponible() {
        return "DISPONIBLE".equals(this.estado);
    }

    @Override
    public String toString() {
        return "Mesa" + numero + " - capacidad: " + capacidad + "-" + estado;
    }

    

}
