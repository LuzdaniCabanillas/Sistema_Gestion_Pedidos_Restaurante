package modelo;

import java.time.LocalDateTime;


public class Pago {

	private int id;

	private LocalDateTime fechaHora;

	private MetodoPago metodoPago;

	private double monto;
        
         private int cuentaId;
        
        
    public Pago() {
    }

    // Para registrar un pago nuevo
    public Pago(
            MetodoPago metodoPago,
            double monto,
            int cuentaId) {

        this.fechaHora = LocalDateTime.now();
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.cuentaId = cuentaId;
    }
    
    
  public boolean validarMetodo() {
        return metodoPago != null;
    }

    public void registrarPago() {
        if (validarMetodo() && monto > 0) {
            fechaHora = LocalDateTime.now();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(int cuentaId) {
        this.cuentaId = cuentaId;
    }

  

}
