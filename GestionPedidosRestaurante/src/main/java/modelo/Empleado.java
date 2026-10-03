package modelo;
public abstract class Empleado {

	private int id;

	private String nombre;

	private String dni;

	private String turno;
        

    public Empleado() {
    }

    public Empleado(int id, String nombre, String dni, String turno) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.turno = turno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

        
        
        
        
	public void mostrarDatos() {

	}

	public void registrarPago() {

	}

}
