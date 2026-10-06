
package modelo;

import java.time.LocalDateTime;

public class AtencionMesa {
    private int id;
    private int mesaId;
    private int mozoId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;

    public AtencionMesa() {
    }

    public AtencionMesa(int id, int mesaId, int mozoId, LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado) {
        this.id = id;
        this.mesaId = mesaId;
        this.mozoId = mozoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMesaId() {
        return mesaId;
    }

    public void setMesaId(int mesaId) {
        this.mesaId = mesaId;
    }

    public int getMozoId() {
        return mozoId;
    }

    public void setMozoId(int mozoId) {
        this.mozoId = mozoId;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public void iniciarAtencion() {
        this.fechaInicio = LocalDateTime.now();
        this.fechaFin = null;
        this.estado = "ACTIVA";
    }

    public void finalizarAtencion() {
        this.fechaFin = LocalDateTime.now();
        this.estado = "FINALIZADA";
    }
    
}
