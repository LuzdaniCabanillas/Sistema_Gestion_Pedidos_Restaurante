package modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//SERGIO
public class Pago {

    private int id;
    private LocalDateTime fechaHora;
    private String metodoPago;
    private BigDecimal monto;
    private int cuentaId;

    private Integer mozoId;
    private Integer cajeroId;

    private String modalidadPago;
    private String estadoPago;

    private BigDecimal montoRecibido;
    private BigDecimal vuelto;

    private String referencia;
    private String observacion;

    public Pago() {
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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

   

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public int getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(int cuentaId) {
        this.cuentaId = cuentaId;
    }

    public Integer getMozoId() {
        return mozoId;
    }

    public void setMozoId(Integer mozoId) {
        this.mozoId = mozoId;
    }

    public Integer getCajeroId() {
        return cajeroId;
    }

    public void setCajeroId(Integer cajeroId) {
        this.cajeroId = cajeroId;
    }

    public String getModalidadPago() {
        return modalidadPago;
    }

    public void setModalidadPago(String modalidadPago) {
        this.modalidadPago = modalidadPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public BigDecimal getVuelto() {
        return vuelto;
    }

    public void setVuelto(BigDecimal vuelto) {
        this.vuelto = vuelto;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    
    
  
    public void registrarPago() {

    }

    public boolean validarMetodo() {
        return false;
    }

}
